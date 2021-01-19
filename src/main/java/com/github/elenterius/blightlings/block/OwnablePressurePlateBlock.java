package com.github.elenterius.blightlings.block;

import com.github.elenterius.blightlings.init.ModBlocks;
import com.github.elenterius.blightlings.tileentity.SimpleOwnableTileEntity;
import com.github.elenterius.blightlings.util.TooltipUtil;
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
		setDefaultState(getDefaultState().with(USER_SENSITIVITY, UserSensitivity.AUTHORIZED));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(USER_SENSITIVITY);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		OwnableBlock.addOwnableTooltip(stack, tooltip, flagIn);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			OwnableBlock.attachDataToOwnableTile(worldIn, (SimpleOwnableTileEntity) tileEntity, placer, stack);
		}
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return stateIn; //don't check if pressure plate is standing on a block, let it float
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).canPlayerUse(player)) { //only interact with authorized players
				if (player.isSneaking()) {
					state = state.with(USER_SENSITIVITY, state.get(USER_SENSITIVITY).switchAuth());
					worldIn.setBlockState(pos, state, Constants.BlockFlags.BLOCK_UPDATE);
					return ActionResultType.func_233537_a_(worldIn.isRemote);
				}
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			SimpleOwnableTileEntity ownableTile = (SimpleOwnableTileEntity) tileEntity;

			AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
			List<? extends Entity> list = worldIn.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);

			if (!list.isEmpty()) {
				BlockState state = worldIn.getBlockState(pos);
				UserSensitivity sensitivity = state.get(USER_SENSITIVITY);

				for (Entity entity : list) {
					if (!entity.doesEntityNotTriggerPressurePlate()) {
						if (sensitivity == UserSensitivity.UNAUTHORIZED) {
							if (!ownableTile.isAuthorized(entity.getUniqueID())) {
								return 15;
							}
						}
						else if (ownableTile.isAuthorized(entity.getUniqueID())) {
							return 15;
						}
					}
				}
			}
		}

		return 0;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		OwnableBlock.dropForCreativePlayer(worldIn, this, pos, player);
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof SimpleOwnableTileEntity) {
			if (((SimpleOwnableTileEntity) tileEntity).isPlayerAuthorized(player)) { //only allow authorized players to mine the block
				return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
			}
		}
		return 0f;
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
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
