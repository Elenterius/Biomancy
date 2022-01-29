package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.statuseffect.EssenceAnemiaEffect;
import com.github.elenterius.biomancy.world.statuseffect.FleshEatingDiseaseEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMobEffects {

	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BiomancyMod.MOD_ID);

	public static final RegistryObject<FleshEatingDiseaseEffect> FLESH_EATING_DISEASE = EFFECTS.register("flesh_eating_disease", () -> new FleshEatingDiseaseEffect(MobEffectCategory.HARMFUL, 0xcc33cc)
			.addModifier(Attributes.MAX_HEALTH, "99DD10E5-2682-4C0D-8F8D-0FED3CE2D3F9", -0.1f, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<EssenceAnemiaEffect> ESSENCE_ANEMIA = EFFECTS.register("essence_anemia", () -> new EssenceAnemiaEffect(MobEffectCategory.HARMFUL, 0x986c76));

	private ModMobEffects() {}

}
