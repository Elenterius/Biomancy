package com.github.elenterius.biomancy.integration.tetra;

import com.github.elenterius.biomancy.api.tribute.SimpleTribute;
import com.github.elenterius.biomancy.api.tribute.Tributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.TetraRegistries;
import se.mickelus.tetra.items.loot.DragonSinewItem;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.properties.IToolProvider;

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
		ResourceLocation dragonSinewId = new ResourceLocation(TetraMod.MOD_ID, DragonSinewItem.identifier);
		TetraRegistries.items.getEntries().stream()
				.filter(registryObject -> registryObject.getId().equals(dragonSinewId))
				.findAny()
				.ifPresent(registryObject -> Tributes.register(registryObject.get(), SimpleTribute.builder().biomass(20).lifeEnergy(25).successModifier(20).hostileModifier(20).anomalyModifier(30).build()));
	}

	static final class TetraHelperImpl implements TetraHelper {
		@Override
		public boolean isToolOrModularItem(Item item) {
			return item instanceof IModularItem || item instanceof IToolProvider;
		}
	}

}
