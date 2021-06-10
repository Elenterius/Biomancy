package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.GulgeTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class GulgeBlock extends OwnableContainerBlock {
	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

	protected static final VoxelShape SHAPE_UD = Block.makeCuboidShape(3d, 0d, 3d, 13d, 16d, 13d);
	protected static final VoxelShape SHAPE_NS = Block.makeCuboidShape(3d, 3d, 0d, 13d, 13d, 16d);
	protected static final VoxelShape SHAPE_WE = Block.makeCuboidShape(0d, 3d, 3d, 16d, 13d, 13d);

	public GulgeBlock(Properties builder) {
		super(builder);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.UP));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getFace());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null) {
			CompoundNBT contents = nbt.getCompound("Inventory");
			if (!contents.isEmpty()) {
				ItemStack storedStack = contents.contains("Item") ? ItemStack.read(contents.getCompound("Item")) : ItemStack.EMPTY;
				if (!storedStack.isEmpty()) {
					int itemAmount = storedStack.getCount();
					if (contents.contains("ItemAmount")) {
						INBT inbt = contents.get("ItemAmount");
						if (inbt instanceof NumberNBT) {
							itemAmount = ((NumberNBT) inbt).getInt();
						}
					}
					tooltip.add(TextUtil.getTooltipText("contains", storedStack.getDisplayName().deepCopy()).mergeStyle(TextFormatting.GRAY));
					DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
					tooltip.add(new StringTextComponent(df.format(itemAmount) + "/" + df.format(GulgeTileEntity.MAX_ITEM_AMOUNT)).mergeStyle(TextFormatting.GRAY));
					return;
				}
			}
		}

		tooltip.add(TextUtil.getTooltipText("empty").mergeStyle(TextFormatting.GRAY));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote()) return ActionResultType.SUCCESS;

		//TODO: verify that authorization works
		INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
		if (containerProvider != null && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
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
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
			case UP:
			case DOWN:
			default:
				return SHAPE_UD;
			case NORTH:
			case SOUTH:
				return SHAPE_NS;
			case WEST:
			case EAST:
				return SHAPE_WE;
		}
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new GulgeTileEntity();
	}

}
