package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ClientSetupHandler;
import com.github.elenterius.biomancy.item.IKeyListener;
import com.github.elenterius.biomancy.util.TooltipUtil;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ProjectileWeaponItem extends ShootableItem implements IVanishable, IKeyListener {

	public static final float ONE_SECOND = 20f; //measured in ticks

	public static final String NBT_KEY_AMMO = "Ammo";
	public static final String NBT_KEY_RELOAD_TIMESTAMP = "ReloadStartTime";
	public static final String NBT_KEY_WEAPON_STATE = "ProjectileWeaponState";
	public static final String NBT_KEY_SHOOT_TIMESTAMP = "ShootTime";

	private final int baseShootDelay; //measured in ticks
	private final int baseMaxAmmo;
	private final float baseAccuracy;
	private final int baseReloadTime;

	public ProjectileWeaponItem(Properties builder, float fireRate, float accuracy, int maxAmmo, int reloadTime) {
		super(builder);
		this.baseShootDelay = Math.max(1, Math.round(ONE_SECOND / fireRate));
		this.baseMaxAmmo = maxAmmo;
		this.baseAccuracy = accuracy;
		this.baseReloadTime = reloadTime;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (TooltipUtil.showExtraInfo(tooltip)) {
			tooltip.add(new StringTextComponent(String.format("Fire Rate: %.2f (%.2f) RPS", getFireRate(), ONE_SECOND / baseShootDelay)));
			tooltip.add(new StringTextComponent(String.format("Accuracy: %.2f (%.2f) (Inaccuracy: %.2f)", getAccuracy(), baseAccuracy, getInaccuracy())));
			tooltip.add(new StringTextComponent(String.format("Ammo: %d/%d (%d)", getAmmo(stack), getMaxAmmo(), baseMaxAmmo)));
			tooltip.add(new StringTextComponent(String.format("Reload Time: %.2f (%.2f)", getReloadTime(stack) / ONE_SECOND, baseReloadTime / ONE_SECOND)));
			tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		}
		tooltip.add(new TranslationTextComponent(BiomancyMod.getTranslationKey("tooltip", "press_button_to"), ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.func_238171_j_().copyRaw().mergeStyle(TextFormatting.AQUA), BiomancyMod.getTranslationText("tooltip", "action_reload")).mergeStyle(TextFormatting.DARK_GRAY));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, World world, PlayerEntity player, byte flags) {
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
		return UseAction.BOW;
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
					float elapsedTime = getUseDuration(stack) - timeLeft;

					if (elapsedTime == 0) { //prevent right click spam attack by user
						if (world.getGameTime() - stack.getOrCreateTag().getLong(NBT_KEY_SHOOT_TIMESTAMP) < getShootDelay()) {
							playSFX(world, shooter, SoundEvents.BLOCK_DISPENSER_FAIL);
							return;
						}
					}

					if (elapsedTime % getShootDelay() == 0) {
						shoot((ServerWorld) world, shooter, shooter.getActiveHand(), stack, getInaccuracy());
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

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity shooter, int timeLeft) {
		setState(stack, State.NONE);
	}

	public void stopShooting(ItemStack stack, ServerWorld world, LivingEntity shooter) {
		//startReload(stack, (ServerWorld) world, livingEntity);
	}

	public abstract void shoot(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack projectileWeapon, float inaccuracy);

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
			setAmmo(stack, getMaxAmmo());
			onReloadFinished(stack, world, shooter);
			return;
		}

		ItemStack ammoStack = findAmmoInInv(stack, shooter);
		if (!ammoStack.isEmpty() && ammoStack.getCount() >= getAmmoReloadCost()) {
			ammoStack.shrink(getAmmoReloadCost());
			setAmmo(stack, getMaxAmmo());
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
		if (getAmmo(stack) >= getMaxAmmo()) return false;
		ItemStack ammo = findAmmoInInv(stack, shooter);
		return !ammo.isEmpty() && ammo.getCount() >= getAmmoReloadCost();
	}

	public float getInaccuracy() {
		return -(1f / 0.0075f) * getAccuracy() + (1f / 0.0075f);
	}

	public float getAccuracy() {
		return baseAccuracy;
	}

	public int getShootDelay() {
		return baseShootDelay;
	}

	public float getFireRate() {
		return ONE_SECOND / getShootDelay();
	}

	public int getReloadTime(ItemStack stack) {
		return baseReloadTime - 5 * EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
	}

	public int getMaxAmmo() {
		return baseMaxAmmo;
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
	public int getAmmoReloadCost() { return 1; }

	public boolean hasAmmo(ItemStack stack) {
		return getAmmo(stack) > 0;
	}

	public int getAmmo(ItemStack stack) {
		return stack.getOrCreateTag().getInt(NBT_KEY_AMMO);
	}

	public void setAmmo(ItemStack stack, int amount) {
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putInt(NBT_KEY_AMMO, MathHelper.clamp(amount, 0, getMaxAmmo()));
	}

	public void addAmmo(ItemStack stack, int amount) {
		if (amount == 0) return;
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putInt(NBT_KEY_AMMO, Math.max(0, nbt.getInt(NBT_KEY_AMMO) + amount));
	}

	public void consumeAmmo(ItemStack stack, int amount) {
		addAmmo(stack, -amount);
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
