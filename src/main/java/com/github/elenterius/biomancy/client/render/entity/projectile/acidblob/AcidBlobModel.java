package com.github.elenterius.biomancy.client.render.entity.projectile.acidblob;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.AcidBlobProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AcidBlobModel extends DefaultedEntityGeoModel<AcidBlobProjectile> {

	public AcidBlobModel() {
		super(BiomancyMod.createRL("projectile/acid_blob"));
	}

	@Override
	public RenderType getRenderType(AcidBlobProjectile animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}

}
