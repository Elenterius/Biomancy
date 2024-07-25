package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.animation.MobAnimations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FleshCow extends Cow implements GeoEntity {

	protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public FleshCow(EntityType<? extends Cow> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.is(Items.BUCKET) && !isBaby()) {
			player.playSound(SoundEvents.COW_MILK, 1f, 1f);
			player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Items.WATER_BUCKET.getDefaultInstance()));
			return InteractionResult.sidedSuccess(level().isClientSide);
		}

		return super.mobInteract(player, hand);
	}

	@Nullable
	@Override
	public Cow getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		if (otherParent.getClass() != getClass() && random.nextFloat() < 0.15f) {
			return (Cow) otherParent.getBreedOffspring(level, this);
		}

		return ModEntityTypes.FLESH_COW.get().create(level);
	}

	@Override
	public boolean canMate(Animal otherAnimal) {
		if (otherAnimal == this) return false;
		return otherAnimal instanceof Cow && isInLove() && otherAnimal.isInLove();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSoundEvents.FLESH_COW_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return ModSoundEvents.FLESH_COW_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSoundEvents.FLESH_COW_DEATH.get();
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(MobAnimations.walkController(this));
		controllers.add(MobAnimations.babyTransformController(this));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

}
