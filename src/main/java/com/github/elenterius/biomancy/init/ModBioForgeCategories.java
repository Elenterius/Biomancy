package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.recipe.BioForgeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModBioForgeCategories {

	public static final DeferredRegister<BioForgeCategory> BIO_FORGE_CATEGORIES = DeferredRegister.create(BiomancyMod.createRL("bio_forge_category"), BiomancyMod.MOD_ID);
	public static final Supplier<IForgeRegistry<BioForgeCategory>> REGISTRY = BIO_FORGE_CATEGORIES.makeRegistry(BioForgeCategory.class, RegistryBuilder::new);

	public static final RegistryObject<BioForgeCategory> SEARCH = register("search", 1, () -> Items.COMPASS);
	public static final RegistryObject<BioForgeCategory> MISC = register("misc", -1, ModItems.LIVING_FLESH);

	public static final RegistryObject<BioForgeCategory> BLOCKS = register("blocks", ModItems.FLESH_BLOCK);
	public static final RegistryObject<BioForgeCategory> MACHINES = register("machines", ModItems.DECOMPOSER);
	public static final RegistryObject<BioForgeCategory> WEAPONS = register("weapons", ModItems.LONG_CLAWS);

	private ModBioForgeCategories() {}

	private static RegistryObject<BioForgeCategory> register(String name, Supplier<? extends Item> itemSupplier) {
		return BIO_FORGE_CATEGORIES.register(name, () -> new BioForgeCategory(itemSupplier));
	}

	private static RegistryObject<BioForgeCategory> register(String name, int sortPriority, Supplier<? extends Item> itemSupplier) {
		return BIO_FORGE_CATEGORIES.register(name, () -> new BioForgeCategory(sortPriority, itemSupplier));
	}

}
