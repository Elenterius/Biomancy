package com.github.elenterius.biomancy.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public abstract class CustomGeoBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {

	protected CustomGeoBlockRenderer(GeoModel<T> model) {
		super(model);
	}

	@Override
	public int getViewDistance() {
		return 96;
	}

	/**
	 * Max distance at which animations are still played.
	 *
	 * @return value smaller or equal to the view distance
	 */
	public int getAnimationDistance() {
		return 48; //FIXME: NOT FUNCTIONAL ATM
	}

	public boolean shouldAnimate(T blockEntity) {
		Vec3 cameraPos = Minecraft.getInstance().getBlockEntityRenderDispatcher().camera.getPosition();
		return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(cameraPos, getAnimationDistance());
	}

}
