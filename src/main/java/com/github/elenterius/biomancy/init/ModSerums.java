package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.serum.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModSerums {

	public static final DeferredRegister<Serum> SERUMS = DeferredRegister.create(BiomancyMod.createRL("serum"), BiomancyMod.MOD_ID);
	public static final Supplier<IForgeRegistry<Serum>> REGISTRY = SERUMS.makeRegistry(RegistryBuilder::new);

	public static final RegistryObject<Serum> EMPTY = SERUMS.register("empty", () -> BasicSerum.EMPTY);

	public static final RegistryObject<AgeingSerum> AGEING_SERUM = SERUMS.register("ageing_serum", () -> new AgeingSerum(0xACBE00));
	public static final RegistryObject<EnlargementSerum> ENLARGEMENT_SERUM = SERUMS.register("enlargement_serum", () -> new EnlargementSerum(0xDD5225));
	public static final RegistryObject<ShrinkingSerum> SHRINKING_SERUM = SERUMS.register("shrinking_serum", () -> new ShrinkingSerum(0x819D5A));
	public static final RegistryObject<RejuvenationSerum> REJUVENATION_SERUM = SERUMS.register("rejuvenation_serum", () -> new RejuvenationSerum(0x75F36F));
	public static final RegistryObject<BreedingSerum> BREEDING_STIMULANT = SERUMS.register("breeding_stimulant", () -> new BreedingSerum(0x70174E));
	public static final RegistryObject<AbsorptionSerum> ABSORPTION_BOOST = SERUMS.register("absorption_boost", () -> new AbsorptionSerum(0xF5A21F));
	public static final RegistryObject<InsomniaCureSerum> INSOMNIA_CURE = SERUMS.register("insomnia_cure", () -> new InsomniaCureSerum(0x9B70B2));
	public static final RegistryObject<CleansingSerum> CLEANSING_SERUM = SERUMS.register("cleansing_serum", () -> new CleansingSerum(0x371667));
	public static final RegistryObject<FrenzySerum> FRENZY_SERUM = SERUMS.register("frenzy_serum", () -> new FrenzySerum(0xD1001C));

	private ModSerums() {}

}
