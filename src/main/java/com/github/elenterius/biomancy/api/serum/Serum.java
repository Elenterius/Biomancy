package com.github.elenterius.biomancy.api.serum;

import com.github.elenterius.biomancy.util.ComponentUtil;
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
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Experimental
public interface Serum extends IForgeRegistryEntry<Serum> {
	String DATA_TAG = "SerumData";
	String TRANSLATION_PREFIX = "serum.";

	Serum EMPTY = EmptySerum.INSTANCE;

	static CompoundTag getDataTag(ItemStack stack) {
		return stack.getOrCreateTag().getCompound(DATA_TAG);
	}

	static String makeTranslationKey(ResourceLocation registryId) {
		return TRANSLATION_PREFIX + registryId.getNamespace() + "." + registryId.getPath().replace("/", ".");
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
		return ComponentUtil.translatable(getNameTranslationKey());
	}
}
