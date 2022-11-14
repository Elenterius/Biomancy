package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.world.item.IKeyListener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public abstract class BaseGunItem extends ProjectileWeaponItem implements IGun, IKeyListener {
	public static final Set<Enchantment> VALID_VANILLA_ENCHANTMENTS = Set.of(Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS, Enchantments.QUICK_CHARGE);

	private final int baseShootDelay; //measured in ticks
	private final float baseProjectileDamage;
	private final int baseMaxAmmo;
	private final float baseAccuracy;
	private final int baseReloadTime;
	private final boolean isAutoReload;

	protected BaseGunItem(Properties properties, GunProperties gunProperties) {
		super(properties);
		baseShootDelay = gunProperties.baseShootDelay;
		baseProjectileDamage = gunProperties.baseProjectileDamage;
		baseMaxAmmo = gunProperties.baseMaxAmmo;
		baseAccuracy = gunProperties.baseAccuracy;
		baseReloadTime = gunProperties.baseReloadTime;
		isAutoReload = gunProperties.isAutoReload;
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		State state = getState(stack);
		if (state == State.NONE && !canReload(stack, player)) {
			playSFX(level, player, SoundEvents.DISPENSER_FAIL);
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}

		if (state == State.SHOOTING) {
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}

		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		State state = getState(stack);

		if (state == State.NONE) {
			startReload(stack, level, player);
			return;
		}

		if (state == State.RELOADING) {
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
			State state = getState(stack);
			if (state == State.RELOADING && level instanceof ServerLevel serverLevel) {
				cancelReload(stack, serverLevel, player);
			}
			setState(stack, State.SHOOTING);
			player.startUsingItem(usedHand);
			return InteractionResultHolder.consume(stack);
		}

		player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_ammo"), true);
		return InteractionResultHolder.fail(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			if (getState(stack) != State.SHOOTING) return;

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
				shoot(serverLevel, shooter, shooter.getUsedItemHand(), stack, getProjectileProperties(stack));
				stack.getOrCreateTag().putLong(NBT_KEY_SHOOT_TIMESTAMP, serverLevel.getGameTime());
			}
		}
	}

	public ProjectileProperties getProjectileProperties(ItemStack stack) {
		return new ProjectileProperties(getProjectileDamage(stack), getInaccuracy(), getProjectileKnockBack(stack));
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
		setState(stack, State.NONE);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
			if (getState(stack) != State.RELOADING) return;

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
		return VALID_VANILLA_ENCHANTMENTS.contains(enchantment) || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public float getAccuracy() {
		return baseAccuracy;
	}

	@Override
	public int getShootDelay(ItemStack stack) {
		return baseShootDelay - 2 * EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.QUICK_SHOT.get(), stack);
	}

	@Override
	public int getReloadTime(ItemStack stack) {
		return baseReloadTime - 5 * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
	}

	@Override
	public float getProjectileDamage(ItemStack stack) {
		return baseProjectileDamage + 0.6f * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
	}

	public int getProjectileKnockBack(ItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
	}

	@Override
	public int getMaxAmmo(ItemStack stack) {
		return Mth.floor(baseMaxAmmo + baseMaxAmmo * 0.5f * EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MAX_AMMO.get(), stack));
	}

	@Override
	public void stopShooting(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		if (isAutoReload) startReload(stack, level, shooter);
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
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(TextStyles.LORE));
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#.###");
			tooltip.add(TextComponentUtil.getTooltipText("fire_rate").append(String.format(": %s RPS ", df.format(getFireRate(stack)))).append(addBrackets(df.format(ONE_SECOND / baseShootDelay))));
			tooltip.add(TextComponentUtil.getTooltipText("accuracy").append(String.format(": %s ", df.format(getAccuracy()))).append(addBrackets(df.format(baseAccuracy))));
			tooltip.add(TextComponentUtil.getTooltipText("ammo").append(String.format(": %d/%d ", getAmmo(stack), getMaxAmmo(stack))).append(addBrackets("x/" + baseMaxAmmo)));
			tooltip.add(TextComponentUtil.getTooltipText("reload_time").append(String.format(": %s ", df.format(getReloadTime(stack) / ONE_SECOND))).append(addBrackets(df.format(baseReloadTime / ONE_SECOND))));
			tooltip.add(TextComponentUtil.getTooltipText("projectile_damage").append(String.format(": %s ", df.format(getProjectileDamage(stack)))).append(addBrackets(df.format(baseProjectileDamage))));
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		}
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTranslationText("tooltip", "action_reload")).withStyle(ChatFormatting.DARK_GRAY));
	}

	private MutableComponent addBrackets(Object obj) {
		return new TextComponent("(" + obj + ")").withStyle(ChatFormatting.DARK_GRAY);
	}

	public static class GunProperties {

		private boolean isAutoReload = false;
		private int baseShootDelay = 20; //measured in ticks
		private float baseProjectileDamage = 2;
		private int baseMaxAmmo = 6;
		private float baseAccuracy = 1f;
		private int baseReloadTime = 20;

		public GunProperties shootDelay(int shootDelay) {
			baseShootDelay = shootDelay;
			return this;
		}

		public GunProperties fireRate(float fireRate) {
			baseShootDelay = Math.max(1, Math.round(ONE_SECOND / fireRate));
			return this;
		}

		public GunProperties damage(float projectileDamage) {
			baseProjectileDamage = projectileDamage;
			return this;
		}

		public GunProperties maxAmmo(int maxAmmo) {
			baseMaxAmmo = maxAmmo;
			return this;
		}

		public GunProperties accuracy(float accuracy) {
			if (accuracy < 0f || accuracy > 1f) throw new IllegalArgumentException("Invalid accuracy: " + accuracy);
			baseAccuracy = accuracy;
			return this;
		}

		public GunProperties reloadTime(int reloadTimeInTicks) {
			baseReloadTime = reloadTimeInTicks;
			return this;
		}

		public GunProperties autoReload(boolean bool) {
			isAutoReload = bool;
			return this;
		}

	}
}
