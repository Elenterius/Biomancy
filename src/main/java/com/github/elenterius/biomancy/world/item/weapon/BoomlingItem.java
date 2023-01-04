package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.PotionUtilExt;
import com.github.elenterius.biomancy.world.item.ICustomTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

@Deprecated
public class BoomlingItem extends Item implements ICustomTooltip {

	public BoomlingItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		//		if (context.getLevel().isClientSide()) return InteractionResult.PASS;
		//
		//		Player player = context.getPlayer();
		//		if (player != null) {
		//			if (context.getLevel().mayInteract(player, context.getClickedPos()) && player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {
		//				Boomling entity = ModEntityTypes.BOOMLING.get().create(context.getLevel());
		//				if (entity != null) {
		//					Vec3 pos = MobUtil.getAdjustedSpawnPositionFor(context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), entity);
		//					entity.moveTo(pos.x, pos.y, pos.z, Mth.wrapDegrees(context.getLevel().random.nextFloat() * 360f), 0f);
		//					entity.yHeadRot = entity.getYRot();
		//					entity.yBodyRot = entity.getYRot();
		//					entity.setDeltaMovement(0, 0, 0);
		//					entity.fallDistance = 0;
		//
		//					entity.setPersistenceRequired();
		//					if (stack.hasCustomHoverName()) {
		//						entity.setCustomName(stack.getHoverName());
		//						entity.setCustomNameVisible(true);
		//					}
		//					entity.setOwner(player);
		//					entity.setStoredPotion(PotionUtilExt.getPotionItemStack(stack));
		//
		//					if (context.getLevel().addFreshEntity(entity)) {
		//						entity.playAmbientSound();
		//						stack.shrink(1);
		//						return InteractionResult.SUCCESS;
		//					}
		//				}
		//			}
		//			return InteractionResult.FAIL;
		//		}
		return InteractionResult.PASS;
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
		return PotionUtilExt.getPotion(stack) != Potions.EMPTY;
	}

	public int getPotionColor(ItemStack stack) {
		return PotionUtilExt.getPotionColor(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(TextStyles.LORE));
		if (stack.hasTag() && stack.getTag() != null) {
			String potionTranslationKey = PotionUtilExt.getPotionTranslationKeyFromHost(stack);
			if (!potionTranslationKey.isEmpty())
				tooltip.add(ComponentUtil.translatable(potionTranslationKey).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
		}
		PotionUtilExt.addPotionTooltip(stack, tooltip, 1f);
	}

}
