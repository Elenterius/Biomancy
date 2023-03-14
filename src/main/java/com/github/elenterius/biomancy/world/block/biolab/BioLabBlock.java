package com.github.elenterius.biomancy.world.block.biolab;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import com.github.elenterius.biomancy.world.block.HorizontalFacingMachineBlock;
import com.github.elenterius.biomancy.world.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BioLabBlock extends HorizontalFacingMachineBlock {

	public static final VoxelShape SHAPE_NORTH = makeShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = makeShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = makeShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = makeShape(Direction.EAST);

	public BioLabBlock(Properties properties) {
		super(properties);
	}

	private static VoxelShape makeShape(Direction direction) {
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, createBox(direction, 0.125, 0, 0.125, 0.875, 0.4375, 0.875), BooleanOp.OR);
		shape = Shapes.join(shape, createBox(direction, 0.25, 0.4375, 0.25, 0.75, 1, 0.75), BooleanOp.OR);
		shape = Shapes.join(shape, createBox(direction, 0.3125, 0.0625, 0.75, 0.6875, 0.5625, 0.9375), BooleanOp.OR);
		shape = Shapes.join(shape, createBox(direction, 0.75, 0.0625, 0.3125, 0.9375, 0.625, 0.75), BooleanOp.OR);
		shape = Shapes.join(shape, createBox(direction, 0.3125, 0.0625, 0.0625, 0.6875, 0.5625, 0.25), BooleanOp.OR);
		shape = Shapes.join(shape, createBox(direction, 0.0625, 0.0625, 0.3125, 0.25, 0.625, 0.75), BooleanOp.OR);
		return shape;
	}

	private static VoxelShape createBox(Direction direction, double x1, double y1, double z1, double x2, double y2, double z2) {
		return VoxelShapeUtil.createYRotatedTowards(direction, x1 * 16, y1 * 16, z1 * 16, x2 * 16, y2 * 16, z2 * 16);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.BIO_LAB.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.BIO_LAB.get(), MachineBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof BioLabBlockEntity bioLab && bioLab.canPlayerOpenInv(player)) {
			if (!level.isClientSide) {
				NetworkHooks.openScreen((ServerPlayer) player, bioLab, buffer -> buffer.writeBlockPos(pos));
				SoundUtil.broadcastBlockSound((ServerLevel) level, pos, ModSoundEvents.UI_BIO_LAB_OPEN);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case WEST -> SHAPE_WEST;
			case EAST -> SHAPE_EAST;
			default -> Shapes.block();
		};
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(5) == 0 && Boolean.TRUE.equals(state.getValue(CRAFTING))) {
			int particleAmount = random.nextInt(1, 5);
			int color = 0x9acd32; //yellowgreen
			double r = (color >> 16 & 255) / 255d;
			double g = (color >> 8 & 255) / 255d;
			double b = (color & 255) / 255d;
			for (int i = 0; i < particleAmount; i++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.25F), pos.getY() + 0.65d, pos.getZ() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.25F), r, g, b);
			}
			if (random.nextInt(3) == 0) {
				SoundUtil.clientPlayBlockSound(level, pos, ModSoundEvents.BIO_LAB_CRAFTING_RANDOM, 0.65f);
			}
		}
	}

}
