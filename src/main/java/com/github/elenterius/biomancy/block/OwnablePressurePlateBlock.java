package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.tileentity.SimpleOwnableTileEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class OwnablePressurePlateBlock extends PressurePlateBlock implements IOwnableBlock {

	public static final EnumProperty<UserSensitivity> USER_SENSITIVITY = ModBlocks.USER_SENSITIVITY_PROPERTY;

	public OwnablePressurePlateBlock(Properties propertiesIn) {
		super(Sensitivity.MOBS, propertiesIn);
		registerDefaultState(defaultBlockState().setValue(USER_SENSITIVITY, UserSensitivity.AUTHORIZED));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(USER_SENSITIVITY);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()).setStyle(ClientTextUtil.LORE_STYLE));
		OwnableBlock.addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			OwnableBlock.attachDataToOwnableTile(worldIn, (SimpleOwnableTileEntity) tileEntity, placer, stack);
		}
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return stateIn; //don't check if pressure plate is standing on a block, let it float
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) { //only interact with authorized players
				if (player.isShiftKeyDown()) {
					state = state.setValue(USER_SENSITIVITY, state.getValue(USER_SENSITIVITY).switchAuth());
					worldIn.setBlock(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
					return ActionResultType.sidedSuccess(worldIn.isClientSide);
				}
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	protected int getSignalStrength(World worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			SimpleOwnableTileEntity ownableTile = (SimpleOwnableTileEntity) tileEntity;

			AxisAlignedBB axisalignedbb = TOUCH_AABB.move(pos);
			List<? extends Entity> list = worldIn.getEntitiesOfClass(LivingEntity.class, axisalignedbb);

			if (!list.isEmpty()) {
				BlockState state = worldIn.getBlockState(pos);
				UserSensitivity sensitivity = state.getValue(USER_SENSITIVITY);

				for (Entity entity : list) {
					if (!entity.isIgnoringBlockTriggers()) {
						if (sensitivity == UserSensitivity.UNAUTHORIZED) {
							if (!ownableTile.isUserAuthorized(entity.getUUID())) {
								return 15;
							}
						}
						else if (ownableTile.isUserAuthorized(entity.getUUID())) {
							return 15;
						}
					}
				}
			}
		}

		return 0;
	}

	@Override
	public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		OwnableBlock.dropForCreativePlayer(worldIn, this, pos, player);
		super.playerWillDestroy(worldIn, pos, state, player);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).isUserAuthorized(player)) { //only allow authorized players to mine the block
				return super.getDestroyProgress(state, player, worldIn, pos);
			}
		}
		return 0f;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new SimpleOwnableTileEntity();
	}
}
