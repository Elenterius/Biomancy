package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BiomancyMod.MOD_ID);

	//Block related things
	public static final PlantType BLIGHT_PLANT_TYPE = PlantType.get("blight");

	//BlockProperties
	public static final EnumProperty<UserSensitivity> USER_SENSITIVITY_PROPERTY = EnumProperty.create("sensitivity", UserSensitivity.class);
	public static final BooleanProperty CRAFTING_PROPERTY = BooleanProperty.create("crafting");

	//Blocks
//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_SMALL = BLOCKS.register("blight_pustule_0", () -> new BlightPustuleBlock(glowingPlantProperties(3)));
//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_BIG = BLOCKS.register("blight_pustule_1", () -> new BlightPustuleBlock(glowingPlantProperties(9)));
//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_BIG_AND_SMALL = BLOCKS.register("blight_pustule_2", () -> new BlightPustuleBlock(glowingPlantProperties(12)));
//	public static final RegistryObject<BlightPustuleBlock> BLIGHT_PUSTULE_SMALL_GROUP = BLOCKS.register("blight_pustule_3", () -> new BlightPustuleBlock(glowingPlantProperties(7)));
//
//	public static final RegistryObject<BlightPlantBlock> HAIR = BLOCKS.register("hair", () -> new BlightPlantBlock(true, blighPlantProperties()));
//	public static final RegistryObject<BlightPlantBlock> FLESH_TENTACLE = BLOCKS.register("flesh_tentacle", () -> new BlightPlantBlock(blighPlantProperties()));

	//Materials
	public static final RegistryObject<Block> FLESH_BLOCK = BLOCKS.register("flesh_block", () -> new Block(createFleshProperties()));
	public static final RegistryObject<SlabBlock> FLESH_BLOCK_SLAB = BLOCKS.register("flesh_block_slab", () -> new SlabBlock(createFleshProperties()));
	public static final RegistryObject<MutatedFleshBlock> MUTATED_FLESH_BLOCK = BLOCKS.register("mutated_flesh_block", () -> new MutatedFleshBlock(createFleshProperties()));

	//Bio-Constructs
	public static final RegistryObject<OwnableDoorBlock> BIO_FLESH_DOOR = BLOCKS.register("bioflesh_door", () -> new OwnableDoorBlock(createFleshProperties().notSolid()));
	public static final RegistryObject<OwnableTrapDoorBlock> BIO_FLESH_TRAPDOOR = BLOCKS.register("bioflesh_trapdoor", () -> new OwnableTrapDoorBlock(createFleshProperties().notSolid().setAllowsSpawn(ModBlocks::neverAllowSpawn)));
	public static final RegistryObject<OwnablePressurePlateBlock> BIO_FLESH_PRESSURE_PLATE = BLOCKS.register("bioflesh_pressure_plate", () -> new OwnablePressurePlateBlock(createFleshProperties().doesNotBlockMovement()));

	//Semi-Container
	public static final RegistryObject<MeatsoupCauldronBlock> MEATSOUP_CAULDRON = BLOCKS.register("meatsoup_cauldron", () -> new MeatsoupCauldronBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(2.0F).notSolid()));

	//Containers
	public static final RegistryObject<GulgeBlock> GULGE = BLOCKS.register("gulge", () -> new GulgeBlock(createFleshProperties()));
	public static final RegistryObject<DecomposerBlock> DECOMPOSER = BLOCKS.register("decomposer", () -> new DecomposerBlock(createFleshProperties()));

	private ModBlocks() {}

	public static AbstractBlock.Properties createFleshProperties() {
		return AbstractBlock.Properties.create(Material.ORGANIC, MaterialColor.PINK).harvestTool(ToolType.SHOVEL).hardnessAndResistance(3.0F, 3.0F).sound(SoundType.SLIME).setAllowsSpawn(ModBlocks::limitEntitySpawnToFlesh);
	}

	public static Block.Properties glowingPlantProperties(int i) {
		return Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.2F).sound(SoundType.PLANT).setLightLevel(v -> i);
	}

	public static AbstractBlock.Properties blighPlantProperties() {
		return Block.Properties.create(Material.TALL_PLANTS).doesNotBlockMovement().setAllowsSpawn(ModBlocks::canEntitySpawn).hardnessAndResistance(0.0F).sound(SoundType.PLANT);
	}

	public static boolean limitEntitySpawnToFlesh(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType) {
//		entityType.getTags().contains(); //TODO: implement this
		return false;
	}

	private static boolean neverAllowSpawn(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType) {
		return false;
	}

	public static boolean canEntitySpawn(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType) {
		return state.isSolidSide(reader, pos, Direction.UP);
	}
}
