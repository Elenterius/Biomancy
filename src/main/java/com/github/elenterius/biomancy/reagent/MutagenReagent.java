package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.entity.aberration.FailedCowEntity;
import com.github.elenterius.biomancy.entity.aberration.FailedSheepEntity;
import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.entity.golem.IOwnableCreature;
import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class MutagenReagent extends Reagent {

	public MutagenReagent(int colorIn) {
		super(colorIn);
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (!target.level.isClientSide) {
			Collection<EffectInstance> effects = target.getActiveEffects();
			int amplifier = 0;
			int duration = 0;
			for (EffectInstance effectInstance : effects) {
				if (effectInstance.getEffect() == ModEffects.RAVENOUS_HUNGER.get()) {
					amplifier = effectInstance.getAmplifier();
					duration = effectInstance.getDuration();
					break;
				}
			}
			amplifier += 1;
			duration += 5 * 120;

			if (convertLivingEntity(source, target, amplifier)) {
				if (!target.isSilent()) target.level.levelEvent(null, Constants.WorldEvents.ZOMBIE_INFECT_SOUND, target.blockPosition(), 0);
			}
			else {
				EffectInstance effectInstance = new EffectInstance(ModEffects.RAVENOUS_HUNGER.get(), duration, amplifier);
				target.addEffect(effectInstance);
			}
		}
		return true;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		if (!targetSelf.level.isClientSide) {
			Collection<EffectInstance> effects = targetSelf.getActiveEffects();
			int amplifier = 0;
			int duration = 0;
			for (EffectInstance effectInstance : effects) {
				if (effectInstance.getEffect() == ModEffects.RAVENOUS_HUNGER.get()) {
					amplifier = effectInstance.getAmplifier() + 1;
					duration = effectInstance.getDuration();
					break;
				}
			}
			EffectInstance effectInstance = new EffectInstance(ModEffects.RAVENOUS_HUNGER.get(), 5 * 120 + duration, amplifier);
			targetSelf.addEffect(effectInstance);
		}
		return true;
	}

	private boolean convertLivingEntity(@Nullable LivingEntity source, LivingEntity target, int amplifier) {
		ServerWorld world = (ServerWorld) target.level;
		if (amplifier < 1) return false;

		if (target instanceof FleshBlobEntity) {
			return convertFleshBlob(world, source, (FleshBlobEntity) target);
		}
		else if (target instanceof GuardianEntity) {
			return MobUtil.convertMobEntityTo(world, (GuardianEntity) target, EntityType.ELDER_GUARDIAN);
		}
		else if (target instanceof VillagerEntity) {
			return MobUtil.convertMobEntityTo(world, (VillagerEntity) target, EntityType.PILLAGER, false);
		}
		else if (target instanceof SheepEntity && !(target instanceof FailedSheepEntity)) {
			float v = world.random.nextFloat();
			if (v < 0.04f)
				return MobUtil.convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.CHROMA_SHEEP.get());
			else if (v < 0.2f)
				return MobUtil.convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.THICK_WOOL_SHEEP.get());
			else if (v < 0.45f)
				return MobUtil.convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.SILKY_WOOL_SHEEP.get());
			else if (v < 0.8f)
				return MobUtil.convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.FAILED_SHEEP.get());
		}
		else if (target instanceof CowEntity && !(target instanceof FailedCowEntity)) {
			float v = world.random.nextFloat();
			if (v < 0.05)
				return MobUtil.convertMobEntityTo(world, (CowEntity) target, EntityType.MOOSHROOM);
			if (v < 0.25f)
				return MobUtil.convertMobEntityTo(world, (CowEntity) target, ModEntityTypes.NUTRIENT_SLURRY_COW.get());
			else if (v < 0.8f)
				return MobUtil.convertMobEntityTo(world, (CowEntity) target, ModEntityTypes.FAILED_COW.get());
		}
		else if (target instanceof AnimalEntity && !(target instanceof TameableEntity) && !(target instanceof IOwnableCreature)) {
			if (world.random.nextFloat() < 0.4f) {
				return MobUtil.convertMobEntityTo(world, (AnimalEntity) target, ModEntityTypes.FLESH_BLOB.get(), true, (oldEntity, outcome) -> {
					int size = MathHelper.clamp(Math.round(oldEntity.getDimensions(Pose.STANDING).height), 1, 10);
					if (size > 1) outcome.setBlobSize((byte) size, true);
				});
			}
		}

		return false;
	}

	private boolean convertFleshBlob(ServerWorld level, @Nullable LivingEntity source, FleshBlobEntity target) {
		if (level.random.nextFloat() < 0.7f && target.getBlobSize() < 6 && target.hasForeignEntityDNA()) {
			List<EntityType<?>> entityDNAs = target.getForeignEntityDNA();
			if (entityDNAs != null) {
				int dnaCount = entityDNAs.size();
				if (dnaCount == 1) {
					EntityType<?> entityType = entityDNAs.get(0);
					if (entityType == EntityType.PLAYER) {
						return MobUtil.convertMobEntityTo(level, target, ModEntityTypes.FLESHKIN.get(), false, (fleshBlob, fleshkin) -> {
							if (!fleshBlob.isHangry() && source != null) fleshkin.setOwnerUUID(source.getUUID());
							fleshkin.setBaby(fleshBlob.getBlobSize() < 5);
						});
					}
					else if (entityType == EntityType.BAT && level.random.nextFloat() < 0.2f) {
						return MobUtil.convertMobEntityTo(level, target, ModEntityTypes.OCULUS_OBSERVER.get());
					}

					if (isCloneable(entityType)) {
						return createClone(level, target, entityType);
					}
				}
				else if (dnaCount == 2) {
					if (createChimera(level, source, target, entityDNAs)) return true;
					else target.setHangry();
				}

				float v = 0.15f + 0.07f * dnaCount;
				if (level.random.nextFloat() < v) {
					explodeFleshBlob(level, target, v);
				}
			}
		}
		return false;
	}

	//TODO: white flashing for cloning process? (pokemon evolution)
	private boolean createClone(ServerWorld level, FleshBlobEntity fleshBlob, EntityType<?> entityType) {
		float blobVolume = getVolume(fleshBlob);
		float cloneVolume = getVolume(entityType);

		if (blobVolume / cloneVolume < 0.8f) {
			fleshBlob.setHangry(); //TODO: better indicator that the blob is too small
			if (level.random.nextFloat() < 0.25f) explodeFleshBlob(level, fleshBlob, 0f);
			return false;
		}

		boolean success = MobUtil.convertLivingEntityTo(level, fleshBlob, entityType, MobUtil::isNotUndead);
		float diff = blobVolume - cloneVolume;
		if (success && diff > 0.11f) {
			//drop excess flesh blob volume as flesh lumps
			int count = Math.max(Math.round(diff * 9f), 1); // 9 flesh == 1 flesh block (1 m³)
			ItemEntity itementity = new ItemEntity(level, fleshBlob.getX(), fleshBlob.getY() + 0.5f, fleshBlob.getZ(), new ItemStack(ModItems.FLESH_LUMP.get(), count));
			itementity.setDefaultPickUpDelay();
			level.addFreshEntity(itementity);
		}
		return success;
	}

	private boolean createChimera(ServerWorld level, @Nullable LivingEntity source, FleshBlobEntity target, List<EntityType<?>> entityDNAs) {
		EntityType<?> typeA = entityDNAs.get(0);
		EntityType<?> typeB = entityDNAs.get(1);
		if (anyMatch(EntityType.CAVE_SPIDER, typeA, typeB) && anyMatch(EntityType.CREEPER, typeA, typeB)) {
			return MobUtil.convertMobEntityTo(level, target, ModEntityTypes.BOOMLING.get(), false, (fleshBlob, boomling) -> {
				//set owner to make it possible for the owner to pick it up
				if (source != null) boomling.setOwnerUUID(source.getUUID());
				//because boomlingEntity.onInitialSpawn() was called by MobUtil.convertMobEntityTo() we need to reset the potion
				boomling.setStoredPotion(ItemStack.EMPTY);
			});
		}
		else if (anyMatch(EntityType.VILLAGER, typeA, typeB) && anyMatch(EntityType.COW, typeA, typeB)) {
			return MobUtil.convertMobEntityTo(level, target, EntityType.RAVAGER, false);
		}
		return false;
	}

	private void explodeFleshBlob(ServerWorld level, FleshBlobEntity target, float v) {
		Explosion.Mode mode = ForgeEventFactory.getMobGriefingEvent(level, target) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
		level.explode(target, target.getX(), target.getY(), target.getZ(), target.getBlobSize() + 4f * v, mode);
		target.remove();
	}

	private float getVolume(EntityType<?> entityType) {
		return entityType.getWidth() * entityType.getWidth() * entityType.getHeight();
	}

	private float getVolume(Entity entity) {
		return entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight();
	}

	private boolean isCloneable(EntityType<?> entityType) {
		return !ModTags.EntityTypes.NOT_CLONEABLE.contains(entityType);
	}

	private boolean anyMatch(EntityType<?> target, EntityType<?> a, EntityType<?> b) {
		return a == target || b == target;
	}
}
