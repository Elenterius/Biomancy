package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ItemComments {

	private static final Map<Item, List<Component>> COMMENTS = new IdentityHashMap<>();

	private ItemComments() {}

	static {
		createItemComment(ModItems.PRIMORDIAL_CRADLE.get(), "Basically, at the very bottom of life, which seduces us all, there is only absurdity, and more absurdity. And maybe that's what gives us our joy for living, because the only thing that can defeat absurdity is lucidity.\n- Albert Camus");
		createItemComment(ModItems.PRIMORDIAL_CORE.get(), "I have come for your meat\n- Karth Kalbi");
		createItemComment(ModItems.DIGESTER.get(), "Digest her? I barely know her...\n- spicynips");
		createItemComment(ModItems.ORNATE_FLESH_BLOCK.get(), "Birthplace of Crembo and Jarky");
		createItemComment(ModItems.ACOLYTE_ARMOR_CHESTPLATE.get(), "If you're hot, take off your skin.\n- Karmatic");
		createItemComment(ModItems.ACOLYTE_ARMOR_LEGGINGS.get(), "I've been sprinting towards the horizon of insanity since birth.\n- kd8lvt");
		createItemComment(ModItems.AGEING_SERUM.get(), "We can regard our life as a uselessly disturbing episode in the blissful repose of nothingness.\n- Arthur Schopenhauer");
		createItemComment(ModItems.BREEDING_STIMULANT.get(), "Happiness and the absurd are two sons of the same earth. They are inseparable.\n- Albert Camus");
		createItemComment(ModItems.SHRINKING_SERUM.get(), "Normalize the idea of living inside of Someone.\n- Tarael Blackwing");
	}

	private static void createItemComment(Item item, String text) {
		COMMENTS.put(item, toFleshTongue(text + "\n\n"));
	}

	private static List<Component> toFleshTongue(String text) {
		return ComponentUtil.splitLines(Locale.ENGLISH, text, TextStyles.PRIMORDIAL_RUNES_GRAY);
	}

	public static @Nullable List<Component> getComment(Item item) {
		return COMMENTS.get(item);
	}

}
