package com.github.elenterius.blightlings.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SwordItem.class)
public interface SwordItemMixinAccessor {
	@Accessor
	Multimap<Attribute, AttributeModifier> getAttributeModifiers();
}
