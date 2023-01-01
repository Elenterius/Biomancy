package com.github.elenterius.biomancy.integration.jer;

import jeresources.api.IJERAPI;
import jeresources.api.JERPlugin;


public final class BiomancyJerPlugin {

	// Annotation disabled for now.
	//@JERPlugin
	public static IJERAPI jerApi;

	private BiomancyJerPlugin() {}

	public static void onClientPostSetup() {
		registerFleshBlobDrops();
	}

	private static void registerFleshBlobDrops() {
		//		Level level = jerApi.getLevel();
		//		for (int i = 2; i <= FleshBlob.MAX_SIZE ; i++) {
		//			FleshBlob fleshBlob = Objects.requireNonNull(ModEntityTypes.FLESH_BLOB.get().create(level));
		//			fleshBlob.setBlobSize((byte) i, false);
		//			jerApi.getMobRegistry().register(fleshBlob, fleshBlob.getLootTable());
		//		}
	}

	/**
	 * This won't work with JER, unless it's a loot table approach
	 */
	//	private static void registerSpecialMobLootDrops() {
	//		Level level = jerApi.getLevel();
	//
	//		final SpecialMobLootModifier lootModifier = new SpecialMobLootModifier();
	//		Conditional despoilConditional = new Conditional("jer.biomancy.requiresDespoilOrBoneCleaver", TextModifier.lightRed);
	//
	//		Conditional boneCleaverConditional = new Conditional("jer.biomancy.requiresBoneCleaver", TextModifier.lightRed);
	//		SimpleItem fleshBitsItem = ModItems.FLESH_BITS.get();
	//
	//		for (EntityType<?> entityType : ForgeRegistries.ENTITIES) {
	//			Entity entity = entityType.create(level);
	//			if (!(entity instanceof Mob mob)) continue;
	//
	//			DynamicLootTable lootTable = lootModifier.getLootTableForJER(mob);
	//			float totalWeight = lootTable.getTotalWeight();
	//
	//			List<LootDrop> lootDrops = new ArrayList<>();
	//			for (DynamicWeightedRandomList.IWeightedEntry<DynamicLootTable.ItemLoot> entry : lootTable.getEntries()) {
	//				DynamicLootTable.ItemLoot itemLoot = entry.data();
	//				Item item = itemLoot.itemSupplier().get();
	//				ItemStack stack = new ItemStack(item);
	//				if (stack.isEmpty()) continue;
	//
	//				int maxDrop = itemLoot.itemCountFunc() == DynamicLootTable.RANDOM_ITEM_AMOUNT_FUNC_2 ? 2 : 1;
	//				LootDrop lootDrop = new LootDrop(stack, 1, maxDrop, entry.weight() / totalWeight, 0);
	//				lootDrop.addLootConditions(lootModifier.getConditions());
	//				lootDrop.addConditional(item != fleshBitsItem ? despoilConditional : boneCleaverConditional);
	//
	//				lootDrops.add(lootDrop);
	//			}
	//			if (!lootDrops.isEmpty()) jerApi.getMobRegistry().register(mob, lootDrops.toArray(LootDrop[]::new));
	//		}
	//	}

}
