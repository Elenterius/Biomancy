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

}
