package com.github.elenterius.biomancy.mixin;


import com.github.elenterius.biomancy.entity.mob.ChromaSheep;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Animal.class)
public abstract class AnimalMixin {

	@Inject(at = @At(value = "HEAD"), cancellable = true, method = "canMate")
	private void onCanMate(Animal otherAnimal, CallbackInfoReturnable<Boolean> cir) {
		Animal thisAnimal = (Animal) (Object) this;

		if (thisAnimal == otherAnimal) return;

		if (biomancy$canSheepMate(thisAnimal, otherAnimal)) {
			if (thisAnimal.isInLove() && otherAnimal.isInLove()) {
				cir.setReturnValue(true);
			}
		}
	}

	@Unique
	private static boolean biomancy$canSheepMate(Animal thisAnimal, Animal otherAnimal) {
		return thisAnimal.getClass() == Sheep.class && otherAnimal instanceof ChromaSheep;
	}

}
