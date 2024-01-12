package com.github.elenterius.biomancy.integration.tetra;

import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public final class TetraCompat {
	private TetraCompat() {}

	public static void init(Consumer<TetraHelper> helperSetter) {
		helperSetter.accept(new TetraCompat.TetraHelperImpl());
	}

	public static void onPostSetup() {
		registerDragonSinewAsTribute();
	}

	private static void registerDragonSinewAsTribute() {
		//		ResourceLocation dragonSinewId = new ResourceLocation(TetraMod.MOD_ID, DragonSinewItem.identifier);
		//		TetraRegistries.items.getEntries().stream()
		//				.filter(registryObject -> registryObject.getId().equals(dragonSinewId))
		//				.findAny()
		//				.ifPresent(registryObject -> Tributes.register(registryObject.get(), TributeImpl.builder().biomass(20).lifeEnergy(25).successModifier(20).hostileModifier(20).anomalyModifier(30).create()));
	}

	static final class TetraHelperImpl implements TetraHelper {
		@Override
		public boolean isToolOrModularItem(Item item) {
			return false;
			//			return item instanceof IModularItem || item instanceof IToolProvider;
		}
	}

}
