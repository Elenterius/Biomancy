package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BiomancyMod.MOD_ID);

	//Plant Types
	public static final PlantType FLESH_PLANT_TYPE = PlantType.get("flesh");

	//Materials
	public static final Material FLESH_MATERIAL = new Material.Builder(MaterialColor.PINK).build();

	//Properties
	public static final EnumProperty<UserSensitivity> USER_SENSITIVITY_PROPERTY = EnumProperty.create("sensitivity", UserSensitivity.class);
	public static final BooleanProperty CRAFTING_PROPERTY = BooleanProperty.create("crafting");

	//Decoration Blocks
	public static final RegistryObject<FleshPlantBlock> FLESH_TENTACLE = BLOCKS.register("flesh_tentacle", () -> new FleshPlantBlock(createFleshPlantProperties()));
//	public static final RegistryObject<BlightPlantBlock> HAIR = BLOCKS.register("hair", () -> new BlightPlantBlock(true, blighPlantProperties()));
//	public static final RegistryObject<BlightPlantBlock> OCULUS_LAMP = BLOCKS.register("oculus_lamp", () -> new BlightPlantBlock(true, blighPlantProperties()));

//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_SMALL = BLOCKS.register("blight_pustule_0", () -> new BlightPustuleBlock(glowingPlantProperties(3)));
//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_BIG = BLOCKS.register("blight_pustule_1", () -> new BlightPustuleBlock(glowingPlantProperties(9)));
//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_BIG_AND_SMALL = BLOCKS.register("blight_pustule_2", () -> new BlightPustuleBlock(glowingPlantProperties(12)));
//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_SMALL_GROUP = BLOCKS.register("blight_pustule_3", () -> new BlightPustuleBlock(glowingPlantProperties(7)));

	//Material Blocks
	public static final RegistryObject<Block> FLESH_BLOCK = BLOCKS.register("flesh_block", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<SlabBlock> FLESH_BLOCK_SLAB = BLOCKS.register("flesh_block_slab", () -> new SlabBlock(createFleshProperties()));
	public static final RegistryObject<StairsBlock> FLESH_BLOCK_STAIRS = BLOCKS.register("flesh_block_stairs", () -> new StairsBlock(() -> FLESH_BLOCK.get().getDefaultState(), createFleshProperties()));
	public static final RegistryObject<MutatedFleshBlock> MUTATED_FLESH_BLOCK = BLOCKS.register("mutated_flesh_block", () -> new MutatedFleshBlock(createFleshProperties()));
	public static final RegistryObject<Block> NECROTIC_FLESH_BLOCK = BLOCKS.register("necrotic_flesh_block", () -> new FleshBlock(createFleshProperties()));

	//Plant
	public static final RegistryObject<Block> FLESH_MELON_BLOCK = BLOCKS.register("flesh_melon_block", () -> new Block(Block.Properties.create(Material.GOURD, MaterialColor.PINK).hardnessAndResistance(1f).sound(SoundType.WOOD)));
	public static final RegistryObject<FleshMelonCropBlock> FLESH_MELON_CROP = BLOCKS.register("flesh_melon_crop", () -> new FleshMelonCropBlock(Block.Properties.create(Material.PLANTS, MaterialColor.PINK).tickRandomly().hardnessAndResistance(0.2f).sound(SoundType.STEM)));

	//Bio-Constructs
	public static final RegistryObject<OwnableDoorBlock> BIO_FLESH_DOOR = BLOCKS.register("bioflesh_door", () -> new OwnableDoorBlock(createFleshProperties().notSolid()));
	public static final RegistryObject<OwnableTrapDoorBlock> BIO_FLESH_TRAPDOOR = BLOCKS.register("bioflesh_trapdoor", () -> new OwnableTrapDoorBlock(createFleshProperties().notSolid().setAllowsSpawn(ModBlocks::neverAllowSpawn)));
	public static final RegistryObject<OwnablePressurePlateBlock> BIO_FLESH_PRESSURE_PLATE = BLOCKS.register("bioflesh_pressure_plate", () -> new OwnablePressurePlateBlock(createFleshProperties().doesNotBlockMovement()));

	//Semi-Container
	public static final RegistryObject<MeatsoupCauldronBlock> MEATSOUP_CAULDRON = BLOCKS.register("meatsoup_cauldron", () -> new MeatsoupCauldronBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(2.0F).notSolid()));

	//Containers
	public static final RegistryObject<GulgeBlock> GULGE = BLOCKS.register("gulge", () -> new GulgeBlock(createFleshProperties()));
	public static final RegistryObject<FleshChestBlock> FLESH_CHEST = BLOCKS.register("bioflesh_chest", () -> new FleshChestBlock(createFleshProperties()));

	//machine containers
	public static final RegistryObject<ChewerBlock> CHEWER = BLOCKS.register("chewer", () -> new ChewerBlock(createFleshProperties()));
	public static final RegistryObject<DecomposerBlock> DIGESTER = BLOCKS.register("digester", () -> new DecomposerBlock(createFleshProperties()));
	public static final RegistryObject<DecomposerBlock> DECOMPOSER = BLOCKS.register("decomposer", () -> new DecomposerBlock(createFleshProperties()));
	public static final RegistryObject<EvolutionPoolBlock> EVOLUTION_POOL = BLOCKS.register("evolution_pool", () -> new EvolutionPoolBlock(createFleshProperties()));

	private ModBlocks() {}

	public static AbstractBlock.Properties createFleshProperties() {
		return AbstractBlock.Properties.create(FLESH_MATERIAL).harvestTool(ToolType.SHOVEL).hardnessAndResistance(3.0F, 3.0F).sound(SoundType.SLIME).setAllowsSpawn(ModBlocks::limitEntitySpawnToFlesh);
	}

	public static Block.Properties createGlowingPlantProperties(int i) {
		return Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.2F).sound(SoundType.PLANT).setLightLevel(v -> i);
	}

	public static AbstractBlock.Properties createFleshPlantProperties() {
		return Block.Properties.create(getReplaceablePlantMat(), MaterialColor.PINK).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.SLIME);
	}

	private static Material getReplaceablePlantMat() {
		return Material.TALL_PLANTS; //is flammable
	}

	private static Material getPlantMat() {
		return Material.PLANTS; // is not replaceable nor flammable
	}

	public static boolean limitEntitySpawnToFlesh(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType) {
//		entityType.getTags().contains(); //TODO: implement this
		return false;
	}

	private static boolean neverAllowSpawn(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType) {
		return false;
	}

}
