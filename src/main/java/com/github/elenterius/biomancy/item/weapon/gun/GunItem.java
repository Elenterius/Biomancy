package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.projectile.BaseProjectile;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.shooting.Gun;
import com.github.elenterius.biomancy.util.shooting.GunProperties;
import com.github.elenterius.biomancy.util.shooting.GunState;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class GunItem<T extends BaseProjectile> extends ProjectileWeaponItem implements Gun<T>, KeyPressListener {

	protected final GunProperties<T> gunProperties;

	protected GunItem(Properties properties, GunProperties<T> gunProperties) {
		super(properties);
		this.gunProperties = gunProperties;
	}

	@Override
	public GunProperties<T> getGunProperties() {
		return gunProperties;
	}

	@Override
	public KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		GunState state = getGunState(stack);
		if (state == GunState.NONE && !canReload(stack, player)) {
			playSFX(level, player, SoundEvents.DISPENSER_FAIL);
			return KeyPressResult.fail(); //don't send button press to server
		}

		if (state == GunState.SHOOTING_OR_CHARGING) {
			return KeyPressResult.fail(); //don't send button press to server
		}

		return KeyPressResult.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		GunState state = getGunState(stack);

		if (state == GunState.NONE) {
			startReload(stack, level, player);
			return;
		}

		if (state == GunState.RELOADING) {
			cancelReload(stack, level, player);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		GunState state = getGunState(stack);

		if (getAmmo(stack) >= getAmmoCost(stack)) {
			if (state == GunState.RELOADING && level instanceof ServerLevel serverLevel) {
				cancelReload(stack, serverLevel, player);
			}
			setGunState(stack, GunState.SHOOTING_OR_CHARGING);
			player.startUsingItem(usedHand);
			return InteractionResultHolder.consume(stack);
		}

		player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_ammo"), true);
		return InteractionResultHolder.fail(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

			if (getAmmo(stack) < getAmmoCost(stack)) {
				if (shooter instanceof Player player) {
					player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_ammo"), true);
				}
				shooter.releaseUsingItem();
				stopShooting(stack, serverLevel, shooter);
				return;
			}

			int elapsedTime = getUseDuration(stack) - remainingUseDuration;
			int delayBetweenShots = getDelayBetweenShots(stack);

			//prevent right click spam attack by user
			if (elapsedTime == 0 && serverLevel.getGameTime() - getShootTimestamp(stack) < delayBetweenShots) {
				// play sound to indicate that the gun is "jammed"
				// playSFX(serverLevel, shooter, SoundEvents.DISPENSER_FAIL); //we don't play this sounds because it's misleading
				return;
			}

			boolean canShoot = switch (gunProperties.shootBehavior()) {
				case INSTANT -> elapsedTime % delayBetweenShots == 0;
				case ON_FULL_CHARGE -> elapsedTime % delayBetweenShots == 0 && elapsedTime > 0;
				case ON_RELEASE_INSTANT, ON_RELEASE_WITH_FULL_CHARGE -> false;
			};

			if (canShoot) {
				shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack);
				stack.getOrCreateTag().putLong(SHOOT_TIMESTAMP_KEY, serverLevel.getGameTime());
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int remainingUseDuration) {

		if (level instanceof ServerLevel serverLevel && gunProperties.shootBehavior().isOnRelease()) {
			int elapsedTime = getUseDuration(stack) - remainingUseDuration;
			int delayBetweenShots = getDelayBetweenShots(stack);

			switch (gunProperties.shootBehavior()) {
				case ON_RELEASE_INSTANT -> {
					shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack);
					stack.getOrCreateTag().putLong(SHOOT_TIMESTAMP_KEY, serverLevel.getGameTime());
				}
				case ON_RELEASE_WITH_FULL_CHARGE -> {
					if (elapsedTime >= delayBetweenShots && serverLevel.getGameTime() - getShootTimestamp(stack) < delayBetweenShots) {
						shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack);
						stack.getOrCreateTag().putLong(SHOOT_TIMESTAMP_KEY, serverLevel.getGameTime());
					}
				}
				default -> {}
			}
		}

		setGunState(stack, GunState.NONE);

		if (shooter instanceof Player player) {
			player.awardStat(Stats.ITEM_USED.get(this));
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
			if (getGunState(stack) != GunState.RELOADING) return;

			if (isSelected && canReload(stack, livingEntity)) {
				long elapsedTime = serverLevel.getGameTime() - getReloadStartTime(stack);
				if (elapsedTime < 0) return;

				onReloadTick(stack, serverLevel, livingEntity, elapsedTime);

				if (elapsedTime >= getReloadDurationTicks(stack)) {
					finishReload(stack, serverLevel, livingEntity);
				}
				return;
			}

			stopReload(stack, serverLevel, livingEntity);
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return VALID_ENCHANTMENTS.contains(enchantment) || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		ItemStack ammo = shooter.getProjectile(stack); //vanilla mobs only look for held ammo (i.e. in off-hand)
		if (ammo.getItem() == Items.ARROW) { //if mobs/creative players can't find any ammo they will return arrows
			if (shooter instanceof Player player && player.getAbilities().instabuild) {
				ammo = ammo.copy();
				ammo.setCount(getReloadCost(stack));
				return ammo;
			}

			if (getSupportedHeldProjectiles().test(ammo) || getAllSupportedProjectiles().test(ammo)) return ammo;
			return ItemStack.EMPTY;
		}
		return ammo;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		appendGunStats(stack, tooltip);
		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("reload")).withStyle(TextStyles.DARK_GRAY));
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return ONE_HOUR_IN_TICKS;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

}
