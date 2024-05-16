package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public abstract class GunItem extends ProjectileWeaponItem implements Gun, KeyPressListener {

	public static final Set<Enchantment> VALID_ENCHANTMENTS = Set.of(Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS, Enchantments.QUICK_CHARGE);

	private final GunProperties gunProperties;

	protected GunItem(Properties properties, GunProperties gunProperties) {
		super(properties);
		this.gunProperties = gunProperties;
	}

	protected static int getBonusReloadReduction(ItemStack stack) {
		return 5 * stack.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
	}

	protected static float getBonusProjectileDamageModifier(ItemStack stack) {
		return 0.6f * stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
	}

	protected static int getBonusShootDelayReduction(ItemStack stack) {
		//int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.QUICK_SHOT.get(), stack);
		int level = 0;
		return 2 * level;
	}

	protected static int getBonusProjectileKnockBackModifier(ItemStack stack) {
		return stack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		GunState state = getState(stack);
		if (state == GunState.NONE && !canReload(stack, player)) {
			playSFX(level, player, SoundEvents.DISPENSER_FAIL);
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}

		if (state == GunState.SHOOTING) {
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}

		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		GunState state = getState(stack);

		if (state == GunState.NONE) {
			startReload(stack, level, player);
			return;
		}

		if (state == GunState.RELOADING) {
			cancelReload(stack, level, player);
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 3600 * 20;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (hasAmmo(stack)) {
			GunState state = getState(stack);
			if (state == GunState.RELOADING && level instanceof ServerLevel serverLevel) {
				cancelReload(stack, serverLevel, player);
			}
			setState(stack, GunState.SHOOTING);
			player.startUsingItem(usedHand);
			return InteractionResultHolder.consume(stack);
		}

		player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_ammo"), true);
		return InteractionResultHolder.fail(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			if (getState(stack) != GunState.SHOOTING) return;

			if (!hasAmmo(stack)) {
				if (shooter instanceof Player player) {
					player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_ammo"), true);
				}
				shooter.releaseUsingItem();
				stopShooting(stack, serverLevel, shooter);
				return;
			}

			float elapsedTime = (getUseDuration(stack) - remainingUseDuration);
			int shootDelay = getShootDelay(stack);
			//prevent right click spam attack by user
			if (elapsedTime == 0 && serverLevel.getGameTime() - getShootTimestamp(stack) < shootDelay) {
				playSFX(serverLevel, shooter, SoundEvents.DISPENSER_FAIL);
				return;
			}

			if (elapsedTime % shootDelay == 0) {
				shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack);
				stack.getOrCreateTag().putLong(SHOOT_TIMESTAMP_KEY, serverLevel.getGameTime());
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
		setState(stack, GunState.NONE);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
			if (getState(stack) != GunState.RELOADING) return;

			if (isSelected && canReload(stack, livingEntity)) {
				long elapsedTime = serverLevel.getGameTime() - getReloadStartTime(stack);
				if (elapsedTime < 0) return;

				onReloadTick(stack, serverLevel, livingEntity, elapsedTime);

				if (elapsedTime >= getReloadTime(stack)) {
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
	public float getAccuracy(ItemStack stack) {
		return gunProperties.accuracy();
	}

	@Override
	public int getShootDelay(ItemStack stack) {
		return gunProperties.shootDelayTicks() - getBonusShootDelayReduction(stack);
	}

	@Override
	public int getReloadTime(ItemStack stack) {
		return gunProperties.reloadDurationTicks() - getBonusReloadReduction(stack);
	}

	@Override
	public float getProjectileDamageModifier(ItemStack stack) {
		return gunProperties.projectileDamageModifier() + getBonusProjectileDamageModifier(stack);
	}

	@Override
	public int getProjectileKnockBackModifier(ItemStack stack) {
		return getBonusProjectileKnockBackModifier(stack);
	}

	@Override
	public int getMaxAmmo(ItemStack stack) {
		//		int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MAX_AMMO.get(), stack);
		int level = 0;
		return Mth.floor(gunProperties.maxAmmo() + gunProperties.maxAmmo() * 0.5f * level);
	}

	@Override
	public void stopShooting(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		if (gunProperties.isAutoReload()) startReload(stack, level, shooter);
	}

	@Override
	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		ItemStack ammo = shooter.getProjectile(stack); //vanilla mobs only look for held ammo (i.e. in off-hand)
		if (ammo.getItem() == Items.ARROW) { //if mobs/creative players can't find any ammo they will return arrows
			if (shooter instanceof Player player && player.getAbilities().instabuild) {
				ammo = ammo.copy();
				ammo.setCount(getAmmoReloadCost());
				return ammo;
			}

			if (getSupportedHeldProjectiles().test(ammo) || getAllSupportedProjectiles().test(ammo)) return ammo;
			return ItemStack.EMPTY;
		}
		return ammo;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#.###");
			tooltip.add(TextComponentUtil.getTooltipText("fire_rate").append(String.format(": %s RPS ", df.format(getFireRate(stack)))).append(addBrackets(df.format(ONE_SECOND_IN_TICKS / gunProperties.shootDelayTicks()))));
			tooltip.add(TextComponentUtil.getTooltipText("accuracy").append(String.format(": %s ", df.format(getAccuracy(stack)))).append(addBrackets(df.format(gunProperties.accuracy()))));
			tooltip.add(TextComponentUtil.getTooltipText("ammo").append(String.format(": %d/%d ", getAmmo(stack), getMaxAmmo(stack))).append(addBrackets("x/" + gunProperties.maxAmmo())));
			tooltip.add(TextComponentUtil.getTooltipText("reload_time").append(String.format(": %s ", df.format(getReloadTime(stack) / ONE_SECOND_IN_TICKS))).append(addBrackets(df.format(gunProperties.reloadDurationTicks() / ONE_SECOND_IN_TICKS))));
			tooltip.add(TextComponentUtil.getTooltipText("projectile_damage").append(String.format(": %s ", df.format(getProjectileDamageModifier(stack)))).append(addBrackets(df.format(gunProperties.projectileDamageModifier()))));
			tooltip.add(ComponentUtil.emptyLine());
		}
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTranslationText("tooltip", "action_reload")).withStyle(ChatFormatting.DARK_GRAY));
	}

	private MutableComponent addBrackets(Object obj) {
		return ComponentUtil.literal("(" + obj + ")").withStyle(ChatFormatting.DARK_GRAY);
	}

}
