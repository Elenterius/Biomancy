package com.github.elenterius.biomancy.datagen.models;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.*;
import com.github.elenterius.biomancy.block.property.DirectionalSlabType;
import com.github.elenterius.biomancy.block.property.Orientation;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {

	protected static final ResourceLocation FLESH_PARTICLE_TEXTURE = new ResourceLocation("biomancy:block/packed_flesh");
	protected static final ResourceLocation PRIMAL_PARTICLE_TEXTURE = new ResourceLocation("biomancy:block/primal_flesh");

	public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
		super(generator, BiomancyMod.MOD_ID, fileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlockWithItem(ModBlocks.FLESH.get());
		directionalSlabBlockWithItem(ModBlocks.FLESH_SLAB.get(), ModBlocks.FLESH.get());
		stairsBlock(ModBlocks.FLESH_STAIRS.get(), blockTexture(ModBlocks.FLESH.get()));
		simpleBlockItem(ModBlocks.FLESH_STAIRS.get());
		wallBlock(ModBlocks.FLESH_WALL.get(), ModBlocks.FLESH.get());

		simpleBlockWithItem(ModBlocks.PACKED_FLESH.get());
		directionalSlabBlockWithItem(ModBlocks.PACKED_FLESH_SLAB.get(), ModBlocks.PACKED_FLESH.get());
		stairsBlock(ModBlocks.PACKED_FLESH_STAIRS.get(), blockTexture(ModBlocks.PACKED_FLESH.get()));
		simpleBlockItem(ModBlocks.PACKED_FLESH_STAIRS.get());
		wallBlock(ModBlocks.PACKED_FLESH_WALL.get(), ModBlocks.PACKED_FLESH.get());

		simpleBlockWithItem(ModBlocks.PRIMAL_FLESH.get());
		directionalSlabBlockWithItem(ModBlocks.PRIMAL_FLESH_SLAB.get(), ModBlocks.PRIMAL_FLESH.get());
		stairsBlock(ModBlocks.PRIMAL_FLESH_STAIRS.get(), blockTexture(ModBlocks.PRIMAL_FLESH.get()));
		simpleBlockItem(ModBlocks.PRIMAL_FLESH_STAIRS.get());
		existingBlockWithItem(ModBlocks.CORRUPTED_PRIMAL_FLESH.get());

		simpleBlockWithItem(ModBlocks.MALIGNANT_FLESH.get());
		directionalSlabBlockWithItem(ModBlocks.MALIGNANT_FLESH_SLAB.get(), ModBlocks.MALIGNANT_FLESH.get());
		stairsBlock(ModBlocks.MALIGNANT_FLESH_STAIRS.get(), blockTexture(ModBlocks.MALIGNANT_FLESH.get()));
		simpleBlockItem(ModBlocks.MALIGNANT_FLESH_STAIRS.get());
		veinsBlock(ModBlocks.MALIGNANT_FLESH_VEINS.get());

		irisDoor(ModBlocks.FLESH_IRIS_DOOR.get(), true);
		fleshDoor();

		axisBlock(ModBlocks.FLESH_PILLAR.get());
		simpleBlockItem(ModBlocks.FLESH_PILLAR.get());

		storageSac(ModBlocks.STORAGE_SAC.get());

		geckolibModel(ModBlocks.FLESH_SPIKE.get());

		bioLantern(ModBlocks.BIO_LANTERN.get());
		tendonChain(ModBlocks.TENDON_CHAIN.get());
	}

	public ResourceLocation blockModel(Block block) {
		ResourceLocation rl = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
		return new ResourceLocation(rl.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + rl.getPath());
	}

	public ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}

	private String path(Block block) {
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
	}

	public void simpleBlockItem(Block block) {
		String path = path(block);
		itemModels().getBuilder(path).parent(models().getBuilder(path));
	}

	public void wallBlock(WallBlock block, Block textureBlock) {
		ResourceLocation texture = blockTexture(textureBlock);
		wallBlock(block, texture);
	}

	public void simpleBlockWithItem(Block block) {
		ModelFile model = cubeAll(block);
		simpleBlock(block, model);
		simpleBlockItem(block, model);
	}

	public void existingBlock(Block block) {
		existingBlock(block, blockModel(block));
	}

	public void existingBlock(Block block, ResourceLocation existingModel) {
		ModelFile.ExistingModelFile modelFile = models().getExistingFile(existingModel);
		simpleBlock(block, modelFile);
	}

	public void existingBlockWithItem(Block block) {
		ModelFile.ExistingModelFile existingModel = models().getExistingFile(blockModel(block));
		simpleBlock(block, existingModel);
		simpleBlockItem(block, existingModel);
	}

	public void directionalBlockWithItem(Block block) {
		ModelFile.ExistingModelFile existingModel = models().getExistingFile(blockModel(block));
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
		ResourceLocation path = blockModel(block);
		ModelFile.ExistingModelFile[] models = {
				models().getExistingFile(extend(path, "_1")),
				models().getExistingFile(extend(path, "_2")),
				models().getExistingFile(extend(path, "_3"))
		};
		directionalBlock(block, blockState -> models[FleshSpikeBlock.getSpikes(blockState) - 1], BlockStateProperties.WATERLOGGED);

		simpleBlockItem(block, models[0]);
	}

	private void geoBlockItem(Block block, Vector3f modelBounds) {
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

	public void geckolibModel(Block block, ResourceLocation particleTexture) {
		String path = path(block);
		simpleBlock(block, models().getBuilder(path).texture("particle", particleTexture));
	}

	public void directionalSlabBlockWithItem(DirectionalSlabBlock slab, Block block) {
		directionalSlabBlockWithItem(slab, blockModel(block), blockTexture(block));
	}

	public void directionalSlabBlockWithItem(DirectionalSlabBlock block, ResourceLocation full, ResourceLocation texture) {
		directionalSlabBlock(block, full, texture, texture, texture);
		simpleBlockItem(block);
	}

	public void directionalSlabBlock(DirectionalSlabBlock block, ResourceLocation full, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		directionalSlabBlock(block, models().slab(path(block), side, bottom, top), models().getExistingFile(full));
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

	public void storageSac(Block block) {
		ModelFile.ExistingModelFile existingModel = models().getExistingFile(blockModel(block));
		getVariantBuilder(block)
				.forAllStates(state -> {
					Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
					return ConfiguredModel.builder()
							.modelFile(existingModel)
							.rotationY(((int) dir.toYRot() + 180) % 360)
							.build();
				});

		simpleBlockItem(block, existingModel);
	}

	public void tendonChain(FleshChainBlock block) {
		ResourceLocation file = blockModel(block);
		ModelFile.ExistingModelFile model = models().getExistingFile(file);

		getVariantBuilder(block)
				.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
				.modelForState().modelFile(model).addModel()
				.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
				.modelForState().modelFile(model).rotationX(90).addModel()
				.partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
				.modelForState().modelFile(model).rotationX(90).rotationY(90).addModel();

		simpleBlockItem(block, model);
	}

	public void bioLantern(FleshLanternBlock block) {
		ResourceLocation file = blockModel(block);
		ModelFile.ExistingModelFile model = models().getExistingFile(file);
		ModelFile.ExistingModelFile hangingModel = models().getExistingFile(new ResourceLocation(file.getNamespace(), file.getPath() + "_hanging"));

		getVariantBuilder(block)
				.forAllStatesExcept(
						state -> ConfiguredModel.builder().modelFile(Boolean.TRUE.equals(state.getValue(FleshLanternBlock.HANGING)) ? hangingModel : model).build(),
						FleshLanternBlock.WATERLOGGED
				);

		simpleBlockItem(block, hangingModel);
	}

	public void veinsBlock(MultifaceBlock block) {
		String name = path(block);
		ModelFile model = models().singleTexture(name, BiomancyMod.createRL("block/template_veins"), blockTexture(block));

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
		ResourceLocation texture = blockTexture(block);
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

	private void fleshDoor() {
		ModelFile.ExistingModelFile bottomModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom"));
		ModelFile.ExistingModelFile bottomOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom_open"));
		ModelFile.ExistingModelFile bottomMiddleModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom_middle"));
		ModelFile.ExistingModelFile bottomMiddleOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_bottom_middle_open"));
		ModelFile.ExistingModelFile topModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top"));
		ModelFile.ExistingModelFile topOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top_open"));
		ModelFile.ExistingModelFile topMiddleModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top_middle"));
		ModelFile.ExistingModelFile topMiddleOpenModel = models().getExistingFile(BiomancyMod.createRL("block/flesh_door_top_middle_open"));

		FleshDoorBlock block = ModBlocks.FLESH_DOOR.get();
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

}
