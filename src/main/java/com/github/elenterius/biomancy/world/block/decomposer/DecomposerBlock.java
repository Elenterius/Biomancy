package com.github.elenterius.biomancy.world.block.decomposer;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.block.HorizontalFacingMachineBlock;
import com.github.elenterius.biomancy.world.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

import java.util.stream.Stream;

public class DecomposerBlock extends HorizontalFacingMachineBlock {

	protected static final VoxelShape AABB = Stream.of(Block.box(0, 0, 0, 16, 14, 16), Block.box(1, 14, 1, 15, 17, 15), Block.box(2, 17, 2, 14, 18, 14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public DecomposerBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.DECOMPOSER.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.DECOMPOSER.get(), MachineBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof DecomposerBlockEntity decomposer && decomposer.canPlayerOpenInv(player)) {
			if (!level.isClientSide) {
				NetworkHooks.openScreen((ServerPlayer) player, decomposer, buffer -> buffer.writeBlockPos(pos));
				SoundUtil.broadcastBlockSound((ServerLevel) level, pos, ModSoundEvents.UI_DECOMPOSER_OPEN);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(5) == 0 && Boolean.TRUE.equals(state.getValue(CRAFTING))) {
			int particleAmount = random.nextInt(1, 5);
			int color = 0xc7b15d;
			double r = (color >> 16 & 255) / 255d;
			double g = (color >> 8 & 255) / 255d;
			double b = (color & 255) / 255d;
			for (int i = 0; i < particleAmount; i++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.3F), pos.getY() + 0.75d, pos.getZ() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.3F), r, g, b);
			}

			if (random.nextInt(3) == 0) {
				SoundUtil.clientPlayBlockSound(level, pos, ModSoundEvents.DECOMPOSER_CRAFTING_RANDOM, 0.65f);
			}
		}
	}

}
