package com.github.elenterius.biomancy.api.serum;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Experimental
public interface Serum {
	String DATA_TAG_KEY = "serum_data";
	String TRANSLATION_PREFIX = "serum.";

	Serum EMPTY = new Serum() {
		@Override
		public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
			return false;
		}

		@Override
		public void affectEntity(ServerLevel level, CompoundTag nbt, @Nullable LivingEntity source, LivingEntity target) {}

		@Override
		public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
			return false;
		}

		@Override
		public void affectPlayerSelf(CompoundTag nbt, ServerPlayer targetSelf) {}

		@Override
		public void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int getColor() {
			return 0xFF_FFFFFF;
		}

		@Override
		public String getNameTranslationKey() {
			return TRANSLATION_PREFIX + "biomancy.empty";
		}

	};

	static CompoundTag getDataTag(ItemStack stack) {
		return stack.getOrCreateTag().getCompound(DATA_TAG_KEY);
	}

	static String makeTranslationKey(ResourceLocation key) {
		return TRANSLATION_PREFIX + key.getNamespace() + "." + key.getPath().replace("/", ".");
	}

	static void removeDataTag(CompoundTag tag) {
		tag.remove(DATA_TAG_KEY);
	}

	static void copyDataTag(CompoundTag fromTag, CompoundTag toTag) {
		if (fromTag.contains(DATA_TAG_KEY)) {
			CompoundTag data = fromTag.getCompound(DATA_TAG_KEY);
			if (!data.isEmpty()) toTag.put(DATA_TAG_KEY, data.copy());
		}
	}

	boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target);

	void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target);

	boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf);

	void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf);

	default boolean isEmpty() {
		return false;
	}

	/**
	 * @return ARGB32 color for tinting the vial on the injector item model
	 */
	int getColor();

	void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag);

	String getNameTranslationKey();

	default String getDescriptionTranslationKey() {
		return getNameTranslationKey() + ".tooltip";
	}

	default MutableComponent getDisplayName() {
		return Component.translatable(getNameTranslationKey());
	}
}
