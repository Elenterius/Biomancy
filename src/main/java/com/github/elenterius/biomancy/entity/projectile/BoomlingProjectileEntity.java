package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BoomlingProjectileEntity extends AbstractProjectileEntity {

	private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(BoomlingProjectileEntity.class, DataSerializers.VARINT);
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
	protected void registerData() {
		super.registerData();
		dataManager.register(COLOR, -1);
	}

	@Override
	public float getGravity() {
		return 0.04f;
	}

	@Override
	public float getDrag() {
		return 0.98f;
	}

	private void spawnBoomling(@Nullable BlockPos targetPos, @Nullable LivingEntity targetEntity) {
		BoomlingEntity entity = ModEntityTypes.BOOMLING.get().create(world);
		if (entity != null) {
			entity.enablePersistence();

			Entity shooter = getShooter();
			if (shooter instanceof LivingEntity) {
				entity.setOwnerUUID(shooter.getUniqueID());
			}

			if (customEffects.isEmpty() && potion == Potions.EMPTY) {
				entity.setStoredPotion(ItemStack.EMPTY);
			}
			else {
				ItemStack stack = new ItemStack(Items.POTION);
				PotionUtils.addPotionToItemStack(stack, potion);
				PotionUtils.appendEffects(stack, customEffects);
				entity.setStoredPotion(stack);
			}
			entity.setTargetBlockPos(targetPos);
			entity.setAttackTarget(targetEntity);
			entity.setLocationAndAngles(getPosX(), getPosY(), getPosZ(), (float) (Math.PI * 2 * rand.nextFloat()), 0);

			if (world.addEntity(entity)) {
				entity.playAmbientSound();
			}
		}
	}

	@Override
	protected void onEntityHit(EntityRayTraceResult result) {
		super.onEntityHit(result);
		if (!world.isRemote) {
			remove();
			Entity shooter = getShooter();
			if (potion == Potions.WATER && customEffects.isEmpty()) {
				BoomlingEntity.causeWaterAOE(world, shooter == null ? this : shooter);
			}
			else {
				LivingEntity attacker = shooter instanceof LivingEntity ? (LivingEntity) shooter : null;
				BoomlingEntity.spawnEffectAOE(world, attacker, result.getHitVec(), potion, customEffects, getColor());
			}
			customEffects.clear(); //reset potions & effects
			potion = Potions.EMPTY;
			LivingEntity victim = result.getEntity() instanceof LivingEntity ? (LivingEntity) result.getEntity() : null;
			spawnBoomling(null, victim);
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		super.onImpact(result);
		if (!world.isRemote) {
			spawnBoomling(getPosition(), null);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean isBurning() {
		return false;
	}

	public int getColor() {
		return dataManager.get(COLOR);
	}

	public void setPotionAndEffect(Potion potionIn, Collection<EffectInstance> effectsIn, int color) {
		potion = potionIn;
		if (!effectsIn.isEmpty()) {
			for (EffectInstance effect : effectsIn) customEffects.add(new EffectInstance(effect));
		}
		if (color == -1) updateColor();
		else setCustomColor(color);
	}

	public void setPotionStack(ItemStack stack) {
		potion = PotionUtils.getPotionFromItem(stack);
		List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(stack);
		if (!effects.isEmpty()) {
			for (EffectInstance effect : effects) {
				customEffects.add(new EffectInstance(effect));
			}
		}

		CompoundNBT nbt = stack.getTag();
		if (nbt != null && nbt.contains("CustomPotionColor", Constants.NBT.TAG_ANY_NUMERIC)) {
			int color = nbt.getInt("CustomPotionColor");
			if (color == -1) updateColor();
			else setCustomColor(color);
		}
	}

	private void setCustomColor(int color) {
		customColor = true;
		dataManager.set(COLOR, color);
	}

	private void updateColor() {
		customColor = false;
		if (potion == Potions.EMPTY && customEffects.isEmpty()) {
			dataManager.set(COLOR, -1);
		}
		else {
			dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(potion, customEffects)));
		}
	}

	public void addEffect(EffectInstance effect) {
		customEffects.add(effect);
		getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(potion, customEffects)));
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);

		if (potion != Potions.EMPTY && potion != null) {
			compound.putString("Potion", Registry.POTION.getKey(potion).toString());
		}

		if (!customEffects.isEmpty()) {
			ListNBT list = new ListNBT();
			for (EffectInstance effect : customEffects) {
				list.add(effect.write(new CompoundNBT()));
			}
			compound.put("CustomPotionEffects", list);
		}

		if (customColor) compound.putInt("Color", getColor());
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);

		if (compound.contains("Potion", Constants.NBT.TAG_STRING)) {
			potion = PotionUtils.getPotionTypeFromNBT(compound);
		}

		for (EffectInstance effectinstance : PotionUtils.getFullEffectsFromTag(compound)) {
			addEffect(effectinstance);
		}

		if (compound.contains("Color", Constants.NBT.TAG_ANY_NUMERIC)) {
			setCustomColor(compound.getInt("Color"));
		}
		else {
			this.updateColor();
		}
	}

}
