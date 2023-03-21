package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.*;
import com.github.elenterius.biomancy.world.block.bioforge.BioForgeBlock;
import com.github.elenterius.biomancy.world.block.biolab.BioLabBlock;
import com.github.elenterius.biomancy.world.block.cradle.PrimordialCradleBlock;
import com.github.elenterius.biomancy.world.block.decomposer.DecomposerBlock;
import com.github.elenterius.biomancy.world.block.digester.DigesterBlock;
import com.github.elenterius.biomancy.world.block.fleshkinchest.FleshkinChestBlock;
import com.github.elenterius.biomancy.world.block.mawhopper.MawHopperBlock;
import com.github.elenterius.biomancy.world.block.modularlarynx.VoiceBoxBlock;
import com.github.elenterius.biomancy.world.block.ownable.OwnableDoorBlock;
import com.github.elenterius.biomancy.world.block.ownable.OwnablePressurePlateBlock;
import com.github.elenterius.biomancy.world.block.ownable.OwnableTrapDoorBlock;
import com.github.elenterius.biomancy.world.block.property.Orientation;
import com.github.elenterius.biomancy.world.block.property.UserSensitivity;
import com.github.elenterius.biomancy.world.block.storagesac.StorageSacBlock;
import com.github.elenterius.biomancy.world.block.tongue.TongueBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BiomancyMod.MOD_ID);

	//# Plant Types
	public static final PlantType FLESH_PLANT_TYPE = PlantType.get("flesh");

	//# Materials
	public static final Material FLESH_MATERIAL = new Material.Builder(MaterialColor.COLOR_PINK).build();

	//# Block Properties
	public static final BooleanProperty CRAFTING_PROPERTY = BooleanProperty.create("crafting");
	public static final IntegerProperty CHARGE = IntegerProperty.create("charge", 0, 15);
	public static final EnumProperty<UserSensitivity> USER_SENSITIVITY_PROPERTY = EnumProperty.create("sensitivity", UserSensitivity.class);
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);

	//# Blocks

	//## Crafting
	public static final RegistryObject<PrimordialCradleBlock> PRIMORDIAL_CRADLE = BLOCKS.register("primordial_cradle", () -> new PrimordialCradleBlock(createFleshProperties()));
	public static final RegistryObject<BioForgeBlock> BIO_FORGE = BLOCKS.register("bio_forge", () -> new BioForgeBlock(createFleshProperties()));

	//## Machine
	public static final RegistryObject<DecomposerBlock> DECOMPOSER = BLOCKS.register("decomposer", () -> new DecomposerBlock(createFleshProperties().noOcclusion()));
	public static final RegistryObject<BioLabBlock> BIO_LAB = BLOCKS.register("bio_lab", () -> new BioLabBlock(createFleshProperties()));
	public static final RegistryObject<DigesterBlock> DIGESTER = BLOCKS.register("digester", () -> new DigesterBlock(createFleshProperties()));

	//## Automation & Storage
	public static final RegistryObject<StorageSacBlock> STORAGE_SAC = BLOCKS.register("storage_sac", () -> new StorageSacBlock(createFleshProperties()));
	public static final RegistryObject<TongueBlock> TONGUE = BLOCKS.register("tongue", () -> new TongueBlock(createFleshProperties()));
	public static final RegistryObject<MawHopperBlock> MAW_HOPPER = BLOCKS.register("maw_hopper", () -> new MawHopperBlock(createFleshProperties()));

	//## Ownable
	public static final RegistryObject<FleshkinChestBlock> FLESHKIN_CHEST = BLOCKS.register("fleshkin_chest", () -> new FleshkinChestBlock(createFleshProperties()));
	public static final RegistryObject<OwnableDoorBlock> FLESHKIN_DOOR = BLOCKS.register("fleshkin_door", () -> new OwnableDoorBlock(createFleshProperties()));
	public static final RegistryObject<OwnableTrapDoorBlock> FLESHKIN_TRAPDOOR = BLOCKS.register("fleshkin_trapdoor", () -> new OwnableTrapDoorBlock(createFleshProperties()));
	public static final RegistryObject<OwnablePressurePlateBlock> FLESHKIN_PRESSURE_PLATE = BLOCKS.register("fleshkin_pressure_plate", () -> new OwnablePressurePlateBlock(createFleshProperties()));

	//## Building Materials
	public static final RegistryObject<FleshBlock> FLESH = BLOCKS.register("flesh", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<StairBlock> FLESH_STAIRS = BLOCKS.register("flesh_stairs", () -> new StairBlock(() -> FLESH.get().defaultBlockState(), createFleshProperties()));
	public static final RegistryObject<DirectionalSlabBlock> FLESH_SLAB = BLOCKS.register("flesh_slab", () -> new DirectionalSlabBlock(createFleshProperties()));
	public static final RegistryObject<WallBlock> FLESH_WALL = BLOCKS.register("flesh_wall", () -> new WallBlock(createFleshProperties()));
	public static final RegistryObject<FleshBlock> PACKED_FLESH = BLOCKS.register("packed_flesh", () -> new FleshBlock(createToughFleshProperties()));
	public static final RegistryObject<StairBlock> PACKED_FLESH_STAIRS = BLOCKS.register("packed_flesh_stairs", () -> new StairBlock(() -> PACKED_FLESH.get().defaultBlockState(), createToughFleshProperties()));
	public static final RegistryObject<DirectionalSlabBlock> PACKED_FLESH_SLAB = BLOCKS.register("packed_flesh_slab", () -> new DirectionalSlabBlock(createToughFleshProperties()));
	public static final RegistryObject<WallBlock> PACKED_FLESH_WALL = BLOCKS.register("packed_flesh_wall", () -> new WallBlock(createToughFleshProperties()));
	public static final RegistryObject<FleshBlock> PRIMAL_FLESH = BLOCKS.register("primal_flesh", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<DirectionalSlabBlock> PRIMAL_FLESH_SLAB = BLOCKS.register("primal_flesh_slab", () -> new DirectionalSlabBlock(createFleshProperties()));
	public static final RegistryObject<StairBlock> PRIMAL_FLESH_STAIRS = BLOCKS.register("primal_flesh_stairs", () -> new StairBlock(() -> PRIMAL_FLESH.get().defaultBlockState(), createFleshProperties()));

	public static final RegistryObject<FleshBlock> CORRUPTED_PRIMAL_FLESH = BLOCKS.register("corrupted_primal_flesh", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<FleshBlock> MALIGNANT_FLESH = BLOCKS.register("malignant_flesh", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<DirectionalSlabBlock> MALIGNANT_FLESH_SLAB = BLOCKS.register("malignant_flesh_slab", () -> new DirectionalSlabBlock(createFleshProperties()));
	public static final RegistryObject<StairBlock> MALIGNANT_FLESH_STAIRS = BLOCKS.register("malignant_flesh_stairs", () -> new StairBlock(() -> MALIGNANT_FLESH.get().defaultBlockState(), createFleshProperties()));
	public static final RegistryObject<FleshVeinsBlock> MALIGNANT_FLESH_VEINS = BLOCKS.register("malignant_flesh_veins", () -> new FleshVeinsBlock(createFleshProperties().noCollission().noOcclusion()));
	public static final RegistryObject<FleshFenceBlock> FLESH_FENCE = BLOCKS.register("flesh_fence", () -> new FleshFenceBlock(createFleshProperties()));

	//## Misc
	public static final RegistryObject<VoiceBoxBlock> VOICE_BOX = BLOCKS.register("voice_box", () -> new VoiceBoxBlock(createFleshProperties()));
	public static final RegistryObject<LadderBlock> FLESH_LADDER = BLOCKS.register("flesh_ladder", () -> new LadderBlock(createFleshyBoneProperties().noOcclusion()));
	public static final RegistryObject<FleshFenceGateBlock> FLESH_FENCE_GATE = BLOCKS.register("flesh_fence_gate", () -> new FleshFenceGateBlock(createFleshyBoneProperties().noOcclusion()));
	public static final RegistryObject<IrisDoorBlock> FLESH_IRIS_DOOR = BLOCKS.register("flesh_iris_door", () -> new IrisDoorBlock(createFleshProperties()));
	public static final RegistryObject<FleshDoorBlock> FLESH_DOOR = BLOCKS.register("flesh_door", () -> new FleshDoorBlock(createFleshProperties()));
	public static final RegistryObject<FullFleshDoorBlock> FULL_FLESH_DOOR = BLOCKS.register("full_flesh_door", () -> new FullFleshDoorBlock(createFleshProperties()));
	public static final RegistryObject<BoneSpikeBlock> BONE_SPIKE = BLOCKS.register("bone_spike", () -> new BoneSpikeBlock(createFleshyBoneProperties()));
	public static final RegistryObject<FleshLanternBlock> BIO_LANTERN = BLOCKS.register("bio_lantern", () -> new FleshLanternBlock(createFleshProperties().sound(SoundType.SHROOMLIGHT).lightLevel(x -> 15).noOcclusion()));
	public static final RegistryObject<FleshChainBlock> TENDON_CHAIN = BLOCKS.register("tendon_chain", () -> new FleshChainBlock(createFleshProperties().noOcclusion()));

	private ModBlocks() {}

	public static BlockBehaviour.Properties copyProperties(BlockBehaviour behaviour) {
		return BlockBehaviour.Properties.copy(behaviour);
	}

	public static BlockBehaviour.Properties createFleshProperties() {
		return BlockBehaviour.Properties.of(FLESH_MATERIAL).strength(3f, 3f).sound(ModSoundTypes.FLESH_BLOCK).isValidSpawn(ModBlocks::limitEntitySpawnToFlesh);
	}

	public static BlockBehaviour.Properties createToughFleshProperties() {
		return createFleshProperties().strength(6f, 6f);
	}

	public static BlockBehaviour.Properties createFleshyBoneProperties() {
		return BlockBehaviour.Properties.of(FLESH_MATERIAL).strength(3f, 3f).sound(SoundType.BONE_BLOCK).isValidSpawn(ModBlocks::limitEntitySpawnToFlesh);
	}

	public static BlockBehaviour.Properties createGlowingPlantProperties(int i) {
		// is not replaceable nor flammable
		return BlockBehaviour.Properties.of(Material.PLANT).noCollission().strength(0.2f).sound(SoundType.GRASS).lightLevel(v -> i);
	}

	public static BlockBehaviour.Properties createFleshPlantProperties() {
		//is flammable
		return BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT, MaterialColor.COLOR_PINK).noCollission().strength(0f).sound(SoundType.SLIME_BLOCK);
	}

	public static boolean limitEntitySpawnToFlesh(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		//		entityType.getTags().contains(); //TODO: implement this
		return false;
	}

	private static boolean neverAllowSpawn(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entityType) {
		return false;
	}

}
