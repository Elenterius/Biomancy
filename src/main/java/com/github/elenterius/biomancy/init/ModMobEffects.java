package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.statuseffect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMobEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<CorrosiveEffect> CORROSIVE = EFFECTS.register("corrosive", () -> new CorrosiveEffect(MobEffectCategory.HARMFUL, 0x39FF14));
	public static final RegistryObject<StatusEffect> ARMOR_SHRED = EFFECTS.register("armor_shred", () -> new ArmorShredEffect(MobEffectCategory.HARMFUL, 20, 0x909090)
			.addModifier(Attributes.ARMOR, "a15ed03e-c5db-4cf8-a0f5-4eb4657bb731", -1f, AttributeModifier.Operation.ADDITION));
	public static final RegistryObject<BleedEffect> BLEED = EFFECTS.register("bleed", () -> new BleedEffect(MobEffectCategory.HARMFUL, 0x8a0303, 2));

	public static final RegistryObject<EssenceAnemiaEffect> ESSENCE_ANEMIA = EFFECTS.register("essence_anemia", () -> new EssenceAnemiaEffect(MobEffectCategory.HARMFUL, 0xfefefe)
			.addModifier(Attributes.MAX_HEALTH, "a6ca3300-17d9-41c7-b29d-af93fa367b23", -0.2f, AttributeModifier.Operation.MULTIPLY_BASE)
	);
	public static final RegistryObject<DespoilEffect> DESPOIL = EFFECTS.register("despoil", () -> new DespoilEffect(MobEffectCategory.BENEFICIAL, 0xdd77ff));
	public static final RegistryObject<LibidoEffect> LIBIDO = EFFECTS.register("libido", () -> new LibidoEffect(MobEffectCategory.NEUTRAL, 0xe06a78));
	public static final RegistryObject<DrowsyEffect> DROWSY = EFFECTS.register("drowsy", () -> new DrowsyEffect(MobEffectCategory.NEUTRAL, 0x9b70b2));

	public static final RegistryObject<AdrenalineEffect> ADRENALINE_RUSH = EFFECTS.register("adrenaline_rush", () -> new AdrenalineEffect(MobEffectCategory.BENEFICIAL, 0xff9532)
			.addAttackDamageModifier("1f1fb00f-d6bc-4b42-8533-422054cea63d", 4f, 0, AttributeModifier.Operation.ADDITION)
			.addModifier(Attributes.MOVEMENT_SPEED, "14e2a39c-abb5-43a4-9449-522eec57ff2e", 0.225f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_SPEED, "08a20d5b-60ce-4769-9e67-71cab0abe989", 0.175f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final RegistryObject<AdrenalineEffect> ADRENAL_FATIGUE = EFFECTS.register("adrenal_fatigue", () -> new AdrenalineEffect(MobEffectCategory.HARMFUL, 0x60443f)
			.addAttackDamageModifier("8dadcbe5-9098-4545-b07c-3e9120c84232", -4, 0, AttributeModifier.Operation.ADDITION)
			.addModifier(Attributes.MOVEMENT_SPEED, "0f1be88c-cbb2-455c-8559-0b420caa980d", -0.225f, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addModifier(Attributes.ATTACK_SPEED, "ab116bd1-196b-4bf8-a136-6c24e7c0e80d", -0.125f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	private ModMobEffects() {}

}
