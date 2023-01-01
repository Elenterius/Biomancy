package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModAttributes {
	
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, BiomancyMod.MOD_ID);

	private ModAttributes() {}

}
