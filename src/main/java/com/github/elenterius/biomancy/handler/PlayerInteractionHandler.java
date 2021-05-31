package com.github.elenterius.biomancy.handler;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.EvolutionPoolBlock;
import com.github.elenterius.biomancy.block.MeatsoupCauldronBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerInteractionHandler {
	private PlayerInteractionHandler() {}

	@SubscribeEvent
	public static void onPlayerInteraction(final PlayerInteractEvent.EntityInteractSpecific event) {
		//workaround for interacting with armor stands
		if (event.getTarget() instanceof ArmorStandEntity && event.getItemStack().getItem() == ModItems.INJECTION_DEVICE.get()) {
			if (event.getWorld().isRemote()) return; //TODO: figure out what to do on the client side to make it work the correct way

			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.FAIL);

			//manually call the function
			ModItems.INJECTION_DEVICE.get().itemInteractionForEntity(event.getItemStack(), event.getPlayer(), (LivingEntity) event.getTarget(), event.getHand());
		}
	}

	@SubscribeEvent
	public static void onPlayerRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().isRemote()) return;

		if (!event.getItemStack().isEmpty()) {
			Item item = event.getItemStack().getItem();
			if (item.isIn(ModTags.Items.RAW_MEATS)) {
				BlockState blockState = event.getWorld().getBlockState(event.getPos());
				if (blockState.matchesBlock(Blocks.CAULDRON) && blockState == Blocks.CAULDRON.getDefaultState()) {
					if (!event.getPlayer().abilities.isCreativeMode) {
						event.getItemStack().grow(-1);
					}
					event.getPlayer().addStat(Stats.USE_CAULDRON);

					BlockState meatState = ModBlocks.MEATSOUP_CAULDRON.get().getDefaultState().with(MeatsoupCauldronBlock.LEVEL, 1);
					event.getWorld().setBlockState(event.getPos(), meatState, Constants.BlockFlags.BLOCK_UPDATE);
					event.getWorld().playSound(null, event.getPos(), SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
				}
			}
			else if (item == ModItems.MUTAGENIC_BILE.get()) {
				BlockPos pos = event.getPos();
				BlockState blockState = event.getWorld().getBlockState(pos);
				if (EvolutionPoolBlock.tryToCreate2x2EvolutionPool(event.getWorld(), blockState, pos)) {
					if (!event.getPlayer().abilities.isCreativeMode) {
						event.getItemStack().grow(-1);
					}
					event.getWorld().playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
					event.setCanceled(true);
				}
			}
		}
	}
}
