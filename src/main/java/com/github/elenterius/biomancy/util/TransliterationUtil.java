package com.github.elenterius.biomancy.util;

import com.ibm.icu.text.Transliterator;
import net.minecraft.network.chat.Component;

import java.util.function.UnaryOperator;

public final class TransliterationUtil {

	/**
	 * 1. convert any text into latin script <br>
	 * 2. normalize text with Canonical Decomposition into composite characters with marks <br>
	 * 3. remove all marks that are none spacing (i.e. strips accents) <br>
	 * 4. normalize text with Canonical Decomposition, followed by Canonical Composition <br>
	 * 5. convert latin text into ASCII
	 *
	 * @see <a href="https://unicode-org.github.io/icu/userguide/">ICU Userguide</a>
	 */
	private static final String ANY_TO_LATIN_WITHOUT_ACCENTS_TO_ASCII = "Any-Latin; NFD; [:Nonspacing Mark:] Remove; NFC; Latin-ASCII;";

	private static final Transliterator ASCII_TRANSLITERATOR = Transliterator.getInstance(ANY_TO_LATIN_WITHOUT_ACCENTS_TO_ASCII);

	private TransliterationUtil() {}

	public static String transliterate(String text) {
		if (text.isBlank()) return text;
		return ASCII_TRANSLITERATOR.transliterate(text);
	}

	public static Component transliterate(Component component, UnaryOperator<String> resultModifier) {
		String original = component.getString();
		String transliterated = transliterate(original);

		if (transliterated.equals(original)) return component;

		return ComponentUtil.literal(resultModifier.apply(transliterated)).setStyle(component.getStyle());
	}

}
