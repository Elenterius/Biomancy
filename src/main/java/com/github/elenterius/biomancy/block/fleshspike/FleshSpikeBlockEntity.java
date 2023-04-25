package com.github.elenterius.biomancy.block.fleshspike;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class FleshSpikeBlockEntity extends BlockEntity implements IAnimatable {

	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

	public FleshSpikeBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlockEntities.FLESH_SPIKE.get(), pos, blockState);
	}

	//	private <E extends BlockEntity & IAnimatable> PlayState handleAnim(AnimationEvent<E> event) {
	//		return PlayState.CONTINUE;
	//	}

	@Override
	public void registerControllers(AnimationData data) {
		//		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::handleAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

}
