package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.item.CopycatFluteItem;
import com.github.elenterius.biomancy.tileentity.VoiceBoxTileEntity;
import com.github.elenterius.biomancy.util.BlockPropertyUtil;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class VoiceBoxBlock extends Block {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

	public VoiceBoxBlock(Properties properties) {
		super(properties);
		registerDefaultState(getStateDefinition().any().setValue(NOTE, 0).setValue(POWERED, Boolean.FALSE));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POWERED, NOTE);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltips, ITooltipFlag flag) {
		super.appendHoverText(stack, level, tooltips, flag);
		tooltips.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand usedHand, BlockRayTraceResult hit) {
		if (level.isClientSide) return ActionResultType.SUCCESS;

		ItemStack heldStack = player.getItemInHand(usedHand);
		if (!heldStack.isEmpty()) {
			if (heldStack.getItem() instanceof CopycatFluteItem) {
				TileEntity tile = level.getBlockEntity(pos);
				if (tile instanceof VoiceBoxTileEntity) {
					ItemStack storedStack = ((VoiceBoxTileEntity) tile).getCopycatFlute();
					if (storedStack.isEmpty()) {
						((VoiceBoxTileEntity) tile).setCopycatFlute(heldStack.copy());
						heldStack.shrink(1);
						return ActionResultType.CONSUME;
					}
				}
			}
		}
		else if (player.isShiftKeyDown()) {
			TileEntity tile = level.getBlockEntity(pos);
			if (tile instanceof VoiceBoxTileEntity) {
				ItemStack storedStack = ((VoiceBoxTileEntity) tile).getCopycatFlute();
				if (!storedStack.isEmpty()) {
					((VoiceBoxTileEntity) tile).setCopycatFlute(ItemStack.EMPTY);

					float x = pos.getX() + level.random.nextFloat() * 0.7F + 0.15F;
					float y = pos.getY() + 1f + level.random.nextFloat() * 0.15F;
					float z = pos.getZ() + level.random.nextFloat() * 0.7F + 0.15F;
					ItemEntity itemEntity = new ItemEntity(level, x, y, z, storedStack.copy());
					itemEntity.setDefaultPickUpDelay();
					level.addFreshEntity(itemEntity);
					return ActionResultType.CONSUME;
				}
			}
		}

		if (!player.isShiftKeyDown()) {
			state = state.cycle(NOTE);
		}
		else {
			state = state.setValue(NOTE, BlockPropertyUtil.getPrevious(NOTE, state.getValue(NOTE)));
		}

		level.setBlock(pos, state, Constants.BlockFlags.DEFAULT);
		playSound(state, level, pos);
		return ActionResultType.CONSUME;
	}

	@Override
	public void attack(BlockState state, World level, BlockPos pos, PlayerEntity player) {
		playSound(state, level, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		boolean hasSignal = level.hasNeighborSignal(pos);
		if (hasSignal != Boolean.TRUE.equals(state.getValue(POWERED))) {
			if (hasSignal) playSound(state, level, pos);
			level.setBlock(pos, state.setValue(POWERED, hasSignal), Constants.BlockFlags.DEFAULT);
		}
	}

	private void playSound(BlockState state, World level, BlockPos pos) {
		if (!level.isClientSide && level.isEmptyBlock(pos.above())) {
			TileEntity tile = level.getBlockEntity(pos);
			if (tile instanceof VoiceBoxTileEntity) {
				int note = state.getValue(NOTE);
				float pitch = (float) Math.pow(2d, (note - 12) / 12d);
				if (!((VoiceBoxTileEntity) tile).playVoice(3f, pitch)) {
					level.playSound(null, pos, SoundEvents.PLAYER_BREATH, SoundCategory.RECORDS, 3f, pitch);
				}
				level.blockEvent(pos, this, 0, note);
			}
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
		if (id == 0) {
			level.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5d, pos.getY() + 1.2d, pos.getZ() + 0.5d, param / 24d, 0d, 0d);
		}
		return true;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity tileEntity = level.getBlockEntity(pos);
			if (tileEntity instanceof VoiceBoxTileEntity) {
				((VoiceBoxTileEntity) tileEntity).dropAllInvContents(level, pos);
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
		return new VoiceBoxTileEntity();
	}

}
