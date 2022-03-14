package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.item.SerumItem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
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
import java.util.UUID;

public abstract class Serum extends ForgeRegistryEntry<Serum> {

	public static final String PREFIX = "serum.";

	public static final String ID_TAG = "SerumId";
	public static final String DATA_TAG = "SerumData";
	public static final String COLOR_TAG = "SerumColor";

	private final int color;
	@Nullable
	private Multimap<Attribute, AttributeModifier> attributeModifiers = null;

	protected Serum(int colorIn) {
		color = colorIn;
	}

	public static CompoundTag getDataTag(ItemStack stack) {
		return stack.getOrCreateTag().getCompound(DATA_TAG);
	}

	public static void remove(CompoundTag nbt) {
		nbt.remove(ID_TAG);
		nbt.remove(DATA_TAG);
		nbt.remove(COLOR_TAG);
	}

	public static void copyAdditionalData(CompoundTag fromNbt, CompoundTag toNbt) {
		if (fromNbt.contains(DATA_TAG)) {
			CompoundTag data = fromNbt.getCompound(DATA_TAG);
			if (!data.isEmpty()) toNbt.put(DATA_TAG, data.copy());
		}
	}

	public static void serialize(Serum reagent, CompoundTag nbt) {
		ResourceLocation key = ModSerums.REGISTRY.get().getKey(reagent);
		if (key != null) {
			nbt.putString(ID_TAG, key.toString());
			nbt.putInt(COLOR_TAG, reagent.getColor());
		}
	}

	@Nullable
	public static Serum deserialize(CompoundTag nbt) {
		if (nbt.contains(ID_TAG)) {
			ResourceLocation key = ResourceLocation.tryParse(nbt.getString(ID_TAG));
			if (key != null) return ModSerums.REGISTRY.get().getValue(key);
		}
		return null;
	}

	@Nullable
	public static String getTranslationKey(CompoundTag nbt) {
		if (nbt.contains(ID_TAG)) {
			String str = nbt.getString(ID_TAG);
			return str.isEmpty() ? null : PREFIX + str.replace(":", ".").replace("/", ".");
		}
		return null;
	}

	public static String getTranslationKey(ResourceLocation registryKey) {
		return PREFIX + registryKey.getNamespace() + "." + registryKey.getPath().replace("/", ".");
	}

	public static int getColor(CompoundTag nbt) {
		return nbt.contains(COLOR_TAG) ? nbt.getInt(COLOR_TAG) : -1;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInfoToTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		if (!(stack.getItem() instanceof SerumItem)) {
			tooltip.add(TextComponentUtil.getTooltipText("contains", new TranslatableComponent(getTranslationKey())).withStyle(ChatFormatting.GRAY));
		}
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(new TranslatableComponent(getTranslationKey().replace(PREFIX, "tooltip.")).withStyle(ClientTextUtil.LORE_STYLE));
		}
	}

	public String getTranslationKey() {
		//noinspection ConstantConditions
		return getTranslationKey(getRegistryName());
	}

	public int getColor() {
		return color;
	}

	public boolean isAttributeModifier() {return attributeModifiers != null;}

	public abstract boolean affectBlock(CompoundTag nbt, @Nullable LivingEntity source, Level world, BlockPos pos, Direction facing);

	public abstract boolean affectEntity(CompoundTag nbt, @Nullable LivingEntity source, LivingEntity target);

	public abstract boolean affectPlayerSelf(CompoundTag nbt, Player targetSelf);

	public Serum addAttributeModifier(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation) {
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
