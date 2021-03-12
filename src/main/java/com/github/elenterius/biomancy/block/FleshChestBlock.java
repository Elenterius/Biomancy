package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.FleshChestTileEntity;
import com.github.elenterius.biomancy.util.TooltipUtil;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class FleshChestBlock extends OwnableContainerBlock implements IWaterLoggable {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE = Block.makeCuboidShape(1d, 0d, 1d, 15d, 14d, 15d);

	public FleshChestBlock(Properties builder) {
		super(builder);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stackIn, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stackIn, worldIn, tooltip, flagIn);

		CompoundNBT nbt = stackIn.getChildTag("BlockEntityTag");
		if (nbt != null) {

			CompoundNBT invNbt = nbt.getCompound("Inventory");
			if (!invNbt.isEmpty()) {
				if (invNbt.contains("Items", 9)) {
					int size = invNbt.contains("Size") ? invNbt.getInt("Size") : FleshChestTileEntity.INV_SLOTS_COUNT;
					NonNullList<ItemStack> itemList = NonNullList.withSize(size, ItemStack.EMPTY);
					ItemStackHelper.loadAllItems(invNbt, itemList);
					int count = 0;
					int totalCount = 0;

					for (ItemStack stack : itemList) {
						if (!stack.isEmpty()) {
							totalCount++;
							if (count <= 4) {
								count++;
								IFormattableTextComponent textComponent = stack.getDisplayName().deepCopy();
								textComponent.appendString(" x").appendString(String.valueOf(stack.getCount())).mergeStyle(TextFormatting.GRAY);
								tooltip.add(textComponent);
							}
						}
					}

					if (totalCount - count > 0) {
						tooltip.add((new TranslationTextComponent("container.shulkerBox.more", totalCount - count)).mergeStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
					}
					tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
					tooltip.add(new StringTextComponent(String.format("%d/%d ", totalCount, FleshChestTileEntity.INV_SLOTS_COUNT)).appendSibling(new TranslationTextComponent("tooltip.biomancy.slots")).mergeStyle(TextFormatting.GRAY));
				}
			}
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
		return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote()) return ActionResultType.SUCCESS;

		if (!ChestBlock.isBlocked(worldIn, pos)) {
			INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
			if (containerProvider != null && player instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
				NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.FAIL;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof FleshChestTileEntity) {
			return Container.calcRedstoneFromInventory(((FleshChestTileEntity) tileEntity).getInventory());
		}
		return 0;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new FleshChestTileEntity();
	}

}
