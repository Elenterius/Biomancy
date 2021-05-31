package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModReagents;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class Reagent extends ForgeRegistryEntry<Reagent> {

	public static final String NBT_KEY_ID = "ReagentId";
	public static final String NBT_KEY_DATA = "ReagentData";
	public static final String NBT_KEY_COLOR = "ReagentColor";

	@Nullable
	private Multimap<Attribute, AttributeModifier> attributeModifiers = null;
	private final int color;

	public Reagent(int colorIn) {
		color = colorIn;
	}

	public static void remove(CompoundNBT nbt) {
		nbt.remove(NBT_KEY_ID);
		nbt.remove(NBT_KEY_DATA);
		nbt.remove(NBT_KEY_COLOR);
	}

	public static void copyAdditionalData(CompoundNBT fromNbt, CompoundNBT toNbt) {
		toNbt.put(NBT_KEY_DATA, fromNbt.getCompound(NBT_KEY_DATA).copy());
	}

	public static void serialize(Reagent reagent, CompoundNBT nbt) {
		ResourceLocation key = ModReagents.REGISTRY.get().getKey(reagent);
		if (key != null) {
			nbt.putString(NBT_KEY_ID, key.toString());
			nbt.putInt(NBT_KEY_COLOR, reagent.getColor());
		}
	}

	@Nullable
	public static Reagent deserialize(CompoundNBT nbt) {
		if (nbt.contains(NBT_KEY_ID)) {
			ResourceLocation key = ResourceLocation.tryCreate(nbt.getString(NBT_KEY_ID));
			if (key != null) return ModReagents.REGISTRY.get().getValue(key);
		}
		return null;
	}

	@Nullable
	public static String getTranslationKey(CompoundNBT nbt) {
		if (nbt.contains(NBT_KEY_ID)) {
			String str = nbt.getString(NBT_KEY_ID);
			return str.isEmpty() ? null : "reagent." + str.replace(":", ".").replace("/", ".");
		}
		return null;
	}

	public static String getTranslationKey(ResourceLocation registryKey) {
		return "reagent." + registryKey.getNamespace() + "." + registryKey.getPath().replace("/", ".");
	}

	public static int getColor(CompoundNBT nbt) {
		return nbt.contains(NBT_KEY_COLOR) ? nbt.getInt(NBT_KEY_COLOR) : -1;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInfoToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.getItem() != ModItems.REAGENT.get())
			tooltip.add(new TranslationTextComponent(BiomancyMod.getTranslationKey("tooltip", "contains"), new TranslationTextComponent(getTranslationKey())).mergeStyle(TextFormatting.GRAY));
	}

	public String getTranslationKey() {
		//noinspection ConstantConditions
		return getTranslationKey(getRegistryName());
	}

	public int getColor() {
		return color;
	}

	public boolean isAttributeModifier() { return attributeModifiers != null; }

	public abstract boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing);

	public abstract boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target);

	public abstract boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf);

	public Reagent addAttributeModifier(Attribute attribute, String uuid, double amount, AttributeModifier.Operation operation) {
		if (attributeModifiers == null) attributeModifiers = HashMultimap.create();

		AttributeModifier modifier = new AttributeModifier(UUID.fromString(uuid), getTranslationKey(), amount, operation);
		attributeModifiers.put(attribute, modifier);
		return this;
	}

	public void removeAttributesModifiersFromEntity(LivingEntity livingEntity) {
		if (attributeModifiers != null) livingEntity.getAttributeManager().removeModifiers(attributeModifiers);
	}

	public void applyAttributesModifiersToEntity(LivingEntity livingEntity) {
		if (attributeModifiers != null) livingEntity.getAttributeManager().reapplyModifiers(attributeModifiers);
	}
}
