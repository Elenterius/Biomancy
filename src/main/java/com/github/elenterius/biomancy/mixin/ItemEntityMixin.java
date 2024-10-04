package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.AcidInteractions;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Inject(at=@At("TAIL"),method={"tick()V"})
	private void onTick(CallbackInfo ci) {
		ItemEntity self = (ItemEntity)((Object)this); //I hate casting like this on so many levels
		if (self.isRemoved()) return;
		boolean onClient = self.level().isClientSide();

		if (onClient) {
			Vec3 pos = self.position();
			RandomSource random = self.level().getRandom();
			self.level().addParticle(ModParticleTypes.ACID_BUBBLE.get(), pos.x + random.nextGaussian(), pos.y, pos.z + random.nextGaussian(), random.nextGaussian(), 0.1, random.nextGaussian());
		}
		if (self.getAge() % 10 != 0) return; //Only fire once every 10 ticks
		AcidInteractions.tryDigest(self,onClient);
	}
}