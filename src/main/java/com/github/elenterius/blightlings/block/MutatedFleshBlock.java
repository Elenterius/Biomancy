package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class MutatedFleshBlock extends Block {

	public static final EnumProperty<MutationType> MUTATION_TYPE = EnumProperty.create("type", MutationType.class);

	public MutatedFleshBlock(Properties properties) {
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(MUTATION_TYPE, MutationType.EYE_0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(MUTATION_TYPE);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack stack = player.getHeldItem(handIn);
		if (stack.getItem() == ModItems.MUTAGENIC_BILE.get()) {
			if (!worldIn.isRemote) {
				BlockState newState = state.with(MUTATION_TYPE, MutationType.pickRandom(worldIn.rand));
				if (newState != state) {
					worldIn.setBlockState(pos, newState, Constants.BlockFlags.DEFAULT);
				}
				if (!player.abilities.isCreativeMode) {
					stack.shrink(1);
				}
			}
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		else {
			return ActionResultType.PASS;
		}
	}

//	@Override
//	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
//		items.add(new ItemStack(this));
//	}

	public enum MutationType implements IStringSerializable {
		EYE_0("eye_0"),
		EYE_1("eye_1"),
		MOUTH("mouth");

		private final String name;

		MutationType(String name) {
			this.name = name;
		}

		public static MutationType pickRandom(Random random) {
			int i = random.nextInt(3);
			if (i == 0) return EYE_0;
			if (i == 1) return EYE_1;
			return MOUTH;
		}

		@Override
		public String getString() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
