package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.statuseffect.StatusEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class ModEffects
{
    public static UUID FRENZY_ATTACK_DAMAGE_UUID = UUID.fromString("35E6819F-064F-435C-A93E-E77022498BFA");
    public static AttributeModifier FRENZY_ATTACK_DAMAGE_MODIFIER = new AttributeModifier(FRENZY_ATTACK_DAMAGE_UUID, "confused_attack_damage", 0.25f, AttributeModifier.Operation.ADDITION);

    public static final DeferredRegister<Effect> EFFECT_REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, BlightlingsMod.MOD_ID);
    public static final RegistryObject<StatusEffect> DREAD = EFFECT_REGISTRY.register("dread", () -> (StatusEffect) new StatusEffect(EffectType.HARMFUL, 0x1f1f23)
            .addAttributesModifier(Attributes.KNOCKBACK_RESISTANCE, "011F6C08-EAD8-4F06-8F1A-05CFFA2DE55C", -0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(Attributes.ATTACK_SPEED, "D0D84456-EF73-4A52-81C6-E51713B76C90", -0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<StatusEffect> FRENZY = EFFECT_REGISTRY.register("frenzy", () -> (StatusEffect) new StatusEffect(EffectType.HARMFUL, 0x1f1f23)
            .addAttributesModifier(Attributes.ATTACK_SPEED, "FD74324D-939A-4BF3-8E3B-A3717A7E363B", 0.4f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(Attributes.ATTACK_KNOCKBACK, "B98514E1-C175-4C93-85D5-5BEF3A9CF418", 0.2f, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributesModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.3f, AttributeModifier.Operation.MULTIPLY_TOTAL));
}
