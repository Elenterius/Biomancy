package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.ClientTextUtil;
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
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class OculiGogglesArmorItem extends ArmorItem implements IEntityUnveilerHeadSlotItem {

	public static final String ARMOR_TEXTURE = BiomancyMod.MOD_ID + ":textures/models/armor/oculi_of_unveiling.png";

	public OculiGogglesArmorItem(IArmorMaterial materialIn, Properties properties) {
		super(materialIn, EquipmentSlotType.HEAD, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).withStyle(ClientTextUtil.LORE_STYLE));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		if (stack.getOrCreateTag().getBoolean(NBT_KEY)) {
			tooltip.add(TextUtil.getTranslationText("tooltip", "item_is_awake").withStyle(TextFormatting.GRAY));
			tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTranslationText("tooltip", "action_deactivate")).withStyle(TextFormatting.DARK_GRAY));
		}
		else {
			tooltip.add(TextUtil.getTranslationText("tooltip", "item_is_inert").withStyle(TextFormatting.GRAY));
			tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTranslationText("tooltip", "action_activate")).withStyle(TextFormatting.DARK_GRAY));
		}
		tooltip.add(new StringTextComponent("If equipped, use ").append(ClientTextUtil.getCtrlKey()).append(" + ").append(ClientTextUtil.getDefaultKey().append(" instead.")).withStyle(TextFormatting.DARK_GRAY));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		if (displayName instanceof IFormattableTextComponent) {
			String key = stack.getOrCreateTag().getBoolean(NBT_KEY) ? TextUtil.getTranslationKey("tooltip", "awake") : TextUtil.getTranslationKey("tooltip", "inert");
			return ((IFormattableTextComponent) displayName).append(" (").append(new TranslationTextComponent(key)).append(")");
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
		if (world.isClientSide()) return;

		if (player.hasEffect(Effects.BLINDNESS)) {
			player.removeEffect(Effects.BLINDNESS);
		}

		EffectInstance activeEffect = player.getEffect(Effects.NIGHT_VISION);
		if (activeEffect == null) {
			EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
			if (player.canBeAffected(effectInstance)) {
				player.addEffect(effectInstance);
			}
		}
		else if (player.tickCount % 1200 == 0 && activeEffect.getDuration() < Integer.MAX_VALUE - 2000) {
			EffectInstance effectInstance = new EffectInstance(Effects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
			if (player.canBeAffected(effectInstance)) {
				player.addEffect(effectInstance);
			}
		}
	}

	public void cancelEffect(LivingEntity entity) {
		if (entity.hasEffect(Effects.NIGHT_VISION)) entity.removeEffect(Effects.NIGHT_VISION);
	}

	@Nullable
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return ARMOR_TEXTURE;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return ModItems.MENISCUS_LENS.get() == repair.getItem();
	}
}
