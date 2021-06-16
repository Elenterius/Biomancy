package com.github.elenterius.biomancy.datagen;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.util.Comparator;

import static net.minecraft.item.Items.*;

public class ModItemTagsProvider extends ItemTagsProvider {

	public final Logger LOGGER = BiomancyMod.LOGGER;
	public final Marker logMarker = MarkerManager.getMarker("ModItemTagsProvider");

	public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, BiomancyMod.MOD_ID, existingFileHelper);
	}

	protected final ItemComparator ITEM_ID_COMPARATOR = new ItemComparator();

	static class ItemComparator implements Comparator<IItemProvider> {
		@Override
		public int compare(IItemProvider a, IItemProvider b) {
			ResourceLocation idA = Registry.ITEM.getKey(a.asItem());
			ResourceLocation idB = Registry.ITEM.getKey(b.asItem());
			return idA.compareTo(idB);
		}
	}

	@Override
	protected void registerTags() {
		LOGGER.info(logMarker, "registering item tags...");

		getOrCreateBuilder(ModTags.Items.COOKED_MEATS)
				.add(COOKED_BEEF, COOKED_PORKCHOP, COOKED_CHICKEN, COOKED_SALMON, COOKED_MUTTON, COOKED_COD, COOKED_RABBIT);

		getOrCreateBuilder(ModTags.Items.RAW_MEATS)
				.add(BEEF, PORKCHOP, CHICKEN, COD, SALMON, RABBIT, MUTTON, TROPICAL_FISH, PUFFERFISH);

		getOrCreateBuilder(ModTags.Items.SUGARS)
				.add(SUGAR, COOKIE, CAKE, HONEYCOMB, HONEY_BLOCK, HONEYCOMB_BLOCK, HONEY_BOTTLE, SWEET_BERRIES, COCOA_BEANS);

		getOrCreateBuilder(ModTags.Items.POOR_BIOMASS)
				.addTag(ItemTags.FLOWERS).addTag(Tags.Items.SEEDS).addTag(ItemTags.LEAVES)
				.add(SWEET_BERRIES, SUGAR_CANE, KELP, DRIED_KELP, GRASS, SEAGRASS, VINE, FERN, BAMBOO);

		getOrCreateBuilder(ModTags.Items.AVERAGE_BIOMASS)
				.addTag(ItemTags.SAPLINGS).addTag(ModTags.Items.RAW_MEATS)
				.add(WHEAT, BEETROOT, POTATO, CARROT, COOKIE, CACTUS, APPLE, CHORUS_FRUIT, MELON_SLICE, SPIDER_EYE, WARPED_FUNGUS,
						NETHER_SPROUTS, WEEPING_VINES, TWISTING_VINES, LARGE_FERN, TALL_GRASS, WARPED_ROOTS, CRIMSON_ROOTS, NETHER_WART,
						CRIMSON_FUNGUS, RED_MUSHROOM, BROWN_MUSHROOM);

		getOrCreateBuilder(ModTags.Items.GOOD_BIOMASS)
				.addTag(ModTags.Items.COOKED_MEATS)
				.add(BREAD, MUSHROOM_STEM, SUSPICIOUS_STEW, COCOA_BEANS, BAKED_POTATO, HONEY_BOTTLE, MELON, PUMPKIN, DRIED_KELP_BLOCK,
						SEA_PICKLE, LILY_PAD, CARVED_PUMPKIN, WARPED_WART_BLOCK, NETHER_WART_BLOCK, RED_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK,
						SHROOMLIGHT, MUSHROOM_STEM);

		getOrCreateBuilder(ModTags.Items.SUPERB_BIOMASS)
				.add(CAKE, PUMPKIN_PIE, RABBIT_STEW, BEETROOT_SOUP, POISONOUS_POTATO, HAY_BLOCK);

		Builder<Item> builder = getOrCreateBuilder(ModTags.Items.BIOMASS);
//		ComposterBlock.CHANCES.keySet().stream().sorted(ITEM_ID_COMPARATOR).map(IItemProvider::asItem).forEach(builder::addItemEntry);
		builder.addOptionalTag(ModTags.Items.POOR_BIOMASS.getName());
		builder.addOptionalTag(ModTags.Items.AVERAGE_BIOMASS.getName());
		builder.addOptionalTag(ModTags.Items.GOOD_BIOMASS.getName());
		builder.addOptionalTag(ModTags.Items.SUPERB_BIOMASS.getName());

		getOrCreateBuilder(ModTags.Items.STOMACHS)
				.add(ModItems.ARTIFICIAL_STOMACH.get(), ModItems.STOMACH.get());

		LOGGER.info(logMarker, "registering secretion item tags...");
		getOrCreateBuilder(ModTags.Items.OXIDES)
				.add(NETHERITE_SCRAP, TURTLE_EGG, BONE_MEAL)
				.addTag(Tags.Items.INGOTS_IRON).addTag(Tags.Items.NUGGETS_IRON)
				.addTag(Tags.Items.INGOTS_GOLD).addTag(Tags.Items.NUGGETS_GOLD)
				.addTag(Tags.Items.INGOTS_NETHERITE)
				.addTag(Tags.Items.EGGS).addTag(Tags.Items.BONES);

		getOrCreateBuilder(ModTags.Items.SILICATES)
				.add(ANDESITE, DIORITE, GRANITE, REDSTONE, KELP, DRIED_KELP, NAUTILUS_SHELL, SHULKER_SHELL, GLOWSTONE_DUST)
				.addTag(Tags.Items.GEMS_EMERALD).addTag(Tags.Items.GEMS_LAPIS).addTag(Tags.Items.GEMS_QUARTZ).addTag(Tags.Items.GEMS_PRISMARINE)
				.addOptional(new ResourceLocation("minecraft", "amethyst_shard"));

		getOrCreateBuilder(ModTags.Items.KERATINS)
				.add(SCUTE, LEAD, PHANTOM_MEMBRANE, RABBIT_HIDE, PRISMARINE_SHARD)
				.addTag(Tags.Items.STRING).addTag(ItemTags.WOOL).addTag(ItemTags.CARPETS)
				.addTag(Tags.Items.FEATHERS).addTag(Tags.Items.LEATHER)
				.addOptional(new ResourceLocation("minecraft", "goat_horn"));

		getOrCreateBuilder(ModTags.Items.HORMONES)
				.add(INK_SAC, RABBIT_FOOT, SPIDER_EYE, GHAST_TEAR, FERMENTED_SPIDER_EYE, HONEYCOMB, SLIME_BALL)
				.addOptional(new ResourceLocation("minecraft", "glow_ink_sac"));
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " " + super.getName();
	}
}
