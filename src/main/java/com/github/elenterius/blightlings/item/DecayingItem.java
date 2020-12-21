package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.capabilities.IItemDecayTracker;
import com.github.elenterius.blightlings.init.ModCapabilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class DecayingItem extends Item {

    public final int halfTime;
    public final float decayFactor;

    public DecayingItem(int halfTimeInSeconds, float decayFactor, Properties properties) {
        super(properties);
        this.halfTime = halfTimeInSeconds;
        this.decayFactor = decayFactor;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        //sync capability to client??
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        LazyOptional<IItemDecayTracker> capability = stack.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
        capability.ifPresent(decayTracker -> decayTracker.onUpdate(stack, entity.world, entity, halfTime * 20L, decayFactor, false));
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        LazyOptional<IItemDecayTracker> capability = stack.getCapability(ModCapabilities.ITEM_DECAY_CAPABILITY);
        capability.ifPresent(decayTracker -> decayTracker.onUpdate(stack, world, entity, halfTime * 20L, decayFactor, false));
    }
}
