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

	public static final RegistryObject<CorrosiveEffect> CORROSIVE = EFFECTS.register("corrosive", () -> new CorrosiveEffect(MobEffectCategory.HARMFUL, 0xbee040));
	public static final RegistryObject<StatusEffect> ARMOR_SHRED = EFFECTS.register("armor_shred", () -> new ArmorShredEffect(MobEffectCategory.HARMFUL, 20, 0xbee040)
			.addModifier(Attributes.ARMOR, "a15ed03e-c5db-4cf8-a0f5-4eb4657bb731", -1f, AttributeModifier.Operation.ADDITION));

	public static final RegistryObject<FleshEatingDiseaseEffect> FLESH_EATING_DISEASE = EFFECTS.register("flesh_eating_disease", () -> new FleshEatingDiseaseEffect(MobEffectCategory.HARMFUL, 0xcc33cc)
			.addModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));

	public static final RegistryObject<EssenceAnemiaEffect> ESSENCE_ANEMIA = EFFECTS.register("essence_anemia", () -> new EssenceAnemiaEffect(MobEffectCategory.HARMFUL, 0x986c76));
	public static final RegistryObject<LibidoEffect> LIBIDO = EFFECTS.register("libido", () -> new LibidoEffect(MobEffectCategory.NEUTRAL, 0xe06a78));

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
