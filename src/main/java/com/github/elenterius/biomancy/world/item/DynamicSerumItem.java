package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public final class DynamicSerumItem extends SerumItem {

	public static final String COLOR_TAG = "SerumColor";
	public static final String ID_TAG = "SerumId";

	public DynamicSerumItem(Properties properties) {
		super(properties, null);
	}

	@Nullable
	@Override
	public Serum getSerum(ItemStack stack) {
		return deserialize(stack.getOrCreateTag());
	}

	@Override
	public int getSerumColor(ItemStack stack) {
		return getColor(stack.getOrCreateTag());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		Serum serum = deserialize(stack.getOrCreateTag());
		if (serum != null) {
			serum.addInfoToTooltip(stack, level, tooltip, isAdvanced);
		}
		else tooltip.add(TextComponentUtil.getTooltipText("contains_nothing"));
	}

	@Override
	public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
		if (allowdedIn(category)) {
			for (Serum serum : ModSerums.REGISTRY.get()) {
				SerumItem serumItem = SerumItem.getItemForSerum(serum);
				if (serumItem == null) {
					ItemStack stack = new ItemStack(ModItems.GENERIC_SERUM.get());
					serialize(serum, stack.getOrCreateTag());
					items.add(stack);
				}
			}
		}
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		Serum serum = deserialize(stack.getOrCreateTag());
		return serum != null ? serum.getTranslationKey() : super.getDescriptionId(stack);
	}

	public static void clearSerumData(CompoundTag nbt) {
		nbt.remove(ID_TAG);
		nbt.remove(COLOR_TAG);
	}

	public static int getColor(CompoundTag nbt) {
		return nbt.contains(COLOR_TAG) ? nbt.getInt(COLOR_TAG) : -1;
	}

	public static void serialize(Serum serum, CompoundTag nbt) {
		ResourceLocation key = ModSerums.REGISTRY.get().getKey(serum);
		if (key != null) {
			nbt.putString(ID_TAG, key.toString());
			nbt.putInt(COLOR_TAG, serum.getColor());
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

//	@Nullable
//	public static String getTranslationKey(CompoundTag nbt) {
//		if (nbt.contains(ID_TAG)) {
//			String str = nbt.getString(ID_TAG);
//			return str.isEmpty() ? null : Serum.PREFIX + str.replace(":", ".").replace("/", ".");
//		}
//		return null;
//	}

}
