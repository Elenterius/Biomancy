package com.github.elenterius.biomancy.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * @deprecated use forges {@link IClientItemExtensions#getArmPose} instead
 */
@Deprecated(forRemoval = true)
public interface IArmPoseProvider {

	HumanoidModel.ArmPose getArmPose(Player player, InteractionHand usedHand, ItemStack stack);

}
