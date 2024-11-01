package com.github.elenterius.biomancy.item.armor;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class AcolyteArmorUpgrades {

	private static final List<Upgrade> UPGRADES = new ArrayList<>();

	public static final Upgrade PRIMORDIAL_SIGHT = register("primordial_sight");

	private AcolyteArmorUpgrades() {}

	public static Upgrade register(ResourceLocation name) {
		Upgrade upgrade = new Upgrade(name);
		UPGRADES.add(upgrade);
		return upgrade;
	}

	private static Upgrade register(String name) {
		Upgrade upgrade = new Upgrade(BiomancyMod.createRL(name));
		UPGRADES.add(upgrade);
		return upgrade;
	}

	public static ItemStack addUpgrade(ItemStack stack, Upgrade upgrade) {
		if (stack.is(ModItems.ACOLYTE_ARMOR_HELMET.get())) {
			stack.getOrCreateTag().putBoolean(upgrade.id.toString(), true);
		}
		return stack;
	}

	public static boolean hasUpgrade(ItemStack stack, Upgrade upgrade) {
		if (stack.is(ModItems.ACOLYTE_ARMOR_HELMET.get())) {
			CompoundTag tag = stack.getTag();
			return tag != null && tag.contains(upgrade.id.toString());
		}
		return false;
	}

	public static void appendHoverText(ItemStack stack, List<Component> tooltip) {
		for (Upgrade upgrade : UPGRADES) {
			if (hasUpgrade(stack, upgrade)) {
				tooltip.add(ComponentUtil.emptyLine());
				upgrade.appendHoverText(stack, tooltip);
			}
		}
	}

	public static class Upgrade {

		private final ResourceLocation id;

		public Upgrade(ResourceLocation id) {
			this.id = id;
		}

		public ResourceLocation getId() {
			return id;
		}

		public void appendHoverText(ItemStack stack, List<Component> tooltip) {
			String translationKey = id.getPath();
			tooltip.add(TextComponentUtil.getAbilityText(translationKey).withStyle(TextStyles.GRAY));
			tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText(translationKey + ".desc")).withStyle(TextStyles.DARK_GRAY));
		}

	}

}