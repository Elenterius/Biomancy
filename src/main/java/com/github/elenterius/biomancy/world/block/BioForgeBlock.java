package com.github.elenterius.biomancy.world.block;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.world.block.entity.BioForgeBlockEntity;
import com.github.elenterius.biomancy.world.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BioForgeBlock extends HorizontalFacingMachineBlock {

	public static final VoxelShape AABB = Block.box(1, 0, 1, 15, 15, 15);

	public BioForgeBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.BIO_FORGE.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.BIO_FORGE.get(), MachineBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof BioForgeBlockEntity bioForge && bioForge.canPlayerOpenInv(player)) {
			NetworkHooks.openGui(serverPlayer, bioForge, buffer -> buffer.writeBlockPos(pos));
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
		return AABB;
	}

}
