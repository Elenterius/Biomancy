package com.creativechasm.blightlings.mixin;

import com.creativechasm.blightlings.item.IRevealInvisible;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>
{
    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "isInvisibleToPlayer", at = @At("HEAD"), cancellable = true)
    protected void onIsInvisibleToPlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = player.inventory.armorInventory.get(EquipmentSlotType.HEAD.getSlotIndex() - 1);
        if (stack.getItem() instanceof IRevealInvisible) {
            if (((IRevealInvisible<?>) stack.getItem()).canRevealInvisibleEntity(stack, player, (Entity) (Object) this)) {
                cir.setReturnValue(false);
            }
        }
    }
}
