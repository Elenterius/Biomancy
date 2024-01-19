package com.github.elenterius.biomancy.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.function.Supplier;

public final class ModBlockSetTypes {
	public static final Supplier<BlockSetType> FLESH_SET_TYPE = () -> new BlockSetType("flesh", true, ModSoundTypes.FLESH_BLOCK,
			ModSoundEvents.FLESH_DOOR_CLOSE.get(), ModSoundEvents.FLESH_DOOR_OPEN.get(),
			ModSoundEvents.FLESH_DOOR_CLOSE.get(), ModSoundEvents.FLESH_DOOR_OPEN.get(),
			SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON);

	private ModBlockSetTypes() {}
}
