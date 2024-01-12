package com.github.elenterius.biomancy.init;

import net.minecraftforge.common.util.ForgeSoundType;

public final class ModSoundTypes {

	public static final ForgeSoundType FLESH_BLOCK = new ForgeSoundType(1f, 1f,
			ModSoundEvents.FLESH_BLOCK_BREAK, ModSoundEvents.FLESH_BLOCK_STEP,
			ModSoundEvents.FLESH_BLOCK_PLACE, ModSoundEvents.FLESH_BLOCK_HIT, ModSoundEvents.FLESH_BLOCK_FALL);
	public static final ForgeSoundType BONY_FLESH_BLOCK = new ForgeSoundType(1f, 1f,
			ModSoundEvents.BONY_FLESH_BLOCK_BREAK, ModSoundEvents.BONY_FLESH_BLOCK_STEP,
			ModSoundEvents.BONY_FLESH_BLOCK_PLACE, ModSoundEvents.BONY_FLESH_BLOCK_HIT, ModSoundEvents.BONY_FLESH_BLOCK_FALL);

	private ModSoundTypes() {}

}
