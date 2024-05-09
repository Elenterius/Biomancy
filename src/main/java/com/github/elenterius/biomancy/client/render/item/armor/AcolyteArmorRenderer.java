package com.github.elenterius.biomancy.client.render.item.armor;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class AcolyteArmorRenderer extends GeoArmorRenderer<AcolyteArmorItem> {

	public AcolyteArmorRenderer() {
		super(new DefaultedItemGeoModel<>(BiomancyMod.createRL("armor/acolyte_armor")));
	}

	@Override
	public RenderType getRenderType(AcolyteArmorItem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityCutout(texture);
	}

}