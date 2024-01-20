package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModBioForgeTabs {

	public static final ResourceLocation REGISTRY_KEY = BiomancyMod.createRL("bio_forge_tab");
	public static final DeferredRegister<BioForgeTab> BIO_FORGE_TABS = DeferredRegister.create(REGISTRY_KEY, BiomancyMod.MOD_ID);
	public static final Supplier<IForgeRegistry<BioForgeTab>> REGISTRY = BIO_FORGE_TABS.makeRegistry(RegistryBuilder::new);

	public static final RegistryObject<BioForgeTab> SEARCH = register("search", 99, () -> Items.COMPASS);
	public static final RegistryObject<BioForgeTab> MISC = register("misc", -98, ModItems.FLESH_IRIS_DOOR);
	public static final RegistryObject<BioForgeTab> REPLICAS = register("replicas", -99, ModItems.MOB_CLAW);
	public static final RegistryObject<BioForgeTab> BUILDING_BLOCKS = register("blocks", ModItems.FLESH_BLOCK);
	public static final RegistryObject<BioForgeTab> MACHINES = register("machines", ModItems.DECOMPOSER);
	public static final RegistryObject<BioForgeTab> WEAPONS = register("weapons", ModItems.RAVENOUS_CLAWS);

	private ModBioForgeTabs() {}

	private static RegistryObject<BioForgeTab> register(String name, Supplier<? extends Item> itemSupplier) {
		return BIO_FORGE_TABS.register(name, () -> new BioForgeTab(itemSupplier.get()));
	}

	private static RegistryObject<BioForgeTab> register(String name, int sortPriority, Supplier<? extends Item> itemSupplier) {
		return BIO_FORGE_TABS.register(name, () -> new BioForgeTab(sortPriority, itemSupplier.get()));
	}

}
