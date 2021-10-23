package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.ScentDiffuserTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.github.elenterius.biomancy.tileentity.ScentDiffuserTileEntity.Scent;

public class ScentDiffuserBlock extends Block {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<Scent> SCENT = EnumProperty.create("scent", Scent.class);

	public static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 16, 13);

	public ScentDiffuserBlock(Properties properties) {
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(POWERED, Boolean.FALSE).setValue(SCENT, Scent.BAIT));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POWERED, SCENT);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltips, ITooltipFlag flag) {
		super.appendHoverText(stack, level, tooltips, flag);
		tooltips.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand usedHand, BlockRayTraceResult hit) {
		if (level.isClientSide) return ActionResultType.SUCCESS;
		state = state.setValue(SCENT, state.getValue(SCENT).cycle());
		level.setBlock(pos, state, Constants.BlockFlags.DEFAULT);
		return ActionResultType.CONSUME;
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide) return;
		boolean hasSignal = level.hasNeighborSignal(pos);
		if (hasSignal != Boolean.TRUE.equals(state.getValue(POWERED))) {
			level.setBlock(pos, state.setValue(POWERED, hasSignal), Constants.BlockFlags.DEFAULT);
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
		if (id == Scent.BAIT.id) {
			for (int i = 0; i < 4; i++) {
				double x = pos.getX() + 0.25d + level.random.nextDouble() * 0.5d;
				double z = pos.getZ() + 0.25d + level.random.nextDouble() * 0.5d;
				level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, pos.getY() + 1d, z, level.random.nextDouble(), 30d + level.random.nextDouble(), level.random.nextDouble());
			}
		}
		else if (id == Scent.REPEL.id) {
			for (int i = 0; i < 4; i++) {
				float x = pos.getX() + 0.25F + level.random.nextFloat() * 0.5F;
				float z = pos.getZ() + 0.25F + level.random.nextFloat() * 0.5F;
				level.addParticle(ParticleTypes.ANGRY_VILLAGER, x, pos.getY() + 0.9d, z, 0, 0.05d, 0);
			}
		}

		return true;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState state, World level, BlockPos pos, Random random) {
		if (Boolean.FALSE.equals(state.getValue(POWERED))) return;
		level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5d, pos.getY() + 1d, pos.getZ() + 0.5d, 0, 0.07d, 0);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity tileEntity = level.getBlockEntity(pos);
			if (tileEntity instanceof ScentDiffuserTileEntity) {
				((ScentDiffuserTileEntity) tileEntity).dropAllInvContents(level, pos);
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ScentDiffuserTileEntity();
	}

}
