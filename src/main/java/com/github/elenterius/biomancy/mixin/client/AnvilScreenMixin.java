package com.github.elenterius.biomancy.mixin.client;

import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.util.TransliterationUtil;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

	@Shadow
	@Final
	private Player player;

	private AnvilScreenMixin(AnvilMenu menu, Inventory playerInventory, Component title, ResourceLocation texture) {
		super(menu, playerInventory, title, texture);
	}

	@ModifyVariable(method = "onNameChanged", at = @At(value = "HEAD"), argsOnly = true)
	private String onNameChanged(String text) {
		if (menu.getSlot(0).hasItem() && player.hasEffect(ModMobEffects.PRIMORDIAL_INFESTATION.get())) {
			return TransliterationUtil.transliterate(text);
		}

		return text;
	}

}
