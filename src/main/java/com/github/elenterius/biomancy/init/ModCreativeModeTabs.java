package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.livingtool.LivingTool;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.crafting.recipe.PotionSerumRecipes;
import com.github.elenterius.biomancy.item.SerumItem;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public final class ModCreativeModeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BiomancyMod.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_DEV_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BiomancyMod.MOD_ID);

	public static final RegistryObject<CreativeModeTab> MAIN = register("main", () -> ModItems.TAB_ICON.get().getDefaultInstance(), ModCreativeModeTabs::mainTab);
	public static final RegistryObject<CreativeModeTab> BIO_ALCHEMY = register("bio_alchemy", () -> ModItems.INJECTOR.get().getDefaultInstance(), ModCreativeModeTabs::alchemyTab);

	public static final RegistryObject<CreativeModeTab> DEV = registerDev("dev", () -> ModItems.DEV_GUIDE_BOOK.get().getDefaultInstance(), ModCreativeModeTabs::devTab);
	public static final RegistryObject<CreativeModeTab> DEV_STRUCTURES = registerDev("dev_structures", Items.STRUCTURE_BLOCK::getDefaultInstance, ModCreativeModeTabs::devStructuresTab);

	private ModCreativeModeTabs() {}

	private static CreativeModeTab.Builder mainTab(CreativeModeTab.Builder builder) {
		return builder
				.displayItems((params, output) -> {
					Set<RegistryObject<? extends Item>> hiddenItems = Set.of(
							ModItems.TAB_ICON,
							ModItems.ESSENCE,
							ModItems.DEV_GUIDE_BOOK,

							ModItems.BIO_LAB,
							ModItems.INJECTOR,
							ModItems.VIAL,
							ModItems.ORGANIC_COMPOUND,
							ModItems.UNSTABLE_COMPOUND,
							ModItems.GENETIC_COMPOUND,
							ModItems.EXOTIC_COMPOUND,
							ModItems.HEALING_ADDITIVE,
							ModItems.DECAYING_ADDITIVE
					);

					Predicate<RegistryObject<Item>> isNotHidden = entry -> !hiddenItems.contains(entry);
					Predicate<RegistryObject<Item>> isNotSerum = entry -> !(entry.get() instanceof SerumContainer);

					ModItems.ITEMS.getEntries().stream()
							.filter(isNotHidden)
							.filter(isNotSerum)
							.forEach(entry -> {
								Item item = entry.get();

								if (item instanceof LivingTool livingTool) {
									ItemStack itemStack = item.getDefaultInstance();
									livingTool.setNutrients(itemStack, Integer.MAX_VALUE);
									output.accept(itemStack);
								}
								else {
									output.accept(item);
								}
							});

					for (RegistryObject<Enchantment> entry : ModEnchantments.ENCHANTMENTS.getEntries()) {
						Enchantment enchantment = entry.get();
						output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())));
					}
				});
	}

	private static CreativeModeTab.Builder alchemyTab(CreativeModeTab.Builder builder) {
		return builder
				.withTabsBefore(MAIN.getId())
				.displayItems((params, output) -> {
					List<RegistryObject<? extends Item>> includeItems = List.of(
							ModItems.BIO_LAB,
							ModItems.INJECTOR,
							ModItems.VIAL,
							ModItems.ORGANIC_COMPOUND,
							ModItems.UNSTABLE_COMPOUND,
							ModItems.GENETIC_COMPOUND,
							ModItems.EXOTIC_COMPOUND,
							ModItems.HEALING_ADDITIVE,
							ModItems.DECAYING_ADDITIVE
					);
					includeItems.stream().map(RegistryObject::get).forEachOrdered(output::accept);

					ModItems.findItems(SerumItem.class).forEach(output::accept);

					output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.PRIMORDIAL_INFESTATION.get()));
					output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.PRIMORDIAL_INFESTATION.get()));
					output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.PRIMORDIAL_INFESTATION.get()));

					for (Potion potion : PotionSerumRecipes.POTIONS) {
						output.accept(ModItems.POTION_SERUM.get().getInstanceFrom(potion));
					}

				});
	}

	private static CreativeModeTab.Builder devTab(CreativeModeTab.Builder builder) {
		return builder
				.withTabsBefore(BIO_ALCHEMY.getId())
				.displayItems((params, output) -> {
					output.accept(ModItems.DEV_GUIDE_BOOK.get());
					output.accept(ModBlocks.FLESHKIN_CHEST.get().createItemStackForCreativeTab());
					output.accept(Items.DEBUG_STICK);
					output.accept(Items.STRUCTURE_BLOCK);
					output.accept(Items.JIGSAW);
					output.accept(Items.STRUCTURE_VOID);
					output.accept(Items.BARRIER);

					ModItems.stream().filter(item -> item instanceof LivingTool).forEach(output::accept);
				});
	}

	private static CreativeModeTab.Builder devStructuresTab(CreativeModeTab.Builder builder) {
		return builder
				.withTabsBefore(DEV.getId())
				.displayItems((params, output) -> {
					MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
					if (server == null) return;

					//					Map<ResourceLocation, Resource> structures = server.getResourceManager().listResources("structures", id -> id.getNamespace().equals(BiomancyMod.MOD_ID));

					ServerLevel level = server.getLevel(Level.OVERWORLD);
					if (level != null) {
						Stream<ResourceLocation> templates = level.getStructureManager().listTemplates().filter(id -> id.getNamespace().equals(BiomancyMod.MOD_ID));
						templates.forEach(id -> {
							ItemStack stack = new ItemStack(Blocks.STRUCTURE_BLOCK);
							stack.setHoverName(ComponentUtil.literal(id.toString()));

							CompoundTag tag = new CompoundTag();
							tag.putString("name", id.toString());
							tag.putInt("posX", 1);
							tag.putInt("posY", 0);
							tag.putInt("posZ", 1);
							tag.putString("mode", StructureMode.LOAD.toString());
							tag.putBoolean("showair", true);

							stack.addTagElement("BlockEntityTag", tag);

							output.accept(stack);
						});
					}

				});
	}

	private static RegistryObject<CreativeModeTab> register(String name, Supplier<ItemStack> icon, UnaryOperator<CreativeModeTab.Builder> factory) {
		return CREATIVE_TABS.register(name, () -> factory.apply(CreativeModeTab.builder().icon(icon).title(ComponentUtil.translatable(translationKey(name)))).build());
	}

	private static RegistryObject<CreativeModeTab> registerDev(String name, Supplier<ItemStack> icon, UnaryOperator<CreativeModeTab.Builder> factory) {
		return CREATIVE_DEV_TABS.register(name, () -> factory.apply(CreativeModeTab.builder().icon(icon).title(ComponentUtil.translatable(translationKey(name)))).build());
	}

	private static String translationKey(String name) {
		return "creative_tab." + BiomancyMod.MOD_ID + "." + name;
	}

}
