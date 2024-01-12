package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyConfig;
import com.github.elenterius.biomancy.BiomancyMod;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public class ModVillagerTrades {

	@SubscribeEvent
	public static void onVillagerTrades(final VillagerTradesEvent event) {
		if (Boolean.FALSE.equals(BiomancyConfig.SERVER.addTradesToVillagers.get())) return;

		if (event.getType() == VillagerProfession.BUTCHER) {
			addButcherTrades(event.getTrades());
		}
		else if (event.getType() == VillagerProfession.CLERIC) {
			addClericTrades(event.getTrades());
		}
	}

	@SubscribeEvent
	public static void onWandererTrades(final WandererTradesEvent event) {
		if (Boolean.FALSE.equals(BiomancyConfig.SERVER.addTradesToWanderingTrader.get())) return;

		List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
		genericTrades.add(sellToPlayer(ModItems.EXOTIC_DUST.get(), 4, 2, 16, 5));
		genericTrades.add(sellToPlayer(ModItems.FLESH_SPIKE.get(), 2, 16, 5));
		genericTrades.add(buyFromPlayer(ModItems.NUTRIENT_BAR.get(), 2, 8, 5));

		List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();
		rareTrades.add(sellToPlayer(ModItems.INSOMNIA_CURE.get(), 10, 8, 20));
		rareTrades.add(sellToPlayer(ModItems.CREATOR_MIX.get(), 10, 5, 20));
	}

	private static BasicItemListing buyFromPlayer(Item item, int emeralds, int maxTrades, int xp) {
		return new BasicItemListing(new ItemStack(item), new ItemStack(Items.EMERALD, emeralds), maxTrades, xp, 0.05F);
	}

	private static BasicItemListing buyFromPlayer(Item item, int amount, int emeralds, int maxTrades, int xp) {
		return new BasicItemListing(new ItemStack(item, amount), new ItemStack(Items.EMERALD, emeralds), maxTrades, xp, 0.05F);
	}

	private static BasicItemListing convertItem(Item item, int emeralds, Item result, int resultAmount, int maxTrades, int xp) {
		return new BasicItemListing(new ItemStack(item), new ItemStack(Items.EMERALD, emeralds), new ItemStack(result, resultAmount), maxTrades, xp, 0.05F);
	}

	private static BasicItemListing convertItem(Item item, int emeralds, Item result, int maxTrades, int xp) {
		return new BasicItemListing(new ItemStack(item), new ItemStack(Items.EMERALD, emeralds), new ItemStack(result), maxTrades, xp, 0.05F);
	}

	private static BasicItemListing sellToPlayer(Item item, int emeralds, int maxTrades, int xp) {
		return new BasicItemListing(emeralds, new ItemStack(item), maxTrades, xp, 0.05F);
	}

	private static BasicItemListing sellToPlayer(Item item, int amount, int emeralds, int maxTrades, int xp) {
		return new BasicItemListing(emeralds, new ItemStack(item, amount), maxTrades, xp, 0.05F);
	}


	private static void addClericTrades(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades) {
		TradeLevel.JOURNEYMAN.addItemListings(trades,
				buyFromPlayer(ModItems.VIAL.get(), 4, 1, 12, 20)
		);

		TradeLevel.EXPERT.addItemListings(trades,
				buyFromPlayer(ModItems.BILE.get(), 2, 12, 30)
		);

		TradeLevel.MASTER.addItemListings(trades,
				convertItem(ModItems.TOXIN_GLAND.get(), 4, ModItems.TOXIN_EXTRACT.get(), 4, 8, 15)
		);
	}

	private static void addButcherTrades(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades) {
		TradeLevel.NOVICE.addItemListings(trades,
				sellToPlayer(ModItems.MOB_FANG.get(), 12, 16, 2),
				sellToPlayer(ModItems.MOB_CLAW.get(), 10, 16, 2)
		);

		TradeLevel.APPRENTICE.addItemListings(trades,
				buyFromPlayer(ModItems.MOB_SINEW.get(), 4, 1, 8, 5),
				buyFromPlayer(ModItems.GENERIC_MOB_GLAND.get(), 2, 1, 8, 5)
		);

		TradeLevel.JOURNEYMAN.addItemListings(trades,
				buyFromPlayer(ModItems.MOB_MARROW.get(), 4, 1, 12, 20)
		);

		TradeLevel.EXPERT.addItemListings(trades,
				sellToPlayer(ModItems.FLESH_BITS.get(), 2, 12, 30),
				sellToPlayer(ModItems.BONE_FRAGMENTS.get(), 2, 12, 30)
		);

		TradeLevel.MASTER.addItemListings(trades,
				sellToPlayer(ModItems.WITHERED_MOB_MARROW.get(), 20, 8, 30),
				convertItem(ModItems.VOLATILE_GLAND.get(), 4, Items.GUNPOWDER, 4, 8, 15)
		);
	}

	enum TradeLevel {
		NOVICE, APPRENTICE, JOURNEYMAN, EXPERT, MASTER;

		List<VillagerTrades.ItemListing> getItemListings(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades) {
			return trades.get(ordinal() + 1);
		}

		void addItemListings(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, BasicItemListing... listings) {
			getItemListings(trades).addAll(Arrays.asList(listings));
		}

	}

}
