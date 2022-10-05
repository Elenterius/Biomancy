package com.github.elenterius.biomancy.world.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IArmPoseProvider {

	HumanoidModel.ArmPose getArmPose(Player player, InteractionHand usedHand, ItemStack stack);

}
