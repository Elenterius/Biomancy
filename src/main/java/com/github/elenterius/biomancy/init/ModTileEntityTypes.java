package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.tileentity.*;
import com.mojang.datafixers.types.Type;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public final class ModTileEntityTypes {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, BiomancyMod.MOD_ID);
	public static final Type<?> NO_DATA_FIXER = null;

	// Misc
	public static final RegistryObject<TileEntityType<SimpleOwnableTileEntity>> SIMPLE_OWNABLE_TILE = TILE_ENTITIES.register("ownable_tile", () -> TileEntityType.Builder.of(SimpleOwnableTileEntity::new, ModBlocks.FLESHBORN_DOOR.get(), ModBlocks.FLESHBORN_TRAPDOOR.get(), ModBlocks.FLESHBORN_PRESSURE_PLATE.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<VoiceBoxTileEntity>> VOICE_BOX_TILE = TILE_ENTITIES.register("voice_box", () -> TileEntityType.Builder.of(VoiceBoxTileEntity::new, ModBlocks.VOICE_BOX.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<ScentDiffuserTileEntity>> SCENT_DIFFUSER_TILE = TILE_ENTITIES.register("scent_diffuser", () -> TileEntityType.Builder.of(ScentDiffuserTileEntity::new, ModBlocks.SCENT_DIFFUSER.get()).build(NO_DATA_FIXER));

	// Inv Storage
	public static final RegistryObject<TileEntityType<GulgeTileEntity>> GULGE = TILE_ENTITIES.register("gulge", () -> TileEntityType.Builder.of(GulgeTileEntity::new, ModBlocks.GULGE.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<FleshbornChestTileEntity>> FLESH_CHEST = TILE_ENTITIES.register("fleshborn_chest", () -> TileEntityType.Builder.of(FleshbornChestTileEntity::new, ModBlocks.FLESHBORN_CHEST.get()).build(NO_DATA_FIXER));

	// Machines
	public static final RegistryObject<TileEntityType<DecomposerTileEntity>> DECOMPOSER = TILE_ENTITIES.register("decomposer", () -> TileEntityType.Builder.of(DecomposerTileEntity::new, ModBlocks.DECOMPOSER.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<ChewerTileEntity>> CHEWER = TILE_ENTITIES.register("chewer", () -> TileEntityType.Builder.of(ChewerTileEntity::new, ModBlocks.CHEWER.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<DigesterTileEntity>> DIGESTER = TILE_ENTITIES.register("digester", () -> TileEntityType.Builder.of(DigesterTileEntity::new, ModBlocks.DIGESTER.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<SolidifierTileEntity>> SOLIDIFIER = TILE_ENTITIES.register("solidifier", () -> TileEntityType.Builder.of(SolidifierTileEntity::new, ModBlocks.SOLIDIFIER.get()).build(NO_DATA_FIXER));
	public static final RegistryObject<TileEntityType<EvolutionPoolTileEntity>> EVOLUTION_POOL = TILE_ENTITIES.register("evolution_pool", () -> TileEntityType.Builder.of(EvolutionPoolTileEntity::new, ModBlocks.EVOLUTION_POOL.get()).build(NO_DATA_FIXER));

	// Multi-Block Tile Delegator
	public static final RegistryObject<TileEntityType<OwnableTileEntityDelegator>> TILE_DELEGATOR = TILE_ENTITIES.register("tile_delegator", () -> TileEntityType.Builder.of(OwnableTileEntityDelegator::new, ModBlocks.EVOLUTION_POOL.get(), ModBlocks.FLESHBORN_DOOR.get()).build(NO_DATA_FIXER));


	private ModTileEntityTypes() {}

}
