package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BoomlingProjectileEntity extends AbstractProjectileEntity {

	public static final String NBT_KEY_COLOR = "Color";
	private static final DataParameter<Integer> COLOR = EntityDataManager.defineId(BoomlingProjectileEntity.class, DataSerializers.INT);
	private final Set<EffectInstance> customEffects = Sets.newHashSet();
	private Potion potion = Potions.EMPTY;
	private boolean customColor;

	public BoomlingProjectileEntity(EntityType<? extends AbstractProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public BoomlingProjectileEntity(World world, double x, double y, double z) {
		super(ModEntityTypes.BOOMLING_PROJECTILE.get(), world, x, y, z);
	}

	public BoomlingProjectileEntity(World world, LivingEntity shooter) {
		super(ModEntityTypes.BOOMLING_PROJECTILE.get(), world, shooter);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(COLOR, -1);
	}

	@Override
	public float getGravity() {
		return 0.04f;
	}

	@Override
	public float getDrag() {
		return 0.98f;
	}

	private void spawnBoomling(Vector3d hitVec, @Nullable BlockPos targetPos, @Nullable LivingEntity targetEntity) {
		BoomlingEntity entity = ModEntityTypes.BOOMLING.get().create(level);
		if (entity != null) {
			entity.setPersistenceRequired();

			Entity shooter = getOwner();
			if (shooter instanceof LivingEntity) {
				entity.setOwnerUUID(shooter.getUUID());
				if (targetEntity != null && entity.shouldAttackEntity(targetEntity, (LivingEntity) shooter)) {
					entity.setTarget(targetEntity);
				}
			}
			else {
				entity.setTarget(targetEntity);
			}

			if (customEffects.isEmpty() && potion == Potions.EMPTY) {
				entity.setStoredPotion(ItemStack.EMPTY);
			}
			else {
				ItemStack stack = PotionUtilExt.getPotionItemStack(potion, customEffects);
				entity.setStoredPotion(stack);
			}
			entity.setTargetBlockPos(targetPos);

			entity.moveTo(hitVec.x, hitVec.y, hitVec.z, (float) (Math.PI * 2 * random.nextFloat()), 0);

			if (level.addFreshEntity(entity)) entity.playAmbientSound();
		}
	}

	@Override
	protected void onHitEntity(EntityRayTraceResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide) {
			LivingEntity victim = result.getEntity() instanceof LivingEntity ? (LivingEntity) result.getEntity() : null;
			spawnBoomling(result.getLocation(), null, victim);
		}
	}

	/**
	 * onBlockHit
	 */
	@Override
	protected void onHitBlock(BlockRayTraceResult result) {
		super.onHitBlock(result);
		if (!level.isClientSide && !removed) {
			spawnBoomling(result.getLocation(), blockPosition(), null);
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	public int getColor() {
		return entityData.get(COLOR);
	}

	public void setPotion(Potion potionIn, @Nullable Collection<EffectInstance> customEffectsIn, int color) {
		potion = potionIn;
		if (customEffectsIn != null && !customEffectsIn.isEmpty()) {
			for (EffectInstance effect : customEffectsIn) customEffects.add(new EffectInstance(effect));
		}
		if (color == -1) updateColor();
		else setCustomColor(color);
	}

	public void setPotion(ItemStack stack) {
		potion = PotionUtilExt.getPotion(stack);
		List<EffectInstance> effects = PotionUtilExt.getCustomEffects(stack);
		if (!effects.isEmpty()) {
			for (EffectInstance effect : effects) {
				customEffects.add(new EffectInstance(effect));
			}
		}

		CompoundNBT nbt = stack.getTag();
		if (nbt != null && PotionUtilExt.hasCustomColor(nbt)) {
			int color = PotionUtilExt.readCustomColor(nbt);
			if (color == -1) updateColor();
			else setCustomColor(color);
		}
	}

	private void setCustomColor(int color) {
		customColor = true;
		entityData.set(COLOR, color);
	}

	private void updateColor() {
		customColor = false;
		if (potion == Potions.EMPTY && customEffects.isEmpty()) {
			entityData.set(COLOR, -1);
		}
		else {
			entityData.set(COLOR, PotionUtilExt.getMergedColor(potion, customEffects));
		}
	}

	public void addCustomEffect(EffectInstance effect) {
		addCustomEffectRaw(new EffectInstance(effect));
	}

	public void addCustomEffectRaw(EffectInstance effect) {
		customEffects.add(effect);
		getEntityData().set(COLOR, PotionUtilExt.getMergedColor(potion, customEffects));
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);

		PotionUtilExt.writePotion(compound, potion);
		PotionUtilExt.writeCustomEffects(compound, customEffects);

		if (customColor) compound.putInt(NBT_KEY_COLOR, getColor());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);

		potion = PotionUtilExt.readPotion(compound);

		customEffects.clear();
		customEffects.addAll(PotionUtilExt.getCustomEffects(compound));
		getEntityData().set(COLOR, PotionUtilExt.getMergedColor(potion, customEffects));

		if (compound.contains(NBT_KEY_COLOR, Constants.NBT.TAG_ANY_NUMERIC)) {
			setCustomColor(compound.getInt(NBT_KEY_COLOR));
		}
		else updateColor();
	}

}
