package com.github.elenterius.biomancy.mixin.accessor;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SwordItem.class)
public interface SwordItemMixinAccessor {

	@Accessor("defaultModifiers")
	Multimap<Attribute, AttributeModifier> biomancy$getDefaultModifiers();

}
