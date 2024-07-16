package com.github.elenterius.biomancy.item.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

public abstract class LivingArmorGeoItem extends LivingArmorItem implements GeoItem {

	protected LivingArmorGeoItem(ArmorMaterial material, Type type, int maxNutrients, Properties properties) {
		super(material, type, maxNutrients, properties);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, @Nullable String type) {
		return "minecraft:textures/models/armor/diamond_layer_1.png"; //suppress texture not found error, ideally we shouldn't do this
	}

}
