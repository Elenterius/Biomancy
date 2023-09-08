package com.github.elenterius.biomancy.integration.tetra;

import net.minecraft.world.item.Item;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.properties.IToolProvider;

import java.util.function.Consumer;

public final class TetraCompat {
	private TetraCompat() {}

	public static void init(Consumer<TetraHelper> helperSetter) {
		helperSetter.accept(new TetraCompat.TetraHelperImpl());
	}

	public static void onPostSetup() {
		//TODO: register Dragon Sinew as Primordial Cradle tribute
	}

	static final class TetraHelperImpl implements TetraHelper {
		@Override
		public boolean isToolOrModularItem(Item item) {
			return item instanceof IModularItem || item instanceof IToolProvider;
		}
	}

}
