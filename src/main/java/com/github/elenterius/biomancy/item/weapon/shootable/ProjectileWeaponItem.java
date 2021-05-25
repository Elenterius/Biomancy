package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.item.IKeyListener;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ProjectileWeaponItem extends ShootableItem implements IVanishable, IKeyListener {

	public static final float ONE_SECOND = 20f; //measured in ticks
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
		tooltip.add(new StringTextComponent(String.format("Fire Rate: %.2f (%.2f) RPS", getFireRate(), ONE_SECOND / baseShootDelay)));
		tooltip.add(new StringTextComponent(String.format("Accuracy: %.2f (%.2f) (Inaccuracy: %.2f)", getAccuracy(), baseAccuracy, getInaccuracy())));
		tooltip.add(new StringTextComponent(String.format("Ammo: %d/%d (%d)", getAmmo(stack), getMaxAmmo(), baseMaxAmmo)));
		tooltip.add(new StringTextComponent(String.format("Reload Time: %.2f (%.2f)", getReloadTime(stack) / ONE_SECOND, baseReloadTime / ONE_SECOND)));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, World world, PlayerEntity player, byte flags) {
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
			player.sendStatusMessage(new StringTextComponent("No Ammo").mergeStyle(TextFormatting.RED), true);
			return ActionResult.resultFail(stack);
		}
	}

	@Override
	public void onUse(World world, LivingEntity livingEntity, ItemStack stack, int timeLeft) {
		if (world instanceof ServerWorld) {
			State state = getState(stack);
			if (state == State.SHOOTING) {
				if (hasAmmo(stack)) {
					float elapsedTime = getUseDuration(stack) - timeLeft;
					if (elapsedTime % getShootDelay() == 0) {
						shoot((ServerWorld) world, livingEntity, livingEntity.getActiveHand(), stack, getInaccuracy());
					}
				}
				else {
					if (livingEntity instanceof PlayerEntity)
						((PlayerEntity) livingEntity).sendStatusMessage(new StringTextComponent("Out of Ammo").mergeStyle(TextFormatting.RED), true);
					livingEntity.stopActiveHand();
					stopShooting(stack, (ServerWorld) world, livingEntity);
				}
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		setState(stack, State.NONE);
	}

	public void stopShooting(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		//startReload(stack, (ServerWorld) world, livingEntity);
	}

	public abstract void shoot(ServerWorld world, LivingEntity livingEntity, Hand hand, ItemStack projectileWeapon, float inaccuracy);

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
		return State.fromId(stack.getOrCreateTag().getByte("ProjectileWeaponState"));
	}

	public void setState(ItemStack stack, State state) {
		stack.getOrCreateTag().putByte("ProjectileWeaponState", state.getId());
	}

	public enum State {
		NONE((byte) 0), SHOOTING((byte) 1), RELOADING((byte) 2);

		private final byte id;

		State(byte id) {
			this.id = id;
		}

		public byte getId() {
			return id;
		}

		public static State fromId(int id) {
			if (id == 0) return NONE;
			if (id == 1) return SHOOTING;
			if (id == 2) return RELOADING;
			return NONE;
		}
	}

	public long getReloadStartTime(ItemStack stack) {
		return stack.getOrCreateTag().getLong("ReloadStartTime");
	}

	public void startReload(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		setState(stack, State.RELOADING);
		stack.getOrCreateTag().putLong("ReloadStartTime", world.getGameTime());
		onReloadStarted(stack, world, livingEntity);
		playSFX(world, livingEntity, SoundEvents.ITEM_CROSSBOW_LOADING_START);
	}

	public void finishReload(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		setState(stack, State.NONE);
		onReloadFinished(stack, world, livingEntity);
		playSFX(world, livingEntity, SoundEvents.ITEM_CROSSBOW_LOADING_END);
	}

	public void stopReload(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		setState(stack, State.NONE);
		onReloadStopped(stack, world, livingEntity);
		playSFX(world, livingEntity, SoundEvents.ITEM_CROSSBOW_LOADING_END);
	}

	public void cancelReload(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		setState(stack, State.NONE);
		onReloadCanceled(stack, world, livingEntity);
		playSFX(world, livingEntity, SoundEvents.ITEM_CROSSBOW_LOADING_END);
	}

	public void onReloadStarted(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {}

	public void onReloadTick(ItemStack stack, ServerWorld world, LivingEntity livingEntity, long elapsedTime) {}

	public void onReloadStopped(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {}

	public void onReloadCanceled(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {}

	public void onReloadFinished(ItemStack stack, ServerWorld world, LivingEntity livingEntity) {
		setAmmo(stack, getMaxAmmo());
	}

	public float getReloadProgress(long elapsedTime, long reloadTime) {
		return MathHelper.clamp((float) elapsedTime / (float) reloadTime, 0f, 1f);
	}

	public boolean canReload(ItemStack stack, LivingEntity entity) {
		return getAmmo(stack) < getMaxAmmo();
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

	public boolean hasAmmo(ItemStack stack) {
		return getAmmo(stack) > 0;
	}

	public int getAmmo(ItemStack stack) {
		return stack.getOrCreateTag().getInt("Ammo");
	}

	public void setAmmo(ItemStack stack, int amount) {
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putInt("Ammo", MathHelper.clamp(amount, 0, getMaxAmmo()));
	}

	public void addAmmo(ItemStack stack, int amount) {
		if (amount == 0) return;
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.putInt("Ammo", Math.max(0, nbt.getInt("Ammo") + amount));
	}

	public void consumeAmmo(ItemStack stack, int amount) {
		addAmmo(stack, -amount);
	}

	public void playSFX(World world, LivingEntity shooter, SoundEvent soundEvent) {
		SoundCategory soundcategory = shooter instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
		world.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), soundEvent, soundcategory, 1f, 1f / (random.nextFloat() * 0.5f + 1f) + 0.2f);
	}
}
