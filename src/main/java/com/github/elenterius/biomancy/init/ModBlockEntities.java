package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.*;
import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, BiomancyMod.MOD_ID);

	public static final RegistryObject<BlockEntityType<CreatorBlockEntity>> CREATOR = BLOCK_ENTITIES.register("creator", () -> BlockEntityType.Builder.of(CreatorBlockEntity::new, ModBlocks.CREATOR.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<DecomposerBlockEntity>> DECOMPOSER = BLOCK_ENTITIES.register("decomposer", () -> BlockEntityType.Builder.of(DecomposerBlockEntity::new, ModBlocks.DECOMPOSER.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<GlandBlockEntity>> GLAND = BLOCK_ENTITIES.register("gland", () -> BlockEntityType.Builder.of(GlandBlockEntity::new, ModBlocks.GLAND.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<SacBlockEntity>> SAC = BLOCK_ENTITIES.register("sac", () -> BlockEntityType.Builder.of(SacBlockEntity::new, ModBlocks.SAC.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<GulgeBlockEntity>> GULGE = BLOCK_ENTITIES.register("gulge", () -> BlockEntityType.Builder.of(GulgeBlockEntity::new, ModBlocks.GULGE.get()).build(noDataFixer()));

	//	// Misc
	//	public static final RegistryObject<TileEntityType<VoiceBoxTileEntity>> VOICE_BOX_TILE = TILE_ENTITIES.register("voice_box", () -> TileEntityType.Builder.of(VoiceBoxTileEntity::new, ModBlocks.VOICE_BOX.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<SimpleOwnableTileEntity>> SIMPLE_OWNABLE_TILE = TILE_ENTITIES.register("ownable_tile", () -> TileEntityType.Builder.of(SimpleOwnableTileEntity::new, ModBlocks.FLESHBORN_DOOR.get(), ModBlocks.FLESHBORN_TRAPDOOR.get(), ModBlocks.FLESHBORN_PRESSURE_PLATE.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<ScentDiffuserTileEntity>> SCENT_DIFFUSER_TILE = TILE_ENTITIES.register("scent_diffuser", () -> TileEntityType.Builder.of(ScentDiffuserTileEntity::new, ModBlocks.SCENT_DIFFUSER.get()).build(NO_DATA_FIXER));
//
//	// Inv Storage
//	public static final RegistryObject<TileEntityType<FleshbornChestTileEntity>> FLESH_CHEST = TILE_ENTITIES.register("fleshborn_chest", () -> TileEntityType.Builder.of(FleshbornChestTileEntity::new, ModBlocks.FLESHBORN_CHEST.get()).build(NO_DATA_FIXER));
//
//	public static final RegistryObject<TileEntityType<DigesterTileEntity>> DIGESTER = TILE_ENTITIES.register("digester", () -> TileEntityType.Builder.of(DigesterTileEntity::new, ModBlocks.DIGESTER.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<EvolutionPoolTileEntity>> EVOLUTION_POOL = TILE_ENTITIES.register("evolution_pool", () -> TileEntityType.Builder.of(EvolutionPoolTileEntity::new, ModBlocks.EVOLUTION_POOL.get()).build(NO_DATA_FIXER));
//
//	// Multi-Block Tile Delegator
//	public static final RegistryObject<TileEntityType<OwnableTileEntityDelegator>> TILE_DELEGATOR = TILE_ENTITIES.register("tile_delegator", () -> TileEntityType.Builder.of(OwnableTileEntityDelegator::new, ModBlocks.EVOLUTION_POOL.get(), ModBlocks.FLESHBORN_DOOR.get()).build(NO_DATA_FIXER));

	private ModBlockEntities() {}

	private static Type<?> noDataFixer() {
		//noinspection ConstantConditions
		return null;
	}

}
