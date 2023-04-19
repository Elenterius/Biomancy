package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;

import java.util.UUID;

public final class ModAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Attribute.class, BiomancyMod.MOD_ID);

	private ModAttributes() {}

	public static Attribute getBlockReach() {
		return ForgeMod.REACH_DISTANCE.get();
	}

	public static final class UUIDS {
		public static final UUID BLOCK_REACH = UUID.fromString("3525f9ee-59e9-4624-a0f2-e1717ad320d3");

		private UUIDS() {}
	}

}
