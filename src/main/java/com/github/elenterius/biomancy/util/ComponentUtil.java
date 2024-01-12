package com.github.elenterius.biomancy.util;

import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.PlaceholderComponent;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.*;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.Nullable;

public final class ComponentUtil {

	private static final Component SPACE = literal(" ");
	private static final Component ELLIPSIS = literal("...");
	private static final Component TEXT_SEPARATOR = literal(", ");

	private ComponentUtil() {}

	public static Component nullToEmpty(@Nullable String text) {
		return Component.nullToEmpty(text);
	}

	public static MutableComponent literal(String text) {
		return new TextComponent(text);
	}

	public static MutableComponent translatable(String translationKey) {
		return new TranslatableComponent(translationKey);
	}

	public static MutableComponent translatable(String translationKey, Object... args) {
		return new TranslatableComponent(translationKey, args);
	}

	public static MutableComponent mutable() {
		return literal("");
	}

	public static Component empty() {
		return TextComponent.EMPTY;
	}

	/**
	 * whitespace
	 */
	public static Component space() {
		return SPACE;
	}

	/**
	 * force empty line in item tooltips
	 */
	public static PlaceholderComponent emptyLine() {
		return TooltipHacks.EMPTY_LINE_COMPONENT;
	}

	public static Component newLine() {
		return CommonComponents.NEW_LINE;
	}

	public static Component ellipsis() {
		return ELLIPSIS;
	}

	public static Component textSeparator() {
		return TEXT_SEPARATOR;
	}

	/**
	 * horizontal line in item tooltips
	 */
	public static PlaceholderComponent horizontalLine() {
		return TooltipHacks.HR_COMPONENT;
	}

	/**
	 * client sided due to KeyMapping
	 */
	public static MutableComponent keybind(KeyMapping keyMapping) {
		return keybind(keyMapping.getName());
	}

	public static MutableComponent keybind(String keyName) {
		return new KeybindComponent(keyName);
	}

	//	public static MutableComponent nbt(String nbtPathPattern, boolean interpreting, Optional<Component> separator, DataSource dataSource) {
	//		return Component.nbt(nbtPathPattern, interpreting, separator, dataSource);
	//	}

	public static MutableComponent score(String name, String objective) {
		return new ScoreComponent(name, objective);
	}

	public static MutableComponent selector(String pattern, String objective) {
		return new ScoreComponent(pattern, objective);
	}

	public static MutableComponent tooltip(TooltipComponent component) {
		return TooltipHacks.wrap(component);
	}

	private static final class TooltipHacks {

		static final PlaceholderComponent HR_COMPONENT = wrap(new HrTooltipComponent());

		/**
		 * This is a component for {@link TextComponent#EMPTY}
		 * <br><br>
		 * When tooltip text is too wide it is wrapped around by forge ({@link ForgeHooksClient#gatherTooltipComponents}) to the next line and {@link TextComponent#EMPTY}
		 * components (empty strings) are discarded by minecraft's {@link net.minecraft.client.StringSplitter#splitLines StringSplitter#splitLines} method.<br>
		 */
		static final PlaceholderComponent EMPTY_LINE_COMPONENT = wrap(new EmptyLineTooltipComponent());

		private TooltipHacks() {}

		private static PlaceholderComponent wrap(TooltipComponent component) {
			return new PlaceholderComponent(component);
		}

	}

}
