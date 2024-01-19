package com.github.elenterius.biomancy.client.render.entity.projectile.acidblob;

import com.github.elenterius.biomancy.entity.projectile.AcidBlobProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AcidBlobProjectileRenderer extends GeoEntityRenderer<AcidBlobProjectile> {

	public AcidBlobProjectileRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new AcidBlobModel());
	}

	//poseStack.translate(0, projectile.getBbHeight() / 2f, 0);
	//poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, projectile.yRotO, projectile.getYRot()) + 180f)); //wtf?
	//poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, projectile.xRotO, projectile.getXRot())));
}
