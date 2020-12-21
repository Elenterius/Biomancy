package com.github.elenterius.blightlings.handler;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.capabilities.IItemDecayTracker;
import com.github.elenterius.blightlings.init.ModCapabilities;
import com.github.elenterius.blightlings.init.ModEffects;
import com.github.elenterius.blightlings.item.DecayingItem;
import com.github.elenterius.blightlings.mixin.ServerPlayerEntityMixin;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ItemDecayHandler {
	private ItemDecayHandler() {
	}

    /*
    We don't use the forge event because it sucks (it does not supply the container provider)
    @SubscribeEvent
    public static void onPlayerOpenContainer(final PlayerContainerEvent.Open event) {}
    */

	/**
	 * invoked when a player opens an container
	 *
	 * @see ServerPlayerEntityMixin
	 */
	public static void decayItemsInContainer(ServerPlayerEntity playerEntity, Container container, INamedContainerProvider containerProvider) {
		if (containerProvider instanceof TileEntity) {
			decayItemsInTileContainer(container, playerEntity.getServerWorld(), (TileEntity) containerProvider);
		} else if (containerProvider instanceof Entity) {
			decayItemsInEntityContainer(container, playerEntity.getServerWorld(), (Entity) containerProvider);
		} else if (!(container instanceof MerchantContainer) && containerProvider instanceof SimpleNamedContainerProvider) {
			decayItemsInEntityContainer(container, playerEntity.getServerWorld(), playerEntity); // e.g. Ender-Chest
		}
		//anything else is not supported
	}

	private static void decayItemsInEntityContainer(Container container, ServerWorld world, Entity entity) {
		for (ItemStack stack : container.getInventory()) {
			if (!stack.isEmpty() && stack.getItem() instanceof DecayingItem) {
				LazyOptional<IItemDecayTracker> capability = stack.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
				capability.ifPresent(decayTracker -> decayTracker.onUpdate(stack, world, entity, ((DecayingItem) stack.getItem()).halfTime * 20L, ((DecayingItem) stack.getItem()).decayFactor, true));
			}
		}
		container.detectAndSendChanges();
	}

	private static void decayItemsInTileContainer(Container container, ServerWorld world, TileEntity entity) {
		AtomicInteger decayAmount = new AtomicInteger();
		for (ItemStack stack : container.getInventory()) {
			if (!stack.isEmpty() && stack.getItem() instanceof DecayingItem) {
				LazyOptional<IItemDecayTracker> capability = stack.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
				capability.ifPresent(decayTracker -> decayAmount.addAndGet(doDecay(decayTracker, world, stack, ((DecayingItem) stack.getItem()).halfTime * 20L, ((DecayingItem) stack.getItem()).decayFactor)));
			}
		}
		container.detectAndSendChanges();

		if (decayAmount.get() > 1) {
			int n = MathHelper.ceil(decayAmount.get() / 32f);
			EffectInstance effectInstance = new EffectInstance(ModEffects.BLIGHT_INFECTION.get(), n * 10 * 20, n);
			Vector3d pos = Vector3d.copyCentered(entity.getPos());
			AreaEffectCloudEntity aoeCloud = new AreaEffectCloudEntity(world, pos.x, pos.y, pos.z);
			aoeCloud.setDuration(n * 30 * 20);
			aoeCloud.setRadius(1.45F);
			aoeCloud.setWaitTime(10);
			aoeCloud.setRadiusPerTick(-aoeCloud.getRadius() / (float) aoeCloud.getDuration());
			aoeCloud.addEffect(effectInstance);
			world.addEntity(aoeCloud);
		}
	}

	private static int doDecay(IItemDecayTracker decayTracker, ServerWorld world, ItemStack stack, long halfTime, float decayFactor) {
		if (decayTracker.canDecay(stack) && stack.getCount() > 0) {
			if (decayTracker.getStartTime() == 0) {
				decayTracker.setStartTime(world.getGameTime());
			} else {
				int oldCount = stack.getCount();
				decayTracker.performDecayStep(stack, world, halfTime, decayFactor);
				return oldCount - stack.getCount();
			}
		}
		return 0;
	}
}
