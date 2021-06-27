package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.DigesterTileEntity;
import com.github.elenterius.biomancy.tileentity.state.DigesterStateData;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class DigesterBlock extends MachineBlock<DigesterTileEntity> {

	public static final VoxelShape UP_SHAPE = createVoxelShape(Direction.UP);

	public DigesterBlock(Properties builder) {
		super(builder);
	}

	private static VoxelShape createVoxelShape(Direction direction) {
		return Stream.of(
				Block.makeCuboidShape(4.5, 14, 4.5, 11.5, 16, 11.5),
				Block.makeCuboidShape(4, 0, 4, 12, 4, 12),
				Block.makeCuboidShape(3, 4, 3, 13, 14, 13)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
	}

	@Nullable
	@Override
	public DigesterTileEntity createNewTileEntity(IBlockReader worldIn) {
		return new DigesterTileEntity();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
		if (nbt != null) {
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			CompoundNBT fuelNbt = nbt.getCompound(DigesterStateData.NBT_KEY_FUEL);
			int fuel = fuelNbt.getInt("Amount");
			String translationKey = "fluid." + fuelNbt.getString("FluidName").replace(":", ".").replace("/", ".");
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
			tooltip.add(new TranslationTextComponent(translationKey).appendString(String.format(": %s/%s", df.format(fuel), df.format(DigesterTileEntity.MAX_FUEL))));
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return UP_SHAPE;
//		Direction facing = state.get(FACING);
//		switch (facing) {
//			case NORTH:
//				return NORTH_SHAPE;
//			case SOUTH:
//				return SOUTH_SHAPE;
//			case WEST:
//				return WEST_SHAPE;
//			case EAST:
//				return EAST_SHAPE;
//		}
//		return VoxelShapes.fullCube();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (worldIn.getGameTime() % 10L == 0 && rand.nextInt(2) == 0) {
			boolean isCrafting = stateIn.get(CRAFTING);
			if (isCrafting) {
				worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.BLOCKS, 0.3f + rand.nextFloat() * 0.2f, 0.75f + rand.nextFloat() * 0.5f, false);
			}
		}
	}

}
