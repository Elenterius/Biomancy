package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.tileentity.DecomposerTileEntity;
import com.github.elenterius.biomancy.tileentity.FleshChestTileEntity;
import com.github.elenterius.biomancy.tileentity.GulgeTileEntity;
import com.github.elenterius.biomancy.tileentity.SimpleOwnableTileEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public class ModTileEntityTypes {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BiomancyMod.MOD_ID);
	public static final Type<?> NO_DATA_FIXER = null;

	// Misc
	public static final RegistryObject<TileEntityType<SimpleOwnableTileEntity>> SIMPLE_OWNABLE_TILE = TILE_ENTITIES.register("ownable_tile", () -> TileEntityType.Builder.create(SimpleOwnableTileEntity::new, ModBlocks.BIO_FLESH_DOOR.get(), ModBlocks.BIO_FLESH_TRAPDOOR.get(), ModBlocks.BIO_FLESH_PRESSURE_PLATE.get()).build(NO_DATA_FIXER));

	// Inv Storage
	public static final RegistryObject<TileEntityType<GulgeTileEntity>> GULGE = TILE_ENTITIES.register("gulge", () -> TileEntityType.Builder.create(GulgeTileEntity::new, ModBlocks.GULGE.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<FleshChestTileEntity>> FLESH_CHEST = TILE_ENTITIES.register("bioflesh_chest", () -> TileEntityType.Builder.create(FleshChestTileEntity::new, ModBlocks.FLESH_CHEST.get()).build(NO_DATA_FIXER));

	// Machines
	public static final RegistryObject<TileEntityType<DecomposerTileEntity>> DECOMPOSER = TILE_ENTITIES.register("decomposer", () -> TileEntityType.Builder.create(DecomposerTileEntity::new, ModBlocks.DECOMPOSER.get()).build(NO_DATA_FIXER));

	private ModTileEntityTypes() {}

}
