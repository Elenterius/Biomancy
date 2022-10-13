package com.github.elenterius.biomancy.handler.event;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.EvolutionPoolBlock;
import com.github.elenterius.biomancy.block.MeatsoupCauldronBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.init.ModTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
			if (event.getWorld().isClientSide()) return; //TODO: figure out what to do on the client side to make it work the correct way

			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.FAIL);

			//manually call the function
			ModItems.INJECTION_DEVICE.get().interactLivingEntity(event.getItemStack(), event.getPlayer(), (LivingEntity) event.getTarget(), event.getHand());
		}
	}

	@SubscribeEvent
	public static void onPlayerRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
		if (!event.getItemStack().isEmpty()) {
			Item item = event.getItemStack().getItem();
			if (ModTags.Items.isRawMeat(item)) {
				if (!event.getWorld().isClientSide()) {
					BlockState blockState = event.getWorld().getBlockState(event.getPos());
					if (blockState.is(Blocks.CAULDRON) && blockState == Blocks.CAULDRON.defaultBlockState()) {
						if (!event.getPlayer().abilities.instabuild) {
							event.getItemStack().grow(-1);
						}
						event.getPlayer().awardStat(Stats.USE_CAULDRON);

						BlockState meatState = ModBlocks.MEATSOUP_CAULDRON.get().defaultBlockState().setValue(MeatsoupCauldronBlock.LEVEL, 1);
						event.getWorld().setBlock(event.getPos(), meatState, Constants.BlockFlags.BLOCK_UPDATE);
						event.getWorld().playSound(null, event.getPos(), SoundEvents.SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
					}
				}
			}
			else if (item == ModItems.MUTAGENIC_BILE.get()) {
				BlockPos pos = event.getPos();
				BlockState blockState = event.getWorld().getBlockState(pos);
				if (EvolutionPoolBlock.isValidStairsBlock(blockState)) {
					if (!event.getWorld().isClientSide()) {
						if (EvolutionPoolBlock.tryToCreate2x2EvolutionPool(event.getWorld(), blockState, pos)) {
							if (!event.getPlayer().abilities.instabuild) event.getItemStack().grow(-1);

							event.getWorld().playSound(null, pos, SoundEvents.SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);

							if (event.getPlayer() instanceof ServerPlayerEntity) {
								ModTriggers.EVOLUTION_POOL_CREATED.trigger((ServerPlayerEntity) event.getPlayer());
							}
						}
					}
					event.setCanceled(true);
					event.setCancellationResult(ActionResultType.FAIL);
				}
			}
		}
	}
}
