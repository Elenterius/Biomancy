package com.github.elenterius.biomancy.world.serum;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
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

	public static final String NBT_KEY_ID = "ReagentId";
	public static final String NBT_KEY_DATA = "ReagentData";
	public static final String NBT_KEY_COLOR = "ReagentColor";
	private final int color;
	@Nullable
	private Multimap<Attribute, AttributeModifier> attributeModifiers = null;

	public Serum(int colorIn) {
		color = colorIn;
	}

	public static void remove(CompoundTag nbt) {
		nbt.remove(NBT_KEY_ID);
		nbt.remove(NBT_KEY_DATA);
		nbt.remove(NBT_KEY_COLOR);
	}

	public static void copyAdditionalData(CompoundTag fromNbt, CompoundTag toNbt) {
		if (fromNbt.contains(NBT_KEY_DATA)) {
			CompoundTag data = fromNbt.getCompound(NBT_KEY_DATA);
			if (!data.isEmpty()) toNbt.put(NBT_KEY_DATA, data.copy());
		}
	}

	public static void serialize(Serum reagent, CompoundTag nbt) {
		ResourceLocation key = ModSerums.REGISTRY.get().getKey(reagent);
		if (key != null) {
			nbt.putString(NBT_KEY_ID, key.toString());
			nbt.putInt(NBT_KEY_COLOR, reagent.getColor());
		}
	}

	@Nullable
	public static Serum deserialize(CompoundTag nbt) {
		if (nbt.contains(NBT_KEY_ID)) {
			ResourceLocation key = ResourceLocation.tryParse(nbt.getString(NBT_KEY_ID));
			if (key != null) return ModSerums.REGISTRY.get().getValue(key);
		}
		return null;
	}

	@Nullable
	public static String getTranslationKey(CompoundTag nbt) {
		if (nbt.contains(NBT_KEY_ID)) {
			String str = nbt.getString(NBT_KEY_ID);
			return str.isEmpty() ? null : "reagent." + str.replace(":", ".").replace("/", ".");
		}
		return null;
	}

	public static String getTranslationKey(ResourceLocation registryKey) {
		return "reagent." + registryKey.getNamespace() + "." + registryKey.getPath().replace("/", ".");
	}

	public static int getColor(CompoundTag nbt) {
		return nbt.contains(NBT_KEY_COLOR) ? nbt.getInt(NBT_KEY_COLOR) : -1;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInfoToTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.getItem() != ModItems.SERUM.get()) {
			tooltip.add(TextComponentUtil.getTooltipText("contains", new TranslatableComponent(getTranslationKey())).withStyle(ChatFormatting.GRAY));
		}
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(new TranslatableComponent(getTranslationKey().replace("reagent", "tooltip")).withStyle(ClientTextUtil.LORE_STYLE));
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
