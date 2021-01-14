package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.tileentity.GulgeTileEntity;
import com.github.elenterius.blightlings.util.TooltipUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GulgeBlock extends ContainerBlock {
	public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

	protected static final VoxelShape SHAPE_UD = Block.makeCuboidShape(3d, 0d, 3d, 13d, 16d, 13d);
	protected static final VoxelShape SHAPE_NS = Block.makeCuboidShape(3d, 3d, 0d, 13d, 13d, 16d);
	protected static final VoxelShape SHAPE_WE = Block.makeCuboidShape(0d, 3d, 3d, 16d, 13d, 13d);

	public GulgeBlock(Properties builder) {
		super(builder);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.UP));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());

		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null) {
			CompoundNBT contents = nbt.getCompound("Contents");
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
					tooltip.add(new TranslationTextComponent("tooltip.blightlings.contains", storedStack.getDisplayName().deepCopy()).mergeStyle(TextFormatting.GRAY));
					tooltip.add(new StringTextComponent(String.format("%d/%d", itemAmount, GulgeTileEntity.MAX_ITEM_AMOUNT)).mergeStyle(TextFormatting.GRAY));
					return;
				}
			}
		}

		tooltip.add(new TranslationTextComponent("tooltip.blightlings.empty"));
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote()) return ActionResultType.SUCCESS;

		INamedContainerProvider containerProvider = getContainer(state, worldIn, pos);
		if (containerProvider != null && player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, containerProvider, (packetBuffer) -> {});
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof GulgeTileEntity) {
				GulgeTileEntity gulgeTileEntity = (GulgeTileEntity) tileEntity;
//				gulgeTileEntity.dropAllContents(world, blockPos);
			}
			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof GulgeTileEntity) {
				((GulgeTileEntity) tileentity).setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof GulgeTileEntity) {
			GulgeTileEntity tile = (GulgeTileEntity) tileEntity;
			if (!worldIn.isRemote()) {
				if (!player.isCreative() || (player.isCreative() && !tile.isEmpty())) { // we drop the item manually
					ItemStack stack = new ItemStack(this);
					CompoundNBT nbt = tile.write(new CompoundNBT());
					if (!nbt.isEmpty()) {
						stack.setTagInfo("BlockEntityTag", nbt);
					}

					if (tile.hasCustomName()) {
						stack.setDisplayName(tile.getCustomName());
					}

					ItemEntity itementity = new ItemEntity(worldIn, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack);
					itementity.setDefaultPickupDelay();
					worldIn.addEntity(itementity);
				}
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Collections.emptyList(); // fuck copy nbt in loot tables //TODO: figure out how to copy nbt to loot
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		ItemStack itemstack = super.getItem(worldIn, pos, state);
		GulgeTileEntity tileEntity = (GulgeTileEntity) worldIn.getTileEntity(pos);
		CompoundNBT nbt = tileEntity.write(new CompoundNBT());
		if (!nbt.isEmpty()) {
			itemstack.setTagInfo("BlockEntityTag", nbt);
		}
		return itemstack;
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.BLOCK;
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

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getFace());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

}
