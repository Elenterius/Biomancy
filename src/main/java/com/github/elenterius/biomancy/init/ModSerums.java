package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class ModSerums {

	public static final DeferredRegister<Serum> SERUMS = DeferredRegister.create(Serum.class, BiomancyMod.MOD_ID);
	public static final Supplier<IForgeRegistry<Serum>> REGISTRY = SERUMS.makeRegistry("serum", RegistryBuilder::new);

//	public static final RegistryObject<GrowthReagent> GROWTH_SERUM = REAGENTS.register("growth_serum", () -> new GrowthReagent(0xb9d6c2));
//	public static final RegistryObject<RejuvenationReagent> REJUVENATION_SERUM = REAGENTS.register("rejuvenation_serum", () -> new RejuvenationReagent(0x4cbb17));
//	public static final RegistryObject<BreedingReagent> BREEDING_STIMULANT = REAGENTS.register("breeding_stimulant", () -> new BreedingReagent(0xe4658e));
//	public static final RegistryObject<AbsorptionReagent> ABSORPTION_BOOST = REAGENTS.register("absorption_boost", () -> new AbsorptionReagent(0xe7bd42));
//	public static final RegistryObject<InsomniaCureReagent> INSOMNIA_CURE = REAGENTS.register("insomnia_cure", () -> new InsomniaCureReagent(0xa79ca1));
//	public static final RegistryObject<CleansingReagent> CLEANSING_SERUM = REAGENTS.register("cleansing_serum", () -> new CleansingReagent(0x97a399));
//	public static final RegistryObject<DecayReagent> DECAY_AGENT = REAGENTS.register("decay_agent", () -> new DecayReagent(0x8d4e85));
//	public static final RegistryObject<MutagenReagent> MUTAGEN_SERUM = REAGENTS.register("mutagen_serum", () -> new MutagenReagent(0x60963a));
//	public static final RegistryObject<AdrenalineReagent> ADRENALINE_SERUM = REAGENTS.register("adrenaline_serum", () -> new AdrenalineReagent(0xff9532));
//
//	public static final RegistryObject<DNASampleReagent> BLOOD_SAMPLE = REAGENTS.register("blood_sample", () -> new DNASampleReagent(0x660000));
//	public static final RegistryObject<RottenBloodReagent> ROTTEN_BLOOD_SAMPLE = REAGENTS.register("rotten_blood", () -> new RottenBloodReagent(0x470000));
//	public static final RegistryObject<BoneMarrowReagent> BONE_MARROW_SAMPLE = REAGENTS.register("bone_marrow", () -> new BoneMarrowReagent(0xefd9a8));

	private ModSerums() {}

}
