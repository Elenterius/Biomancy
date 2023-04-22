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

	public static final RegistryObject<AgeingSerum> AGEING_SERUM = SERUMS.register("ageing_serum", () -> new AgeingSerum(0x09DF5B));
	public static final RegistryObject<EnlargementSerum> ENLARGEMENT_SERUM = SERUMS.register("enlargement_serum", () -> new EnlargementSerum(0x09DF5B));
	public static final RegistryObject<ShrinkingSerum> SHRINKING_SERUM = SERUMS.register("shrinking_serum", () -> new ShrinkingSerum(0x69CB49));
	public static final RegistryObject<RejuvenationSerum> REJUVENATION_SERUM = SERUMS.register("rejuvenation_serum", () -> new RejuvenationSerum(0x69CB49));
	public static final RegistryObject<BreedingSerum> BREEDING_STIMULANT = SERUMS.register("breeding_stimulant", () -> new BreedingSerum(0x70174E));
	public static final RegistryObject<AbsorptionSerum> ABSORPTION_BOOST = SERUMS.register("absorption_boost", () -> new AbsorptionSerum(0xFFE114));
	public static final RegistryObject<InsomniaCureSerum> INSOMNIA_CURE = SERUMS.register("insomnia_cure", () -> new InsomniaCureSerum(0x9B70B2));
	public static final RegistryObject<CleansingSerum> CLEANSING_SERUM = SERUMS.register("cleansing_serum", () -> new CleansingSerum(0x371667));

	//	public static final RegistryObject<DecaySerum> DECAY_AGENT = SERUMS.register("decay_agent", () -> new DecaySerum(0x8d4e85));
	//	public static final RegistryObject<AdrenalineSerum> ADRENALINE_SERUM = SERUMS.register("adrenaline_serum", () -> new AdrenalineSerum(0x8F1834));

	private ModSerums() {}

	//	@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	//	private static class CommonHandler {
	//		private static final Map<Serum, SerumItem> SERUM_TO_ITEM_MAP = new IdentityHashMap<>();
	//		@Nullable
	//		public static SerumItem getItemForSerum(Serum serum) {
	//			return SERUM_TO_ITEM_MAP.get(serum);
	//		}
	//		@SubscribeEvent
	//		public static void onSetup(FMLCommonSetupEvent event) {
	//			Set<Map.Entry<ResourceKey<Item>, Item>> entries = ForgeRegistries.ITEMS.getEntries();
	////			entries.stream().map(Map.Entry::getValue)
	////					.filter(SerumItem.class::isInstance).map(SerumItem.class::cast)
	////					.map(item -> item.getSerum(new ItemStack(item)))
	////					.filter(serum -> !serum.isEmpty())
	////					.forEach(SERUM_TO_ITEM_MAP);
	//		}
	//	}
}
