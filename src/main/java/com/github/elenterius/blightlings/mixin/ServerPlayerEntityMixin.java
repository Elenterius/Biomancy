package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.handler.ItemDecayHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
        super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
    }

    @Inject(method = "openContainer", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"))
    protected void onContainerOpen(INamedContainerProvider p_213829_1_, CallbackInfoReturnable<OptionalInt> cir) {
        ItemDecayHandler.decayItemsInContainer((ServerPlayerEntity) (Object) this, openContainer, p_213829_1_);
    }
}
