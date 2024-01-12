package com.github.elenterius.biomancy.item;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;

public final class BlockPlaceContextWrapper extends BlockPlaceContext {

	private final UseOnContext originalContext;

	public BlockPlaceContextWrapper(UseOnContext context) {
		super(context);
		originalContext = context;
	}

	public UseOnContext getOriginalUseContext() {
		return originalContext;
	}

}
