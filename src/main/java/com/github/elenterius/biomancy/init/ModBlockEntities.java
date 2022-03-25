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
	public static final RegistryObject<BlockEntityType<BioForgeBlockEntity>> BIO_FORGE = BLOCK_ENTITIES.register("bio_forge", () -> BlockEntityType.Builder.of(BioForgeBlockEntity::new, ModBlocks.BIO_FORGE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<BioLabBlockEntity>> BIO_LAB = BLOCK_ENTITIES.register("bio_lab", () -> BlockEntityType.Builder.of(BioLabBlockEntity::new, ModBlocks.BIO_LAB.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<DigesterBlockEntity>> DIGESTER = BLOCK_ENTITIES.register("digester", () -> BlockEntityType.Builder.of(DigesterBlockEntity::new, ModBlocks.DIGESTER.get()).build(noDataFixer()));

	public static final RegistryObject<BlockEntityType<GlandBlockEntity>> GLAND = BLOCK_ENTITIES.register("gland", () -> BlockEntityType.Builder.of(GlandBlockEntity::new, ModBlocks.GLAND.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<TongueBlockEntity>> TONGUE = BLOCK_ENTITIES.register("tongue", () -> BlockEntityType.Builder.of(TongueBlockEntity::new, ModBlocks.TONGUE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<SacBlockEntity>> SAC = BLOCK_ENTITIES.register("sac", () -> BlockEntityType.Builder.of(SacBlockEntity::new, ModBlocks.SAC.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<GulgeBlockEntity>> GULGE = BLOCK_ENTITIES.register("gulge", () -> BlockEntityType.Builder.of(GulgeBlockEntity::new, ModBlocks.GULGE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<FleshkinChestBlockEntity>> FLESHKIN_CHEST = BLOCK_ENTITIES.register("fleshkin_chest", () -> BlockEntityType.Builder.of(FleshkinChestBlockEntity::new, ModBlocks.FLESH_CHEST.get()).build(noDataFixer()));

	//# Misc
	public static final RegistryObject<BlockEntityType<VoiceBoxBlockEntity>> VOICE_BOX = BLOCK_ENTITIES.register("voice_box", () -> BlockEntityType.Builder.of(VoiceBoxBlockEntity::new, ModBlocks.VOICE_BOX.get()).build(noDataFixer()));

//	public static final RegistryObject<BlockEntityType<SimpleOwnableTileEntity>> SIMPLE_OWNABLE_TILE = BLOCK_ENTITIES.register("ownable_tile", () -> BlockEntityType.Builder.of(SimpleOwnableTileEntity::new, ModBlocks.FLESHBORN_DOOR.get(), ModBlocks.FLESHBORN_TRAPDOOR.get(), ModBlocks.FLESHBORN_PRESSURE_PLATE.get()).build(noDataFixer()));
//	public static final RegistryObject<BlockEntityType<ScentDiffuserTileEntity>> SCENT_DIFFUSER_TILE = BLOCK_ENTITIES.register("scent_diffuser", () -> BlockEntityType.Builder.of(ScentDiffuserTileEntity::new, ModBlocks.SCENT_DIFFUSER.get()).build(noDataFixer()));
//
//	public static final RegistryObject<BlockEntityType<DigesterTileEntity>> DIGESTER = BLOCK_ENTITIES.register("digester", () -> BlockEntityType.Builder.of(DigesterTileEntity::new, ModBlocks.DIGESTER.get()).build(noDataFixer()));
//	public static final RegistryObject<BlockEntityType<EvolutionPoolTileEntity>> EVOLUTION_POOL = BLOCK_ENTITIES.register("evolution_pool", () -> BlockEntityType.Builder.of(EvolutionPoolTileEntity::new, ModBlocks.EVOLUTION_POOL.get()).build(noDataFixer()));
//
//	// Multi-Block Tile Delegator
//	public static final RegistryObject<BlockEntityType<OwnableTileEntityDelegator>> TILE_DELEGATOR = BLOCK_ENTITIES.register("tile_delegator", () -> BlockEntityType.Builder.of(OwnableTileEntityDelegator::new, ModBlocks.EVOLUTION_POOL.get(), ModBlocks.FLESHBORN_DOOR.get()).build(noDataFixer()));

	private ModBlockEntities() {}

	private static Type<?> noDataFixer() {
		//noinspection ConstantConditions
		return null;
	}

}
