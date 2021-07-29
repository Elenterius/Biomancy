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

	@Deprecated //TODO: remove when forge merges https://github.com/MinecraftForge/MinecraftForge/pull/7808
	public static final RegistryObject<Attribute> ATTACK_DISTANCE_MODIFIER = ATTRIBUTES.register("attack_distance", () -> new RangedAttribute("attribute.generic.attack_distance", 0, -5, 5).setShouldWatch(true));

	private ModAttributes() {}

	public static Attribute getBlockReachDistance() {
		return ForgeMod.REACH_DISTANCE.get();
	}

	@Deprecated
	public static Attribute getAttackDistanceModifier() {
		return ATTACK_DISTANCE_MODIFIER.get();
	}

	public static double getValue(@Nullable PlayerEntity playerEntity, Attribute attribute) {
		if (playerEntity == null) return attribute.getDefaultValue();

		ModifiableAttributeInstance instance = playerEntity.getAttribute(attribute);
		return instance != null ? instance.getValue() : attribute.getDefaultValue();
	}

	@Deprecated
	public static double getAttackReachDistanceModifier(@Nullable PlayerEntity playerEntity) {
		return getValue(playerEntity, getAttackDistanceModifier());
	}

	public static double getBlockReachDistance(@Nullable PlayerEntity playerEntity) {
		return getValue(playerEntity, getBlockReachDistance());
	}

	@Deprecated
	public static double getDefaultCombinedReachDistance() {
		return getBlockReachDistance().getDefaultValue() + getAttackDistanceModifier().getDefaultValue() - 0.5d;
	}

	@Deprecated
	public static double getCombinedReachDistance(@Nullable PlayerEntity playerEntity) {
		if (playerEntity == null) return getDefaultCombinedReachDistance();
		double reachDistance = getBlockReachDistance(playerEntity);
		double modifier = getAttackReachDistanceModifier(playerEntity);
		return reachDistance + modifier - (playerEntity.isCreative() ? 0 : 0.5d);
	}

	public static final class UUIDS {
		private UUIDS() {}

		public static final UUID ATTACK_DISTANCE_MODIFIER = UUID.fromString("d9d02608-66a1-4490-827d-0d27a2a054d8");
		public static final UUID BLOCK_REACH_DISTANCE = UUID.fromString("3525f9ee-59e9-4624-a0f2-e1717ad320d3");
	}

}
