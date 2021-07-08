package com.github.elenterius.biomancy.handler;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.MeatsoupCauldronBlock;
import com.github.elenterius.biomancy.damagesource.ModEntityDamageSource;
import com.github.elenterius.biomancy.enchantment.AttunedDamageEnchantment;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.weapon.ClawWeaponItem;
import com.github.elenterius.biomancy.item.weapon.FleshbornGuanDaoItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)

public final class BlockEventHandler {

	private BlockEventHandler() {}
	@SubscribeEvent
	public void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
		if (event.getState().matchesBlock(ModBlocks.MEATSOUP_CAULDRON.get())) {
			System.out.println("meat beaten!");
			BiomancyMod.LOGGER.info("meat beaten!");
			BlockState Bl = event.getState();
            MeatsoupCauldronBlock block = (MeatsoupCauldronBlock) Bl.getBlock();
            block.TryToUseHopper((World) event.getWorld(), event.getPos(), Bl, Bl.get(block.LEVEL));
		}
	}
}