package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.bioforge.BioForgeBlockEntity;
import com.github.elenterius.biomancy.world.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.world.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.world.block.decomposer.DecomposerBlockEntity;
import com.github.elenterius.biomancy.world.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.BlockEntityDelegator;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlockEntity;
import com.github.elenterius.biomancy.world.block.modularlarynx.VoiceBoxBlockEntity;
import com.github.elenterius.biomancy.world.block.ownable.OwnableBlockEntity;
import com.github.elenterius.biomancy.world.block.storagesac.StorageSacBlockEntity;
import com.github.elenterius.biomancy.world.block.tongue.TongueBlockEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BiomancyMod.MOD_ID);

	private static <T extends BlockEntity, B extends Block> RegistryObject<BlockEntityType<T>> register(RegistryObject<B> blockHolder, BlockEntityType.BlockEntitySupplier<T> factory) {
		return BLOCK_ENTITIES.register(blockHolder.getId().getPath(), () -> BlockEntityType.Builder.of(factory, blockHolder.get()).build(noDataFixer()));
	}	public static final RegistryObject<BlockEntityType<PrimordialCradleBlockEntity>> PRIMORDIAL_CRADLE = register(ModBlocks.PRIMORDIAL_CRADLE, PrimordialCradleBlockEntity::new);



	public static final RegistryObject<BlockEntityType<DecomposerBlockEntity>> DECOMPOSER = BLOCK_ENTITIES.register("decomposer", () -> BlockEntityType.Builder.of(DecomposerBlockEntity::new, ModBlocks.DECOMPOSER.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<BioForgeBlockEntity>> BIO_FORGE = BLOCK_ENTITIES.register("bio_forge", () -> BlockEntityType.Builder.of(BioForgeBlockEntity::new, ModBlocks.BIO_FORGE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<BioLabBlockEntity>> BIO_LAB = BLOCK_ENTITIES.register("bio_lab", () -> BlockEntityType.Builder.of(BioLabBlockEntity::new, ModBlocks.BIO_LAB.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<DigesterBlockEntity>> DIGESTER = BLOCK_ENTITIES.register("digester", () -> BlockEntityType.Builder.of(DigesterBlockEntity::new, ModBlocks.DIGESTER.get()).build(noDataFixer()));

	//	public static final RegistryObject<BlockEntityType<GlandBlockEntity>> GLAND = BLOCK_ENTITIES.register("gland", () -> BlockEntityType.Builder.of(GlandBlockEntity::new, ModBlocks.GLAND.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<TongueBlockEntity>> TONGUE = BLOCK_ENTITIES.register("tongue", () -> BlockEntityType.Builder.of(TongueBlockEntity::new, ModBlocks.TONGUE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<StorageSacBlockEntity>> STORAGE_SAC = BLOCK_ENTITIES.register("storage_sac", () -> BlockEntityType.Builder.of(StorageSacBlockEntity::new, ModBlocks.STORAGE_SAC.get()).build(noDataFixer()));
	//	public static final RegistryObject<BlockEntityType<GulgeBlockEntity>> GULGE = BLOCK_ENTITIES.register("gulge", () -> BlockEntityType.Builder.of(GulgeBlockEntity::new, ModBlocks.GULGE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<FleshkinChestBlockEntity>> FLESHKIN_CHEST = BLOCK_ENTITIES.register("fleshkin_chest", () -> BlockEntityType.Builder.of(FleshkinChestBlockEntity::new, ModBlocks.FLESHKIN_CHEST.get()).build(noDataFixer()));

	//# Misc
	public static final RegistryObject<BlockEntityType<VoiceBoxBlockEntity>> VOICE_BOX = BLOCK_ENTITIES.register("voice_box", () -> BlockEntityType.Builder.of(VoiceBoxBlockEntity::new, ModBlocks.VOICE_BOX.get()).build(noDataFixer()));
	//	public static final RegistryObject<BlockEntityType<ScentDiffuserTileEntity>> SCENT_DIFFUSER_TILE = BLOCK_ENTITIES.register("scent_diffuser", () -> BlockEntityType.Builder.of(ScentDiffuserTileEntity::new, ModBlocks.SCENT_DIFFUSER.get()).build(noDataFixer()));

	//# Special
	public static final RegistryObject<BlockEntityType<OwnableBlockEntity>> OWNABLE_BE = BLOCK_ENTITIES.register("ownable_block_entity", () -> BlockEntityType.Builder.of(OwnableBlockEntity::new, ModBlocks.FLESHKIN_DOOR.get(), ModBlocks.FLESHKIN_TRAPDOOR.get(), ModBlocks.FLESHKIN_PRESSURE_PLATE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<BlockEntityDelegator>> BE_DELEGATOR = BLOCK_ENTITIES.register("block_entity_delegator", () -> BlockEntityType.Builder.of(BlockEntityDelegator::new, ModBlocks.FLESHKIN_DOOR.get()).build(noDataFixer()));

	private ModBlockEntities() {}

	private static Type<?> noDataFixer() {
		//noinspection ConstantConditions
		return null;
	}

}
