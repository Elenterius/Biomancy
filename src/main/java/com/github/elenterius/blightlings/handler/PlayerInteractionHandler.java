package com.github.elenterius.blightlings.handler;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.block.MeatsoupCauldronBlock;
import com.github.elenterius.blightlings.init.ModBlocks;
import com.github.elenterius.blightlings.init.ModItems;
import com.github.elenterius.blightlings.init.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerInteractionHandler {
	private PlayerInteractionHandler() {}

	@SubscribeEvent
	public static void onPlayerInteraction(final PlayerInteractEvent.EntityInteract event) {
		if (event.getWorld().isRemote()) return;
		if (event.getPlayer().isSneaking() && event.getTarget() instanceof ItemFrameEntity && !event.getItemStack().isEmpty() && event.getItemStack().getItem() == ModItems.LUMINESCENT_SPORES.get()) {
			event.setCanceled(true);
			event.getTarget().setInvisible(!event.getTarget().isInvisible());
			if (!event.getPlayer().isCreative()) {
				event.getItemStack().shrink(1);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerInteraction(final PlayerInteractEvent.EntityInteractSpecific event) {
		if (event.getWorld().isRemote()) return;
		if (event.getPlayer().isSneaking() && event.getTarget() instanceof ArmorStandEntity && !event.getItemStack().isEmpty() && event.getItemStack().getItem() == ModItems.LUMINESCENT_SPORES.get()) {
			event.setCanceled(true);
			event.getTarget().setInvisible(!event.getTarget().isInvisible());
			if (!event.getPlayer().isCreative()) {
				event.getItemStack().shrink(1);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().isRemote()) return;
		if (!event.getItemStack().isEmpty() && event.getItemStack().getItem().isIn(ModTags.Items.RAW_MEATS)) {
			BlockState blockState = event.getWorld().getBlockState(event.getPos());
			if (blockState.isIn(Blocks.CAULDRON) && blockState == Blocks.CAULDRON.getDefaultState()) {
				if (!event.getPlayer().abilities.isCreativeMode) {
					event.getItemStack().grow(-1);
				}
				event.getPlayer().addStat(Stats.USE_CAULDRON);

				BlockState meatState = ModBlocks.MEATSOUP_CAULDRON.get().getDefaultState().with(MeatsoupCauldronBlock.LEVEL, 1);
				event.getWorld().setBlockState(event.getPos(), meatState, Constants.BlockFlags.BLOCK_UPDATE);
				event.getWorld().playSound(null, event.getPos(), SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
			}
		}
	}
}
