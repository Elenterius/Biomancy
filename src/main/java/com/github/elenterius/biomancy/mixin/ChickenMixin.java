package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.entity.mob.FleshChicken;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Animal {

	protected ChickenMixin(EntityType<? extends Animal> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At(value = "HEAD"), cancellable = true, method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Chicken;")
	private void onGetBreedOffspring(ServerLevel level, AgeableMob otherParent, CallbackInfoReturnable<Chicken> cir) {
		float p = (hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f) + (otherParent.hasEffect(ModMobEffects.LIBIDO.get()) ? 0.1f : 0f);
		if (p > 0 && random.nextFloat() < p) {
			FleshChicken chicken = ModEntityTypes.FLESH_CHICKEN.get().create(level);
			cir.setReturnValue(chicken);
		}
	}

}
