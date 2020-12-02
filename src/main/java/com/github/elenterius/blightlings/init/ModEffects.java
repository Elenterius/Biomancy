package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.statuseffect.StatusEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEffects
{
    public static final DeferredRegister<Effect> EFFECT_REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, BlightlingsMod.MOD_ID);

    public static final RegistryObject<StatusEffect> DREAD = EFFECT_REGISTRY.register("dread", () -> (StatusEffect) new StatusEffect(EffectType.HARMFUL, 0x1f1f23, false)
            .addAttributesModifier(Attributes.KNOCKBACK_RESISTANCE, "011F6C08-EAD8-4F06-8F1A-05CFFA2DE55C", -0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(Attributes.ATTACK_SPEED, "D0D84456-EF73-4A52-81C6-E51713B76C90", -0.4f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(Attributes.ATTACK_DAMAGE, "f568d23e-515f-401a-af2b-b64d8f227fe7", -0.3f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(ForgeMod.REACH_DISTANCE.get(), "0d243fc9-2fcc-4ec8-b30e-66d5e1a4aa4e", -1f, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<StatusEffect> FRENZY = EFFECT_REGISTRY.register("frenzy", () -> (StatusEffect) new StatusEffect(EffectType.NEUTRAL, 0xce0018)
            .addAttributesModifier(Attributes.ATTACK_DAMAGE, "20e38c06-1506-499f-8b54-ec8a52539737", 0.25f, AttributeModifier.Operation.ADDITION)
            .addAttributesModifier(Attributes.ATTACK_SPEED, "FD74324D-939A-4BF3-8E3B-A3717A7E363B", 0.25f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(Attributes.ATTACK_KNOCKBACK, "B98514E1-C175-4C93-85D5-5BEF3A9CF418", 0.15f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<StatusEffect> BLIGHT_INFECTION = EFFECT_REGISTRY.register("blight_infection", () -> (StatusEffect) new StatusEffect(EffectType.HARMFUL, 0xcc33cc)
            .addAttributesModifier(Attributes.ARMOR_TOUGHNESS, "934873c2-0168-474f-a090-7d4e89e18090", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));
}
