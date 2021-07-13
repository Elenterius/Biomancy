package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

	//Fluids
	public static RegistryObject<FlowingFluidBlock> NUTRIENT_SLURRY_FLUID = BLOCKS.register("nutrient_slurry_fluid", () -> new FlowingFluidBlock(ModFluids.NUTRIENT_SLURRY, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100f).noDrops()));

	//Material Blocks
	public static final RegistryObject<Block> FLESH_BLOCK = BLOCKS.register("flesh_block", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<SlabBlock> FLESH_BLOCK_SLAB = BLOCKS.register("flesh_block_slab", () -> new SlabBlock(createFleshProperties()));
	public static final RegistryObject<StairsBlock> FLESH_BLOCK_STAIRS = BLOCKS.register("flesh_block_stairs", () -> new StairsBlock(() -> FLESH_BLOCK.get().getDefaultState(), createFleshProperties()));
	//	public static final RegistryObject<MutatedFleshBlock> MUTATED_FLESH_BLOCK = BLOCKS.register("mutated_flesh_block", () -> new MutatedFleshBlock(createFleshProperties()));
	public static final RegistryObject<Block> NECROTIC_FLESH_BLOCK = BLOCKS.register("necrotic_flesh_block", () -> new FleshBlock(createFleshProperties()));

	//Plant
//	public static final RegistryObject<Block> VILE_MELON_BLOCK = BLOCKS.register("vile_melon_block", () -> new Block(Block.Properties.create(Material.GOURD, MaterialColor.PINK).hardnessAndResistance(1f).sound(SoundType.WOOD)));
//	public static final RegistryObject<FleshMelonCropBlock> VILE_MELON_CROP = BLOCKS.register("vile_melon_crop", () -> new FleshMelonCropBlock(Block.Properties.create(Material.PLANTS, MaterialColor.PINK).tickRandomly().hardnessAndResistance(0.2f).sound(SoundType.STEM)));

	//Bio-Constructs
	public static final RegistryObject<OwnableDoorBlock> FLESHBORN_DOOR = BLOCKS.register("fleshborn_door", () -> new OwnableDoorBlock(createFleshProperties().notSolid()));
	public static final RegistryObject<OwnableTrapDoorBlock> FLESHBORN_TRAPDOOR = BLOCKS.register("fleshborn_trapdoor", () -> new OwnableTrapDoorBlock(createFleshProperties().notSolid().setAllowsSpawn(ModBlocks::neverAllowSpawn)));
	public static final RegistryObject<OwnablePressurePlateBlock> FLESHBORN_PRESSURE_PLATE = BLOCKS.register("fleshborn_pressure_plate", () -> new OwnablePressurePlateBlock(createFleshProperties().doesNotBlockMovement()));

	//Semi-Container
	public static final RegistryObject<MeatsoupCauldronBlock> MEATSOUP_CAULDRON = BLOCKS.register("meatsoup_cauldron", () -> new MeatsoupCauldronBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.STONE).setRequiresTool().hardnessAndResistance(2.0F).notSolid()));

	//Containers
	public static final RegistryObject<FleshChestBlock> FLESHBORN_CHEST = BLOCKS.register("fleshborn_chest", () -> new FleshChestBlock(createFleshProperties()));
	public static final RegistryObject<GulgeBlock> GULGE = BLOCKS.register("gulge", () -> new GulgeBlock(createFleshProperties()));

	//machine containers
	public static final RegistryObject<ChewerBlock> CHEWER = BLOCKS.register("chewer", () -> new ChewerBlock(createFleshProperties()));
	public static final RegistryObject<DigesterBlock> DIGESTER = BLOCKS.register("digester", () -> new DigesterBlock(createFleshProperties()));
	public static final RegistryObject<SolidifierBlock> SOLIDIFIER = BLOCKS.register("solidifier", () -> new SolidifierBlock(createFleshProperties()));
	public static final RegistryObject<DecomposerBlock> DECOMPOSER = BLOCKS.register("decomposer", () -> new DecomposerBlock(createFleshProperties()));
	public static final RegistryObject<EvolutionPoolBlock> EVOLUTION_POOL = BLOCKS.register("evolution_pool", () -> new EvolutionPoolBlock(createFleshProperties()));

	private ModBlocks() {}

	@OnlyIn(Dist.CLIENT)
	protected static void setRenderLayers() {
		RenderTypeLookup.setRenderLayer(FLESH_TENTACLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(FLESHBORN_DOOR.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(FLESHBORN_TRAPDOOR.get(), RenderType.getCutout());

		RenderTypeLookup.setRenderLayer(DIGESTER.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(CHEWER.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(SOLIDIFIER.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(DECOMPOSER.get(), renderType -> renderType == RenderType.getCutout() || renderType == RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(EVOLUTION_POOL.get(), RenderType.getTranslucent());

		//block with "glowing" overlay texture, also needs a overlay model see onModelBakeEvent() in ClientSetupHandler
		//RenderTypeLookup.setRenderLayer(ModBlocks.FOOBAR.get(), renderType -> renderType == RenderType.getCutout() || renderType == RenderType.getTranslucent());
	}

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
