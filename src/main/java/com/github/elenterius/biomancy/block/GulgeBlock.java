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

	protected static final VoxelShape SHAPE_UD = Block.box(3d, 0d, 3d, 13d, 16d, 13d);
	protected static final VoxelShape SHAPE_NS = Block.box(3d, 3d, 0d, 13d, 13d, 16d);
	protected static final VoxelShape SHAPE_WE = Block.box(0d, 3d, 3d, 16d, 13d, 13d);

	public GulgeBlock(Properties builder) {
		super(builder);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		CompoundNBT nbt = stack.getTagElement("BlockEntityTag");
		if (nbt != null) {
			CompoundNBT contents = nbt.getCompound("Inventory");
			if (!contents.isEmpty()) {
				ItemStack storedStack = contents.contains("Item") ? ItemStack.of(contents.getCompound("Item")) : ItemStack.EMPTY;
				if (!storedStack.isEmpty()) {
					int itemAmount = storedStack.getCount();
					if (contents.contains("ItemAmount")) {
						INBT inbt = contents.get("ItemAmount");
						if (inbt instanceof NumberNBT) {
							itemAmount = ((NumberNBT) inbt).getAsInt();
						}
					}
					tooltip.add(TextUtil.getTooltipText("contains", storedStack.getHoverName().copy()).withStyle(TextFormatting.GRAY));
					DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
					tooltip.add(new StringTextComponent(df.format(itemAmount) + "/" + df.format(GulgeTileEntity.MAX_ITEM_AMOUNT)).withStyle(TextFormatting.GRAY));
					return;
				}
			}
		}

		tooltip.add(TextUtil.getTooltipText("empty").withStyle(TextFormatting.GRAY));
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isClientSide()) return ActionResultType.SUCCESS;

		//TODO: verify that authorization works
		INamedContainerProvider containerProvider = getMenuProvider(state, worldIn, pos);
		if (containerProvider != null && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
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
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.getValue(FACING)) {
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
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new GulgeTileEntity();
	}

}
