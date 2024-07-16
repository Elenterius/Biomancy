package com.github.elenterius.biomancy.item.shield;

import com.github.elenterius.biomancy.api.livingtool.SimpleLivingTool;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LivingShieldItem extends SimpleShieldItem implements SimpleLivingTool {

	private final int maxNutrients;

	public LivingShieldItem(int maxNutrients, Properties properties) {
		super(properties);
		this.maxNutrients = maxNutrients;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltip, isAdvanced);
		tooltip.add(ComponentUtil.emptyLine());

		appendLivingToolTooltip(stack, tooltip);

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}

	@Override
	public int getMaxNutrients(ItemStack stack) {
		return maxNutrients;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (handleOverrideStackedOnOther(stack, slot, action, player)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (handleOverrideOtherStackedOnMe(stack, other, slot, action, player, access)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return super.canPerformAction(stack, toolAction) && hasNutrients(stack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!hasNutrients(stack)) {
			if (level.isClientSide()) {
				player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
				player.playSound(ModSoundEvents.FLESHKIN_NO.get(), 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
			}
			return InteractionResultHolder.fail(stack);
		}

		return super.use(level, player, hand);
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide()) return;

		if (!hasNutrients(stack)) {
			livingEntity.stopUsingItem();
		}
	}

	public void damageCurrentlyUsedLivingShield(ItemStack usedShield, float damageAmount, LivingEntity entity) {
		if (!MobUtil.isCreativePlayer(entity)) {
			int amount = 1 + Mth.floor(damageAmount);
			decreaseNutrients(usedShield, amount);
		}
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

	protected void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
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
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return isValidEnchantment(stack, enchantment) && super.canApplyAtEnchantingTable(stack, enchantment);
	}

}
