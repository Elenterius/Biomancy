package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.FleshbornChestTileEntity;
import com.github.elenterius.biomancy.tileentity.IOwnableTile;
import com.github.elenterius.biomancy.util.ClientTextUtil;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class FleshChestBlock extends OwnableContainerBlock implements IWaterLoggable {

	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected static final VoxelShape SHAPE = Block.box(1d, 0d, 1d, 15d, 14d, 15d);

	public FleshChestBlock(Properties builder) {
		super(builder);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		super.appendHoverText(stack, level, tooltip, flagIn);

		CompoundNBT nbt = stack.getTagElement("BlockEntityTag");
		if (nbt != null) {
			CompoundNBT invNbt = nbt.getCompound("Inventory");
			if (!invNbt.isEmpty() && invNbt.contains("Items", Constants.NBT.TAG_LIST)) {
				int size = invNbt.contains("Size") ? invNbt.getInt("Size") : FleshbornChestTileEntity.INV_SLOTS_COUNT;
				NonNullList<ItemStack> itemList = NonNullList.withSize(size, ItemStack.EMPTY);
				ItemStackHelper.loadAllItems(invNbt, itemList);
				int count = 0;
				int totalCount = 0;

				for (ItemStack storedStack : itemList) {
					if (!storedStack.isEmpty()) {
						totalCount++;
						if (count < 5) {
							count++;
							IFormattableTextComponent textComponent = storedStack.getHoverName().copy();
							textComponent.append(" x").append(String.valueOf(storedStack.getCount())).withStyle(TextFormatting.GRAY);
							tooltip.add(textComponent);
						}
					}
				}

				if (totalCount - count > 0) {
					tooltip.add((new TranslationTextComponent("container.shulkerBox.more", totalCount - count)).withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
				}
				tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
				tooltip.add(new StringTextComponent(String.format("%d/%d ", totalCount, FleshbornChestTileEntity.INV_SLOTS_COUNT)).append(new TranslationTextComponent("tooltip.biomancy.slots")).withStyle(TextFormatting.GRAY));
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (Boolean.TRUE.equals(state.getValue(WATERLOGGED))) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}
		return super.updateShape(state, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isClientSide()) return ActionResultType.SUCCESS;

		if (!ChestBlock.isChestBlockedAt(worldIn, pos)) {
			TileEntity tileEntity = worldIn.getBlockEntity(pos);
			if (tileEntity instanceof IOwnableTile && ((IOwnableTile) tileEntity).canPlayerUse(player)) {
				INamedContainerProvider containerProvider = getMenuProvider(state, worldIn, pos);
				if (containerProvider != null && player instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
					NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
					return ActionResultType.SUCCESS;
				}
			}
		}

		return ActionResultType.FAIL;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof FleshbornChestTileEntity) {
			return Container.getRedstoneSignalFromContainer(((FleshbornChestTileEntity) tileEntity).getInventory());
		}
		return 0;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new FleshbornChestTileEntity();
	}

}
