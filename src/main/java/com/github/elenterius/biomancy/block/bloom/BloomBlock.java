package com.github.elenterius.biomancy.block.bloom;

import com.github.elenterius.biomancy.block.base.WaterloggedFacingBlock;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModPlantTypes;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import com.github.elenterius.biomancy.util.VectorUtil;
import com.github.elenterius.biomancy.world.PrimordialEcosystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class BloomBlock extends WaterloggedFacingBlock implements IPlantable {

	public static final EnhancedIntegerProperty AGE = EnhancedIntegerProperty.wrap(BlockStateProperties.AGE_7);
	protected static final int AIM_DISTANCE = 8;

	public BloomBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(AGE.get(), AGE.getMin()));
		BloomShapes.computePossibleShapes(stateDefinition.getPossibleStates());
	}

	public static int getAge(BlockState state) {
		return AGE.getValue(state);
	}

	public static int getStage(BlockState state) {
		return getStageFromAge(AGE.getValue(state));
	}

	public static int getStageFromAge(int age) {
		int[] stages = {0, 0, 1, 1, 2, 2, 3, 4};

		age = Mth.clamp(age, 0, 7);
		return stages[age];
	}

	private static int getGrowthSpeed(ServerLevel level, BlockPos pos) {
		int veins = PrimordialEcosystem.countMalignantVeinsAroundPos(level, pos);
		int maxVeins = 3 * 3 * 3 - 1;
		return Math.max(maxVeins - veins, 0) + 1;
	}

	public static int getLightEmission(BlockState state) {
		int age = getAge(state);
		if (age < 6) return 0;
		if (age < 7) return 10;
		return 12;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AGE.get());
	}

	public BlockState getStateForPlacement(BlockGetter level, BlockPos pos, Direction direction) {
		boolean isWaterlogged = level.getFluidState(pos).getType() == Fluids.WATER;
		return defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, isWaterlogged);
	}

	public boolean hasUnobstructedAim(BlockGetter level, BlockPos pos, Direction direction) {
		return hasUnobstructedAim(level, pos, pos.relative(direction, AIM_DISTANCE));
	}

	public boolean hasUnobstructedAim(BlockGetter level, BlockPos origin, BlockPos target) {
		return level.clip(new ClipContext(Vec3.atCenterOf(origin), Vec3.atCenterOf(target), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getType() == HitResult.Type.MISS;
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isAreaLoaded(pos, 1)) return;

		Direction direction = getFacing(state);
		BlockPos relativePos = pos.relative(direction);
		BlockState relativeState = level.getBlockState(relativePos);
		if (relativeState.isSolid() || !relativeState.getCollisionShape(level, relativePos).isEmpty()) return;

		int age = AGE.getValue(state);
		if (age < AGE.getMax()) {
			int growthSpeed = age < AGE.getMax() - 1 ? getGrowthSpeed(level, pos) : 1;

			if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt(growthSpeed) == 0)) {
				level.setBlock(pos, AGE.addValue(state, 1), Block.UPDATE_CLIENTS);
				ForgeHooks.onCropsGrowPost(level, pos, state);
			}
		}
		else {
			level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
			level.setBlock(pos, AGE.setValue(state, AGE.getMin()), Block.UPDATE_CLIENTS);

			int range = 6;
			Vec3i plane = VectorUtil.axisAlignedPlane3i(direction);
			int offsetX = plane.getX() * random.nextIntBetweenInclusive(-range, range);
			int offsetY = plane.getY() * random.nextIntBetweenInclusive(-range, range);
			int offsetZ = plane.getZ() * random.nextIntBetweenInclusive(-range, range);
			BlockPos target = pos.relative(direction, AIM_DISTANCE).offset(offsetX, offsetY, offsetZ);

			ModProjectiles.BLOOMBERRY.shoot(level, Vec3.atCenterOf(pos), Vec3.atCenterOf(target));
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		int age = AGE.getValue(state);
		if (age > 5 && player.getItemInHand(hand).isEmpty()) {
			if (!level.isClientSide) {
				int count = 1 + (age > 6 ? level.random.nextInt(2) : 0);
				popResource(level, pos, new ItemStack(ModItems.BLOOMBERRY.get(), count));

				player.hurt(level.damageSources().sweetBerryBush(), 1f);

				level.playSound(null, pos, SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1f, 0.5f + level.random.nextFloat() * 0.4f);
				BlockState blockState = AGE.setValue(state, AGE.getMin());
				level.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
				level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockState));
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return super.use(state, level, pos, player, hand, hit);
	}

	@Override
	public BlockState getPlant(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() != this) return defaultBlockState();
		return state;
	}

	@Override
	public PlantType getPlantType(BlockGetter level, BlockPos pos) {
		return ModPlantTypes.PRIMAL_FLESH;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
		return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
	}

	public boolean mayPlaceOn(BlockGetter level, BlockPos pos, BlockState state, Direction facing) {
		return state.canSustainPlant(level, pos, facing, this);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockPos = pos.relative(direction.getOpposite());
		return mayPlaceOn(level, blockPos, level.getBlockState(blockPos), direction);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return BloomShapes.getBoundingShape(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return hasCollision ? BloomShapes.getCollisionShape(state) : Shapes.empty();
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return false;
	}

}
