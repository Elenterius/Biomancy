package com.github.elenterius.biomancy.world.item.state;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;

public enum LivingToolState {

	DORMANT, AWAKE, EXALTED;

	public static final String STATE_KEY = "LivingToolState";

	public static LivingToolState deserialize(CompoundTag tag) {
		return deserialize(tag.getByte(STATE_KEY));
	}

	public static LivingToolState deserialize(byte stateId) {
		if (stateId >= values().length) return DORMANT;
		if (stateId < 0) return DORMANT;

		return values()[stateId];
	}

	public static String getTooltipTranslationKey() {
		return "tooltip." + BiomancyMod.MOD_ID + ".living_tool_state_is";
	}

	public LivingToolState cycle() {
		return deserialize((byte) (ordinal() + 1));
	}

	public TranslatableComponent getTooltip() {
		return new TranslatableComponent(getTooltipTranslationKey(), new TranslatableComponent(getTranslationKey()));
	}

	public String getTranslationKey() {
		return "state." + BiomancyMod.MOD_ID + ".living_tool." + name().toLowerCase(Locale.ENGLISH);
	}

	public TranslatableComponent getDisplayName() {
		return new TranslatableComponent(getTranslationKey());
	}

	public void serialize(CompoundTag tag) {
		tag.putByte(STATE_KEY, serialize());
	}

	public byte serialize() {
		return (byte) ordinal();
	}
}
