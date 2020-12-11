package com.github.elenterius.blightlings.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

public abstract class PlayerInteractionUtil {

    public static ActionResultType tryToPlaceBlock(ServerPlayerEntity playerIn, ItemStack stackIn, Hand handIn, BlockRayTraceResult rayTraceResult) {
        BlockPos blockpos = rayTraceResult.getPos();
        PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(playerIn, handIn, blockpos, rayTraceResult.getFace());
        if (event.isCanceled()) return event.getCancellationResult();
        if (playerIn.isSpectator()) return ActionResultType.PASS;

        ItemUseContext itemUseContext = new ItemUseContext(playerIn, handIn, rayTraceResult);
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
    public static class PlayerSurrogate extends ServerPlayerEntity {
        private final LivingEntity surrogate;

        public static PlayerSurrogate of(ServerPlayerEntity player, LivingEntity surrogate) {
            return new PlayerSurrogate(player.server, player.getServerWorld(), player.getGameProfile(), player.interactionManager, surrogate);
        }

        private PlayerSurrogate(MinecraftServer server, ServerWorld worldIn, GameProfile profile, PlayerInteractionManager interactionManagerIn, LivingEntity surrogate) {
            super(server, worldIn, profile, interactionManagerIn);
            this.surrogate = surrogate;
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
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isSecondaryUseActive() {
            return false;
        }
    }
}
