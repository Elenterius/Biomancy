package com.github.elenterius.biomancy.enchantment;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEffects;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class AttunedDamageEnchantment extends Enchantment {

	public static final String NBT_KEY = StringUtils.capitalize(BiomancyMod.MOD_ID) + "AttunedTarget";

	public AttunedDamageEnchantment(Rarity rarityIn, EquipmentSlotType... slots) {
		super(rarityIn, EnchantmentType.WEAPON, slots);
	}

	public static boolean isAttuned(ItemStack stack) {
		return stack.getOrCreateTag().contains(NBT_KEY);
	}

	public static void setAttunedTarget(ItemStack stack, Entity target) {
		ResourceLocation registryName = target.getType().getRegistryName();
		CompoundNBT nbt = stack.getOrCreateChildTag(NBT_KEY);
		nbt.putString("EntityType", registryName != null ? registryName.toString() : "");
		if (target instanceof PlayerEntity) {
			nbt.putUniqueId("PlayerUUID", target.getUniqueID());
			nbt.putString("PlayerName", ((PlayerEntity) target).getGameProfile().getName());
		}
		else {
			nbt.remove("PlayerUUID");
			nbt.remove("PlayerName");
		}
	}

	@Nullable
	public static ResourceLocation getAttunedTarget(ItemStack stack) {
		String key = stack.getOrCreateTag().getCompound(NBT_KEY).getString("EntityType");
		return ResourceLocation.tryCreate(key);
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public boolean canGenerateInLoot() {
		return false;
	}

	@Override
	public boolean canVillagerTrade() {
		return false;
	}

	@Override
	public boolean canApply(ItemStack stack) {
		Item item = stack.getItem();
		return item instanceof AxeItem || item instanceof TridentItem || super.canApply(stack);
	}

	@Override
	protected boolean canApplyTogether(Enchantment enchantment) {
		return !(enchantment instanceof DamageEnchantment);
	}

	public ITextComponent getDisplayName(int level, ItemStack stack) {
		ITextComponent name = null;
		CompoundNBT nbt = stack.getOrCreateTag().getCompound(NBT_KEY);
		if (nbt.hasUniqueId("PlayerUUID")) {
//			name = Minecraft.getInstance().world.getPlayerByUuid(nbt.getUniqueId("playerUUID")).getDisplayName();
			name = new StringTextComponent(nbt.getString("PlayerName"));
		}
		else {
			ResourceLocation registryName = ResourceLocation.tryCreate(nbt.getString("EntityType"));
			if (registryName != null) {
				EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(registryName);
				if (entityType != null) {
					name = entityType.getName();
				}
			}
		}
		IFormattableTextComponent textComponent = new TranslationTextComponent("enchantment.biomancy.attuned_bane_of", name != null ? name : new StringTextComponent("null").mergeStyle(TextFormatting.OBFUSCATED, TextFormatting.RED));

		if (level != 1 || getMaxLevel() != 1) {
			textComponent.appendString(" ").append(new TranslationTextComponent("enchantment.level." + level));
		}

		return textComponent.mergeStyle(TextFormatting.GRAY);
	}

	public float getAttackDamageModifier(ItemStack stack, LivingEntity attacker, Entity target) {
		if (!stack.isEmpty()) {
			int level = EnchantmentHelper.getEnchantmentLevel(this, stack);
			if (level > 0f) {
				ResourceLocation targetKey = target.getType().getRegistryName();
				if (targetKey != null && targetKey.equals(getAttunedTarget(stack))) {
					return 1f + Math.max(0, level - 1) * 0.5f;
				}
			}
		}
		return 0f;
	}

	@Override
	public void onEntityDamaged(LivingEntity attacker, Entity target, int level) {
		if (target instanceof LivingEntity) {
			ItemStack heldStack = attacker.getHeldItemMainhand();
			if (!heldStack.isEmpty() && isAttuned(heldStack)) {
				ResourceLocation targetKey = target.getType().getRegistryName();
				if (targetKey != null && targetKey.equals(getAttunedTarget(heldStack))) {
					int ticks = 20 + attacker.getRNG().nextInt(10 * level);
					int amplifier = 1 + level >> 1; // 6,5 => 3; 4,3 => 2; 2,1 => 1
					((LivingEntity) target).addPotionEffect(new EffectInstance(ModEffects.DREAD.get(), ticks, amplifier));
				}
			}
		}
	}
}
