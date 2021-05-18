package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.tileentity.*;
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
	public static final RegistryObject<TileEntityType<DecomposerTileEntity>> DECOMPOSER = TILE_ENTITIES.register("decomposer", () -> TileEntityType.Builder.create(DecomposerTileEntity::new, ModBlocks.DIGESTER.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<ChewerTileEntity>> CHEWER = TILE_ENTITIES.register("chewer", () -> TileEntityType.Builder.create(ChewerTileEntity::new, ModBlocks.CHEWER.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<EvolutionPoolTileEntity>> EVOLUTION_POOL = TILE_ENTITIES.register("evolution_pool", () -> TileEntityType.Builder.create(EvolutionPoolTileEntity::new, ModBlocks.EVOLUTION_POOL.get()).build(NO_DATA_FIXER));

	// Multi-Block Tile Delegator
	public static final RegistryObject<TileEntityType<OwnableTileEntityDelegator>> TILE_DELEGATOR = TILE_ENTITIES.register("tile_delegator", () -> TileEntityType.Builder.create(OwnableTileEntityDelegator::new, ModBlocks.EVOLUTION_POOL.get(), ModBlocks.BIO_FLESH_DOOR.get()).build(NO_DATA_FIXER));


	private ModTileEntityTypes() {}

}
