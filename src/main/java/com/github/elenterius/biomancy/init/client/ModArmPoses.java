package com.github.elenterius.biomancy.init.client;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.client.IArmPoseTransformer;

public final class ModArmPoses {

	public static HumanoidModel.ArmPose HOLD_AND_AIM_GUN_TWO_HANDED = create("hold_and_aim_gun_two_handed", true, (model, livingEntity, arm) -> {
		//based on AnimationUtils#animateCrossbowHold

		boolean rightHanded = arm == HumanoidArm.RIGHT;
		float elapsedTicks = Mth.clamp(livingEntity.getTicksUsingItem(), 0f, 5f);
		float progress = elapsedTicks / 2.5f;

		ModelPart mainHand = rightHanded ? model.rightArm : model.leftArm;
		ModelPart offHand = rightHanded ? model.leftArm : model.rightArm;

		mainHand.yRot = (rightHanded ? -0.3f : 0.3f) + model.head.yRot;

		//mainHand.xRot = -Mth.HALF_PI + model.head.xRot + 0.1f;
		mainHand.xRot = -Mth.lerp(progress, Mth.HALF_PI * 0.8f, Mth.HALF_PI) + model.head.xRot + 0.1f;

		//the off-hand is transformed in such a ways that it support the main hand

		//offHand.yRot = (rightHanded ? 0.6f : -0.6f) + model.head.yRot;
		offHand.yRot = Mth.lerp(progress, 0.774533f, 0.6f) * (rightHanded ? 1f : -1f) + model.head.yRot; // -44.3775 - 34.3775 deg

		//offHand.xRot = -1.5f + model.head.xRot;
		offHand.xRot = -Mth.lerp(progress, 1.15017f, 1.5f) + model.head.xRot; // 65.9 - 85.9 deg
	});

	private ModArmPoses() {}

	static void init() {}

	private static HumanoidModel.ArmPose create(String name, boolean twoHanded, IArmPoseTransformer poseTransformer) {
		return HumanoidModel.ArmPose.create(BiomancyMod.MOD_ID + "_" + name, twoHanded, poseTransformer);
	}

}
