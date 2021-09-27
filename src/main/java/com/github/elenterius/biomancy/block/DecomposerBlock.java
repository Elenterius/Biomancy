package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.tileentity.DecomposerTileEntity;
import com.github.elenterius.biomancy.tileentity.state.DecomposerStateData;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
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

public class DecomposerBlock extends MachineBlock<DecomposerTileEntity> {

	public static final VoxelShape SHAPE = createVoxelShape();

	public DecomposerBlock(Properties builder) {
		super(builder);
	}

	private static VoxelShape createVoxelShape() {
		return Stream.of(
				Block.box(1, 0, 1, 15, 1, 15),
				Block.box(0, 1, 0, 16, 10, 16),
				Block.box(1, 10, 1, 15, 12, 15),
				Block.box(2, 12, 2, 14, 16, 14)
		).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	}

	@Nullable
	@Override
	public DecomposerTileEntity newBlockEntity(IBlockReader worldIn) {
		return new DecomposerTileEntity();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		CompoundNBT nbt = stack.getTagElement("BlockEntityTag");
		if (nbt != null && nbt.contains(DecomposerStateData.NBT_KEY_FUEL)) {
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");

			CompoundNBT fuelNbt = nbt.getCompound(DecomposerStateData.NBT_KEY_FUEL);
			int fuel = fuelNbt.getInt("Amount");
			String translationKey = "fluid." + fuelNbt.getString("FluidName").replace(":", ".").replace("/", ".");
			tooltip.add(new TranslationTextComponent(translationKey).append(String.format(": %s/%s", df.format(fuel), df.format(DecomposerTileEntity.MAX_FUEL))));
		}
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(4) == 0) {
			boolean isCrafting = stateIn.getValue(CRAFTING);
			if (isCrafting) {
				int n = rand.nextInt(5);
				int color = 0xc7b15d;
				double r = (double) (color >> 16 & 255) / 255d;
				double g = (double) (color >> 8 & 255) / 255d;
				double b = (double) (color & 255) / 255d;
				for (int i = 0; i < n; i++) {
					worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.2d + rand.nextFloat() - 0.2d, pos.getY() + 0.3d, pos.getZ() + 0.2d + rand.nextFloat() - 0.2d, r, g, b);
				}
				if (n > 0 && rand.nextInt(3) == 0) {
					worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.TROPICAL_FISH_FLOP, SoundCategory.BLOCKS, 0.2f + rand.nextFloat() * 0.2f, 0.9f + rand.nextFloat() * 0.15f, false);
				}
			}
		}
	}
}
