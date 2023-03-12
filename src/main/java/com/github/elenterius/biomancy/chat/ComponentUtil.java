package com.github.elenterius.biomancy.chat;

import com.github.elenterius.biomancy.tooltip.EmptyLineTooltipComponent;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.TooltipContents;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.SelectorContents;
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
		return Component.literal(text);
	}

	public static MutableComponent translatable(String translationKey) {
		return Component.translatable(translationKey);
	}

	public static MutableComponent translatable(String translationKey, Object... args) {
		return Component.translatable(translationKey, args);
	}

	public static MutableComponent mutable() {
		return Component.empty();
	}

	public static Component empty() {
		return CommonComponents.EMPTY;
	}

	/**
	 * force empty line iin item tooltips
	 */
	public static Component emptyLine() {
		return TooltipHacks.EMPTY_LINE_COMPONENT;
	}

	/**
	 * horizontal line in item tooltips
	 */
	public static Component horizontalLine() {
		return TooltipHacks.HR_COMPONENT;
	}

	/**
	 * client sided due to KeyMapping
	 */
	public static MutableComponent keybind(KeyMapping keyMapping) {
		return Component.keybind(keyMapping.getName());
	}

	public static MutableComponent keybind(String keyName) {
		return Component.keybind(keyName);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static MutableComponent nbt(String nbtPathPattern, boolean interpreting, Optional<Component> separator, DataSource dataSource) {
		return Component.nbt(nbtPathPattern, interpreting, separator, dataSource);
	}

	public static MutableComponent score(String name, String objective) {
		return Component.score(name, objective);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static MutableComponent selector(String pattern, Optional<Component> separator) {
		return MutableComponent.create(new SelectorContents(pattern, separator));
	}

	public static MutableComponent tooltip(TooltipComponent component) {
		return TooltipHacks.wrap(component);
	}

	private static final class TooltipHacks {

		static final MutableComponent HR_COMPONENT = wrap(new HrTooltipComponent());

		/**
		 * This is a component for {@link CommonComponents#EMPTY}
		 * <br><br>
		 * When tooltip text is too wide it is wrapped around by forge ({@link ForgeHooksClient#gatherTooltipComponents}) to the next line and {@link CommonComponents#EMPTY}
		 * components (empty strings) are discarded by minecraft's {@link net.minecraft.client.StringSplitter#splitLines StringSplitter#splitLines} method.<br>
		 */
		static final MutableComponent EMPTY_LINE_COMPONENT = wrap(new EmptyLineTooltipComponent());

		private TooltipHacks() {}

		private static MutableComponent wrap(TooltipComponent component) {
			return MutableComponent.create(new TooltipContents(component));
		}

	}

}
