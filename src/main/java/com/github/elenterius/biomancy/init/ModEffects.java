package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
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

	public static final RegistryObject<StatusEffect> ARMOR_BRITTLENESS = EFFECTS.register("armor_brittleness", () -> new StatusEffect(EffectType.HARMFUL, 0x1f1f23)
			.addModifier(Attributes.ARMOR_TOUGHNESS, "934873c2-0168-474f-a090-7d4e89e18090", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final RegistryObject<RavenousHungerEffect> RAVENOUS_HUNGER = EFFECTS.register("ravenous_hunger", () -> new RavenousHungerEffect(EffectType.NEUTRAL, 0xce0018)
			.addModifier(Attributes.ATTACK_DAMAGE, "20e38c06-1506-499f-8b54-ec8a52539737", 0.25f, AttributeModifier.Operation.ADDITION)
			.addModifier(Attributes.ATTACK_SPEED, "FD74324D-939A-4BF3-8E3B-A3717A7E363B", 0.25f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_KNOCKBACK, "B98514E1-C175-4C93-85D5-5BEF3A9CF418", 0.15f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final RegistryObject<FleshEatingDiseaseEffect> FLESH_EATING_DISEASE = EFFECTS.register("flesh_eating_disease", () -> new FleshEatingDiseaseEffect(EffectType.HARMFUL, 0xcc33cc)
			.addModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	private ModEffects() {}
}
