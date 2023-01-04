package com.github.elenterius.biomancy.chat;

import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.PlaceholderComponent;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.*;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.ForgeHooksClient;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("deprecation")
public final class ComponentUtil {

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
	 * force empty line iin item tooltips
	 */
	public static PlaceholderComponent emptyLine() {
		return TooltipHacks.EMPTY_LINE_COMPONENT;
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

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static MutableComponent selector(String pattern, Optional<Component> separator) {
		return new SelectorComponent(pattern, separator);
	}

	private static final class TooltipHacks {

		static final PlaceholderComponent HR_COMPONENT = create(new HrTooltipComponent());

		/**
		 * This is a replacement for {@link TextComponent#EMPTY}
		 * <br><br>
		 * When tooltip text is too wide it is wrapped around by forge ({@link ForgeHooksClient#gatherTooltipComponents}) to the next line and {@link TextComponent#EMPTY}
		 * components (empty strings) are discarded by minecraft's {@link net.minecraft.client.StringSplitter#splitLines StringSplitter#splitLines} method.<br>
		 */
		static final PlaceholderComponent EMPTY_LINE_COMPONENT = create(new EmptyLineTooltipComponent());

		private TooltipHacks() {}

		static PlaceholderComponent create(TooltipComponent component) {
			return new PlaceholderComponent(component);
		}

	}

}
