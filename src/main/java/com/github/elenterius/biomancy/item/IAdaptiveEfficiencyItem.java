package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface IAdaptiveEfficiencyItem {

	String NBT_KEY = StringUtils.capitalize(BiomancyMod.MOD_ID) + "AdaptiveEfficiency";
	float MAX_EFFICIENCY = 16f;

	@OnlyIn(Dist.CLIENT)
	static void addAdaptiveEfficiencyTooltip(ItemStack stack, List<ITextComponent> tooltip) {
		CompoundNBT nbt = stack.getOrCreateChildTag(NBT_KEY);
		String blockName = nbt.getString("BlockName");
		if (!blockName.isEmpty()) {
			float modifier = nbt.getByte("Level") * 0.5f;
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			tooltip.add(new TranslationTextComponent(TextUtil.getTranslationKey("tooltip", "mining_bonus"), new TranslationTextComponent(blockName)).mergeStyle(TextFormatting.GRAY));
			tooltip.add(new StringTextComponent(" +" + modifier + " ").appendSibling(new TranslationTextComponent("enchantment.minecraft.efficiency")).mergeStyle(TextFormatting.BLUE));
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		}
	}

	static float getEfficiencyModifier(ItemStack stack, BlockState state) {
		ResourceLocation targetBlockId = state.getBlock().getRegistryName();
		if (targetBlockId != null) {
			CompoundNBT nbt = stack.getOrCreateChildTag(NBT_KEY);
			if (targetBlockId.toString().equals(nbt.getString("BlockId"))) {
				return nbt.getByte("Level") * 0.5f;
			}
		}
		return 0f;
	}

	static void updateEfficiencyModifier(ItemStack stack, BlockState state, float baseEfficiency, float efficiencyIn) {
		ResourceLocation targetBlockId = state.getBlock().getRegistryName();
		if (targetBlockId != null) {
			CompoundNBT nbt = stack.getOrCreateChildTag(NBT_KEY);
			if (efficiencyIn >= baseEfficiency) {
				if (targetBlockId.toString().equals(nbt.getString("BlockId"))) {
					int level = nbt.getByte("Level");
					if (level < Byte.MAX_VALUE && efficiencyIn + level * 0.5f < MAX_EFFICIENCY) nbt.putByte("Level", (byte) (level + 1));
				}
				else {
					nbt.putString("BlockId", targetBlockId.toString());
					nbt.putString("BlockName", state.getBlock().getTranslationKey());
					nbt.putByte("Level", (byte) 1);
				}
			}
			else {
				nbt.putString("BlockId", "");
				nbt.putString("BlockName", "");
				nbt.putByte("Level", (byte) 0);
			}
		}
	}

}
