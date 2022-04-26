package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.block.*;
import com.github.elenterius.biomancy.world.block.property.UserSensitivity;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	public static final EnumProperty<UserSensitivity> USER_SENSITIVITY_PROPERTY = EnumProperty.create("sensitivity", UserSensitivity.class);

	//# Blocks

	//## Crafting
	public static final RegistryObject<CreatorBlock> CREATOR = BLOCKS.register("creator", () -> new CreatorBlock(createFleshProperties()));
	public static final RegistryObject<BioForgeBlock> BIO_FORGE = BLOCKS.register("bio_forge", () -> new BioForgeBlock(createFleshProperties()));

	//## Machine
	public static final RegistryObject<DecomposerBlock> DECOMPOSER = BLOCKS.register("decomposer", () -> new DecomposerBlock(createFleshProperties()));
	public static final RegistryObject<BioLabBlock> BIO_LAB = BLOCKS.register("bio_lab", () -> new BioLabBlock(createFleshProperties()));
	public static final RegistryObject<DigesterBlock> DIGESTER = BLOCKS.register("digester", () -> new DigesterBlock(createFleshProperties()));

	//## Automation & Storage
	public static final RegistryObject<SacBlock> SAC = BLOCKS.register("sac", () -> new SacBlock(createFleshProperties()));
	public static final RegistryObject<TongueBlock> TONGUE = BLOCKS.register("tongue", () -> new TongueBlock(createFleshProperties()));

	//	public static final RegistryObject<GlandBlock> GLAND = BLOCKS.register("gland", () -> new GlandBlock(createFleshProperties()));
	public static final RegistryObject<GulgeBlock> GULGE = BLOCKS.register("gulge", () -> new GulgeBlock(createFleshProperties()));

	//## Ownable
	public static final RegistryObject<FleshkinChestBlock> FLESHKIN_CHEST = BLOCKS.register("fleshkin_chest", () -> new FleshkinChestBlock(createFleshProperties()));
	public static final RegistryObject<OwnableDoorBlock> FLESHKIN_DOOR = BLOCKS.register("fleshkin_door", () -> new OwnableDoorBlock(createFleshProperties()));
	public static final RegistryObject<OwnableTrapDoorBlock> FLESHKIN_TRAPDOOR = BLOCKS.register("fleshkin_trapdoor", () -> new OwnableTrapDoorBlock(createFleshProperties()));
	public static final RegistryObject<OwnablePressurePlateBlock> FLESHKIN_PRESSURE_PLATE = BLOCKS.register("fleshkin_pressure_plate", () -> new OwnablePressurePlateBlock(createFleshProperties()));

	//## Misc
	public static final RegistryObject<Block> FLESH_BLOCK = BLOCKS.register("flesh_block", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<SlabBlock> FLESH_BLOCK_SLAB = BLOCKS.register("flesh_block_slab", () -> new SlabBlock(createFleshProperties()));
	public static final RegistryObject<StairBlock> FLESH_BLOCK_STAIRS = BLOCKS.register("flesh_block_stairs", () -> new StairBlock(() -> FLESH_BLOCK.get().defaultBlockState(), createFleshProperties()));
	public static final RegistryObject<Block> NECROTIC_FLESH_BLOCK = BLOCKS.register("necrotic_flesh_block", () -> new FleshBlock(createFleshProperties()));
	public static final RegistryObject<IrisDoorBlock> FLESH_IRISDOOR = BLOCKS.register("flesh_irisdoor", () -> new IrisDoorBlock(createFleshProperties()));
	public static final RegistryObject<FleshFenceBlock> FLESH_FENCE = BLOCKS.register("flesh_fence", () -> new FleshFenceBlock(createFleshProperties()));
	public static final RegistryObject<LadderBlock> FLESH_LADDER = BLOCKS.register("flesh_ladder", () -> new LadderBlock(createFleshProperties().noOcclusion()));

	public static final RegistryObject<VoiceBoxBlock> VOICE_BOX = BLOCKS.register("voice_box", () -> new VoiceBoxBlock(createFleshProperties()));

	private ModBlocks() {}

	@OnlyIn(Dist.CLIENT)
	static void setRenderLayers() {
		ItemBlockRenderTypes.setRenderLayer(CREATOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(TONGUE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(GULGE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(FLESHKIN_CHEST.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(DECOMPOSER.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(BIO_LAB.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(BIO_FORGE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(DIGESTER.get(), RenderType.cutout());

//		ItemBlockRenderTypes.setRenderLayer(FLESHKIN_DOOR.get(), RenderType.cutout());
//		ItemBlockRenderTypes.setRenderLayer(FLESHKIN_TRAPDOOR.get(), RenderType.cutout());
//
		ItemBlockRenderTypes.setRenderLayer(VOICE_BOX.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FLESH_IRISDOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(FLESH_FENCE.get(), RenderType.cutout());

		//block with "glowing" overlay texture, also needs a overlay model see onModelBakeEvent() in ClientSetupHandler
		//ItemBlockRenderTypes.setRenderLayer(ModBlocks.FOOBAR.get(), renderType -> renderType == RenderType.getCutout() || renderType == RenderType.getTranslucent());
	}

	public static BlockBehaviour.Properties createFleshProperties() {
		return BlockBehaviour.Properties.of(FLESH_MATERIAL).strength(3f, 3f).sound(SoundType.SLIME_BLOCK).isValidSpawn(ModBlocks::limitEntitySpawnToFlesh);
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
