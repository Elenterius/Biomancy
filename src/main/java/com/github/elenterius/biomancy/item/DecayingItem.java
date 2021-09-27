package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.capabilities.IItemDecayTracker;
import com.github.elenterius.biomancy.init.ModCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class DecayingItem extends Item {

	public final int halfTime;
	public final float decayFactor;

	public DecayingItem(int halfTimeInSeconds, float decayFactor, Properties properties) {
		super(properties);
		this.halfTime = halfTimeInSeconds;
		this.decayFactor = decayFactor;
	}

//	@OnlyIn(Dist.CLIENT)
//	@Override
//	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//		if (flagIn.isAdvanced() && worldIn != null) {
//			LazyOptional<IItemDecayTracker> capability = stack.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
//			capability.ifPresent(decayTracker -> {
//				long elapsedTime = worldIn.getGameTime() - decayTracker.getStartTime();
//				tooltip.add(new StringTextComponent("Elapsed Time: " + elapsedTime));
//			});
//		}
//	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		LazyOptional<IItemDecayTracker> capability = stack.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
		capability.ifPresent(decayTracker -> decayTracker.onUpdate(stack, entity.level, entity, halfTime * 20L, decayFactor, false));
		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		LazyOptional<IItemDecayTracker> capability = stack.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
		capability.ifPresent(decayTracker -> decayTracker.onUpdate(stack, world, entity, halfTime * 20L, decayFactor, false));
	}
}
