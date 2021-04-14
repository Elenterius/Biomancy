package com.github.elenterius.biomancy.util;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class PlayerInteractionUtil {
	private PlayerInteractionUtil() {}

	public static boolean harvestBlock(ServerWorld world, ServerPlayerEntity player, BlockState blockState, BlockPos pos) {
		GameType gameType = player.interactionManager.getGameType();

		int exp = ForgeHooks.onBlockBreakEvent(world, gameType, player, pos);
		if (exp == -1) return false; //check if block break event is canceled
		if (player.blockActionRestricted(world, pos, gameType)) return false;

		if (player.isCreative()) {
			removeBlock(world, player, blockState, pos, false);
			return true;
		}

		TileEntity tileEntity = world.getTileEntity(pos);
		Block block = blockState.getBlock();

		ItemStack heldStack = player.getHeldItemMainhand();
		ItemStack stackCopy = heldStack.copy();
		boolean canHarvest = blockState.canHarvestBlock(world, pos, player);
		heldStack.onBlockDestroyed(world, blockState, pos, player);
		if (heldStack.isEmpty() && !stackCopy.isEmpty()) ForgeEventFactory.onPlayerDestroyItem(player, stackCopy, Hand.MAIN_HAND);
		boolean isRemoved = removeBlock(world, player, blockState, pos, canHarvest);

		if (isRemoved && canHarvest) {
			block.harvestBlock(world, player, pos, blockState, tileEntity, stackCopy);
		}

		if (isRemoved && exp > 0) {
			block.dropXpOnBlockBreak(world, pos, exp);
		}

		return isRemoved;
	}

	private static boolean removeBlock(World world, ServerPlayerEntity player, BlockState blockState, BlockPos pos, boolean canHarvest) {
		boolean removed = blockState.removedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
		if (removed) blockState.getBlock().onPlayerDestroy(world, pos, blockState);
		return removed;
	}

	private static List<BlockPos> findBlockNeighbors(World world, ServerPlayerEntity player, BlockState targetState, BlockPos startPos, int range) {
		ModifiableAttributeInstance attribute = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
		double reachDistance = attribute != null ? attribute.getValue() : ForgeMod.REACH_DISTANCE.get().getDefaultValue();
		BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) player.pick(reachDistance, 1f, false);
		return findBlockNeighbors(world, rayTraceResult, targetState, startPos, range);
	}

	public static List<BlockPos> findBlockNeighbors(World world, BlockRayTraceResult rayTraceResult, BlockState targetState, BlockPos startPos, int range) {
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS || !rayTraceResult.getPos().equals(startPos)) {
			return Collections.emptyList();
		}

		Block targetBlock = targetState.getBlock();
		List<BlockPos> neighbors = new ArrayList<>();

		Direction.Axis axis = rayTraceResult.getFace().getAxis();
		if (axis == Direction.Axis.Y) {
			int y = 0;
			for (int x = -range; x <= range; x++) {
				for (int z = -range; z <= range; z++) {
					if (x == 0 && z == 0) continue;
					BlockPos pos = startPos.add(x, y, z);
					if (world.getBlockState(pos).matchesBlock(targetBlock)) neighbors.add(pos);
				}
			}
		}
		else if (axis == Direction.Axis.Z) {
			int y = 0;
			for (int x = -range; x <= range; x++) {
				for (int z = -range; z <= range; z++) {
					if (x == 0 && z == 0) continue;
					BlockPos pos = startPos.add(x, z, y);
					if (world.getBlockState(pos).matchesBlock(targetBlock)) neighbors.add(pos);
				}
			}
		}
		else if (axis == Direction.Axis.X) {
			int y = 0;
			for (int x = -range; x <= range; x++) {
				for (int z = -range; z <= range; z++) {
					if (x == 0 && z == 0) continue;
					BlockPos pos = startPos.add(y, x, z);
					if (world.getBlockState(pos).matchesBlock(targetBlock)) neighbors.add(pos);
				}
			}
		}

		return neighbors;
	}

	public static ActionResultType tryToPlaceBlock(ServerPlayerEntity playerIn, ItemStack stackIn, Hand handIn, BlockRayTraceResult rayTraceResult, Direction horizontalFacing) {
		BlockPos blockpos = rayTraceResult.getPos();
		PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(playerIn, handIn, blockpos, rayTraceResult.getFace());
		if (event.isCanceled()) return event.getCancellationResult();
		if (playerIn.isSpectator()) return ActionResultType.PASS;

		ItemUseContext itemUseContext = new ItemUseContext(playerIn, handIn, rayTraceResult) {
			@Override
			public Direction getPlacementHorizontalFacing() {
				return horizontalFacing;
			}
		};
		if (event.getUseItem() != Event.Result.DENY) {
			ActionResultType result = stackIn.onItemUseFirst(itemUseContext);
			if (result != ActionResultType.PASS) return result;
		}
		ItemStack stackCopy = stackIn.copy();
		if (!stackIn.isEmpty() && !playerIn.getCooldownTracker().hasCooldown(stackIn.getItem())) {
			if (event.getUseItem() == Event.Result.DENY) return ActionResultType.PASS;
			ActionResultType actionResultType = stackIn.onItemUse(itemUseContext);
			if (playerIn.isCreative()) stackIn.setCount(stackCopy.getCount());

			if (actionResultType.isSuccessOrConsume())
				CriteriaTriggers.RIGHT_CLICK_BLOCK_WITH_ITEM.test(playerIn, blockpos, stackCopy);

			return actionResultType;

		}
		else return ActionResultType.PASS;
	}

	/**
	 * DO NOT CACHE THIS!
	 */
	public static class PlayerSurrogate extends FakePlayer {
		private final LivingEntity surrogate;
		private final ServerPlayerEntity owner;

		public static PlayerSurrogate of(ServerPlayerEntity player, LivingEntity surrogate) {
			return new PlayerSurrogate(player, surrogate);
		}

		private PlayerSurrogate(ServerPlayerEntity player, LivingEntity surrogate) {
			super(player.getServerWorld(), player.getGameProfile());
			this.owner = player;
			this.surrogate = surrogate;
			setHealth(surrogate.getHealth());
		}

		@Override
		public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
			owner.sendStatusMessage(chatComponent, actionBar);
		}

		@Override
		public void sendMessage(ITextComponent component, UUID senderUUID) {
			owner.sendMessage(component, senderUUID);
		}

		@Override
		public void addStat(Stat stat, int amount) {
			owner.addStat(stat, amount);
		}

		@Override
		public boolean canAttackPlayer(PlayerEntity player) {
			return owner.canAttackPlayer(player);
		}

		@Override
		public boolean isSpectator() {
			return false;
		}

		@Override
		public boolean isCreative() {
			return false;
		}

		@Override
		public boolean isSecondaryUseActive() {
			return false;
		}

		@Override
		public Vector3d getPositionVec() {
			return surrogate.getPositionVec();
		}

		@Override
		public BlockPos getPosition() {
			return surrogate.getPosition();
		}

		@Override
		public boolean isInvulnerableTo(DamageSource source) {
			return surrogate.isInvulnerableTo(source);
		}

		@Override
		public ItemStack getHeldItem(Hand hand) {
			return surrogate.getHeldItem(hand);
		}

		@Override
		public ItemStack getHeldItemMainhand() {
			return surrogate.getHeldItemMainhand();
		}

		@Override
		public ItemStack getHeldItemOffhand() {
			return surrogate.getHeldItemOffhand();
		}

		@Override
		public Direction getHorizontalFacing() {
			return surrogate.getHorizontalFacing();
		}

		@Override
		public float getEyeHeight(Pose pose) {
			return surrogate.getEyeHeight(pose);
		}

		@Override
		public double getPosYEye() {
			return surrogate.getPosYEye();
		}

		@Override
		public float getHealth() {
			return surrogate.getHealth();
		}

		@Override
		public void heal(float healAmount) {
			surrogate.heal(healAmount);
		}

		@Override
		public boolean shouldHeal() {
			return getHealth() > 0.0F && getHealth() < surrogate.getMaxHealth();
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return surrogate.attackEntityFrom(source, amount);
		}
	}
}
