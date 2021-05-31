package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.entity.aberration.FailedSheepEntity;
import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.entity.golem.IOwnableCreature;
import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import java.util.function.BiConsumer;

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
		if (!target.world.isRemote) {
			Collection<EffectInstance> effects = target.getActivePotionEffects();
			int amplifier = 0;
			int duration = 0;
			for (EffectInstance effectInstance : effects) {
				if (effectInstance.getPotion() == ModEffects.FLESH_EATING_DISEASE.get()) {
					amplifier = effectInstance.getAmplifier();
					duration = effectInstance.getDuration();
					break;
				}
			}
			amplifier += 1;
			duration += 5 * 120;

			if (!convertLivingEntity(source, target, amplifier)) {
				EffectInstance effectInstance = new EffectInstance(ModEffects.FLESH_EATING_DISEASE.get(), duration, amplifier);
				target.addPotionEffect(effectInstance);
			}
		}
		return true;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		if (!targetSelf.world.isRemote) {
			Collection<EffectInstance> effects = targetSelf.getActivePotionEffects();
			int amplifier = 0;
			int duration = 0;
			for (EffectInstance effectInstance : effects) {
				if (effectInstance.getPotion() == ModEffects.RAVENOUS_HUNGER.get()) {
					amplifier = effectInstance.getAmplifier() + 1;
					duration = effectInstance.getDuration();
					break;
				}
			}
			EffectInstance effectInstance = new EffectInstance(ModEffects.RAVENOUS_HUNGER.get(), 5 * 120 + duration, amplifier);
			targetSelf.addPotionEffect(effectInstance);
		}
		return true;
	}

	private boolean convertLivingEntity(@Nullable LivingEntity source, LivingEntity target, int amplifier) {
		ServerWorld world = (ServerWorld) target.world;
		if (amplifier < 2) return false;

		if (target instanceof GuardianEntity) {
			return convertMobEntityTo(world, (GuardianEntity) target, EntityType.ELDER_GUARDIAN);
		}
		else if (target instanceof VillagerEntity) {
			return convertMobEntityTo(world, (VillagerEntity) target, EntityType.PILLAGER, false);
		}
		else if (target instanceof SheepEntity && !(target instanceof FailedSheepEntity)) {
			float v = world.rand.nextFloat();
			if (v < 0.04f)
				return convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.CHROMA_SHEEP.get());
			else if (v < 0.2f)
				return convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.THICK_WOOL_SHEEP.get());
			else if (v < 0.45f)
				return convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.SILKY_WOOL_SHEEP.get());
			else if (v < 0.8f)
				return convertMobEntityTo(world, (SheepEntity) target, ModEntityTypes.FAILED_SHEEP.get());
		}
		else if (target instanceof FleshBlobEntity) {
			return convertFleshBlob(world, source, (FleshBlobEntity) target);
		}
		else if (target instanceof AnimalEntity && !(target instanceof TameableEntity) && !(target instanceof IOwnableCreature)) {
			if (world.rand.nextFloat() < 0.4f) {
				return convertMobEntityTo(world, (AnimalEntity) target, ModEntityTypes.FLESH_BLOB.get(), true, (oldEntity, outcome) -> {
					int size = MathHelper.clamp(Math.round(oldEntity.getSize(Pose.STANDING).height), 1, 10);
					if (size > 1) outcome.setBlobSize((byte) size, true);
				});
			}
		}

		return false;
	}

	private <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E entityIn, EntityType<T> outcomeType) {
		return convertMobEntityTo(world, entityIn, outcomeType, true);
	}

	private <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E entityIn, EntityType<T> outcomeType, boolean copyEquipment) {
		return convertMobEntityTo(world, entityIn, outcomeType, copyEquipment, (oldEntity, outcome) -> {});
	}

	private <E extends MobEntity, T extends MobEntity> boolean convertMobEntityTo(ServerWorld world, E oldEntity, EntityType<T> outcomeType, boolean copyEquipment, BiConsumer<E, T> onConvert) {
		if (ForgeEventFactory.canLivingConvert(oldEntity, outcomeType, (timer) -> {})) {
			T newEntity = oldEntity.func_233656_b_(outcomeType, copyEquipment);// create new entity with same settings & equipment and remove old entity
			if (newEntity != null) {
				newEntity.onInitialSpawn(world, world.getDifficultyForLocation(oldEntity.getPosition()), SpawnReason.CONVERSION, null, null);
				newEntity.hurtResistantTime = 60;
				onConvert.accept(oldEntity, newEntity);
				ForgeEventFactory.onLivingConvert(oldEntity, newEntity);
				if (!oldEntity.isSilent()) {
					world.playEvent(null, Constants.WorldEvents.ZOMBIE_INFECT_SOUND, oldEntity.getPosition(), 0);
				}
				return true;
			}
		}
		return false;
	}

	private boolean convertFleshBlob(ServerWorld world, @Nullable LivingEntity source, FleshBlobEntity target) {
		if (world.rand.nextFloat() < 0.7f && target.getBlobSize() < 6) {
			if (target.hasForeignEntityDNA()) {
				List<EntityType<?>> entityDNAs = target.getForeignEntityDNA();
				if (entityDNAs != null) {
					int dnaCount = entityDNAs.size();
					if (dnaCount == 1) {
						EntityType<?> entityType = entityDNAs.get(0);
						if (entityType == EntityType.PLAYER) {
							return convertMobEntityTo(world, target, ModEntityTypes.FLESHKIN.get(), false, (fleshBlob, fleshkin) -> {
								if (!fleshBlob.isHangry()) {
									if (source != null) fleshkin.setOwnerUUID(source.getUniqueID());
								}
								fleshkin.setChild(target.getBlobSize() < 5);
							});
						}
						else {
							return convertLivingEntityTo(world, target, entityType);
						}
					}
					else if (dnaCount == 2) {
						EntityType<?> typeA = entityDNAs.get(0);
						EntityType<?> typeB = entityDNAs.get(1);
						if ((typeA == EntityType.CAVE_SPIDER && typeB == EntityType.CREEPER) || (typeB == EntityType.CAVE_SPIDER && typeA == EntityType.CREEPER)) {
							return convertMobEntityTo(world, target, ModEntityTypes.BOOMLING.get(), false);
						}
					}
					else {
						if (!target.isHangry()) {
							target.setHangry();
						}
					}

					float v = 0.15f + 0.07f * dnaCount;
					if (world.rand.nextFloat() < v) {
						Explosion.Mode mode = ForgeEventFactory.getMobGriefingEvent(world, target) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
						world.createExplosion(target, target.getPosX(), target.getPosY(), target.getPosZ(), target.getBlobSize() + 4f * v, mode);
						target.remove();
					}
				}
			}
		}
		return false;
	}

	private boolean convertLivingEntityTo(ServerWorld world, LivingEntity oldEntity, EntityType<?> outcomeType) {
		if (oldEntity.removed) return false;

		Entity entity = outcomeType.create(world);
		if (entity != null) {
			if (entity instanceof LivingEntity) {
				//noinspection unchecked
				EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>) outcomeType;
				if (ForgeEventFactory.canLivingConvert(oldEntity, entityType, (timer) -> {})) {
					entity.copyLocationAndAnglesFrom(oldEntity);
					oldEntity.remove();
					world.addEntity(entity);
					if (entity instanceof MobEntity) {
						((MobEntity) entity).onInitialSpawn(world, world.getDifficultyForLocation(oldEntity.getPosition()), SpawnReason.CONVERSION, null, null);
					}
					entity.hurtResistantTime = 60;
					ForgeEventFactory.onLivingConvert(oldEntity, (LivingEntity) entity);
					if (!oldEntity.isSilent()) world.playEvent(null, Constants.WorldEvents.ZOMBIE_INFECT_SOUND, oldEntity.getPosition(), 0);
					return true;
				}
			}

			entity.remove();
		}

		return false;
	}

}
