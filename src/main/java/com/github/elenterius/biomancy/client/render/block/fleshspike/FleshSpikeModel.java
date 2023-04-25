package com.github.elenterius.biomancy.client.render.block.fleshspike;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlock;
import com.github.elenterius.biomancy.block.fleshspike.FleshSpikeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FleshSpikeModel extends AnimatedGeoModel<FleshSpikeBlockEntity> {

	protected static final ResourceLocation SPIKE_1 = BiomancyMod.createRL("geo/block/flesh_spike_1.geo.json");
	protected static final ResourceLocation SPIKE_2 = BiomancyMod.createRL("geo/block/flesh_spike_2.geo.json");
	protected static final ResourceLocation SPIKE_3 = BiomancyMod.createRL("geo/block/flesh_spike_3.geo.json");
	protected static final ResourceLocation[] MODELS = new ResourceLocation[]{SPIKE_1, SPIKE_2, SPIKE_3};

	protected static final ResourceLocation TEXTURE = BiomancyMod.createRL("textures/block/flesh_spike.png");
	protected static final ResourceLocation ANIMATION = BiomancyMod.createRL("animations/block/flesh_spike.animation.json");

	@Override
	public ResourceLocation getModelResource(FleshSpikeBlockEntity blockEntity) {
		int spikes = FleshSpikeBlock.getSpikes(blockEntity.getBlockState());
		return MODELS[spikes - 1];
	}

	@Override
	public ResourceLocation getTextureResource(FleshSpikeBlockEntity blockEntity) {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationResource(FleshSpikeBlockEntity blockEntity) {
		return ANIMATION;
	}

}
