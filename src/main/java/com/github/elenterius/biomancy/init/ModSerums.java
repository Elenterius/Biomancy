package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.serum.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModSerums {

	public static final DeferredRegister<Serum> SERUMS = DeferredRegister.create(Serum.class, BiomancyMod.MOD_ID);
	public static final Supplier<IForgeRegistry<Serum>> REGISTRY = SERUMS.makeRegistry("serum", RegistryBuilder::new);

	public static final RegistryObject<GrowthSerum> GROWTH_SERUM = SERUMS.register("growth_serum", () -> new GrowthSerum(0xb9d6c2));
	public static final RegistryObject<RejuvenationSerum> REJUVENATION_SERUM = SERUMS.register("rejuvenation_serum", () -> new RejuvenationSerum(0x4cbb17));
	public static final RegistryObject<BreedingSerum> BREEDING_STIMULANT = SERUMS.register("breeding_stimulant", () -> new BreedingSerum(0xe4658e));
	public static final RegistryObject<AbsorptionSerum> ABSORPTION_BOOST = SERUMS.register("absorption_boost", () -> new AbsorptionSerum(0xe7bd42));
	public static final RegistryObject<InsomniaCureSerum> INSOMNIA_CURE = SERUMS.register("insomnia_cure", () -> new InsomniaCureSerum(0xa79ca1));
	public static final RegistryObject<CleansingSerum> CLEANSING_SERUM = SERUMS.register("cleansing_serum", () -> new CleansingSerum(0x97a399));
	public static final RegistryObject<DecaySerum> DECAY_AGENT = SERUMS.register("decay_agent", () -> new DecaySerum(0x8d4e85));
	//	public static final RegistryObject<MutagenReagent> MUTAGEN_SERUM = SERUMS.register("mutagen_serum", () -> new MutagenReagent(0x60963a));
	public static final RegistryObject<AdrenalineSerum> ADRENALINE_SERUM = SERUMS.register("adrenaline_serum", () -> new AdrenalineSerum(0xff9532));

	private ModSerums() {}

}
