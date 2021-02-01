package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.util.TooltipUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BagItem extends Item implements IOwnableItem {

	public BagItem(Properties properties) {
		super(properties);
	}

	/**
	 * @return fullness as percentage (0 - 1)
	 */
	public float getFullness(ItemStack stack) {
		return 0f;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (flagIn.isAdvanced() && worldIn != null) {
			Optional<UUID> owner = getOwner(stack);
			owner.ifPresent(uuid -> {
				PlayerEntity player = worldIn.getPlayerByUuid(uuid);
				tooltip.add(new StringTextComponent("Owner: ").append(player != null ? player.getDisplayName() : new StringTextComponent(owner.toString())));
			});
		}
		tooltip.add(TooltipUtil.getTooltip(this).setStyle(TooltipUtil.LORE_STYLE));
		tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
	}

	public void onPlayerInteractWithItem(ItemStack stack, LivingEntity entity) {
		if (hasOwner(stack) && !isOwner(stack, entity.getUniqueID())) {
			entity.attackEntityFrom(ModDamageSources.SYMBIONT_BITE, 1.5f);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

}
