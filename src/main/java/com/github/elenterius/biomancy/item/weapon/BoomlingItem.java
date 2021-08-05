package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
			String potionTranslationKey = PotionUtilExt.getPotionTranslationKeyFromHost(stack);
			if (!potionTranslationKey.isEmpty())
				tooltip.add(new TranslationTextComponent(potionTranslationKey).setStyle(Style.EMPTY.setFormatting(TextFormatting.GRAY)));
		}
		PotionUtilExt.addPotionTooltip(stack, tooltip, 1f);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		if (!hasContainerItem(stack)) {
			return ItemStack.EMPTY;
		}
		else {
			ItemStack stack1 = stack.copy();
			PotionUtilExt.removePotionFromHost(stack1);
			return stack1;
		}
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return containsPotion(stack);
	}

	public boolean containsPotion(ItemStack stack) {
		return PotionUtilExt.getPotionFromItem(stack) != Potions.EMPTY;
	}

	public int getPotionColor(ItemStack stack) {
		return PotionUtilExt.getPotionColor(stack);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getWorld().isRemote()) return ActionResultType.PASS;

		PlayerEntity player = context.getPlayer();
		if (player != null) {
			if (context.getWorld().isBlockModifiable(player, context.getPos()) && player.canPlayerEdit(context.getPos(), context.getFace(), stack)) {
				BoomlingEntity entity = ModEntityTypes.BOOMLING.get().create(context.getWorld());
				if (entity != null) {
					Vector3d pos = MobUtil.getSimpleOffsetPosition(context.getHitVec(), context.getFace(), entity);
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
					entity.setStoredPotion(PotionUtilExt.getPotionItemStack(stack));

					if (context.getWorld().addEntity(entity)) {
						entity.playAmbientSound();
						stack.shrink(1);
						return ActionResultType.SUCCESS;
					}
				}
			}
			return ActionResultType.FAIL;
		}
		return ActionResultType.PASS;
	}

}
