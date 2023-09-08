package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.*;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlock;
import com.github.elenterius.biomancy.block.ownable.OwnablePressurePlateBlock;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.block.property.Orientation;
import com.github.elenterius.biomancy.block.property.UserSensitivity;
import com.github.elenterius.biomancy.block.veins.FleshVeinsBlock;
import com.github.elenterius.biomancy.block.vialholder.VialHolderBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {

	protected static final ResourceLocation FLESH_PARTICLE_TEXTURE = new ResourceLocation("biomancy:block/packed_flesh");
	protected static final ResourceLocation PRIMAL_PARTICLE_TEXTURE = new ResourceLocation("biomancy:block/primal_flesh");

	public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
		super(generator, BiomancyMod.MOD_ID, fileHelper);
	}

	protected ResourceLocation registryKey(Block block) {
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
	}

	protected String path(Block block) {
		return registryKey(block).getPath();
	}

	protected ResourceLocation blockAsset(ResourceLocation registryKey) {
		return new ResourceLocation(registryKey.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + registryKey.getPath());
	}

	protected ResourceLocation blockAsset(Block block) {
		ResourceLocation registryKey = registryKey(block);
		return new ResourceLocation(registryKey.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + registryKey.getPath());
	}

	protected ResourceLocation extend(ResourceLocation resourceLocation, String suffix) {
		return new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + suffix);
	}

	@Override
	protected void registerStatesAndModels() {
		final int fleshVariants = 7;
		simpleVariantBlockWithItem(ModBlocks.FLESH, fleshVariants);
		directionalSlabBlockWithItem(ModBlocks.FLESH_SLAB, ModBlocks.FLESH);
		stairsBlockWithItem(ModBlocks.FLESH_STAIRS, ModBlocks.FLESH);
		wallBlock(ModBlocks.FLESH_WALL, ModBlocks.FLESH);

		simpleBlockWithItem(ModBlocks.PACKED_FLESH);
		directionalSlabBlockWithItem(ModBlocks.PACKED_FLESH_SLAB, ModBlocks.PACKED_FLESH);
		stairsBlockWithItem(ModBlocks.PACKED_FLESH_STAIRS, ModBlocks.PACKED_FLESH);
		wallBlock(ModBlocks.PACKED_FLESH_WALL, ModBlocks.PACKED_FLESH);

		axisBlockWithItem(ModBlocks.FLESH_PILLAR);
		simpleBlockWithItem(ModBlocks.FIBROUS_FLESH);
		simpleBlockWithItem(ModBlocks.CHISELED_FLESH);
		axisBlockWithItem(ModBlocks.ORNATE_FLESH);
		axisBlockWithItem(ModBlocks.TUBULAR_FLESH_BLOCK);

		simpleBlockWithItem(ModBlocks.PRIMAL_FLESH);
		directionalSlabBlockWithItem(ModBlocks.PRIMAL_FLESH_SLAB, ModBlocks.PRIMAL_FLESH);
		stairsBlockWithItem(ModBlocks.PRIMAL_FLESH_STAIRS, ModBlocks.PRIMAL_FLESH);
		wallBlock(ModBlocks.PRIMAL_FLESH_WALL, ModBlocks.PRIMAL_FLESH);

		simpleBlockWithItem(ModBlocks.MALIGNANT_FLESH);
		directionalSlabBlockWithItem(ModBlocks.MALIGNANT_FLESH_SLAB, ModBlocks.MALIGNANT_FLESH);
		stairsBlockWithItem(ModBlocks.MALIGNANT_FLESH_STAIRS, ModBlocks.MALIGNANT_FLESH);
		wallBlock(ModBlocks.MALIGNANT_FLESH_WALL, ModBlocks.MALIGNANT_FLESH);
		veinsBlock(ModBlocks.MALIGNANT_FLESH_VEINS);

		irisDoor(ModBlocks.FLESH_IRIS_DOOR, true);
		fleshDoor(ModBlocks.FLESH_DOOR);
		fleshSpikes(ModBlocks.FLESH_SPIKE);
		translucentBlockWithItem(ModBlocks.IMPERMEABLE_MEMBRANE);
		membraneWithItem(ModBlocks.BABY_PERMEABLE_MEMBRANE);
		membraneWithItem(ModBlocks.ADULT_PERMEABLE_MEMBRANE);
		bioLantern(ModBlocks.YELLOW_BIO_LANTERN);
		bioLantern(ModBlocks.BLUE_BIO_LANTERN);
		bioLantern(ModBlocks.PRIMORDIAL_BIO_LANTERN);
		tendonChain(ModBlocks.TENDON_CHAIN);
		vialHolder(ModBlocks.VIAL_HOLDER);

		geckolibModel(ModBlocks.PRIMORDIAL_CRADLE, PRIMAL_PARTICLE_TEXTURE);
		geoBlockItem(ModBlocks.PRIMORDIAL_CRADLE, new Vector3f(16, 16, 16));

		geckolibModel(ModBlocks.DECOMPOSER, FLESH_PARTICLE_TEXTURE);
		geckolibModel(ModBlocks.BIO_FORGE, FLESH_PARTICLE_TEXTURE);
		geckolibModel(ModBlocks.BIO_LAB, FLESH_PARTICLE_TEXTURE);
		geckolibModel(ModBlocks.DIGESTER, FLESH_PARTICLE_TEXTURE);

		geckolibModel(ModBlocks.MAW_HOPPER, FLESH_PARTICLE_TEXTURE);
		geckolibModel(ModBlocks.TONGUE, FLESH_PARTICLE_TEXTURE);
		geckolibModel(ModBlocks.FLESHKIN_CHEST, FLESH_PARTICLE_TEXTURE);
		fleshkinPressurePlate(ModBlocks.FLESHKIN_PRESSURE_PLATE);
		storageSac(ModBlocks.STORAGE_SAC);
	}

	public <T extends Block> void translucentBlockWithItem(RegistryObject<T> block) {
		translucentBlockWithItem(block.get());
	}

	public <T extends FleshChainBlock> void tendonChain(RegistryObject<T> block) {
		tendonChain(block.get());
	}

	public <T extends FleshLanternBlock> void bioLantern(RegistryObject<T> block) {
		bioLantern(block.get());
	}

	public void stairsBlockWithItem(RegistryObject<StairBlock> block, RegistryObject<FleshBlock> textureBlock) {
		stairsBlockWithItem(block.get(), textureBlock.get());
	}

	public void stairsBlockWithItem(StairBlock block, Block textureBlock) {
		stairsBlock(block, blockAsset(textureBlock));
		simpleBlockItem(block);
	}

	public <T extends RotatedPillarBlock> void axisBlockWithItem(RegistryObject<T> block) {
		axisBlockWithItem(block.get());
	}

	public void axisBlockWithItem(RotatedPillarBlock block) {
		axisBlock(block);
		simpleBlockItem(block);
	}

	public void vialHolder(VialHolderBlock block) {
		ResourceLocation baseModel = blockAsset(block);
		ModelFile.ExistingModelFile frameModel = models().getExistingFile(extend(baseModel, "_frame"));

		DirectionProperty facingProperty = BlockStateProperties.HORIZONTAL_FACING;

		MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

		facingProperty.getPossibleValues().forEach(direction -> {
			int rotY = (((int) direction.toYRot()) + 180) % 360;
			builder.part().modelFile(frameModel).rotationY(rotY).addModel().condition(facingProperty, direction).end();
		});

		for (BooleanProperty vialProperty : VialHolderBlock.getVialProperties()) {
			ModelFile.ExistingModelFile vialModel = models().getExistingFile(extend(baseModel, "_" + vialProperty.getName()));

			facingProperty.getPossibleValues().forEach(direction -> {
				int rotY = (((int) direction.toYRot()) + 180) % 360;
				builder.part().modelFile(vialModel).rotationY(rotY).addModel().condition(facingProperty, direction).condition(vialProperty, true).end();
			});
		}

		itemModels().getBuilder(path(block)).parent(frameModel);
	}

	public <T extends OwnablePressurePlateBlock> void fleshkinPressurePlate(RegistryObject<T> pressurePlate) {
		fleshkinPressurePlate(pressurePlate.get());
	}

	public void fleshkinPressurePlate(OwnablePressurePlateBlock block) {
		String path = path(block);
		ResourceLocation baseTexture = blockAsset(block);

		BlockModelBuilder pressurePlateModel = createPressurePlateModel(path, baseTexture, UserSensitivity.FRIENDLY);

		getVariantBuilder(block)
				.partialState()
				.with(PressurePlateBlock.POWERED, true).with(OwnablePressurePlateBlock.USER_SENSITIVITY, UserSensitivity.FRIENDLY)
				.addModels(new ConfiguredModel(createPressurePlateDownModel(path, baseTexture, UserSensitivity.FRIENDLY)))
				.partialState()
				.with(PressurePlateBlock.POWERED, false).with(OwnablePressurePlateBlock.USER_SENSITIVITY, UserSensitivity.FRIENDLY)
				.addModels(new ConfiguredModel(pressurePlateModel))
				.partialState()
				.with(PressurePlateBlock.POWERED, true).with(OwnablePressurePlateBlock.USER_SENSITIVITY, UserSensitivity.HOSTILE)
				.addModels(new ConfiguredModel(createPressurePlateDownModel(path, baseTexture, UserSensitivity.HOSTILE)))
				.partialState()
				.with(PressurePlateBlock.POWERED, false).with(OwnablePressurePlateBlock.USER_SENSITIVITY, UserSensitivity.HOSTILE)
				.addModels(new ConfiguredModel(createPressurePlateModel(path, baseTexture, UserSensitivity.HOSTILE)))
				.partialState()
				.with(OwnablePressurePlateBlock.USER_SENSITIVITY, UserSensitivity.NONE)
				.addModels(new ConfiguredModel(pressurePlateModel));

		simpleBlockItem(block, pressurePlateModel);
	}

	public BlockModelBuilder createPressurePlateModel(String path, ResourceLocation baseTexture, UserSensitivity sensitivity) {
		return models().pressurePlate(path + "_" + sensitivity.getSerializedName(), extend(baseTexture, "_" + sensitivity.getSerializedName()));
	}

	public BlockModelBuilder createPressurePlateDownModel(String path, ResourceLocation baseTexture, UserSensitivity sensitivity) {
		return models().pressurePlateDown(path + "_" + sensitivity.getSerializedName() + "_down", extend(baseTexture, "_" + sensitivity.getSerializedName() + "_down"));
	}

	public void simpleBlockItem(Block block) {
		String path = path(block);
		itemModels().getBuilder(path).parent(models().getBuilder(path));
	}

	public <W extends WallBlock, B extends Block> void wallBlock(RegistryObject<W> wallBlock, RegistryObject<B> textureBlock) {
		wallBlock(wallBlock.get(), textureBlock.get());
	}

	public void wallBlock(WallBlock block, Block textureBlock) {
		ResourceLocation texture = blockAsset(textureBlock);
		wallBlock(block, texture);
	}

	public <T extends Block> void simpleBlockWithItem(RegistryObject<T> block) {
		simpleBlockWithItem(block.get());
	}

	public void simpleBlockWithItem(Block block) {
		ModelFile model = cubeAll(block);
		simpleBlock(block, model);
		simpleBlockItem(block, model);
	}

	public void translucentBlockWithItem(Block block) {
		BlockModelBuilder modelBuilder = models()
				.cubeAll(path(block), blockAsset(block))
				.renderType("translucent");
		simpleBlock(block, modelBuilder);
		simpleBlockItem(block, modelBuilder);
	}

	public <T extends Block> void simpleVariantBlockWithItem(RegistryObject<T> block, int variants) {
		simpleVariantBlockWithItem(block.get(), variants);
	}

	public void simpleVariantBlockWithItem(Block block, int variants) {
		String path = path(block);
		ResourceLocation texture = blockAsset(block);

		ModelFile mainModel = models().cubeAll(path, texture);
		simpleBlockItem(block, mainModel);

		ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(mainModel).weight(2); //make main model more frequent than the variants

		for (int i = 1; i < variants; i++) {
			String suffix = "_" + i;
			BlockModelBuilder modelVariant = models().cubeAll(path + suffix, extend(texture, suffix));
			builder = builder.nextModel().modelFile(modelVariant).weight(1);
		}

		getVariantBuilder(block).partialState().setModels(builder.build());
	}

	public void existingBlock(Block block) {
		existingBlock(block, blockAsset(block));
	}

	public void existingBlock(Block block, ResourceLocation existingModel) {
		ModelFile.ExistingModelFile modelFile = models().getExistingFile(existingModel);
		simpleBlock(block, modelFile);
	}

	public <T extends Block> void existingBlockWithItem(RegistryObject<T> block) {
		existingBlockWithItem(block.get());
	}

	public void existingBlockWithItem(Block block) {
		ModelFile.ExistingModelFile existingModel = models().getExistingFile(blockAsset(block));
		simpleBlock(block, existingModel);
		simpleBlockItem(block, existingModel);
	}

	public void directionalBlockWithItem(Block block) {
		ModelFile.ExistingModelFile existingModel = models().getExistingFile(blockAsset(block));
		directionalBlock(block, blockState -> existingModel, BlockStateProperties.WATERLOGGED);
		simpleBlockItem(block, existingModel);
	}

	public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, Property<?>... ignored) {
		getVariantBuilder(block)
				.forAllStatesExcept(blockState -> {
					Direction direction = blockState.getValue(BlockStateProperties.FACING);
					int rotX = direction == Direction.DOWN ? 180 : 0;
					int rotY = 0;

					if (direction.getAxis().isHorizontal()) {
						rotX = 90;
						rotY = ((int) direction.toYRot()) + 180;
					}

					return ConfiguredModel.builder()
							.modelFile(modelFunc.apply(blockState))
							.rotationX(rotX)
							.rotationY(rotY % 360)
							.build();
				}, ignored);
	}

	public void fleshSpikes(Block block) {
		ResourceLocation model = blockAsset(block);
		ModelFile.ExistingModelFile[] models = {
				models().getExistingFile(extend(model, "_1")),
				models().getExistingFile(extend(model, "_2")),
				models().getExistingFile(extend(model, "_3"))
		};
		directionalBlock(block, blockState -> models[FleshSpikeBlock.getSpikes(blockState) - 1], BlockStateProperties.WATERLOGGED);

		itemModels().basicItem(block.asItem());
	}

	public <T extends Block> void storageSac(RegistryObject<T> block) {
		storageSac(block.get());
	}

	public void storageSac(Block block) {
		ModelFile.ExistingModelFile existingModel = models().getExistingFile(blockAsset(block));
		directionalBlock(block, blockState -> existingModel, BlockStateProperties.WATERLOGGED);
		simpleBlockItem(block, existingModel);
	}

	public <T extends Block> void geoBlockItem(RegistryObject<T> block, Vector3f modelBounds) {
		geoBlockItem(block.get(), modelBounds);
	}

	public void geoBlockItem(Block block, Vector3f modelBounds) {
		String path = path(block);

		float xMul = modelBounds.x() <= 1e-5f ? 0 : 16 / modelBounds.x();
		float yMul = modelBounds.y() <= 1e-5f ? 0 : 16 / modelBounds.y();
		float zMul = modelBounds.z() <= 1e-5f ? 0 : 16 / modelBounds.z();
		float scaleMultiplier = Math.max(Math.max(xMul, yMul), zMul);
		float xPct = modelBounds.x() / 16;
		float yPct = modelBounds.y() / 16;
		float zPct = modelBounds.z() / 16;

		float scale1P = 0.4f;
		float scale3P = 0.375f;
		float scaleGUI = 0.625f;
		float scaleFixed = 0.5f;
		float scaleGround = 0.25f;
		int scaleHead = 1;
		float translation3P = 2.5f;

		itemModels().getBuilder(path).parent(new ModelFile.UncheckedModelFile(new ResourceLocation("builtin/entity")))
				.transforms()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND).rotation(0, 45, 0).translation(0, (1 - yPct) * 6.5f, 0).scale(scale1P * scaleMultiplier).end()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND).rotation(0, 225, 0).translation(0, (1 - yPct) * 6.5f, 0).scale(scale1P * scaleMultiplier).end()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND).rotation(75, 45, 0).translation(0, (1 - zPct) * translation3P, (1 - yPct) * translation3P).scale(scale3P * scaleMultiplier).end()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND).rotation(75, 45, 0).translation(0, (1 - zPct) * translation3P, (1 - yPct) * translation3P).scale(scale3P * scaleMultiplier).end()
				.transform(ItemTransforms.TransformType.GUI).rotation(30, 225, 0).translation(0, -4.25f * yPct, 0).scale(scaleGUI * scaleMultiplier).end()
				.transform(ItemTransforms.TransformType.FIXED).translation(0, -4f * yPct, 0).scale(scaleFixed * scaleMultiplier).end()
				.transform(ItemTransforms.TransformType.GROUND).translation(0, -0.25f * yPct, 0).scale(scaleGround * scaleMultiplier).end()
				.transform(ItemTransforms.TransformType.HEAD).translation(0, -8 * yPct, 0).scale(scaleHead * scaleMultiplier).end();
	}

	public <T extends Block> void geckolibModel(RegistryObject<T> geoBlock, ResourceLocation particleTexture) {
		geckolibModel(geoBlock.get(), particleTexture);
	}

	public void geckolibModel(Block block, ResourceLocation particleTexture) {
		String path = path(block);
		simpleBlock(block, models().getBuilder(path).texture("particle", particleTexture));
	}

	public <S extends DirectionalSlabBlock, B extends Block> void directionalSlabBlockWithItem(RegistryObject<S> slab, RegistryObject<B> fullBlock) {
		directionalSlabBlockWithItem(slab.get(), fullBlock.get());
	}

	public void directionalSlabBlockWithItem(DirectionalSlabBlock slab, Block fullBlock) {
		ResourceLocation fullModel = blockAsset(fullBlock);
		ResourceLocation texture = blockAsset(fullBlock);
		directionalSlabBlockWithItem(slab, fullModel, texture);
	}

	public void directionalSlabBlockWithItem(DirectionalSlabBlock block, ResourceLocation existingModelFull, ResourceLocation texture) {
		directionalSlabBlock(block, existingModelFull, texture, texture, texture);
		simpleBlockItem(block);
	}

	public void directionalSlabBlock(DirectionalSlabBlock block, ResourceLocation existingModelFull, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		directionalSlabBlock(block, models().slab(path(block), side, bottom, top), models().getExistingFile(existingModelFull));
	}

	public void directionalSlabBlock(DirectionalSlabBlock block, ModelFile half, ModelFile full) {
		getVariantBuilder(block)
				.forAllStatesExcept(
						state -> {
							DirectionalSlabType type = state.getValue(DirectionalSlabBlock.TYPE);
							Direction facing = type.getFacing();
							if (type != DirectionalSlabType.FULL) {
								int xRotation = 0;
								if (facing == Direction.DOWN) xRotation = 180;
								else if (facing.getAxis().isHorizontal()) xRotation = 90;

								int yRotation = facing.getAxis().isVertical() ? 0 : ((int) facing.toYRot() + 180) % 360;

								return ConfiguredModel.builder()
										.modelFile(half)
										.rotationX(xRotation)
										.rotationY(yRotation)
										.build();
							}

							return ConfiguredModel.builder().modelFile(full).build();
						},
						DirectionalSlabBlock.WATERLOGGED
				);
	}

	public void tendonChain(FleshChainBlock block) {
		ResourceLocation file = blockAsset(block);
		ModelFile.ExistingModelFile model = models().getExistingFile(file);

		getVariantBuilder(block)
				.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
				.modelForState().modelFile(model).addModel()
				.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
				.modelForState().modelFile(model).rotationX(90).addModel()
				.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
				.modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();

		itemModels().basicItem(block.asItem());
	}

	public void membraneWithItem(RegistryObject<MembraneBlock> registryObject) {
		membraneWithItem(registryObject.get());
	}

	public void membraneWithItem(MembraneBlock block) {
		String path = path(block);
		//		ResourceLocation texture = blockAsset(block);
		ResourceLocation template = BiomancyMod.createRL("block/template_membrane");

		ModelFile model = models()
				.withExistingParent(path, template)
				.texture("base", BiomancyMod.createRL("block/membrane_base"))
				.texture("overlay", BiomancyMod.createRL("block/membrane_overlay"));

		simpleBlock(block, model);
		simpleBlockItem(block, model);
	}

	public void bioLantern(FleshLanternBlock block) {
		String path = path(block);
		ResourceLocation texture = blockAsset(block);
		ResourceLocation template = BiomancyMod.createRL("block/template_bio_lantern");

		ModelFile model = models().singleTexture(path, template, texture).renderType("cutout");
		ModelFile hangingModel = models().singleTexture(path + "_hanging", extend(template, "_hanging"), texture).renderType("cutout");

		getVariantBuilder(block)
				.forAllStatesExcept(
						state -> ConfiguredModel.builder().modelFile(Boolean.TRUE.equals(state.getValue(FleshLanternBlock.HANGING)) ? hangingModel : model).build(),
						FleshLanternBlock.WATERLOGGED
				);

		simpleBlockItem(block, hangingModel);
	}

	public void veinsBlock(MultifaceBlock block) {
		String name = path(block);
		ModelFile model = models().singleTexture(name, BiomancyMod.createRL("block/template_veins"), blockAsset(block));

		MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

		Collection<BooleanProperty> properties = PipeBlock.PROPERTY_BY_DIRECTION.values();

		PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, property) -> {
			if (direction.getAxis().isHorizontal()) {
				int rotY = (((int) direction.toYRot()) + 180) % 360;

				builder.part().modelFile(model)
						.rotationY(rotY).uvLock(true).addModel()
						.condition(property, true)
						.end();

				MultiPartBlockStateBuilder.PartBuilder partBuilder = builder.part().modelFile(model)
						.rotationY(rotY).uvLock(true).addModel();
				properties.forEach(p -> partBuilder.condition(p, false));
				partBuilder.end();
			}
			else if (direction.getAxis().isVertical()) {
				int rotX = direction == Direction.UP ? 270 : 90;

				builder.part().modelFile(model)
						.rotationX(rotX).uvLock(true).addModel()
						.condition(property, true)
						.end();

				MultiPartBlockStateBuilder.PartBuilder partBuilder = builder.part().modelFile(model)
						.rotationX(rotX).uvLock(true).addModel();
				properties.forEach(p -> partBuilder.condition(p, false));
				partBuilder.end();
			}
		});
	}

	public void irisDoor(IrisDoorBlock block, boolean simpleBlockItem) {
		ResourceLocation texture = blockAsset(block);
		String name = path(block);

		ModelFile openModel = models().singleTexture(name + "_open", BiomancyMod.createRL("block/template_iris_door"), extend(texture, "_open"));
		ModelFile middleOpenModel = models().singleTexture(name + "_middle_open", BiomancyMod.createRL("block/template_iris_door_middle"), extend(texture, "_open"));
		ModelFile closedModel = models().singleTexture(name + "_closed", BiomancyMod.createRL("block/template_iris_door"), extend(texture, "_closed"));
		ModelFile middleClosedModel = models().singleTexture(name + "_middle_closed", BiomancyMod.createRL("block/template_iris_door_middle"), extend(texture, "_closed"));

		irisDoor(block, openModel, closedModel, middleOpenModel, middleClosedModel);

		if (simpleBlockItem) simpleBlockItem(block, middleClosedModel);
	}

	public void irisDoor(IrisDoorBlock block, ModelFile open, ModelFile closed, ModelFile middleOpen, ModelFile middleClosed) {
		getVariantBuilder(block)
				.forAllStatesExcept(state -> {
					boolean isOpen = state.getValue(IrisDoorBlock.OPEN);
					Orientation orientation = state.getValue(IrisDoorBlock.ORIENTATION);
					ModelFile openModel = orientation.isMiddle() ? middleOpen : open;
					ModelFile closedModel = orientation.isMiddle() ? middleClosed : closed;
					ModelFile model = isOpen ? openModel : closedModel;

					if (orientation.axis == Direction.Axis.Y) {
						return ConfiguredModel.builder()
								.modelFile(model)
								.rotationX(orientation.isNegative() ? 270 : 90)
								.build();
					}

					if (orientation.axis == Direction.Axis.X) {
						return ConfiguredModel.builder()
								.modelFile(model)
								.rotationY(orientation.isNegative() ? 270 : 90)
								.build();
					}

					//z axis
					return ConfiguredModel.builder()
							.modelFile(model)
							.rotationY(orientation.isPositive() ? 180 : 0)
							.build();
				}, IrisDoorBlock.POWERED, IrisDoorBlock.WATERLOGGED);
	}

	public void fleshDoor(FleshDoorBlock block) {
		ModelFile.ExistingModelFile bottomModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom"));
		ModelFile.ExistingModelFile bottomOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom_open"));
		ModelFile.ExistingModelFile bottomMiddleModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom_middle"));
		ModelFile.ExistingModelFile bottomMiddleOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom_middle_open"));
		ModelFile.ExistingModelFile topModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top"));
		ModelFile.ExistingModelFile topOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top_open"));
		ModelFile.ExistingModelFile topMiddleModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top_middle"));
		ModelFile.ExistingModelFile topMiddleOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top_middle_open"));

		getVariantBuilder(block)
				.forAllStatesExcept(state -> {
					boolean isOpen = block.isOpen(state);
					boolean isLowerHalf = block.isLowerHalf(state);
					Orientation orientation = state.getValue(FleshDoorBlock.ORIENTATION);

					ModelFile model;
					if (isLowerHalf) {
						ModelFile openModel = orientation.isMiddle() ? bottomMiddleOpenModel : bottomOpenModel;
						ModelFile closedModel = orientation.isMiddle() ? bottomMiddleModel : bottomModel;
						model = isOpen ? openModel : closedModel;
					}
					else {
						ModelFile openModel = orientation.isMiddle() ? topMiddleOpenModel : topOpenModel;
						ModelFile closedModel = orientation.isMiddle() ? topMiddleModel : topModel;
						model = isOpen ? openModel : closedModel;
					}

					if (orientation.axis == Direction.Axis.Y) {
						return ConfiguredModel.builder()
								.modelFile(model)
								.rotationX(orientation.isNegative() ? 270 : 90)
								.build();
					}

					if (orientation.axis == Direction.Axis.X) {
						return ConfiguredModel.builder()
								.modelFile(model)
								.rotationY(orientation.isNegative() ? 270 : 90)
								.build();
					}

					//z axis
					return ConfiguredModel.builder()
							.modelFile(model)
							.rotationY(orientation.isPositive() ? 180 : 0)
							.build();

				}, FleshDoorBlock.POWERED, FleshDoorBlock.HINGE, FleshDoorBlock.FACING);
	}

	public <T extends FleshSpikeBlock> void fleshSpikes(RegistryObject<T> block) {
		fleshSpikes(block.get());
	}

	public <T extends FleshDoorBlock> void fleshDoor(RegistryObject<T> block) {
		fleshDoor(block.get());
	}

	public <T extends IrisDoorBlock> void irisDoor(RegistryObject<T> block, boolean simpleBlockItem) {
		irisDoor(block.get(), simpleBlockItem);
	}

	public <T extends FleshVeinsBlock> void veinsBlock(RegistryObject<T> block) {
		veinsBlock(block.get());
	}

	public <T extends VialHolderBlock> void vialHolder(RegistryObject<T> block) {
		vialHolder(block.get());
	}
}
