package com.github.elenterius.biomancy.sounds;

import com.github.elenterius.biomancy.util.shooting.Gun;
import com.github.elenterius.biomancy.util.shooting.GunState;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class LoopingGunSoundInstance extends LoopingEntitySoundInstance {

	public LoopingGunSoundInstance(Entity entity, Supplier<SoundEvent> soundEventSupplier) {
		this(entity, soundEventSupplier.get());
	}

	public LoopingGunSoundInstance(Entity entity, SoundEvent soundEvent) {
		super(entity, soundEvent);
	}

	public boolean canContinuePlaying() {
		if (!entity.isRemoved() && entity instanceof LivingEntity livingEntity && livingEntity.isUsingItem()) {
			ItemStack stack = livingEntity.getUseItem();
			return stack.getItem() instanceof Gun<?> gun && gun.getGunState(stack) == GunState.SHOOTING_OR_CHARGING;
		}
		return false;
	}

	@Override
	public void tick() {
		if (!canContinuePlaying()) {
			fadeOut();
		}
		super.tick();
	}

}
