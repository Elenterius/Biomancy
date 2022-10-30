package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.block.entity.DigesterBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.MachineBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class DigesterBlock extends HorizontalFacingMachineBlock {

	public DigesterBlock(Properties properties) {
		super(properties);
	}

	protected static final VoxelShape SHAPE = createShape();

	private static VoxelShape createShape() {
		VoxelShape base = Block.box(3d, 0d, 3d, 13d, 12d, 13d);
		VoxelShape lid = Block.box(5d, 12d, 5d, 11d, 16d, 11d);
		return Shapes.join(base, lid, BooleanOp.OR);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.DIGESTER.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.DIGESTER.get(), MachineBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (level.getBlockEntity(pos) instanceof DigesterBlockEntity digester) {
			player.openMenu(digester);
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
		if (random.nextInt(5) == 0 && Boolean.TRUE.equals(state.getValue(CRAFTING))) {
			int particleAmount = random.nextInt(1, 5);
			int color = 0x867e36; //old moss green
			double r = (color >> 16 & 255) / 255d;
			double g = (color >> 8 & 255) / 255d;
			double b = (color & 255) / 255d;
			for (int i = 0; i < particleAmount; i++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5d + random.nextFloat(-0.125f, 0.125f), pos.getY() + 0.9d, pos.getZ() + 0.5d + random.nextFloat(-0.125f, 0.125f), r, g, b);
			}
			if (random.nextInt(3) == 0) {
				SoundUtil.playLocalBlockSound((ClientLevel) level, pos, ModSoundEvents.DIGESTER_CRAFTING_RANDOM);
			}
		}
	}

}
