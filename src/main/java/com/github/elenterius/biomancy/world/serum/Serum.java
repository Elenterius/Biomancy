package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.world.item.SerumItem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class Serum extends ForgeRegistryEntry<Serum> {

	public static final Serum EMPTY = new Serum(0) {
		@Override
		public void affectEntity(ServerLevel level, CompoundTag nbt, @Nullable LivingEntity source, LivingEntity target) {}

		@Override
		public void affectPlayerSelf(CompoundTag nbt, ServerPlayer targetSelf) {}
	};

	public static final String PREFIX = "serum.";
	public static final String DATA_TAG = "SerumData";

	private final int color;
	@Nullable
	private Multimap<Attribute, AttributeModifier> attributeModifiers = null;

	protected Serum(int colorIn) {
		color = colorIn;
	}

	public static CompoundTag getDataTag(ItemStack stack) {
		return stack.getOrCreateTag().getCompound(DATA_TAG);
	}

	public static void remove(CompoundTag tag) {
		tag.remove(DATA_TAG);
	}

	public static void copyAdditionalData(CompoundTag fromTag, CompoundTag toTag) {
		if (fromTag.contains(DATA_TAG)) {
			CompoundTag data = fromTag.getCompound(DATA_TAG);
			if (!data.isEmpty()) toTag.put(DATA_TAG, data.copy());
		}
	}

	public static String getTranslationKey(ResourceLocation registryKey) {
		return PREFIX + registryKey.getNamespace() + "." + registryKey.getPath().replace("/", ".");
	}

	@OnlyIn(Dist.CLIENT)
	public void addInfoToTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		if (!(stack.getItem() instanceof SerumItem)) {
			tooltip.add(TextComponentUtil.getTooltipText("contains", new TranslatableComponent(getTranslationKey())).withStyle(ChatFormatting.GRAY));
		}
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(new TranslatableComponent(getTranslationKey().replace(PREFIX, "tooltip.")).withStyle(TextStyles.LORE));
		}
	}

	public String getTranslationKey() {
		return getTranslationKey(Objects.requireNonNull(getRegistryName()));
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public int getColor() {
		return color;
	}

	public final boolean isAttributeModifier() {return attributeModifiers != null;}

	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return true;
	}

	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		if (isAttributeModifier()) applyAttributesModifiersToEntity(target);
	}

	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return true;
	}

	public void affectPlayerSelf(CompoundTag tag, ServerPlayer targetSelf) {
		if (isAttributeModifier()) applyAttributesModifiersToEntity(targetSelf);
	}

	protected final Serum addAttributeModifier(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation) {
		if (attributeModifiers == null) attributeModifiers = HashMultimap.create();

		AttributeModifier modifier = new AttributeModifier(UUID.fromString(uuid), getTranslationKey(), amount, operation);
		attributeModifiers.put(attribute, modifier);
		return this;
	}

	public void removeAttributesModifiersFromEntity(LivingEntity livingEntity) {
		if (attributeModifiers != null) livingEntity.getAttributes().removeAttributeModifiers(attributeModifiers);
	}

	public void applyAttributesModifiersToEntity(LivingEntity livingEntity) {
		if (attributeModifiers != null) livingEntity.getAttributes().addTransientAttributeModifiers(attributeModifiers);
	}

}
