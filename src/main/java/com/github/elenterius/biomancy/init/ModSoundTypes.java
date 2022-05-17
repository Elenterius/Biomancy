package com.github.elenterius.biomancy.init;

import net.minecraftforge.common.util.ForgeSoundType;

public final class ModSoundTypes {

	private ModSoundTypes() {}

	public static final ForgeSoundType FLESH_BLOCK = new ForgeSoundType(1f, 1f,
			ModSoundEvents.FLESH_BLOCK_BREAK, ModSoundEvents.FLESH_BLOCK_STEP,
			ModSoundEvents.FLESH_BLOCK_PLACE, ModSoundEvents.FLESH_BLOCK_HIT, ModSoundEvents.FLESH_BLOCK_FALL);

}
