package com.github.elenterius.biomancy.item.livingtool;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum LivingToolState {

	BROKEN, DORMANT, AWAKENED;

	public static final String STATE_KEY = "LivingToolState";

	public static LivingToolState deserialize(CompoundTag tag) {
		return deserialize(tag.getByte(STATE_KEY));
	}

	public static LivingToolState deserialize(byte stateId) {
		if (stateId >= values().length) return BROKEN;
		if (stateId < 0) return BROKEN;

		return values()[stateId];
	}

	public static String getTooltipTranslationKey() {
		return "tooltip." + BiomancyMod.MOD_ID + ".living_tool_state_is";
	}

	public LivingToolState cycle() {
		return deserialize((byte) (ordinal() + 1));
	}

	public MutableComponent getTooltip() {
		return ComponentUtil.translatable(getTooltipTranslationKey(), ComponentUtil.translatable(getTranslationKey()));
	}

	public String getTranslationKey() {
		return "state." + BiomancyMod.MOD_ID + ".living_tool." + name().toLowerCase(Locale.ENGLISH);
	}

	public MutableComponent getDisplayName() {
		return ComponentUtil.translatable(getTranslationKey());
	}

	public void serialize(CompoundTag tag) {
		tag.putByte(STATE_KEY, serialize());
	}

	public byte serialize() {
		return (byte) ordinal();
	}
}
