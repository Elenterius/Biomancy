package com.github.elenterius.biomancy.entity.aberration;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class FailedCowEntity extends CowEntity {

	public FailedCowEntity(EntityType<? extends CowEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
		ItemStack itemstack = playerIn.getHeldItem(hand);
		if (itemstack.getItem() == Items.BUCKET) {
			return ActionResultType.PASS;
		}
		return super.getEntityInteractionResult(playerIn, hand);
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() - 1f;
	}

}
