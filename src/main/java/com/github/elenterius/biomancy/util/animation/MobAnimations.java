package com.github.elenterius.biomancy.util.animation;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public final class MobAnimations {

	private MobAnimations() {}

	public static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenPlay("walk");
	public static final RawAnimation BABY_TRANSFORM_ANIMATION = RawAnimation.begin().thenPlay("baby_transform");

	public static <T extends GeoEntity> AnimationController<T> walkController(T geoEntity) {
		return new AnimationController<>(geoEntity, "walk", state -> {
			if (state.isMoving()) {
				return state.setAndContinue(WALK_ANIMATION);
			}
			return PlayState.STOP;
		});
	}

	public static <T extends GeoEntity> AnimationController<T> babyTransformController(T geoEntity) {
		return new AnimationController<>(geoEntity, "baby_transform", state -> {
			if (state.getData(DataTickets.ENTITY_MODEL_DATA).isChild()) {
				return state.setAndContinue(BABY_TRANSFORM_ANIMATION);
			}
			return PlayState.STOP;
		});
	}

}
