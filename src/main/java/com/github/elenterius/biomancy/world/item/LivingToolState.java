package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.styles.TextComponentUtil;
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

	public LivingToolState cycle() {
		return deserialize((byte) (ordinal() + 1));
	}

	public TranslatableComponent getItemTooltip() {
		return TextComponentUtil.getTooltipText("item_is_" + name().toLowerCase(Locale.ENGLISH));
	}

	public TranslatableComponent getTooltip() {
		return TextComponentUtil.getTooltipText(name().toLowerCase(Locale.ENGLISH));
	}

	public void serialize(CompoundTag tag) {
		tag.putByte(STATE_KEY, serialize());
	}

	public byte serialize() {
		return (byte) ordinal();
	}
}
