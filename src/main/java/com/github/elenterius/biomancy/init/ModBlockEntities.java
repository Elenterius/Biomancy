package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.entity.CreatorBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.DecomposerBlockEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("ConstantConditions")
public final class ModBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, BiomancyMod.MOD_ID);
	private static final Type<?> NO_DATA_FIXER = null;

	public static final RegistryObject<BlockEntityType<CreatorBlockEntity>> CREATOR = BLOCK_ENTITIES.register("creator", () -> BlockEntityType.Builder.of(CreatorBlockEntity::new, ModBlocks.CREATOR.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<BlockEntityType<DecomposerBlockEntity>> DECOMPOSER = BLOCK_ENTITIES.register("decomposer", () -> BlockEntityType.Builder.of(DecomposerBlockEntity::new, ModBlocks.DECOMPOSER.get()).build(NO_DATA_FIXER));

	//	// Misc
//	public static final RegistryObject<TileEntityType<SimpleOwnableTileEntity>> SIMPLE_OWNABLE_TILE = TILE_ENTITIES.register("ownable_tile", () -> TileEntityType.Builder.of(SimpleOwnableTileEntity::new, ModBlocks.FLESHBORN_DOOR.get(), ModBlocks.FLESHBORN_TRAPDOOR.get(), ModBlocks.FLESHBORN_PRESSURE_PLATE.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<VoiceBoxTileEntity>> VOICE_BOX_TILE = TILE_ENTITIES.register("voice_box", () -> TileEntityType.Builder.of(VoiceBoxTileEntity::new, ModBlocks.VOICE_BOX.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<ScentDiffuserTileEntity>> SCENT_DIFFUSER_TILE = TILE_ENTITIES.register("scent_diffuser", () -> TileEntityType.Builder.of(ScentDiffuserTileEntity::new, ModBlocks.SCENT_DIFFUSER.get()).build(NO_DATA_FIXER));
//
//	// Inv Storage
//	public static final RegistryObject<TileEntityType<GulgeTileEntity>> GULGE = TILE_ENTITIES.register("gulge", () -> TileEntityType.Builder.of(GulgeTileEntity::new, ModBlocks.GULGE.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<FleshbornChestTileEntity>> FLESH_CHEST = TILE_ENTITIES.register("fleshborn_chest", () -> TileEntityType.Builder.of(FleshbornChestTileEntity::new, ModBlocks.FLESHBORN_CHEST.get()).build(NO_DATA_FIXER));
//
//	// Machines
//	public static final RegistryObject<TileEntityType<ChewerTileEntity>> CHEWER = TILE_ENTITIES.register("chewer", () -> TileEntityType.Builder.of(ChewerTileEntity::new, ModBlocks.CHEWER.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<DigesterTileEntity>> DIGESTER = TILE_ENTITIES.register("digester", () -> TileEntityType.Builder.of(DigesterTileEntity::new, ModBlocks.DIGESTER.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<SolidifierTileEntity>> SOLIDIFIER = TILE_ENTITIES.register("solidifier", () -> TileEntityType.Builder.of(SolidifierTileEntity::new, ModBlocks.SOLIDIFIER.get()).build(NO_DATA_FIXER));
//	public static final RegistryObject<TileEntityType<EvolutionPoolTileEntity>> EVOLUTION_POOL = TILE_ENTITIES.register("evolution_pool", () -> TileEntityType.Builder.of(EvolutionPoolTileEntity::new, ModBlocks.EVOLUTION_POOL.get()).build(NO_DATA_FIXER));
//
//	// Multi-Block Tile Delegator
//	public static final RegistryObject<TileEntityType<OwnableTileEntityDelegator>> TILE_DELEGATOR = TILE_ENTITIES.register("tile_delegator", () -> TileEntityType.Builder.of(OwnableTileEntityDelegator::new, ModBlocks.EVOLUTION_POOL.get(), ModBlocks.FLESHBORN_DOOR.get()).build(NO_DATA_FIXER));

	private ModBlockEntities() {}

}
