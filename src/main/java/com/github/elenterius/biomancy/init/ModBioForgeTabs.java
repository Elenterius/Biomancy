package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.menu.BioForgeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModBioForgeTabs {

	public static final DeferredRegister<BioForgeTab> BIO_FORGE_TABS = DeferredRegister.create(BiomancyMod.createRL("bio_forge_tab"), BiomancyMod.MOD_ID);
	public static final Supplier<IForgeRegistry<BioForgeTab>> REGISTRY = BIO_FORGE_TABS.makeRegistry(RegistryBuilder::new);

	public static final RegistryObject<BioForgeTab> SEARCH = register("search", 1, () -> Items.COMPASS);
	public static final RegistryObject<BioForgeTab> MISC = register("misc", -1, ModItems.LIVING_FLESH);

	public static final RegistryObject<BioForgeTab> BLOCKS = register("blocks", ModItems.FLESH_BLOCK);
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
