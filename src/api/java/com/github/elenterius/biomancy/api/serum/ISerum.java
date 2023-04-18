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
public interface ISerum {
	String DATA_TAG = "SerumData";
	String PREFIX = "serum.";
	ISerum EMPTY = new ISerum() {
		@Override
		public void affectEntity(ServerLevel level, CompoundTag nbt, @Nullable LivingEntity source, LivingEntity target) {}

		@Override
		public void affectPlayerSelf(CompoundTag nbt, ServerPlayer targetSelf) {}

		@Override
		public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
			return false;
		}

		@Override
		public void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {}

		@Override
		public String getTranslationKey() {
			return PREFIX + "biomancy.empty";
		}

		@Override
		public MutableComponent getDisplayName() {
			return Component.translatable(getTranslationKey());
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int getColor() {
			return 0xFF_FFFFFF;
		}

		@Override
		public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
			return false;
		}
	};

	static CompoundTag getDataTag(ItemStack stack) {
		return stack.getOrCreateTag().getCompound(DATA_TAG);
	}

	static String makeTranslationKey(ResourceLocation registryId) {
		return PREFIX + registryId.getNamespace() + "." + registryId.getPath().replace("/", ".");
	}

	static void removeDataTag(CompoundTag tag) {
		tag.remove(DATA_TAG);
	}

	static void copyDataTag(CompoundTag fromTag, CompoundTag toTag) {
		if (fromTag.contains(DATA_TAG)) {
			CompoundTag data = fromTag.getCompound(DATA_TAG);
			if (!data.isEmpty()) toTag.put(DATA_TAG, data.copy());
		}
	}

	void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag);

	String getTranslationKey();

	MutableComponent getDisplayName();

	default boolean isEmpty() {
		return false;
	}

	/**
	 * @return ARGB32 color for tinting the vial on the injector item model
	 */
	int getColor();

	boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target);

	void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target);

	boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf);

	void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf);
}
