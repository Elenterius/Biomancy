package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.Food;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static net.minecraft.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public class FleshbornGuanDaoItem extends PoleWeaponItem {

	public static final AttributeModifier ATTACK_DAMAGE_RIDING_MODIFIER = new AttributeModifier(UUID.fromString("CBD1DE77-3F1D-4E8B-839A-AA471A93D424"), "riding_attack_modifier", 4f, AttributeModifier.Operation.ADDITION);

	public FleshbornGuanDaoItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
	}

	public static void removeSpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.removeModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
	}

	public static void applySpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.addTransientModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
	}

	public static void adaptAttackDamageToTarget(ItemStack stack, LivingEntity attacker, Entity target) {
		CompoundNBT nbt = stack.getOrCreateTagElement(BiomancyMod.MOD_ID);
		UUID targetUUID = target.getUUID();

		if (nbt.hasUUID("TargetUUID")) {
			if (targetUUID.equals(nbt.getUUID("TargetUUID"))) {
				if (!attacker.level.isClientSide()) {
					byte count = (byte) MathHelper.clamp(nbt.getByte("HitCount") + 1, 0, 10);
					nbt.putByte("HitCount", count);
				}
				else attacker.playSound(SoundEvents.PLAYER_BURP, 1f, 1f);
				return;
			}
		}

		if (!attacker.level.isClientSide()) {
			nbt.putUUID("TargetUUID", targetUUID);
			nbt.putByte("HitCount", (byte) 0);
		}
		else attacker.playSound(SoundEvents.STRIDER_EAT, 1f, 1f);
	}

	public static float getAttackDamageModifier(ItemStack stack, LivingEntity attacker, Entity target) {
		if (!stack.isEmpty()) {
			CompoundNBT nbt = stack.getOrCreateTagElement(BiomancyMod.MOD_ID);
			if (nbt.hasUUID("TargetUUID")) {
				int hitCount = nbt.getByte("HitCount");
				if (hitCount > 0 && target.getUUID().equals(nbt.getUUID("TargetUUID"))) {
					float modifier = 1f + Math.max(0, hitCount - 1) * 0.5f;
					nbt.putByte("Hunger", (byte) MathHelper.clamp(nbt.getByte("Hunger") + modifier, -125, 125));
					return modifier;
				}
			}
		}
		return 0f;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		CompoundNBT nbt = stack.getOrCreateTagElement(BiomancyMod.MOD_ID);
		byte hitCount = nbt.getByte("HitCount");
		String key;
		if (hitCount > 9) key = "item_is_exalted";
		else if (hitCount > 0) key = "item_is_excited";
		else key = "item_is_awake";
		tooltip.add(new TranslationTextComponent("tooltip.biomancy." + key).withStyle(TextFormatting.GRAY));
		tooltip.add(new StringTextComponent("Hunger: " + nbt.getByte("Hunger")).withStyle(TextFormatting.DARK_GRAY));
		tooltip.add(new StringTextComponent("Damage Modifier: +" + (hitCount == 0 ? 0 : (1f + Math.max(0, hitCount - 1) * 0.5f))).withStyle(TextFormatting.DARK_GRAY));

		if (stack.isEnchanted()) {
			if (ClientTextUtil.isToolTipVisible(stack, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
				stack.hideTooltipPart(ItemStack.TooltipDisplayFlags.ENCHANTMENTS); //hide enchantment tooltip
			}
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			ItemStack.appendEnchantmentNames(tooltip, stack.getEnchantmentTags()); //add enchantments before custom modifiers
		}

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		tooltip.add(TextUtil.getTranslationText("tooltip", "riding_bonus").setStyle(Style.EMPTY.applyFormat(TextFormatting.GRAY)));
		tooltip.add((new StringTextComponent(" ")).append(new TranslationTextComponent("attribute.modifier.plus." + ATTACK_DAMAGE_RIDING_MODIFIER.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(ATTACK_DAMAGE_RIDING_MODIFIER.getAmount()), new TranslationTextComponent("attribute.name.generic.attack_damage"))).withStyle(TextFormatting.BLUE));
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isClientSide() && worldIn.getGameTime() % 20L == 0L && entityIn instanceof LivingEntity) {
			CompoundNBT nbt = stack.getOrCreateTagElement(BiomancyMod.MOD_ID);
			int hunger = nbt.getByte("Hunger");
			if (hunger > 100) {
				int feedGoal = Math.min(125 + hunger, MathHelper.floor(hunger * 0.7f + stack.getDamageValue() * 0.025f));
				int replenished = tryToEatMeat((LivingEntity) entityIn, feedGoal);
				if (replenished < feedGoal) {
					entityIn.hurt(ModDamageSources.SYMBIONT_EAT, 2f);
				}
				nbt.putByte("Hunger", (byte) MathHelper.clamp(hunger - replenished, -125, 125));
			}
			else if (hunger > 45 && worldIn.random.nextFloat() < 0.3f) {
				int feedGoal = Math.min(125 + hunger, MathHelper.floor(hunger * worldIn.random.nextFloat() + stack.getDamageValue() * 0.025f));
				int replenished = tryToEatMeat((LivingEntity) entityIn, feedGoal);
				if (replenished < feedGoal) {
					entityIn.hurt(ModDamageSources.SYMBIONT_EAT, 0.5f);
				}
				nbt.putByte("Hunger", (byte) MathHelper.clamp(hunger - replenished, -125, 125));
			}
			else if (hunger < 0 && stack.isDamaged() && worldIn.random.nextFloat() < 0.45f) {
				int repairAmount = MathHelper.ceil(-hunger * (0.5f + worldIn.random.nextFloat() * 0.5f));
				int remainder = stack.getDamageValue() - repairAmount;
				if (remainder < stack.getDamageValue()) {
					stack.setDamageValue(remainder);
					nbt.putByte("Hunger", (byte) MathHelper.clamp(hunger + repairAmount - remainder, -125, 0));
				}
			}
		}
	}

	public int tryToEatMeat(LivingEntity entity, final int feedAmountGoal) {
		final int[] replenished = {0};

		LazyOptional<IItemHandler> capability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		capability.ifPresent(itemHandler -> {
			int nSlots = itemHandler.getSlots();
			for (int i = 0; i < nSlots; i++) {
				ItemStack unsafeStack = itemHandler.getStackInSlot(i);
				if (!unsafeStack.isEmpty() && unsafeStack.isEdible()) {
					Food food = unsafeStack.getItem().getFoodProperties();
					if (food != null && food.isMeat()) {
						int feedValue = food.getNutrition();
						int amount = 1;
						int maxAmount = unsafeStack.getCount();
						while (feedValue * amount < feedAmountGoal - replenished[0] && amount + 1 < maxAmount) {
							amount++;
						}
						ItemStack extractedStack = itemHandler.extractItem(i, amount, false);
						replenished[0] += feedValue * extractedStack.getCount();
						if (replenished[0] >= feedAmountGoal) break;
					}
				}
			}
		});

		return replenished[0];
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

}
