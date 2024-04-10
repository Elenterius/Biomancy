package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModCapabilities {

	public static final Capability<FlagCapImpl> NO_KNOCKBACK_FLAG_CAP = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<IItemHandler> ITEM_HANDLER = ForgeCapabilities.ITEM_HANDLER;
	public static final Capability<IFluidHandler> FLUID_HANDLER = ForgeCapabilities.FLUID_HANDLER;

	private ModCapabilities() {}

	@SubscribeEvent
	public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
		event.register(FlagCapImpl.class);
	}

	public interface IFlagCap {
		boolean isEnabled();

		void set(boolean enabled);

		default void enable() {
			set(true);
		}

		default void disable() {
			set(false);
		}

		default void toggle() {
			set(!isEnabled());
		}
	}

	@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static final class CapabilityAttacher {
		private CapabilityAttacher() {}

		@SubscribeEvent
		public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
			if (!(event.getObject() instanceof LivingEntity)) return;

			FlagCapImpl backing = new FlagCapImpl();
			LazyOptional<FlagCapImpl> optionalCap = LazyOptional.of(() -> backing);

			ICapabilityProvider volatileCapProvider = new ICapabilityProvider() {
				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
					return NO_KNOCKBACK_FLAG_CAP.orEmpty(capability, optionalCap);
				}
			};

			event.addCapability(BiomancyMod.createRL("no_knockback"), volatileCapProvider);
			event.addListener(optionalCap::invalidate);
		}

	}

	public static class FlagCapImpl implements IFlagCap {
		private boolean isEnabled = false;

		@Override
		public boolean isEnabled() {
			return isEnabled;
		}

		@Override
		public void set(boolean enabled) {
			isEnabled = enabled;
		}

	}

}
