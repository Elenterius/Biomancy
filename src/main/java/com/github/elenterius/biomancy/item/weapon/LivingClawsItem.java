package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.api.livingtool.LivingToolState;
import com.github.elenterius.biomancy.api.livingtool.SpecialLivingTool;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LivingClawsItem extends SimpleClawsItem implements SpecialLivingTool, KeyPressListener {

	private final int maxNutrients;

	public LivingClawsItem(Tier tier, float baseAttackDamage, float attackSpeedModifier, float attackRangeModifier, int maxNutrients, Properties properties) {
		super(tier, baseAttackDamage, attackSpeedModifier, attackRangeModifier, properties);
		this.maxNutrients = maxNutrients;
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (!hasNutrients(stack)) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
			player.playSound(ModSoundEvents.FLESHKIN_NO.get(), 1f, 1f + player.level().getRandom().nextFloat() * 0.4f);
			return InteractionResultHolder.fail(flags);
		}

		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		updateLivingToolState(stack, level, player);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!MobUtil.isCreativePlayer(attacker)) {
			consumeNutrients(stack, getLivingToolActionCost(stack, ToolActions.SWORD_SWEEP));
		}
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
		if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0f && !MobUtil.isCreativePlayer(entity)) {
			consumeNutrients(stack, getLivingToolActionCost(stack, ToolActions.SWORD_DIG));
		}
		return true;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		LivingToolState toolState = getLivingToolState(stack);
		return toolState == LivingToolState.BROKEN ? 1f : super.getDestroySpeed(stack, state);
	}

	@Override
	public int getMaxNutrients(ItemStack container) {
		return maxNutrients;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack livingTool, Slot slot, ClickAction action, Player player) {
		if (handleOverrideStackedOnOther(livingTool, slot, action, player)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack livingTool, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (handleOverrideOtherStackedOnMe(livingTool, other, slot, action, player, access)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	protected void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
	}

	@Override
	public int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ToolAction toolAction) {
		int baseCost = 0;
		if (toolAction == ToolActions.SWORD_DIG) baseCost = 2;
		if (toolAction == ToolActions.SWORD_SWEEP) baseCost = 1;

		return switch (state) {
			case AWAKENED -> baseCost + 4;
			case DORMANT -> baseCost + 1;
			default -> baseCost;
		};
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return isValidEnchantment(stack, enchantment) && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return getMaxStackSize(stack) == 1;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getNutrients(stack) < getMaxNutrients(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(getNutrientsPct(stack) * 13f);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return ColorStyles.NUTRIENTS_FUEL_BAR;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltip, isAdvanced);
		tooltip.add(ComponentUtil.emptyLine());

		appendLivingToolTooltip(stack, tooltip);

		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.switch_mode")));

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		return ComponentUtil.mutable().append(displayName).append(" (").append(getLivingToolState(stack).getDisplayName()).append(")");
	}

}
