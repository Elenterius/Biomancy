package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.world.block.PillarPlantUtil;
import com.github.elenterius.biomancy.world.block.property.BlockPropertyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class FertilizerItem extends Item implements ICustomTooltip {

	public FertilizerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos clickedPos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();
		if (applyFertilizer(stack, level, clickedPos)) {
			if (!level.isClientSide) {
				stack.shrink(1);
				level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, clickedPos, 0);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	public static boolean applyFertilizer(ItemStack stack, Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof BonemealableBlock bonemealableBlock) {
			if (bonemealableBlock.isValidBonemealTarget(level, pos, state, level.isClientSide)) {
				if (level instanceof ServerLevel serverLevel) {
					growBonmealableBlock(serverLevel, pos, state, bonemealableBlock);
				}
				return true;
			}
		}
		else if (block == Blocks.DIRT) {
			if (level instanceof ServerLevel serverLevel) {
				growDirtIntoGrassBlock(serverLevel, pos);
			}
			return true;
		}
		else if (block instanceof ChorusFlowerBlock) {
			if (level instanceof ServerLevel serverLevel) {
				return growChorusFlower(serverLevel, pos, state);
			}
		}
		else if (PillarPlantUtil.isPillarPlant(block)) {
			return PillarPlantUtil.applyMegaGrowthBoost(level, pos, state, block);
		}
		else if (block instanceof IPlantable) { //e.g. nether wart
			if (level instanceof ServerLevel serverLevel) {
				growPlantableBlock(serverLevel, pos, state, block);
			}
		}

		return false;
	}

	private static boolean growChorusFlower(ServerLevel level, BlockPos pos, BlockState state) {
		if (state.getValue(ChorusFlowerBlock.AGE) >= 5) return false;

		BlockState stateBelow = level.getBlockState(pos.below());

		if (stateBelow.is(Blocks.END_STONE)) {
			ChorusFlowerBlock.generatePlant(level, pos, level.random, 8);
			return true;
		}

		boolean isAttachedToChorusPlant = stateBelow.is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.north()).is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.south()).is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.west()).is(Blocks.CHORUS_PLANT)
				|| level.getBlockState(pos.east()).is(Blocks.CHORUS_PLANT);

		if (isAttachedToChorusPlant) {
			ChorusFlowerBlock.generatePlant(level, pos, level.random, 8);
			return true;
		}

		return false;
	}

	private static void growDirtIntoGrassBlock(ServerLevel level, BlockPos pos) {
		BlockState stateAbove = level.getBlockState(pos.above());
		level.setBlockAndUpdate(pos, Blocks.GRASS.defaultBlockState());
		level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
	}

	private static void growPlantableBlock(ServerLevel level, BlockPos pos, BlockState state, Block block) {
		Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
		if (property.isPresent()) {
			IntegerProperty ageProperty = property.get();
			int age = state.getValue(ageProperty);
			int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
			if (age < maxAge) {
				level.setBlock(pos, state.setValue(ageProperty, maxAge), Block.UPDATE_CLIENTS);
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
			}
		}
		else if (block.isRandomlyTicking(state) && !level.getBlockTicks().willTickThisTick(pos, block)) {
			level.scheduleTick(pos, block, 2);
			level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
		}
	}

	private static void growBonmealableBlock(ServerLevel level, BlockPos pos, BlockState state, BonemealableBlock block) {
		// "power" grow plant to maturity
		Optional<IntegerProperty> property = BlockPropertyUtil.getAgeProperty(state);
		if (property.isPresent()) {
			IntegerProperty ageProperty = property.get();
			int age = state.getValue(ageProperty);
			int maxAge = BlockPropertyUtil.getMaxAge(ageProperty);
			if (age < maxAge) {
				level.setBlock(pos, state.setValue(ageProperty, maxAge), Block.UPDATE_CLIENTS);
				level.levelEvent(LevelEvent.PARTICLES_PLANT_GROWTH, pos, 5);
			}
		}
		else {
			block.performBonemeal(level, level.random, pos, state); //fall back
		}
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		ClientTextUtil.appendItemInfoTooltip(stack.getItem(), tooltip);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

}
