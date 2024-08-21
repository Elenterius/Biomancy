package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.function.Supplier;

public class ModPotions {

	public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, BiomancyMod.MOD_ID);

	public static final RegistryObject<Potion> GASTRIC_JUICE = register("gastric_juice", () -> new MobEffectInstance[]{
			new MobEffectInstance(ModMobEffects.CORROSIVE.get(), 20 * 4, 0),
			new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 20 * (4 + 3), 0)
	});

	public static final RegistryObject<Potion> PRIMORDIAL_INFESTATION = register("primordial_infestation", () -> new MobEffectInstance[]{
			new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION.get(), 20 * 90, 0),
	});

	public static final RegistryObject<Potion> LONG_PRIMORDIAL_INFESTATION = register("primordial_infestation", PotionVariant.LONG, () -> new MobEffectInstance[]{
			new MobEffectInstance(ModMobEffects.PRIMORDIAL_INFESTATION.get(), 20 * 240, 0),
	});

	private ModPotions() {}

	private static RegistryObject<Potion> register(String name, Supplier<MobEffectInstance[]> effects) {
		String translationKey = BiomancyMod.MOD_ID + "." + name;
		return POTIONS.register(name, () -> new Potion(translationKey, effects.get()));
	}

	private static RegistryObject<Potion> register(String name, PotionVariant variant, Supplier<MobEffectInstance[]> effects) {
		String key = variant.name().toLowerCase(Locale.ENGLISH) + "_" + name;
		String translationKey = BiomancyMod.MOD_ID + "." + name;
		return POTIONS.register(key, () -> new Potion(translationKey, effects.get()));
	}

	private enum PotionVariant {
		LONG, STRONG
	}

}
