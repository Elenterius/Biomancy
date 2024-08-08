package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.entity.mob.ChromaSheep;
import com.github.elenterius.biomancy.entity.mob.FleshSheep;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public abstract class SheepMixin extends Animal {

	protected SheepMixin(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At(value = "HEAD"), cancellable = true, method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Sheep;")
	private void onGetBreedOffspring(ServerLevel level, AgeableMob otherParent, CallbackInfoReturnable<Sheep> cir) {
		float p = (hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f) + (otherParent.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f);
		if (p > 0) {
			if (random.nextFloat() < p) {
				FleshSheep sheep = ModEntityTypes.FLESH_SHEEP.get().create(level);
				cir.setReturnValue(sheep);
			}
			else if (random.nextFloat() < p) {
				Sheep sheep = ModEntityTypes.THICK_FUR_SHEEP.get().create(level);
				cir.setReturnValue(sheep);
			}
			else if (random.nextFloat() < p * 0.5f) {
				ChromaSheep sheep = ModEntityTypes.CHROMA_SHEEP.get().create(level);
				cir.setReturnValue(sheep);
			}
		}
	}

}
