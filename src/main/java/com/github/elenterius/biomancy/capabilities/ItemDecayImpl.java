package com.github.elenterius.biomancy.capabilities;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public final class ItemDecayImpl {
	private ItemDecayImpl() {
	}

	public static class DecayTrackerStorage implements Capability.IStorage<IItemDecayTracker> {
		@Nullable
		@Override
		public INBT writeNBT(Capability<IItemDecayTracker> capability, IItemDecayTracker instance, Direction side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<IItemDecayTracker> capability, IItemDecayTracker instance, Direction side, INBT nbt) {
			if (nbt instanceof LongNBT) instance.deserializeNBT((LongNBT) nbt);
		}
	}

	public static class DecayTrackerDefaultImpl implements IItemDecayTracker {
		private long startTime = 0L;

		@Override
		public long getStartTime() {
			return startTime;
		}

		@Override
		public void setStartTime(long gameTime) {
			startTime = gameTime;
		}

		@Override
		public void onItemDecay(ItemStack stack, ServerWorld world, Entity entity, int oldCount, int newCount) {
			if (stack.isEmpty() || newCount != oldCount && world.rand.nextFloat() < 0.4f) {
				int difference = MathHelper.clamp(MathHelper.abs(oldCount - newCount), 1, stack.getMaxStackSize());
				float p = ((float) difference / (float) stack.getMaxStackSize());
				if (entity instanceof LivingEntity && p < 0.46f) {
					Collection<EffectInstance> effects = ((LivingEntity) entity).getActivePotionEffects();
					int amplifier = 0;
					int duration = 0;
					for (EffectInstance effectInstance : effects) {
						if (effectInstance.getPotion() == ModEffects.FLESH_EATING_DISEASE.get()) {
							amplifier = effectInstance.getAmplifier();
							duration = effectInstance.getDuration();
							break;
						}
					}
					EffectInstance effectInstance = new EffectInstance(ModEffects.FLESH_EATING_DISEASE.get(), Math.min(difference, 5) * 120 + duration, Math.round(p) * 2 + amplifier);
					((LivingEntity) entity).addPotionEffect(effectInstance);
				}
				else {
					EffectInstance effectInstance = new EffectInstance(ModEffects.FLESH_EATING_DISEASE.get(), Math.min(difference, 5) * 120, Math.round(p) * 2);
					Vector3d pos = entity.getPositionVec();
					AreaEffectCloudEntity aoeCloud = new AreaEffectCloudEntity(world, pos.x, pos.y, pos.z);
					aoeCloud.setDuration(30 * 20);
					aoeCloud.setRadius(1.25F);
					aoeCloud.setRadiusOnUse(-0.5F); // decrease of radius when entity is affected
					aoeCloud.setWaitTime(10);
					aoeCloud.setRadiusPerTick(-aoeCloud.getRadius() / (float) aoeCloud.getDuration());
					aoeCloud.addEffect(effectInstance);
					world.addEntity(aoeCloud);
				}
			}
		}

	}

	public static class DecayProvider implements ICapabilitySerializable<LongNBT> {
//        private final IItemDecayTracker instance = ITEM_DECAY_CAPABILITY.getDefaultInstance();

		public static final ResourceLocation REGISTRY_KEY = new ResourceLocation(BiomancyMod.MOD_ID, "item_decay");

		private final LazyOptional<IItemDecayTracker> capProvider = LazyOptional.of(DecayTrackerDefaultImpl::new);
//        private final LazyOptional<IItemDecayTracker> capProvider = LazyOptional.of(() -> instance != null ? instance : new DecayTrackerDefaultImpl());

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return ModCapabilities.ITEM_DECAY_CAPABILITY.orEmpty(cap, capProvider);
		}

		@Override
		public LongNBT serializeNBT() {
//            INBT inbt = ITEM_DECAY_CAPABILITY.getStorage().writeNBT(ITEM_DECAY_CAPABILITY, instance, null);
//            return inbt != null ? (CompoundNBT) inbt : new CompoundNBT();

			//noinspection NullableProblems
			return capProvider.map(INBTSerializable::serializeNBT).orElse(LongNBT.valueOf(0));
		}

		@Override
		public void deserializeNBT(LongNBT nbt) {
//            ITEM_DECAY_CAPABILITY.getStorage().readNBT(ITEM_DECAY_CAPABILITY, instance, null, nbt);
			capProvider.ifPresent((provider) -> provider.deserializeNBT(nbt));
		}
	}
}
