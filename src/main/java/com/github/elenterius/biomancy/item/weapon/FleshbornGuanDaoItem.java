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

import static net.minecraft.item.ItemStack.DECIMALFORMAT;

public class FleshbornGuanDaoItem extends PoleWeaponItem {

	public static final AttributeModifier ATTACK_DAMAGE_RIDING_MODIFIER = new AttributeModifier(UUID.fromString("CBD1DE77-3F1D-4E8B-839A-AA471A93D424"), "riding_attack_modifier", 4f, AttributeModifier.Operation.ADDITION);

	public FleshbornGuanDaoItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builderIn) {
		super(tier, attackDamageIn, attackSpeedIn, builderIn);
	}

	public static void removeSpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.removeModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
	}

	public static void applySpecialAttributeModifiers(LivingEntity livingEntity) {
		ModifiableAttributeInstance modifiableAttributeInstance = livingEntity.getAttributeManager().createInstanceIfAbsent(Attributes.ATTACK_DAMAGE);
		if (modifiableAttributeInstance != null && !modifiableAttributeInstance.hasModifier(ATTACK_DAMAGE_RIDING_MODIFIER)) {
			modifiableAttributeInstance.applyNonPersistentModifier(ATTACK_DAMAGE_RIDING_MODIFIER);
		}
	}

	public static void adaptAttackDamageToTarget(ItemStack stack, LivingEntity attacker, Entity target) {
		CompoundNBT nbt = stack.getOrCreateChildTag(BiomancyMod.MOD_ID);
		UUID targetUUID = target.getUniqueID();

		if (nbt.hasUniqueId("TargetUUID")) {
			if (targetUUID.equals(nbt.getUniqueId("TargetUUID"))) {
				if (!attacker.world.isRemote()) {
					byte count = (byte) MathHelper.clamp(nbt.getByte("HitCount") + 1, 0, 10);
					nbt.putByte("HitCount", count);
				}
				else attacker.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1f, 1f);
				return;
			}
		}

		if (!attacker.world.isRemote()) {
			nbt.putUniqueId("TargetUUID", targetUUID);
			nbt.putByte("HitCount", (byte) 0);
		}
		else attacker.playSound(SoundEvents.ENTITY_STRIDER_EAT, 1f, 1f);
	}

	public static float getAttackDamageModifier(ItemStack stack, LivingEntity attacker, Entity target) {
		if (!stack.isEmpty()) {
			CompoundNBT nbt = stack.getOrCreateChildTag(BiomancyMod.MOD_ID);
			if (nbt.hasUniqueId("TargetUUID")) {
				int hitCount = nbt.getByte("HitCount");
				if (hitCount > 0 && target.getUniqueID().equals(nbt.getUniqueId("TargetUUID"))) {
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		CompoundNBT nbt = stack.getOrCreateChildTag(BiomancyMod.MOD_ID);
		byte hitCount = nbt.getByte("HitCount");
		String key;
		if (hitCount > 9) key = "item_is_exalted";
		else if (hitCount > 0) key = "item_is_excited";
		else key = "item_is_awake";
		tooltip.add(new TranslationTextComponent("tooltip.biomancy." + key).mergeStyle(TextFormatting.GRAY));
		tooltip.add(new StringTextComponent("Hunger: " + nbt.getByte("Hunger")).mergeStyle(TextFormatting.DARK_GRAY));
		tooltip.add(new StringTextComponent("Damage Modifier: +" + (hitCount == 0 ? 0 : (1f + Math.max(0, hitCount - 1) * 0.5f))).mergeStyle(TextFormatting.DARK_GRAY));

		if (stack.isEnchanted()) {
			if (ClientTextUtil.isToolTipVisible(stack, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
				stack.func_242395_a(ItemStack.TooltipDisplayFlags.ENCHANTMENTS); //hide enchantment tooltip
			}
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			ItemStack.addEnchantmentTooltips(tooltip, stack.getEnchantmentTagList()); //add enchantments before custom modifiers
		}

		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
		tooltip.add(TextUtil.getTranslationText("tooltip", "riding_bonus").setStyle(Style.EMPTY.applyFormatting(TextFormatting.GRAY)));
		tooltip.add((new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.plus." + ATTACK_DAMAGE_RIDING_MODIFIER.getOperation().getId(), DECIMALFORMAT.format(ATTACK_DAMAGE_RIDING_MODIFIER.getAmount()), new TranslationTextComponent("attribute.name.generic.attack_damage"))).mergeStyle(TextFormatting.BLUE));
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote() && worldIn.getGameTime() % 20L == 0L && entityIn instanceof LivingEntity) {
			CompoundNBT nbt = stack.getOrCreateChildTag(BiomancyMod.MOD_ID);
			int hunger = nbt.getByte("Hunger");
			if (hunger > 100) {
				int feedGoal = Math.min(125 + hunger, MathHelper.floor(hunger * 0.7f + stack.getDamage() * 0.025f));
				int replenished = tryToEatMeat((LivingEntity) entityIn, feedGoal);
				if (replenished < feedGoal) {
					entityIn.attackEntityFrom(ModDamageSources.SYMBIONT_EAT, 2f);
				}
				nbt.putByte("Hunger", (byte) MathHelper.clamp(hunger - replenished, -125, 125));
			}
			else if (hunger > 45 && worldIn.rand.nextFloat() < 0.3f) {
				int feedGoal = Math.min(125 + hunger, MathHelper.floor(hunger * worldIn.rand.nextFloat() + stack.getDamage() * 0.025f));
				int replenished = tryToEatMeat((LivingEntity) entityIn, feedGoal);
				if (replenished < feedGoal) {
					entityIn.attackEntityFrom(ModDamageSources.SYMBIONT_EAT, 0.5f);
				}
				nbt.putByte("Hunger", (byte) MathHelper.clamp(hunger - replenished, -125, 125));
			}
			else if (hunger < 0 && stack.isDamaged() && worldIn.rand.nextFloat() < 0.45f) {
				int repairAmount = MathHelper.ceil(-hunger * (0.5f + worldIn.rand.nextFloat() * 0.5f));
				int remainder = stack.getDamage() - repairAmount;
				if (remainder < stack.getDamage()) {
					stack.setDamage(remainder);
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
				if (!unsafeStack.isEmpty() && unsafeStack.isFood()) {
					Food food = unsafeStack.getItem().getFood();
					if (food != null && food.isMeat()) {
						int feedValue = food.getHealing();
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
