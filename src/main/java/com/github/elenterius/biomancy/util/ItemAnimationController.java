package com.github.elenterius.biomancy.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import java.util.HashMap;
import java.util.List;

public class ItemAnimationController<T extends Item & IAnimatable> extends AnimationController<T> {

	public ItemAnimationController(T animatable, String name, float transitionLengthTicks, IAnimationPredicate<T> animationPredicate) {
		super(animatable, name, transitionLengthTicks, animationPredicate);
	}

	private ItemStack currentStack = ItemStack.EMPTY;

	@Override
	public void process(double tick, AnimationEvent<T> event, List<IBone> modelRendererList, HashMap<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection, MolangParser parser, boolean crashWhenCantFindBone) {
		currentStack = event.getExtraData().stream().filter(ItemStack.class::isInstance).map(ItemStack.class::cast).findFirst().orElse(ItemStack.EMPTY);
		super.process(tick, event, modelRendererList, boneSnapshotCollection, parser, crashWhenCantFindBone);
	}

	// MIXIN TARGET
	// processCurrentAnimation
	// AT
	// SoundKeyframeEvent<T> event = new SoundKeyframeEvent

}
