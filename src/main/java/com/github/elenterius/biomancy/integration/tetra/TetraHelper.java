package com.github.elenterius.biomancy.integration.tetra;

import net.minecraft.world.item.Item;

public sealed interface TetraHelper permits TetraCompat.TetraHelperImpl, TetraHelper.EmptyTetraHelper {

	TetraHelper EMPTY = new TetraHelper.EmptyTetraHelper();

	boolean isToolOrModularItem(Item item);

	final class EmptyTetraHelper implements TetraHelper {
		@Override
		public boolean isToolOrModularItem(Item item) {
			return false;
		}
	}

}
