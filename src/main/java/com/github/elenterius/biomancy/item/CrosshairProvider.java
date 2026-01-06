package com.github.elenterius.biomancy.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface CrosshairProvider {

	int TEXTURE_SIZE = 31;

	ResourceLocation getCrosshairTexture(ItemStack stack, Player player);

}
