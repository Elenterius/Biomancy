package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.client.renderer.ClientRenderHandler;
import com.github.elenterius.blightlings.item.IEntityUnveilerHeadSlotItem;
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
public abstract class EntityMixin
{
    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    @Nullable
    public abstract Team getTeam();

    @Inject(method = "isInvisibleToPlayer", at = @At("HEAD"), cancellable = true)
    protected void onIsInvisibleToPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (IEntityUnveilerHeadSlotItem.canUnveilEntity(player, (Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    //on client side
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    protected void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        //noinspection ConstantConditions
        if (entity == ClientRenderHandler.HIGHLIGHTED_ENTITY) {
            Team team = getTeam();
            if (team == null) cir.setReturnValue(ClientRenderHandler.COLOR_ENEMY);
            else {
                if (!getTeam().isSameTeam(team)) cir.setReturnValue(ClientRenderHandler.COLOR_ENEMY);
                else cir.setReturnValue(ClientRenderHandler.COLOR_FRIENDLY);
            }
        }
        else if (getTeam() == null && IEntityUnveilerHeadSlotItem.canUnveilEntity(Minecraft.getInstance().player, entity)) {
            if (getType().getClassification() == EntityClassification.MONSTER) cir.setReturnValue(0xCE0018);
            else if (getType().getClassification() == EntityClassification.CREATURE) cir.setReturnValue(0x00ff00);
            else cir.setReturnValue(getType() == EntityType.PLAYER ? 0xffd700 : 0xffffff);
        }
    }
}
