package com.github.elenterius.biomancy.client.render.entity.fleshblob;

import com.github.elenterius.biomancy.entity.fleshblob.EaterFleshBlob;
import com.github.elenterius.biomancy.init.client.ModRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Locale;

public class FleshBlobRenderer extends AbstractFleshBlobRenderer<EaterFleshBlob> {

	public FleshBlobRenderer(EntityRendererProvider.Context context) {
		super(context, new FleshBlobModel<>());
	}

	@Override
	public RenderType getRenderType(EaterFleshBlob fleshBlob, float partialTicks, PoseStack stack, @Nullable MultiBufferSource buffer, @Nullable VertexConsumer vertexBuilder, int packedLight, ResourceLocation textureLocation) {
		if (fleshBlob.hasCustomName()) {
			Component customName = fleshBlob.getCustomName();
			if (customName != null) {
				String name = customName.getContents().toString().toLowerCase(Locale.ENGLISH);
				if (name.contains("party_blob")) {
					return ModRenderTypes.getCutoutPartyTime(textureLocation);
				}
			}
		}
		return RenderType.entityCutout(textureLocation);
	}

}
