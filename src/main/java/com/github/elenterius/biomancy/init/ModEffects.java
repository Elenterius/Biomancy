package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.statuseffect.AdrenalineEffect;
import com.github.elenterius.biomancy.statuseffect.FleshEatingDiseaseEffect;
import com.github.elenterius.biomancy.statuseffect.RavenousHungerEffect;
import com.github.elenterius.biomancy.statuseffect.StatusEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModEffects {
	public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, BiomancyMod.MOD_ID);

	public static final RegistryObject<StatusEffect> ATTRACTED = EFFECTS.register("attracted", () -> new StatusEffect(EffectType.HARMFUL, 0xc376cf, false) {
		@Override
		public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
			if (!livingEntity.level.isClientSide) ModNetworkHandler.sendCustomEntityEventToClients(livingEntity, 0);
		}

		@Override
		public boolean isDurationEffectTick(int duration, int amplifier) {
			return duration % 30 == 0;
		}
	});
	public static final RegistryObject<StatusEffect> REPULSED = EFFECTS.register("repulsed", () -> new StatusEffect(EffectType.HARMFUL, 0xc376cf, false) {
		@Override
		public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
			if (!livingEntity.level.isClientSide) ModNetworkHandler.sendCustomEntityEventToClients(livingEntity, 1);
		}

		@Override
		public boolean isDurationEffectTick(int duration, int amplifier) {
			return duration % 30 == 0;
		}
	});

	public static final RegistryObject<RavenousHungerEffect> RAVENOUS_HUNGER = EFFECTS.register("ravenous_hunger", () -> new RavenousHungerEffect(EffectType.NEUTRAL, 0xce0018)
			.addModifier(Attributes.ATTACK_DAMAGE, "20e38c06-1506-499f-8b54-ec8a52539737", 0.25f, AttributeModifier.Operation.ADDITION)
			.addModifier(Attributes.ATTACK_SPEED, "FD74324D-939A-4BF3-8E3B-A3717A7E363B", 0.25f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_KNOCKBACK, "B98514E1-C175-4C93-85D5-5BEF3A9CF418", 0.15f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final RegistryObject<FleshEatingDiseaseEffect> FLESH_EATING_DISEASE = EFFECTS.register("flesh_eating_disease", () -> new FleshEatingDiseaseEffect(EffectType.HARMFUL, 0xcc33cc)
			.addModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final RegistryObject<AdrenalineEffect> ADRENALINE_RUSH = EFFECTS.register("adrenaline_rush", () -> new AdrenalineEffect(EffectType.BENEFICIAL, 0xff9532)
			.addAttackDamageModifier("1f1fb00f-d6bc-4b42-8533-422054cea63d", 4f, 0, AttributeModifier.Operation.ADDITION)
			.addModifier(Attributes.MOVEMENT_SPEED, "14e2a39c-abb5-43a4-9449-522eec57ff2e", 0.225f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_SPEED, "08a20d5b-60ce-4769-9e67-71cab0abe989", 0.175f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final RegistryObject<AdrenalineEffect> ADRENAL_FATIGUE = EFFECTS.register("adrenal_fatigue", () -> new AdrenalineEffect(EffectType.HARMFUL, 0x60443f)
			.addAttackDamageModifier("8dadcbe5-9098-4545-b07c-3e9120c84232", -4, 0, AttributeModifier.Operation.ADDITION)
			.addModifier(Attributes.MOVEMENT_SPEED, "0f1be88c-cbb2-455c-8559-0b420caa980d", -0.225f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_SPEED, "ab116bd1-196b-4bf8-a136-6c24e7c0e80d", -0.125f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	private ModEffects() {}
}
