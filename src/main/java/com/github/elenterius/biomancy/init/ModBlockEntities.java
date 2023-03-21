package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.bioforge.BioForgeBlockEntity;
import com.github.elenterius.biomancy.world.block.biolab.BioLabBlockEntity;
import com.github.elenterius.biomancy.world.block.cradle.PrimordialCradleBlockEntity;
import com.github.elenterius.biomancy.world.block.decomposer.DecomposerBlockEntity;
import com.github.elenterius.biomancy.world.block.digester.DigesterBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.BlockEntityDelegator;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlockEntity;
import com.github.elenterius.biomancy.world.block.mawhopper.MawHopperBlockEntity;
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

	public static final RegistryObject<BlockEntityType<PrimordialCradleBlockEntity>> PRIMORDIAL_CRADLE = register(ModBlocks.PRIMORDIAL_CRADLE, PrimordialCradleBlockEntity::new);

	public static final RegistryObject<BlockEntityType<DecomposerBlockEntity>> DECOMPOSER = register(ModBlocks.DECOMPOSER, DecomposerBlockEntity::new);
	public static final RegistryObject<BlockEntityType<BioForgeBlockEntity>> BIO_FORGE = register(ModBlocks.BIO_FORGE, BioForgeBlockEntity::new);
	public static final RegistryObject<BlockEntityType<BioLabBlockEntity>> BIO_LAB = register(ModBlocks.BIO_LAB, BioLabBlockEntity::new);
	public static final RegistryObject<BlockEntityType<DigesterBlockEntity>> DIGESTER = register(ModBlocks.DIGESTER, DigesterBlockEntity::new);

	public static final RegistryObject<BlockEntityType<TongueBlockEntity>> TONGUE = register(ModBlocks.TONGUE, TongueBlockEntity::new);
	public static final RegistryObject<BlockEntityType<MawHopperBlockEntity>> MAW_HOPPER = register(ModBlocks.MAW_HOPPER, MawHopperBlockEntity::new);
	public static final RegistryObject<BlockEntityType<StorageSacBlockEntity>> STORAGE_SAC = register(ModBlocks.STORAGE_SAC, StorageSacBlockEntity::new);
	public static final RegistryObject<BlockEntityType<FleshkinChestBlockEntity>> FLESHKIN_CHEST = register(ModBlocks.FLESHKIN_CHEST, FleshkinChestBlockEntity::new);
	public static final RegistryObject<BlockEntityType<VoiceBoxBlockEntity>> VOICE_BOX = register(ModBlocks.VOICE_BOX, VoiceBoxBlockEntity::new);

	//# Special
	public static final RegistryObject<BlockEntityType<OwnableBlockEntity>> OWNABLE_BE = BLOCK_ENTITIES.register("ownable_block_entity", () -> BlockEntityType.Builder.of(OwnableBlockEntity::new, ModBlocks.FLESHKIN_DOOR.get(), ModBlocks.FLESHKIN_TRAPDOOR.get(), ModBlocks.FLESHKIN_PRESSURE_PLATE.get()).build(noDataFixer()));
	public static final RegistryObject<BlockEntityType<BlockEntityDelegator>> BE_DELEGATOR = BLOCK_ENTITIES.register("block_entity_delegator", () -> BlockEntityType.Builder.of(BlockEntityDelegator::new, ModBlocks.FLESHKIN_DOOR.get()).build(noDataFixer()));

	private ModBlockEntities() {}

	private static <T extends BlockEntity, B extends Block> RegistryObject<BlockEntityType<T>> register(RegistryObject<B> blockHolder, BlockEntityType.BlockEntitySupplier<T> factory) {
		return BLOCK_ENTITIES.register(blockHolder.getId().getPath(), () -> BlockEntityType.Builder.of(factory, blockHolder.get()).build(noDataFixer()));
	}

	private static Type<?> noDataFixer() {
		//noinspection ConstantConditions
		return null;
	}

}
