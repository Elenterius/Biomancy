package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.HrTooltipComponent;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class LongClawItem extends ClawWeaponItem implements ICriticalHitEntity, IBiomancyItem /*implements IAreaHarvestingItem*/ {

	public static final String NBT_KEY = "LongClawTimeLeft";
	public static final AttributeModifier RETRACTED_CLAW_REACH_MODIFIER = new AttributeModifier(UUID.fromString("d76adb08-2bb3-4e88-997d-766a919f0f6b"), "Weapon modifier", 1f, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier EXTENDED_CLAW_REACH_MODIFIER = new AttributeModifier(UUID.fromString("29ace568-4e32-4809-840c-3c9a0e1ebcd4"), "Weapon modifier", 3f, AttributeModifier.Operation.ADDITION);

	private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiersV2;

	private final int abilityDuration; // in "seconds"

	public LongClawItem(Tier tier, int attackDamage, float attackSpeed, int abilityDuration, Properties properties) {
		super(tier, attackDamage, attackSpeed, properties);
		lazyAttributeModifiersV2 = Lazy.of(this::createAttributeModifiersV2);
		this.abilityDuration = abilityDuration;
	}

	public static boolean isClawExtended(ItemStack stack) {
		return stack.getOrCreateTag().getInt(NBT_KEY) > 0;
	}

	protected Multimap<Attribute, AttributeModifier> createAttributeModifiersV2() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> clawAttributes = lazyAttributeModifiers.get();
		clawAttributes.forEach((attribute, attributeModifier) -> {
			if (attributeModifier != RETRACTED_CLAW_REACH_MODIFIER) {
				builder.put(attribute, attributeModifier);
			}
		});
		builder.put(ForgeMod.ATTACK_RANGE.get(), EXTENDED_CLAW_REACH_MODIFIER);
		return builder.build();
	}

	@Override
	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addAdditionalAttributeModifiers(builder);
		builder.put(ForgeMod.ATTACK_RANGE.get(), RETRACTED_CLAW_REACH_MODIFIER);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return slot == EquipmentSlot.MAINHAND && isClawExtended(stack) ? lazyAttributeModifiersV2.get() : super.getAttributeModifiers(slot, stack);
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		if (!attacker.level.isClientSide()) {
			stack.getOrCreateTag().putInt(NBT_KEY, abilityDuration);
		}
		else {
			attacker.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1f, 1f / (attacker.getRandom().nextFloat() * 0.5f + 1f) + 0.2f);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (!level.isClientSide() && level.getGameTime() % 20L == 0L) {
			CompoundTag tag = stack.getOrCreateTag();
			int timeLeft = tag.getInt(NBT_KEY);
			if (timeLeft > 0) {
				tag.putInt(NBT_KEY, timeLeft - 1);
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

//	@Override
//	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
//		byte harvestRange = getBlockHarvestRange(stack);
//		if (!player.isShiftKeyDown() && harvestRange > 0 && !player.level.isClientSide && player instanceof ServerPlayer serverPlayer) {
//			ServerLevel level = serverPlayer.getLevel();
//			BlockState blockState = level.getBlockState(pos);
//			HitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
//			if (PlayerInteractionUtil.harvestBlock(level, serverPlayer, blockState, pos) && getDestroySpeed(stack, blockState) > 1f) {
//				List<BlockPos> blockNeighbors = PlayerInteractionUtil.findBlockNeighbors(level, hitResult, blockState, pos, harvestRange, getHarvestShape(stack));
//				for (BlockPos neighborPos : blockNeighbors) {
//					PlayerInteractionUtil.harvestBlock(level, serverPlayer, blockState, neighborPos);
//				}
//			}
//			return true;
//		}
//
//		//only called on client side
//		return super.onBlockStartBreak(stack, pos, player);
//	}

//	@Override
//	public boolean isAreaSelectionVisibleFor(ItemStack stack, BlockPos pos, BlockState state) {
//		return super.getDestroySpeed(stack, state) > 1f;
//	}
//
//	@Override
//	public byte getBlockHarvestRange(ItemStack stack) {
//		return (byte) 1;
//	}

//	@Override
//	public GeometricShape getHarvestShape(ItemStack stack) {
//		return GeometricShape.CUBE;
//	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		int timeLeft = stack.getOrCreateTag().getInt(NBT_KEY);
		if (timeLeft > 0) {
			tooltip.add(TextComponentUtil.getTooltipText("item_is_excited").append(" (" + timeLeft + ")").withStyle(ChatFormatting.GRAY));
		}
		else {
			tooltip.add(TextComponentUtil.getTooltipText("item_is_dormant").withStyle(ChatFormatting.GRAY));
		}
		if (stack.isEnchanted()) tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		if (displayName instanceof MutableComponent mutableComponent) {
			String keySuffix = stack.getOrCreateTag().getInt(NBT_KEY) > 0 ? "excited" : "dormant";
			return mutableComponent.append(" (").append(TextComponentUtil.getTooltipText(keySuffix)).append(")");
		}
		return displayName;
	}

}
