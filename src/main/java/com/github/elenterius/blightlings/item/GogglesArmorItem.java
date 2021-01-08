package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.init.ClientSetupHandler;
import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.util.TooltipUtil;
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
	public static final String ARMOR_TEXTURE = BlightlingsMod.MOD_ID + ":textures/models/armor/true_sight_goggles.png";

	public GogglesArmorItem(IArmorMaterial materialIn, Properties properties) {
		super(materialIn, EquipmentSlotType.HEAD, properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TooltipUtil.getTooltip(this).mergeStyle(TooltipUtil.LORE_STYLE));
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		if (stack.hasTag() && stack.getOrCreateTag().contains("BlightlingsItemAbilityEnabled")) {
			if (stack.getOrCreateTag().getBoolean("BlightlingsItemAbilityEnabled")) {
				tooltip.add(new TranslationTextComponent("tooltip.blightlings.item_is_awake").mergeStyle(TextFormatting.GRAY));
				tooltip.add(new TranslationTextComponent("tooltip.blightlings.press_button_to", ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.func_238171_j_().copyRaw().mergeStyle(TextFormatting.AQUA), new TranslationTextComponent("tooltip.blightlings.action_deactivate")).mergeStyle(TextFormatting.DARK_GRAY));
			}
			else {
				tooltip.add(new TranslationTextComponent("tooltip.blightlings.item_is_inert").mergeStyle(TextFormatting.GRAY));
				tooltip.add(new TranslationTextComponent("tooltip.blightlings.press_button_to", ClientSetupHandler.ITEM_DEFAULT_KEY_BINDING.func_238171_j_().copyRaw().mergeStyle(TextFormatting.AQUA), new TranslationTextComponent("tooltip.blightlings.action_activate")).mergeStyle(TextFormatting.DARK_GRAY));
			}
			tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
		}
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		if (displayName instanceof IFormattableTextComponent) {
			String key = stack.getOrCreateTag().getBoolean("BlightlingsItemAbilityEnabled") ? "tooltip.blightlings.awake" : "tooltip.blightlings.inert";
			return ((IFormattableTextComponent) displayName).appendString(" (").append(new TranslationTextComponent(key)).appendString(")");
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
		return ModItems.BLIGHT_SHARD.get() == repair.getItem();
	}
}
