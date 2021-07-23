package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.UUID;

public final class ModAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Attribute.class, BiomancyMod.MOD_ID);

	public static final double DEFAULT_ATTACK_DISTANCE = 3d;
	public static final RegistryObject<Attribute> ATTACK_DISTANCE = ATTRIBUTES.register("attack_distance", () -> new RangedAttribute("attribute.generic.attack_distance", DEFAULT_ATTACK_DISTANCE, 0.0D, 6.0D).setShouldWatch(true));

	private ModAttributes() {}

	public static Attribute getBlockReachDistance() {
		return ForgeMod.REACH_DISTANCE.get();
	}

	public static Attribute getAttackDistance() {
		return ATTACK_DISTANCE.get();
	}

	public static double getAttackReachDistance(@Nullable PlayerEntity playerEntity) {
		if (playerEntity == null) return DEFAULT_ATTACK_DISTANCE;

		ModifiableAttributeInstance attribute = playerEntity.getAttribute(ATTACK_DISTANCE.get());
		return attribute != null ? attribute.getValue() : DEFAULT_ATTACK_DISTANCE;
	}

	public static final class UUIDS {
		private UUIDS () {}
		public static final UUID ATTACK_DISTANCE = UUID.fromString("d9d02608-66a1-4490-827d-0d27a2a054d8");
		public static final UUID BLOCK_REACH_DISTANCE = UUID.fromString("3525f9ee-59e9-4624-a0f2-e1717ad320d3");
	}
}
