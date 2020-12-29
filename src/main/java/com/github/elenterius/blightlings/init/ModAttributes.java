package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nullable;

public final class ModAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Attribute.class, BlightlingsMod.MOD_ID);
	public static final double DEFAULT_ATTACK_REACH_DISTANCE = 3d;
	public static final RegistryObject<Attribute> ATTACK_REACH_DISTANCE = ATTRIBUTES.register("attack_reach_distance", () -> new RangedAttribute("attribute.generic.attack_distance", DEFAULT_ATTACK_REACH_DISTANCE, 0.0D, 6.0D).setShouldWatch(true));

	private ModAttributes() {}

	public static Attribute getBlockReachDistance() {
		return ForgeMod.REACH_DISTANCE.get();
	}

	public static Attribute getAttackReachDistance() {
		return ATTACK_REACH_DISTANCE.get();
	}

	public static double getAttackReachDistance(@Nullable PlayerEntity playerEntity) {
		if (playerEntity == null) return DEFAULT_ATTACK_REACH_DISTANCE;

		ModifiableAttributeInstance attribute = playerEntity.getAttribute(ATTACK_REACH_DISTANCE.get());
		return attribute != null ? attribute.getValue() : DEFAULT_ATTACK_REACH_DISTANCE;
	}

}
