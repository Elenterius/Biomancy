package com.github.elenterius.biomancy.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	//replaced by AttackHandler.onLivingHurt(LivingHurtEvent)
//	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getDamageBonus(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/CreatureAttribute;)F"))
//	protected float biomancy_transformExtraDamageModifier(ItemStack stack, CreatureAttribute creatureAttribute, Entity targetEntity) {
//		if (!stack.isEmpty()) {
//			float modifier = 0f;
//			if (AttunedDamageEnchantment.isAttuned(stack))
//				modifier = ModEnchantments.ATTUNED_BANE.get().getAttackDamageModifier(stack, (PlayerEntity) (Object) this, targetEntity);
//			if (stack.getItem() == ModItems.FLESHBORN_GUAN_DAO.get())
//				modifier += FleshbornGuanDaoItem.getAttackDamageModifier(stack, (PlayerEntity) (Object) this, targetEntity);
//
//			return EnchantmentHelper.getDamageBonus(stack, creatureAttribute) + modifier;
//		}
//		else {
//			return EnchantmentHelper.getDamageBonus(stack, creatureAttribute);
//		}
//	}

}
