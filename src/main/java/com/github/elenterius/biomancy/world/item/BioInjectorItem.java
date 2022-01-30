package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BioInjectorItem extends Item implements IKeyListener {

	public static final String NBT_KEY_SERUM_AMOUNT = "SerumAmount";

	public BioInjectorItem(Properties properties) {
		super(properties);
	}

	public static boolean tryInjectLivingEntity(ServerLevel level, BlockPos pos, ItemStack stack) {
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos), EntitySelector.NO_SPECTATORS);
		if (!entities.isEmpty() && dispenserAffectEntity(level, stack, entities.get(0))) {
			level.playSound(null, pos, ModSoundEvents.INJECT.get(), SoundSource.BLOCKS, 0.8f, 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f);
			return true;
		}
		return false;
	}

	private static boolean dispenserAffectEntity(ServerLevel level, ItemStack stack, LivingEntity target) {
		BioInjectorItem item = (BioInjectorItem) stack.getItem();
		Serum serum = item.getSerum(stack);
		if (serum != null && serum.affectEntity(Serum.getDataTag(stack), null, target)) {
			if (serum.isAttributeModifier()) serum.applyAttributesModifiersToEntity(target);
			item.addSerumAmount(stack, (byte) -1);
			level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, target.blockPosition(), 0);
			return true;
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, ClientLevel level, Player player, byte flags) {
		if (!interactWithPlayerSelf(stack, player)) {
			ModSoundEvents.playItemSFX(level, player, ModSoundEvents.FAIL);
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}
		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (interactWithPlayerSelf(stack, player)) {
			ModSoundEvents.playItemSFX(level, player, ModSoundEvents.INJECT.get());
		}
		else {
			ModSoundEvents.playItemSFX(level, player, ModSoundEvents.FAIL);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		Player player = context.getPlayer();
		if (player != null && !player.mayUseItemAt(context.getClickedPos().relative(context.getClickedFace()), context.getClickedFace(), stack))
			return InteractionResult.FAIL;

		Serum reagent = getSerum(stack);
		if (reagent != null) {
			Level level = context.getLevel();
			boolean success = reagent.affectBlock(Serum.getDataTag(stack), player, level, context.getClickedPos(), context.getClickedFace());
			if (success) {
				if (!level.isClientSide) {
					if (player == null || !player.isCreative()) addSerumAmount(stack, (byte) -1);
					level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, context.getClickedPos().above(), 0);
					if (player != null) {
						ModSoundEvents.playItemSFX(level, player, ModSoundEvents.INJECT.get());
						stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
					}
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			}

			if (level.isClientSide && player != null) ModSoundEvents.playItemSFX(level, player, ModSoundEvents.FAIL);
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	public boolean canInjectIntoEntity(ItemStack stack, LivingEntity target) {
		int pierceLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, stack);
		float pct = CombatRules.getDamageAfterAbsorb(10f, target.getArmorValue(), (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS)) / 10f;
		return target.getRandom().nextFloat() < pct + 0.075f * pierceLevel;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		Serum reagent = getSerum(stack);
		if (reagent != null) {
			if (canInjectIntoEntity(stack, interactionTarget) && reagent.affectEntity(Serum.getDataTag(stack), player, interactionTarget)) {
				if (interactionTarget.level.isClientSide) return InteractionResult.SUCCESS;

				if (reagent.isAttributeModifier()) reagent.applyAttributesModifiersToEntity(interactionTarget);
				if (!player.isCreative()) addSerumAmount(stack, (byte) -1);
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

				interactionTarget.level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, interactionTarget.blockPosition(), 0);
				ModSoundEvents.playItemSFX(interactionTarget.level, player, ModSoundEvents.INJECT.get());
				return InteractionResult.CONSUME;
			}

			if (player.level.isClientSide) ModSoundEvents.playItemSFX(player.level, player, ModSoundEvents.FAIL);
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	public boolean interactWithPlayerSelf(ItemStack stack, Player player) {
		Serum reagent = getSerum(stack);
		if (reagent != null) {
			boolean success = canInjectIntoEntity(stack, player) && reagent.affectPlayerSelf(Serum.getDataTag(stack), player);
			if (success && !player.level.isClientSide) {
				if (reagent.isAttributeModifier()) reagent.applyAttributesModifiersToEntity(player);
				if (!player.isCreative()) addSerumAmount(stack, (byte) -1);
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
			return success;
		}
		return false;
	}

	@Nullable
	public Serum getSerum(ItemStack stack) {
		return Serum.deserialize(stack.getOrCreateTag());
	}

	public int getSerumColor(ItemStack stack) {
		return Serum.getColor(stack.getOrCreateTag());
	}

	public byte getMaxSerumAmount() {
		return (byte) 4;
	}

	public void addSerumAmount(ItemStack stack, byte amount) {
		setSerumAmount(stack, (byte) (getSerumAmount(stack) + amount));
	}

	public void setSerumAmount(ItemStack stack, byte amount) {
		amount = (byte) Mth.clamp(amount, 0, getMaxSerumAmount());
		if (amount == 0) {
			Serum.remove(stack.getOrCreateTag());
		}
		stack.getOrCreateTag().putByte(NBT_KEY_SERUM_AMOUNT, amount);
	}

	public byte getSerumAmount(ItemStack stack) {
		return stack.getOrCreateTag().getByte(NBT_KEY_SERUM_AMOUNT);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.PIERCING || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTranslationText("tooltip", "action_self_inject")).withStyle(ChatFormatting.DARK_GRAY));

		Serum serum = getSerum(stack);
		if (serum != null) {
			byte amount = getSerumAmount(stack);
			tooltip.add(new TextComponent(String.format("Amount: %d/4", amount)).withStyle(ChatFormatting.GRAY));
			serum.addInfoToTooltip(stack, level, tooltip, isAdvanced);
		}
		else tooltip.add(TextComponentUtil.getTranslationText("tooltip", "contains_nothing").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		if (displayName instanceof MutableComponent mutableComponent) {
			Serum serum = getSerum(stack);
			if (serum != null) {
				return mutableComponent.append(" (").append(new TranslatableComponent(serum.getTranslationKey()).withStyle(ChatFormatting.AQUA)).append(")");
			}
		}
		return displayName;
	}

}
