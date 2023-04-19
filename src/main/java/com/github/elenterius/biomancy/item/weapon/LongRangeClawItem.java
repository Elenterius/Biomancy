package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.item.IAreaHarvestingItem;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.GeometricShape;
import com.github.elenterius.biomancy.util.PlayerInteractionUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;

public class LongRangeClawItem extends ClawWeaponItem implements IAreaHarvestingItem {

	public static final String NBT_KEY = "LongClawTimeLeft";
	private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiersV2;

	private final int abilityDuration; // in "seconds"

	public LongRangeClawItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, int abilityDuration, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
		lazyAttributeModifiersV2 = Lazy.of(this::createAttributeModifiersV2);
		this.abilityDuration = abilityDuration;
	}

	public static boolean isClawExtended(ItemStack stack) {
		return stack.getOrCreateTag().getInt(NBT_KEY) > 0;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		int timeLeft = stack.getOrCreateTag().getInt(NBT_KEY);
		if (timeLeft > 0) {
			tooltip.add(TextUtil.getTranslationText("tooltip", "item_is_excited").append(" (" + timeLeft + ")").withStyle(TextFormatting.GRAY));
		}
		else {
			tooltip.add(TextUtil.getTranslationText("tooltip", "item_is_dormant").withStyle(TextFormatting.GRAY));
		}
		if (stack.isEnchanted()) tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		if (displayName instanceof IFormattableTextComponent) {
			String keySuffix = stack.getOrCreateTag().getInt(NBT_KEY) > 0 ? "excited" : "dormant";
			return ((IFormattableTextComponent) displayName).append(" (").append(TextUtil.getTranslationText("tooltip", keySuffix)).append(")");
		}
		return displayName;
	}

	protected Multimap<Attribute, AttributeModifier> createAttributeModifiersV2() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> clawAttributes = lazyAttributeModifiers.get();
		clawAttributes.forEach((attribute, attributeModifier) -> {
			if (attribute == Attributes.ATTACK_DAMAGE && attributeModifier.getId().equals(BASE_ATTACK_DAMAGE_UUID)) {
				builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, attributeModifier.getName(), attributeModifier.getAmount() + 2f, attributeModifier.getOperation()));
			}
			else if (attribute == Attributes.ATTACK_SPEED && attributeModifier.getId().equals(BASE_ATTACK_SPEED_UUID)) {
				builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, attributeModifier.getName(), attributeModifier.getAmount() + 0.5f, attributeModifier.getOperation()));
			}
			else builder.put(attribute, attributeModifier);
		});
		return builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		return slot == EquipmentSlotType.MAINHAND && isClawExtended(stack) ? lazyAttributeModifiersV2.get() : super.getAttributeModifiers(slot, stack);
	}

	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		super.onCriticalHitEntity(stack, attacker, target);
		if (!attacker.level.isClientSide()) {
			stack.getOrCreateTag().putInt(NBT_KEY, abilityDuration);
		}
		else {
			attacker.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1f, 1f / (random.nextFloat() * 0.5f + 1f) + 0.2f);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isClientSide() && worldIn.getGameTime() % 20L == 0L) {
			CompoundNBT nbt = stack.getOrCreateTag();
			int timeLeft = nbt.getInt(NBT_KEY);
			if (timeLeft > 0) {
				nbt.putInt(NBT_KEY, timeLeft - 1);
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
		byte harvestRange = getBlockHarvestRange(stack);
		if (!player.isShiftKeyDown() && harvestRange > 0 && !player.level.isClientSide && player instanceof ServerPlayerEntity) {
			ServerWorld world = (ServerWorld) player.level;
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			BlockState blockState = world.getBlockState(pos);
			BlockRayTraceResult rayTraceResult = Item.getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
			if (PlayerInteractionUtil.harvestBlock(world, serverPlayer, blockState, pos) && getDestroySpeed(stack, blockState) > 1f) {
				List<BlockPos> blockNeighbors = PlayerInteractionUtil.findBlockNeighbors(world, rayTraceResult, blockState, pos, harvestRange, getHarvestShape(stack));
				for (BlockPos neighborPos : blockNeighbors) {
					PlayerInteractionUtil.harvestBlock(world, serverPlayer, blockState, neighborPos);
				}
			}
			return true;
		}

		//only called on client side
		return super.onBlockStartBreak(stack, pos, player);
	}

	@Override
	public boolean isAreaSelectionVisibleFor(ItemStack stack, BlockPos pos, BlockState state) {
		return super.getDestroySpeed(stack, state) > 1f;
	}

	@Override
	public byte getBlockHarvestRange(ItemStack stack) {
		return (byte) 1;
	}

	@Override
	public GeometricShape getHarvestShape(ItemStack stack) {
		return GeometricShape.CUBE;
	}
}