package com.github.elenterius.biomancy.client.model.entity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.entity.ownable.Fleshkin;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FleshkinModel<T extends Fleshkin> extends HumanoidModel<T> {

	public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(BiomancyMod.createRL("fleshkin"), "main");
	public static final ModelLayerLocation INNER_ARMOR_LAYER = new ModelLayerLocation(BiomancyMod.createRL("fleshkin"), "inner_armor");
	public static final ModelLayerLocation OUTER_ARMOR_LAYER = new ModelLayerLocation(BiomancyMod.createRL("fleshkin"), "outer_armor");

	public FleshkinModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		AnimationUtils.animateZombieArms(leftArm, rightArm, isAggressive(entity), attackTime, ageInTicks);
	}

	public boolean isAggressive(T entity) {
		return entity.isAggressive();
	}

}
