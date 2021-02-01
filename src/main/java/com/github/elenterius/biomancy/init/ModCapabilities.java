package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.capabilities.IItemDecayTracker;
import com.github.elenterius.biomancy.capabilities.ItemDecayImpl;
import com.github.elenterius.biomancy.item.DecayingItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModCapabilities {

	@CapabilityInject(IItemDecayTracker.class)
	public static Capability<IItemDecayTracker> ITEM_DECAY_CAPABILITY = null;

	private ModCapabilities() {}

	/**
	 * Attaches capabilities to external objects
	 */
	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject().getItem() instanceof DecayingItem) {
			event.addCapability(ItemDecayImpl.DecayProvider.REGISTRY_KEY, new ItemDecayImpl.DecayProvider());
		}
	}
}
