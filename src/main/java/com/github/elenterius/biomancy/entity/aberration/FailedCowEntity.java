package com.github.elenterius.biomancy.entity.aberration;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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

	@Override
	public CowEntity createChild(ServerWorld world, AgeableEntity mate) {
		if (mate.getClass() != getClass() && rand.nextFloat() < 0.15f) {
			return (CowEntity) mate.createChild(world, this);
		}
		return ModEntityTypes.FAILED_COW.get().create(world);
	}

	@Override
	public boolean canMateWith(AnimalEntity otherAnimal) {
		if (otherAnimal == this) return false;
		return otherAnimal instanceof CowEntity && isInLove() && otherAnimal.isInLove();
	}

}
