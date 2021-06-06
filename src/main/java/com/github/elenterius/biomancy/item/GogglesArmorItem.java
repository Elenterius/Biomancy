package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.util.TooltipUtil;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class GogglesArmorItem extends ArmorItem implements IEntityUnveilerHeadSlotItem {

	public static final String ARMOR_TEXTURE = BiomancyMod.MOD_ID + ":textures/models/armor/oculi_of_unveiling.png";

	public GogglesArmorItem(IArmorMaterial materialIn, Properties properties) {
		super(materialIn, EquipmentSlotType.HEAD, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TooltipUtil.getItemInfoTooltip(this).mergeStyle(TooltipUtil.LORE_STYLE));
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		if (stack.hasTag() && stack.getOrCreateTag().contains(NBT_KEY)) {
			if (stack.getOrCreateTag().getBoolean(NBT_KEY)) {
				tooltip.add(TextUtil.getTranslationText("tooltip", "item_is_awake").mergeStyle(TextFormatting.GRAY));
				tooltip.add(TooltipUtil.pressButtonTo(TooltipUtil.getDefaultKey(), TextUtil.getTranslationText("tooltip", "action_deactivate")).mergeStyle(TextFormatting.DARK_GRAY));
			}
			else {
				tooltip.add(TextUtil.getTranslationText("tooltip", "item_is_inert").mergeStyle(TextFormatting.GRAY));
				tooltip.add(TooltipUtil.pressButtonTo(TooltipUtil.getDefaultKey(), TextUtil.getTranslationText("tooltip", "action_activate")).mergeStyle(TextFormatting.DARK_GRAY));
			}
			tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		}
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		if (displayName instanceof IFormattableTextComponent) {
			String key = stack.getOrCreateTag().getBoolean(NBT_KEY) ? TextUtil.getTranslationKey("tooltip", "awake") : TextUtil.getTranslationKey("tooltip", "inert");
			return ((IFormattableTextComponent) displayName).appendString(" (").appendSibling(new TranslationTextComponent(key)).appendString(")");
		}
		return displayName;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canUnveilEntity(ItemStack stack, PlayerEntity player, @Nullable Entity invisibleEntity) {
		return true;
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
		if (world.isRemote()) return;

		if (player.isPotionActive(Effects.BLINDNESS)) {
			player.removePotionEffect(Effects.BLINDNESS);
		}

		EffectInstance activeEffect = player.getActivePotionEffect(Effects.NIGHT_VISION);
		if (activeEffect == null) {
			EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
			if (player.isPotionApplicable(effectInstance)) {
				player.addPotionEffect(effectInstance);
			}
		}
		else if (player.ticksExisted % 1200 == 0 && activeEffect.getDuration() < Integer.MAX_VALUE - 2000) {
			EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
			if (player.isPotionApplicable(effectInstance)) {
				player.addPotionEffect(effectInstance);
			}
		}
	}

	public void cancelEffect(LivingEntity entity) {
		if (entity.isPotionActive(Effects.NIGHT_VISION)) entity.removePotionEffect(Effects.NIGHT_VISION);
	}

	@Nullable
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return ARMOR_TEXTURE;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return ModItems.MENISCUS_LENS.get() == repair.getItem();
	}
}
