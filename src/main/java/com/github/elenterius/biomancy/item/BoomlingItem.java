package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class BoomlingItem extends Item {

	public BoomlingItem(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		if (stack.hasTag() && stack.getTag() != null) {
			String potionTranslationKey = stack.getTag().getString("PotionName");
			if (!potionTranslationKey.isEmpty())
				tooltip.add(new TranslationTextComponent(potionTranslationKey).setStyle(Style.EMPTY.setFormatting(TextFormatting.GRAY)));
		}
		PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		if (!hasContainerItem(stack)) {
			return ItemStack.EMPTY;
		}
		else {
			ItemStack stack1 = stack.copy();
			stack1.removeChildTag("Potion");
			stack1.removeChildTag("PotionItem");
			return stack1;
		}
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return containsPotion(stack);
	}

	public boolean containsPotion(ItemStack stack) {
		return PotionUtils.getPotionFromItem(stack) != Potions.EMPTY;
	}

	public int getPotionColor(ItemStack stack) {
		Potion potion = PotionUtils.getPotionFromItem(stack);
		if (potion != Potions.EMPTY) {
			return PotionUtils.getPotionColorFromEffectList(PotionUtils.getEffectsFromStack(stack));
		}
		return -1;
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getWorld().isRemote()) return ActionResultType.PASS;

		PlayerEntity player = context.getPlayer();
		if (player != null) {
			if (context.getWorld().isBlockModifiable(player, context.getPos()) && player.canPlayerEdit(context.getPos(), context.getFace(), stack)) {
				BoomlingEntity entity = ModEntityTypes.BOOMLING.get().create(context.getWorld());
				if (entity != null) {
					float widthFactor = entity.getWidth() * 0.6f; //prevent mobs from suffocating in walls as much as possible
					float yOffset = context.getFace().getYOffset();
					float heightModifier = yOffset < 0f ? -entity.getHeight() : yOffset > 0f ? 0f : entity.getHeight() * 0.5f;
					Vector3d pos = context.getHitVec().add(context.getFace().getXOffset() * widthFactor, heightModifier, context.getFace().getZOffset() * widthFactor);
					entity.setLocationAndAngles(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(context.getWorld().rand.nextFloat() * 360f), 0f);
					entity.rotationYawHead = entity.rotationYaw;
					entity.renderYawOffset = entity.rotationYaw;
					entity.setMotion(0, 0, 0);
					entity.fallDistance = 0;

					entity.enablePersistence();
					if (stack.hasDisplayName()) {
						entity.setCustomName(stack.getDisplayName());
						entity.setCustomNameVisible(true);
					}
					entity.setOwner(player);
					entity.setStoredPotion(getPotionItemStack(stack));

					if (context.getWorld().addEntity(entity)) {
						entity.playAmbientSound();
						stack.shrink(1);
						return ActionResultType.SUCCESS;
					}
				}
				return ActionResultType.FAIL;
			}
			return ActionResultType.FAIL;
		}
		return ActionResultType.PASS;
	}

	public ItemStack getPotionItemStack(ItemStack stackIn) {
		Potion potion = PotionUtils.getPotionFromItem(stackIn);
		if (potion != Potions.EMPTY) {
			List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(stackIn);
			Item potionItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(stackIn.getOrCreateTag().getString("PotionItem")));
			ItemStack stack = new ItemStack(potionItem instanceof PotionItem ? potionItem : Items.POTION);
			PotionUtils.addPotionToItemStack(stack, potion);
			PotionUtils.appendEffects(stack, effects);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	public ItemStack setPotionItemStack(ItemStack beetleStack, ItemStack potionStack) {
		if (!potionStack.isEmpty() && potionStack.getItem() instanceof PotionItem) {
			Potion potion = PotionUtils.getPotionFromItem(potionStack);
			List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(potionStack);
			PotionUtils.addPotionToItemStack(beetleStack, potion);
			PotionUtils.appendEffects(beetleStack, effects);
			ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(potionStack.getItem());
			if (registryKey != null) beetleStack.getOrCreateTag().putString("PotionItem", registryKey.toString());
			beetleStack.getOrCreateTag().putString("PotionName", potionStack.getTranslationKey());
		}
		return beetleStack;
	}

}
