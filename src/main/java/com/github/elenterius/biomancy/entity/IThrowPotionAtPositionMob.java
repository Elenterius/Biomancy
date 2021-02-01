package com.github.elenterius.biomancy.entity;

import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public interface IThrowPotionAtPositionMob {
	boolean tryToThrowPotionAtPosition(Vector3d targetPos);

	default boolean hasThrowablePotion() {
		ItemStack stack = getPotionItemStack();
		return !stack.isEmpty() && stack.getItem() instanceof ThrowablePotionItem;
	}

	ItemStack getPotionItemStack();

	void setPotionItemStack(ItemStack stack);

	@Nullable
	Vector3d getTargetPos();

	void setTargetPos(@Nullable IPosition position);
}
