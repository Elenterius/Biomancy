package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.item.IKeyListener;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public abstract class ProjectileWeaponItem extends ShootableItem implements IVanishable, IKeyListener {

	public static final float ONE_SECOND = 20f; //measured in ticks
	public static final float MAX_INACCURACY = 1f; //0.0 - 1.0

	public static final String NBT_KEY_AMMO = "Ammo";
	public static final String NBT_KEY_RELOAD_TIMESTAMP = "ReloadStartTime";
	public static final String NBT_KEY_WEAPON_STATE = "ProjectileWeaponState";
	public static final String NBT_KEY_SHOOT_TIMESTAMP = "ShootTime";

	private final int baseShootDelay; //measured in ticks
	private final float baseProjectileDamage;
	private final int baseMaxAmmo;
	private final float baseAccuracy;
	private final int baseReloadTime;

	/**
	 * @param accuracy from 0.0 to 1.0
	 */
	protected ProjectileWeaponItem(Properties builder, float fireRate, float accuracy, float damage, int maxAmmo, int reloadTime) {
		super(builder);
		assert accuracy >= 0f && accuracy <= 1f;
		baseShootDelay = Math.max(1, Math.round(ONE_SECOND / fireRate));
		baseMaxAmmo = maxAmmo;
		baseAccuracy = accuracy;
		baseProjectileDamage = damage;
		baseReloadTime = reloadTime;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#.###");
			tooltip.add(TextUtil.getTooltipText("fire_rate").appendString(String.format(": %s RPS ", df.format(getFireRate(stack)))).appendSibling(addBrackets(df.format(ONE_SECOND / baseShootDelay))));
			tooltip.add(TextUtil.getTooltipText("accuracy").appendString(String.format(": %s ", df.format(getAccuracy()))).appendSibling(addBrackets(df.format(baseAccuracy))));
			tooltip.add(TextUtil.getTooltipText("ammo").appendString(String.format(": %d/%d ", getAmmo(stack), getMaxAmmo(stack))).appendSibling(addBrackets("x/" + baseMaxAmmo)));
			tooltip.add(TextUtil.getTooltipText("reload_time").appendString(String.format(": %s ", df.format(getReloadTime(stack) / ONE_SECOND))).appendSibling(addBrackets(df.format(baseReloadTime / ONE_SECOND))));
			tooltip.add(TextUtil.getTooltipText("projectile_damage").appendString(String.format(": %s ", df.format(getProjectileDamage(stack)))).appendSibling(addBrackets(df.format(baseProjectileDamage))));
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		}
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTranslationText("tooltip", "action_reload")).mergeStyle(TextFormatting.DARK_GRAY));
	}

	@OnlyIn(Dist.CLIENT)
	private IFormattableTextComponent addBrackets(Object obj) {
		return new StringTextComponent("(" + obj + ")").mergeStyle(TextFormatting.DARK_GRAY);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, ClientWorld world, PlayerEntity player, byte flags) {
		State state = getState(stack);
		if (state == State.NONE && !canReload(stack, player)) {
			playSFX(world, player, SoundEvents.BLOCK_DISPENSER_FAIL);
			return ActionResult.resultFail(flags); //don't send button press to server
		}

		if (state == State.SHOOTING) return ActionResult.resultFail(flags); //don't send button press to server

		return ActionResult.resultSuccess(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		State state = getState(stack);
		if (state == State.NONE) {
			startReload(stack, world, player);
		}
		else if (state == State.RELOADING) {
			cancelReload(stack, world, player);
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 3600 * 20;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (hasAmmo(stack)) {
			State state = getState(stack);
			if (state == State.RELOADING) {
				if (world instanceof ServerWorld) cancelReload(stack, (ServerWorld) world, player);
			}
			setState(stack, State.SHOOTING);
			player.setActiveHand(hand);
			return ActionResult.resultConsume(stack);
		}
		else {
			player.sendStatusMessage(new StringTextComponent("Not Enough Ammo").mergeStyle(TextFormatting.RED), true);
			return ActionResult.resultFail(stack);
		}
	}

	@Override
	public void onUse(World world, LivingEntity shooter, ItemStack stack, int timeLeft) {
		if (world instanceof ServerWorld) {
			State state = getState(stack);
			if (state == State.SHOOTING) {
				if (hasAmmo(stack)) {
					float elapsedTime = (getUseDuration(stack) - timeLeft);

					int shootDelay = getShootDelay(stack);
					if (elapsedTime == 0) { //prevent right click spam attack by user
						if (world.getGameTime() - getShootTimestamp(stack) < shootDelay) {
							playSFX(world, shooter, SoundEvents.BLOCK_DISPENSER_FAIL);
							return;
						}
					}

					if (elapsedTime % shootDelay == 0) {
						shoot((ServerWorld) world, shooter, shooter.getActiveHand(), stack, getProjectileDamage(stack), getInaccuracy());
						stack.getOrCreateTag().putLong(NBT_KEY_SHOOT_TIMESTAMP, world.getGameTime());
					}
				}
				else {
					if (shooter instanceof PlayerEntity) {
						((PlayerEntity) shooter).sendStatusMessage(new StringTextComponent("Out of Ammo").mergeStyle(TextFormatting.RED), true);
					}
					shooter.stopActiveHand();
					stopShooting(stack, (ServerWorld) world, shooter);
				}
			}
		}
	}

	public long getShootTimestamp(ItemStack stack) {
		return stack.getOrCreateTag().getLong(NBT_KEY_SHOOT_TIMESTAMP);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity shooter, int timeLeft) {
		setState(stack, State.NONE);
	}

	public void stopShooting(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		//startReload(stack, (ServerWorld) world, livingEntity);
	}

	public abstract void shoot(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float damage, float inaccuracy);

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world instanceof ServerWorld && entity instanceof LivingEntity) {
			if (getState(stack) == State.RELOADING) {
				LivingEntity livingEntity = (LivingEntity) entity;

				if (isSelected && canReload(stack, livingEntity)) {
					long elapsedTime = world.getGameTime() - getReloadStartTime(stack);
					if (elapsedTime < 0) return;

					onReloadTick(stack, (ServerWorld) world, livingEntity, elapsedTime);

					if (elapsedTime >= getReloadTime(stack)) {
						finishReload(stack, (ServerWorld) world, livingEntity);
					}
				}
				else {
					stopReload(stack, (ServerWorld) world, livingEntity);
				}
			}
		}
	}

	public State getState(ItemStack stack) {
		return State.fromId(stack.getOrCreateTag().getByte(NBT_KEY_WEAPON_STATE));
	}

	public void setState(ItemStack stack, State state) {
		stack.getOrCreateTag().putByte(NBT_KEY_WEAPON_STATE, state.getId());
	}

	public long getReloadStartTime(ItemStack stack) {
		return stack.getOrCreateTag().getLong(NBT_KEY_RELOAD_TIMESTAMP);
	}

	public void startReload(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		if (canReload(stack, shooter)) {
			setState(stack, State.RELOADING);
			stack.getOrCreateTag().putLong(NBT_KEY_RELOAD_TIMESTAMP, world.getGameTime());
			onReloadStarted(stack, world, shooter);
		}
		else {
			playSFX(world, shooter, SoundEvents.BLOCK_DISPENSER_FAIL);
		}
	}

	public void finishReload(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		setState(stack, State.NONE);

		if (shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.isCreativeMode) {
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, world, shooter);
			return;
		}

		ItemStack ammoStack = findAmmoInInv(stack, shooter);
		if (!ammoStack.isEmpty() && ammoStack.getCount() >= getAmmoReloadCost()) {
			ammoStack.shrink(getAmmoReloadCost());
			setAmmo(stack, getMaxAmmo(stack));
			onReloadFinished(stack, world, shooter);
		}
		else {
			playSFX(world, shooter, SoundEvents.BLOCK_DISPENSER_FAIL);
		}
	}

	public void stopReload(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		setState(stack, State.NONE);
		onReloadStopped(stack, world, shooter);
	}

	public void cancelReload(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		setState(stack, State.NONE);
		onReloadCanceled(stack, world, shooter);
	}

	public void onReloadStarted(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.ITEM_CROSSBOW_LOADING_START);
	}

	public void onReloadTick(ItemStack stack, ServerWorld world, LivingEntity shooter, long elapsedTime) {}

	public void onReloadStopped(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.ITEM_CROSSBOW_LOADING_END);
	}

	public void onReloadCanceled(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.ITEM_CROSSBOW_LOADING_END);
	}

	public void onReloadFinished(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		playSFX(world, shooter, SoundEvents.ITEM_CROSSBOW_LOADING_END);
	}

	public float getReloadProgress(long elapsedTime, long reloadTime) {
		return MathHelper.clamp((float) elapsedTime / (float) reloadTime, 0f, 1f);
	}

	public boolean canReload(ItemStack stack, LivingEntity shooter) {
		if (getAmmo(stack) >= getMaxAmmo(stack)) return false;
		ItemStack ammo = findAmmoInInv(stack, shooter);
		return !ammo.isEmpty() && ammo.getCount() >= getAmmoReloadCost();
	}

	public float getInaccuracy() {
		return -MAX_INACCURACY * getAccuracy() + MAX_INACCURACY;
	}

	public float getAccuracy() {
		return baseAccuracy;
	}

	public int getShootDelay(ItemStack stack) {
		return baseShootDelay - 2 * EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICK_SHOT.get(), stack);
	}

	public float getFireRate(ItemStack stack) {
		return ONE_SECOND / getShootDelay(stack);
	}

	public int getReloadTime(ItemStack stack) {
		return baseReloadTime - 5 * EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
	}

	public float getProjectileDamage(ItemStack stack) {
		return baseProjectileDamage + 0.6f * EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
	}

	public int getMaxAmmo(ItemStack stack) {
		return MathHelper.floor(baseMaxAmmo + baseMaxAmmo * 0.5f * EnchantmentHelper.getEnchantmentLevel(ModEnchantments.MAX_AMMO.get(), stack));
	}

	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		ItemStack ammo = shooter.findAmmo(stack); //vanilla mobs only look for held ammo (i.e. in off-hand)
		if (ammo.getItem() == Items.ARROW) { //if mobs/creative players can't find any ammo they will return arrows
			if (shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.isCreativeMode) {
				ammo = ammo.copy();
				ammo.setCount(getAmmoReloadCost());
				return ammo;
			}

			if (getAmmoPredicate().test(ammo) || getInventoryAmmoPredicate().test(ammo)) return ammo;
			return ItemStack.EMPTY;
		}
		return ammo;
	}

	/**
	 * value shouldn't be larger than max ItemStack size of 64
	 */
	public int getAmmoReloadCost() {return 1;}

	public boolean hasAmmo(ItemStack stack) {
		return getAmmo(stack) > 0;
	}

	public int getAmmo(ItemStack stack) {
		return stack.getOrCreateTag().getInt(NBT_KEY_AMMO);
	}

	public void setAmmo(ItemStack stack, int amount) {
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putInt(NBT_KEY_AMMO, MathHelper.clamp(amount, 0, getMaxAmmo(stack)));
	}

	public void addAmmo(ItemStack stack, int amount) {
		if (amount == 0) return;
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putInt(NBT_KEY_AMMO, Math.max(0, nbt.getInt(NBT_KEY_AMMO) + amount));
	}

	public void consumeAmmo(ItemStack stack, int amount) {
		addAmmo(stack, -amount);
	}

	public void consumeAmmo(LivingEntity shooter, ItemStack stack, int amount) {
		if (!(shooter instanceof PlayerEntity) || !((PlayerEntity) shooter).abilities.isCreativeMode) addAmmo(stack, -amount);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.POWER || enchantment == Enchantments.QUICK_CHARGE || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	public void playSFX(World world, LivingEntity shooter, SoundEvent soundEvent) {
		SoundCategory soundcategory = shooter instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
		world.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), soundEvent, soundcategory, 1f, 1f / (random.nextFloat() * 0.5f + 1f) + 0.2f);
	}

	public enum State {
		NONE((byte) 0), SHOOTING((byte) 1), RELOADING((byte) 2);

		private final byte id;

		State(byte id) {
			this.id = id;
		}

		public static State fromId(int id) {
			if (id == 0) return NONE;
			if (id == 1) return SHOOTING;
			if (id == 2) return RELOADING;
			return NONE;
		}

		public byte getId() {
			return id;
		}
	}
}
