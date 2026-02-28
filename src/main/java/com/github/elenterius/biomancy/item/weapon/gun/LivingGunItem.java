package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.api.livingtool.SimpleLivingTool;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.shooting.GunProperties;
import com.github.elenterius.biomancy.util.shooting.GunState;
import com.github.elenterius.biomancy.util.shooting.ProjectileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public abstract class LivingGunItem<T extends BaseProjectile> extends GunItem<T> implements SimpleLivingTool {

	private static final Predicate<ItemStack> AMMO_PREDICATE = itemStack -> false;

	private final int maxNutrients;

	protected LivingGunItem(int maxNutrients, Properties properties, GunProperties<T> gunProperties) {
		super(properties, gunProperties);
		this.maxNutrients = maxNutrients;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (getNutrients(stack) < getDurabilityCost(stack)) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
			playLocalSound(player, ModSoundEvents.FLESHKIN_NO.get());
			return InteractionResultHolder.fail(stack);
		}

		return super.use(level, player, usedHand);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

		if (getNutrients(stack) < getDurabilityCost(stack)) {
			shooter.releaseUsingItem();
			stopShooting(stack, serverLevel, shooter);
			if (shooter instanceof ServerPlayer player) {
				player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
			}
			return;
		}

		super.onUseTick(level, shooter, stack, remainingUseDuration);
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		boolean success = ProjectileUtil.shoot(level, shooter, projectileWeapon, this);

		if (success) {
			if (gunProperties.shootSound() != null) {
				playSFX(level, shooter, gunProperties.shootSound());
			}
			consumeAmmo(shooter, projectileWeapon, getAmmoCost(projectileWeapon));
			consumeNutrients(projectileWeapon, getDurabilityCost(projectileWeapon));
		}
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return AMMO_PREDICATE;
	}

	@Override
	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canReload(ItemStack stack, LivingEntity shooter) {
		return getAmmo(stack) < getMaxAmmo(stack) && getNutrients(stack) >= getReloadCost(stack);
	}

	@Override
	public void finishReload(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		setGunState(stack, GunState.NONE);

		if (shooter instanceof Player player && player.getAbilities().instabuild) {
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, level, shooter);
			return;
		}

		int reloadCost = getReloadCost(stack);

		if (getNutrients(stack) >= reloadCost) {
			setAmmo(stack, getMaxAmmo(stack));
			consumeNutrients(stack, reloadCost);
			onReloadFinished(stack, level, shooter);
		}
		else {
			playSFX(level, shooter, ModSoundEvents.FLESHKIN_NO.get());
		}
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {
		if (elapsedTime % 20L == 0L) playSFX(level, shooter, ModSoundEvents.FLESHKIN_EAT.get());
	}

	@Override
	public void onReloadStarted(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get());
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_BREAK.get());
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_NO.get());
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_BURP.get());
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return isValidEnchantment(stack, enchantment) && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		appendGunStats(stack, tooltip);

		tooltip.add(ComponentUtil.EMPTY_LINE);
		appendLivingToolTooltip(stack, tooltip);

		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("reload")).withStyle(TextStyles.DARK_GRAY));

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.EMPTY_LINE);
		}
	}

	@Override
	public int getMaxNutrients(ItemStack stack) {
		return maxNutrients;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (handleOverrideStackedOnOther(stack, slot, action, player)) {
			playLocalSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (handleOverrideOtherStackedOnMe(stack, other, slot, action, player, access)) {
			playLocalSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
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
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		//do nothing
	}

	@Override
	public int getDamage(ItemStack stack) {
		return 0;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	protected void playLocalSound(Player player, SoundEvent soundEvent) {
		if (!player.level().isClientSide) return;
		player.playSound(soundEvent, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
	}

}
