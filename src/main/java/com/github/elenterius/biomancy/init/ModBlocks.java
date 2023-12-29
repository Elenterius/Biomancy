package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.*;
import com.github.elenterius.biomancy.block.bioforge.BioForgeBlock;
import com.github.elenterius.biomancy.block.biolab.BioLabBlock;
import com.github.elenterius.biomancy.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.block.decomposer.DecomposerBlock;
import com.github.elenterius.biomancy.block.digester.DigesterBlock;
import com.github.elenterius.biomancy.block.fleshkinchest.FleshkinChestBlock;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlock;
import com.github.elenterius.biomancy.block.malignantbloom.MalignantBloomBlock;
import com.github.elenterius.biomancy.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.block.modularlarynx.VoiceBoxBlock;
import com.github.elenterius.biomancy.block.neural.NeuralInterceptorBlock;
import com.github.elenterius.biomancy.block.orifice.OrificeBlock;
import com.github.elenterius.biomancy.block.ownable.OwnablePressurePlateBlock;
import com.github.elenterius.biomancy.block.storagesac.StorageSacBlock;
import com.github.elenterius.biomancy.block.tongue.TongueBlock;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlock;
import com.github.elenterius.biomancy.init.tags.ModEntityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BiomancyMod.MOD_ID);

	//## Manual Crafting
	public static final RegistryObject<PrimordialCradleBlock> PRIMORDIAL_CRADLE = register("primordial_cradle", PrimordialCradleBlock::new);
	public static final RegistryObject<BioForgeBlock> BIO_FORGE = register("bio_forge", BioForgeBlock::new);

	//## Machine
	public static final RegistryObject<DecomposerBlock> DECOMPOSER = register("decomposer", properties -> new DecomposerBlock(properties.noOcclusion()));
	public static final RegistryObject<BioLabBlock> BIO_LAB = register("bio_lab", BioLabBlock::new);
	public static final RegistryObject<DigesterBlock> DIGESTER = register("digester", DigesterBlock::new);

	//## Automation, Redstone & Storage
	public static final RegistryObject<StorageSacBlock> STORAGE_SAC = register("storage_sac", StorageSacBlock::new);
	public static final RegistryObject<TongueBlock> TONGUE = register("tongue", TongueBlock::new);
	public static final RegistryObject<MawHopperBlock> MAW_HOPPER = register("maw_hopper", MawHopperBlock::new);
	public static final RegistryObject<FleshkinChestBlock> FLESHKIN_CHEST = register("fleshkin_chest", FleshkinChestBlock::new);
	public static final RegistryObject<OwnablePressurePlateBlock> FLESHKIN_PRESSURE_PLATE = register("fleshkin_pressure_plate", OwnablePressurePlateBlock::new);

	//## Building Materials
	public static final RegistryObject<FleshBlock> FLESH = register("flesh", FleshBlock::new);
	public static final RegistryObject<StairBlock> FLESH_STAIRS = registerStairs(FLESH, StairBlock::new);
	public static final RegistryObject<WallBlock> FLESH_WALL = registerWall(FLESH, WallBlock::new);
	public static final RegistryObject<DirectionalSlabBlock> FLESH_SLAB = registerSlab(FLESH, DirectionalSlabBlock::new);
	public static final RegistryObject<FleshBlock> PACKED_FLESH = register("packed_flesh", () -> new FleshBlock(createToughFleshProperties()));
	public static final RegistryObject<StairBlock> PACKED_FLESH_STAIRS = registerStairs(PACKED_FLESH, StairBlock::new);
	public static final RegistryObject<WallBlock> PACKED_FLESH_WALL = registerWall(PACKED_FLESH, WallBlock::new);
	public static final RegistryObject<DirectionalSlabBlock> PACKED_FLESH_SLAB = registerSlab(PACKED_FLESH, DirectionalSlabBlock::new);

	//## Decoration
	public static final RegistryObject<RotatedPillarBlock> TUBULAR_FLESH_BLOCK = register("tubular_flesh", RotatedPillarBlock::new);
	public static final RegistryObject<FleshBlock> FIBROUS_FLESH = register("fibrous_flesh", FleshBlock::new);
	public static final RegistryObject<RotatedPillarBlock> FLESH_PILLAR = register("flesh_pillar", () -> new RotatedPillarBlock(createBonyFleshProperties()));
	public static final RegistryObject<FleshBlock> CHISELED_FLESH = register("chiseled_flesh", () -> new FleshBlock(createBonyFleshProperties()));
	public static final RegistryObject<RotatedPillarBlock> ORNATE_FLESH = register("ornate_flesh", () -> new RotatedPillarBlock(createBonyFleshProperties()));

	//## Primal Ecosystem
	public static final RegistryObject<FleshBlock> PRIMAL_FLESH = register("primal_flesh", FleshBlock::new);
	public static final RegistryObject<StairBlock> PRIMAL_FLESH_STAIRS = registerStairs(PRIMAL_FLESH, StairBlock::new);
	public static final RegistryObject<DirectionalSlabBlock> PRIMAL_FLESH_SLAB = registerSlab(PRIMAL_FLESH, DirectionalSlabBlock::new);
	public static final RegistryObject<WallBlock> PRIMAL_FLESH_WALL = registerWall(PRIMAL_FLESH, WallBlock::new);
	public static final RegistryObject<FleshBlock> MALIGNANT_FLESH = register("malignant_flesh", FleshBlock::new);
	public static final RegistryObject<StairBlock> MALIGNANT_FLESH_STAIRS = registerStairs(MALIGNANT_FLESH, StairBlock::new);
	public static final RegistryObject<DirectionalSlabBlock> MALIGNANT_FLESH_SLAB = registerSlab(MALIGNANT_FLESH, DirectionalSlabBlock::new);
	public static final RegistryObject<WallBlock> MALIGNANT_FLESH_WALL = registerWall(MALIGNANT_FLESH, WallBlock::new);
	public static final RegistryObject<FleshVeinsBlock> MALIGNANT_FLESH_VEINS = register("malignant_flesh_veins", properties -> new FleshVeinsBlock(properties.noCollission().noOcclusion()));
	public static final RegistryObject<MalignantBloomBlock> MALIGNANT_BLOOM = register("malignant_bloom", properties -> new MalignantBloomBlock(properties.randomTicks().noOcclusion().lightLevel(MalignantBloomBlock::getLightEmission)));
	public static final RegistryObject<Block> BLOOMLIGHT = register("bloomlight", properties -> new Block(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15)));
	public static final RegistryObject<OrificeBlock> PRIMAL_ORIFICE = register("primal_orifice", properties -> new OrificeBlock(properties.randomTicks()));

	//## Utility
	public static final RegistryObject<VoiceBoxBlock> VOICE_BOX = register("voice_box", VoiceBoxBlock::new);
	public static final RegistryObject<FleshSpikeBlock> FLESH_SPIKE = register("flesh_spike", () -> new FleshSpikeBlock(createBonyFleshProperties().noOcclusion()));
	public static final RegistryObject<VialHolderBlock> VIAL_HOLDER = register("vial_holder", VialHolderBlock::new);
	public static final RegistryObject<NeuralInterceptorBlock> NEURAL_INTERCEPTOR = register("neural_interceptor", NeuralInterceptorBlock::new);

	//## Membranes
	public static final RegistryObject<MembraneBlock> IMPERMEABLE_MEMBRANE = registerMembrane("impermeable_membrane", MembraneBlock.IgnoreEntityCollisionPredicate.NEVER);
	public static final RegistryObject<MembraneBlock> BABY_PERMEABLE_MEMBRANE = registerMembrane("baby_permeable_membrane", MembraneBlock.IgnoreEntityCollisionPredicate.IS_BABY_MOB);
	public static final RegistryObject<MembraneBlock> ADULT_PERMEABLE_MEMBRANE = registerMembrane("adult_permeable_membrane", MembraneBlock.IgnoreEntityCollisionPredicate.IS_ADULT_MOB);
	public static final RegistryObject<MembraneBlock> PRIMAL_PERMEABLE_MEMBRANE = registerMembrane("primal_permeable_membrane", MembraneBlock.IgnoreEntityCollisionPredicate.IS_ALIVE_MOB, SpreadingMembraneBlock::new);
	public static final RegistryObject<MembraneBlock> UNDEAD_PERMEABLE_MEMBRANE = registerMembrane("undead_permeable_membrane", MembraneBlock.IgnoreEntityCollisionPredicate.IS_UNDEAD_MOB);

	//## Light Sources
	public static final RegistryObject<FleshLanternBlock> PRIMORDIAL_BIO_LANTERN = register("primordial_bio_lantern", properties -> new FleshLanternBlock(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15).noOcclusion()));
	public static final RegistryObject<FleshLanternBlock> YELLOW_BIO_LANTERN = register("bio_lantern_yellow", properties -> new FleshLanternBlock(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15).noOcclusion()));
	public static final RegistryObject<FleshLanternBlock> BLUE_BIO_LANTERN = register("bio_lantern_blue", properties -> new FleshLanternBlock(properties.sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15).noOcclusion()));

	//## Fluids
	public static final RegistryObject<LiquidBlock> ACID_FLUID_BLOCK = register("acid_fluid_block", () -> new LiquidBlock(ModFluids.ACID, copyProperties(Blocks.WATER)));

	//## Misc
	public static final RegistryObject<LadderBlock> FLESH_LADDER = register("flesh_ladder", () -> new LadderBlock(createBonyFleshProperties().noOcclusion()));
	public static final RegistryObject<FleshFenceBlock> FLESH_FENCE = register("flesh_fence", FleshFenceBlock::new);
	public static final RegistryObject<FleshFenceGateBlock> FLESH_FENCE_GATE = register("flesh_fence_gate", () -> new FleshFenceGateBlock(createBonyFleshProperties().noOcclusion()));
	public static final RegistryObject<IrisDoorBlock> FLESH_IRIS_DOOR = register("flesh_iris_door", IrisDoorBlock::new);
	public static final RegistryObject<FleshDoorBlock> FLESH_DOOR = register("flesh_door", FleshDoorBlock::new);
	public static final RegistryObject<FullFleshDoorBlock> FULL_FLESH_DOOR = register("full_flesh_door", FullFleshDoorBlock::new);
	public static final RegistryObject<FleshChainBlock> TENDON_CHAIN = register("tendon_chain", properties -> new FleshChainBlock(properties.noOcclusion()));

	private ModBlocks() {}

	private static <T extends Block> RegistryObject<T> register(String name, Function<BlockBehaviour.Properties, T> factory) {
		return BLOCKS.register(name, () -> factory.apply(createFleshProperties()));
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> factory) {
		return BLOCKS.register(name, factory);
	}

	private static <T extends StairBlock> RegistryObject<T> registerStairs(RegistryObject<? extends Block> parent, StairBlockFactory<T> factory) {
		return registerStairs(parent.getId().getPath() + "_stairs", parent, factory);
	}

	private static <T extends StairBlock> RegistryObject<T> registerStairs(String name, RegistryObject<? extends Block> parent, StairBlockFactory<T> factory) {
		return BLOCKS.register(name, () -> factory.create(() -> parent.get().defaultBlockState(), copyProperties(parent.get())));
	}

	private static <T extends WallBlock> RegistryObject<T> registerWall(RegistryObject<? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return registerWall(parent.getId().getPath() + "_wall", parent, factory);
	}

	private static <T extends WallBlock> RegistryObject<T> registerWall(String name, RegistryObject<? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return BLOCKS.register(name, () -> factory.apply(copyProperties(parent.get())));
	}

	private static <T extends DirectionalSlabBlock> RegistryObject<T> registerSlab(RegistryObject<? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return registerSlab(parent.getId().getPath() + "_slab", parent, factory);
	}

	private static <T extends DirectionalSlabBlock> RegistryObject<T> registerSlab(String name, RegistryObject<? extends Block> parent, Function<BlockBehaviour.Properties, T> factory) {
		return BLOCKS.register(name, () -> factory.apply(copyProperties(parent.get())));
	}

	private static RegistryObject<MembraneBlock> registerMembrane(String name, MembraneBlock.IgnoreEntityCollisionPredicate predicate) {
		return registerMembrane(name, predicate, MembraneBlock::new);
	}

	private static <T extends MembraneBlock> RegistryObject<T> registerMembrane(String name, MembraneBlock.IgnoreEntityCollisionPredicate predicate, MembraneBlockFactory<T> factory) {
		return register(name, props -> {
			props = props.noOcclusion().isRedstoneConductor(ModBlocks::neverValid).isSuffocating(ModBlocks::neverValid).isViewBlocking(ModBlocks::neverValid);
			return factory.create(props, predicate);
		});
	}

	public static BlockBehaviour.Properties copyProperties(BlockBehaviour behaviour) {
		return BlockBehaviour.Properties.copy(behaviour);
	}

	public static BlockBehaviour.Properties createFleshProperties() {
		return BlockBehaviour.Properties.of(ModBlockMaterials.FLESH_MATERIAL).strength(3f, 3f).sound(ModSoundTypes.FLESH_BLOCK).isValidSpawn(ModBlocks::isValidFleshkinSpawn);
	}

	public static BlockBehaviour.Properties createToughFleshProperties() {
		return createFleshProperties().strength(6f, 12f);
	}

	public static BlockBehaviour.Properties createBonyFleshProperties() {
		return BlockBehaviour.Properties.of(ModBlockMaterials.FLESH_MATERIAL).strength(4f, 6f).sound(ModSoundTypes.BONY_FLESH_BLOCK).isValidSpawn(ModBlocks::isValidFleshkinSpawn);
	}

	public static boolean isValidFleshkinSpawn(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		return entityType.is(ModEntityTags.FLESHKIN) && state.isFaceSturdy(level, pos, Direction.UP);
	}

	public static boolean neverValid(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		return false;
	}

	public static boolean neverValid(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}


	interface StairBlockFactory<T extends StairBlock> {
		T create(Supplier<BlockState> state, BlockBehaviour.Properties properties);
	}

	interface MembraneBlockFactory<T extends MembraneBlock> {
		T create(BlockBehaviour.Properties properties, MembraneBlock.IgnoreEntityCollisionPredicate predicate);
	}
}
