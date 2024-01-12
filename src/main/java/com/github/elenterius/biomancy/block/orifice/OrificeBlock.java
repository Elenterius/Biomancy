package com.github.elenterius.biomancy.block.orifice;

import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.ModPlantTypes;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import java.util.Optional;
import java.util.Random;

public class OrificeBlock extends Block implements BucketPickup {

	public static final EnhancedIntegerProperty AGE = EnhancedIntegerProperty.wrap(BlockStateProperties.AGE_2);

	public OrificeBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(AGE.get(), AGE.getMin()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AGE.get());
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		PlantType type = plantable.getPlantType(world, pos.relative(facing));
		return type == ModPlantTypes.FLESH_PLANT_TYPE;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (!level.isAreaLoaded(pos, 1)) return;

		if (random.nextFloat() < 0.33f) {
			int age = AGE.getValue(state);
			if (age < AGE.getMax()) {
				level.setBlock(pos, AGE.addValue(state, 1), Block.UPDATE_CLIENTS);
			}
			else {
				if (random.nextFloat() < 0.5f) {
					BlockPos posBelow = pos.below();
					if (FallingBlock.isFree(level.getBlockState(posBelow)) && pos.getY() >= level.getMinBuildHeight()) {
						level.setBlock(pos, AGE.setValue(state, AGE.getMin()), Block.UPDATE_CLIENTS);
						float x = random.nextInt(-6, 6 + 1) / 16f;
						float z = random.nextInt(-6, 6 + 1) / 16f;
						ModProjectiles.FALLING_ACID_BLOB.shoot(level, Vec3.atBottomCenterOf(pos).add(x, 0, z), Vec3.atBottomCenterOf(posBelow).add(x, 0, z));
					}
				}
			}
		}
	}

	@Override
	public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		if (AGE.getValue(state) == AGE.getMax()) {
			level.setBlock(pos, AGE.setValue(state, AGE.getMin()), Block.UPDATE_CLIENTS);
			return new ItemStack(ModFluids.ACID.get().getBucket());
		}

		return ItemStack.EMPTY;
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return ModFluids.ACID.get().getPickupSound();
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (random.nextInt(5) == 0 && AGE.getValue(state) == AGE.getMax()) {
			Direction direction = Direction.getRandom(random);

			if (direction == Direction.UP) {
				return;
			}

			BlockPos neighborPos = pos.relative(direction);
			BlockState neighborState = level.getBlockState(neighborPos);

			if (!state.canOcclude() || !neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
				double x = direction.getStepX() == 0 ? random.nextDouble() : 0.5d + direction.getStepX() * 0.6d;
				double y = direction.getStepY() == 0 ? random.nextDouble() : 0.5d + direction.getStepY() * 0.6d;
				double z = direction.getStepZ() == 0 ? random.nextDouble() : 0.5d + direction.getStepZ() * 0.6d;
				level.addParticle(ModParticleTypes.DRIPPING_ACID.get(), pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0, 0, 0);
			}
		}
	}

}
