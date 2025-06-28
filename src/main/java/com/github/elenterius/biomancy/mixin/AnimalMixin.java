package com.github.elenterius.biomancy.mixin;


import com.github.elenterius.biomancy.entity.mob.ChromaSheep;
import com.github.elenterius.biomancy.entity.mob.FleshPig;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
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

		if (biomancy$canMate(thisAnimal, otherAnimal)) {
			if (thisAnimal.isInLove() && otherAnimal.isInLove()) {
				cir.setReturnValue(true);
			}
		}
	}

	@Unique
	private static boolean biomancy$canMate(Animal thisAnimal, Animal otherAnimal) {
		return biomancy$canSheepMate(thisAnimal, otherAnimal) || biomancy$canPigMate(thisAnimal, otherAnimal) || biomancy$canHoglinMate(thisAnimal, otherAnimal);
	}

	@Unique
	private static boolean biomancy$canSheepMate(Animal thisAnimal, Animal otherAnimal) {
		return thisAnimal.getClass() == Sheep.class && otherAnimal instanceof ChromaSheep;
	}

	@Unique
	private static boolean biomancy$canPigMate(Animal thisAnimal, Animal otherAnimal) {
		return thisAnimal.getClass() == Pig.class && otherAnimal instanceof FleshPig;
	}

	@Unique
	private static boolean biomancy$canHoglinMate(Animal thisAnimal, Animal otherAnimal) {
		return thisAnimal.getClass() == Hoglin.class && otherAnimal instanceof FleshPig;
	}

}
