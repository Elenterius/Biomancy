package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.biomancy.init.ModAttributes;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.weapon.FleshbornGuanDaoItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.MarkerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@Deprecated
	@Inject(method = "func_234570_el_", at = @At(value = "RETURN"))
	private static void biomancy_onRegisterAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
		BiomancyMod.LOGGER.debug(MarkerManager.getMarker("ATTRIBUTE INJECTION"), "adding attack distance attribute to player...");
		cir.getReturnValue().createMutableAttribute(ModAttributes.getAttackDistanceModifier());
	}

	@Deprecated
	@Redirect(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getDistanceSq(Lnet/minecraft/entity/Entity;)D"))
	protected double biomancy_transformSweepDistSq(PlayerEntity playerEntity, Entity entityIn) {
		double distSq = playerEntity.getDistanceSq(entityIn);
		double maxDist = ModAttributes.getCombinedReachDistance(playerEntity);
		if (distSq < maxDist * maxDist) {
			return distSq < 9d ? distSq : 8.99d; //hack to allow sweep attacks with attack distance greater than 3/4 ?
		}
		return distSq;
	}

	@Redirect(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getModifierForCreature(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/CreatureAttribute;)F"))
	protected float biomancy_transformExtraDamageModifier(ItemStack stack, CreatureAttribute creatureAttribute, Entity targetEntity) {
		if (!stack.isEmpty()) {
			float modifier = 0f;
			if (AttunedDamageEnchantment.isAttuned(stack))
				modifier = ModEnchantments.ATTUNED_BANE.get().getAttackDamageModifier(stack, (PlayerEntity) (Object) this, targetEntity);
			if (stack.getItem() == ModItems.FLESHBORN_GUAN_DAO.get())
				modifier += FleshbornGuanDaoItem.getAttackDamageModifier(stack, (PlayerEntity) (Object) this, targetEntity);

			return EnchantmentHelper.getModifierForCreature(stack, creatureAttribute) + modifier;
		}
		else {
			return EnchantmentHelper.getModifierForCreature(stack, creatureAttribute);
		}
	}

}
