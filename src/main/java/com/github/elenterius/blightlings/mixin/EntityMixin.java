package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.item.IEntityUnveiler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>
{
    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    @Nullable
    public abstract Team getTeam();

    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "isInvisibleToPlayer", at = @At("HEAD"), cancellable = true)
    protected void onIsInvisibleToPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (IEntityUnveiler.canUnveilEntity(player, (Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    protected void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (getTeam() == null && IEntityUnveiler.canUnveilEntity(Minecraft.getInstance().player, (Entity) (Object) this)) {
            if (getType().getClassification() == EntityClassification.MONSTER) cir.setReturnValue(0x800080);
            else if (getType().getClassification() == EntityClassification.CREATURE) cir.setReturnValue(0x00ff00);
            else cir.setReturnValue(getType() == EntityType.PLAYER ? 0xffd700 : 0xffffff);
        }
    }
}
