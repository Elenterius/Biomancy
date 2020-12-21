package com.github.elenterius.blightlings.util;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stat;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

public abstract class PlayerInteractionUtil {

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

		} else return ActionResultType.PASS;
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
