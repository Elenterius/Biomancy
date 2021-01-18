package com.github.elenterius.blightlings.tileentity;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ModEntityTypes;
import net.minecraft.util.text.ITextComponent;

public class SimpleOwnableTileEntity extends OwnableTileEntity {

	public SimpleOwnableTileEntity() {
		super(ModEntityTypes.SIMPLE_OWNABLE_TILE.get());
	}

	@Override
	protected ITextComponent getDefaultName() {
		return BlightlingsMod.getTranslationText("tile", "ownable");
	}

}
