package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.capabilities.IItemDecayTracker;
import com.github.elenterius.blightlings.capabilities.ItemDecayImpl;
import com.github.elenterius.blightlings.item.DecayingItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModCapabilities {
    private ModCapabilities() {
    }

    @CapabilityInject(IItemDecayTracker.class)
    public static Capability<IItemDecayTracker> ITEM_DECAY_CAPABILITY = null;

    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof DecayingItem) {
            event.addCapability(ItemDecayImpl.DecayProvider.REGISTRY_KEY, new ItemDecayImpl.DecayProvider());
        }
    }

}
