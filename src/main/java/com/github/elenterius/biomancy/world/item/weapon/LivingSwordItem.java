package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import com.github.elenterius.biomancy.world.item.IKeyListener;
import com.github.elenterius.biomancy.world.item.INutrientsContainerItem;
import com.github.elenterius.biomancy.world.item.LivingToolState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class LivingSwordItem extends SwordItem implements IBiomancyItem, INutrientsContainerItem, IKeyListener {

	private final int maxNutrients;

	public LivingSwordItem(Tier tier, int damageModifier, float attackSpeedModifier, int maxNutrients, Properties properties) {
		super(tier, damageModifier, attackSpeedModifier, properties);
		this.maxNutrients = maxNutrients;
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (!hasNutrients(stack)) {
			player.playSound(SoundEvents.VILLAGER_NO, 0.8f, 0.8f + player.getLevel().getRandom().nextFloat() * 0.4f);
			return InteractionResultHolder.fail(flags);
		}

		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		LivingToolState state = getLivingToolState(stack);
		boolean hasNutrients = hasNutrients(stack);

		if (state == LivingToolState.AWAKE) {
			setLivingToolState(stack, hasNutrients ? LivingToolState.EXALTED : LivingToolState.DORMANT);
			SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_PLACE.get());
		}
		else if (state == LivingToolState.EXALTED) {
			setLivingToolState(stack, hasNutrients ? LivingToolState.AWAKE : LivingToolState.DORMANT);
			SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_HIT.get());
		}
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
		return toolState == LivingToolState.DORMANT ? 1f : super.getDestroySpeed(stack, state);
	}

	@Override
	public int getMaxNutrients(ItemStack stack) {
		return maxNutrients;
	}

	@Override
	public void onNutrientsChanged(ItemStack stack, int oldValue, int newValue) {
		LivingToolState prevState = getLivingToolState(stack);
		LivingToolState state = prevState;

		if (newValue <= 0) {
			if (state != LivingToolState.DORMANT) setLivingToolState(stack, LivingToolState.DORMANT);
			return;
		}

		if (state == LivingToolState.DORMANT) {
			state = LivingToolState.AWAKE;
		}

		int digCost = getLivingToolActionCost(stack, ToolActions.SWORD_DIG, state);
		int attackCost = getLivingToolActionCost(stack, ToolActions.SWORD_SWEEP, state);
		int maxCost = Math.max(digCost, attackCost);

		if (newValue < maxCost) {
			if (state == LivingToolState.EXALTED) state = LivingToolState.AWAKE;
			else if (state == LivingToolState.AWAKE) state = LivingToolState.DORMANT;
		}

		if (state != prevState) setLivingToolState(stack, state);
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (stack.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY) return false;

		final ItemStack stackInSlot = slot.getItem();
		if (!stackInSlot.isEmpty()) {
			ItemStack fuelStack = slot.safeTake(1, 1, player);
			ItemStack remainder = addFuel(stack, fuelStack);
			slot.safeInsert(remainder);
			int insertedAmount = fuelStack.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				playSound(player, SoundEvents.GENERIC_EAT);
			}
		}

		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (stack.getCount() > 1) return false;
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

		if (!other.isEmpty()) {
			ItemStack remainder = addFuel(stack, other);
			int insertedAmount = other.getCount() - remainder.getCount();
			if (insertedAmount > 0) {
				playSound(player, SoundEvents.GENERIC_EAT);
				other.shrink(insertedAmount);
			}
		}

		return true;
	}

	private void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.getLevel().getRandom().nextFloat() * 0.4f);
	}

	public boolean isValidFuel(ItemStack fuelStack) {
		return NutrientFuelUtil.isValidFuel(fuelStack);
	}

	public int getFuelValue(ItemStack fuelStack) {
		return NutrientFuelUtil.getFuelValue(fuelStack);
	}

	public ItemStack addFuel(ItemStack livingToolStack, ItemStack fuelStack) {
		if (fuelStack.isEmpty()) return fuelStack;
		if (!isValidFuel(fuelStack)) return fuelStack;

		final int nutrients = getNutrients(livingToolStack);
		int maxNutrients = getMaxNutrients(livingToolStack);
		if (nutrients >= maxNutrients) return fuelStack;

		int fuelValue = getFuelValue(fuelStack);
		if (fuelValue <= 0) return fuelStack;

		int neededCount = Mth.floor(Math.max(0, maxNutrients - nutrients) / (float) fuelValue);
		if (neededCount > 0) {
			setNutrients(livingToolStack, nutrients + fuelValue);
			return ItemHandlerHelper.copyStackWithSize(fuelStack, fuelStack.getCount() - 1);
		}
		return fuelStack;
	}

	public int getLivingToolActionCost(ItemStack stack, ToolAction toolAction) {
		LivingToolState state = getLivingToolState(stack);
		return getLivingToolActionCost(stack, toolAction, state);
	}

	private int getLivingToolActionCost(ItemStack stack, ToolAction toolAction, LivingToolState state) {
		int baseCost = 0;
		if (toolAction == ToolActions.SWORD_DIG) baseCost = 1;
		if (toolAction == ToolActions.SWORD_SWEEP) baseCost = 2;

		return switch (state) {
			case EXALTED -> baseCost + 4;
			case AWAKE -> baseCost + 2;
			default -> baseCost;
		};
	}

	public LivingToolState getLivingToolState(ItemStack stack) {
		boolean hasNutrients = hasNutrients(stack);
		if (!hasNutrients) return LivingToolState.DORMANT;

		return LivingToolState.deserialize(stack.getOrCreateTag());
	}

	public void setLivingToolState(ItemStack stack, LivingToolState state) {
		state.serialize(stack.getOrCreateTag());
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
	public boolean isBarVisible(ItemStack stack) {
		return getNutrients(stack) > 0;
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
		ClientTextUtil.appendItemInfoTooltip(stack.getItem(), tooltip);

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		tooltip.add(getLivingToolState(stack).getItemTooltip().withStyle(ChatFormatting.GRAY));

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
		tooltip.add(new TextComponent("Nutrients").withStyle(ChatFormatting.GRAY));
		tooltip.add(new TextComponent("%s/%s u".formatted(df.format(getNutrients(stack)), df.format(getMaxNutrients(stack)))).withStyle(Style.EMPTY.withColor(0x65b52a)));

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		tooltip.add(new TextComponent("Consumption").withStyle(ChatFormatting.GRAY));
		for (ToolAction swordAction : ToolActions.DEFAULT_SWORD_ACTIONS) {
			if (!canPerformAction(stack, swordAction)) continue;
			int actionCost = getLivingToolActionCost(stack, swordAction);
			String text = "%s:  %s u".formatted(swordAction.name(), df.format(actionCost));
			tooltip.add(new TextComponent(text).withStyle(Style.EMPTY.withColor(0xe7bd42)));
		}

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action_cycle")));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		return new TextComponent("").append(displayName).append(" (").append(getLivingToolState(stack).getTooltip()).append(")");
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

}
