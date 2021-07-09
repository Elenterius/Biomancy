package com.github.elenterius.biomancy;

import com.github.elenterius.biomancy.init.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(BiomancyMod.MOD_ID)
public final class BiomancyMod {
	public static final String MOD_ID = "biomancy";
	public static final Logger LOGGER = LogManager.getLogger();

	public BiomancyMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


		ModItems.ITEMS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModEnchantments.ENCHANTMENTS.register(modEventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITIES.register(modEventBus);
		ModContainerTypes.CONTAINERS.register(modEventBus);

		ModAttributes.ATTRIBUTES.register(modEventBus);
		ModEffects.EFFECTS.register(modEventBus);
		ModEntityTypes.ENTITIES.register(modEventBus);
		ModReagents.REAGENTS.register(modEventBus);

		ModSoundEvents.SOUND_EVENTS.register(modEventBus);
	}

	public static ResourceLocation createRL(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {

		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(ModItems.OCULUS.get());
		}

		@Override
		public void fill(@Nonnull NonNullList<ItemStack> items) {
			super.fill(items);
			for (RegistryObject<Enchantment> entry : ModEnchantments.ENCHANTMENTS.getEntries()) {
				Enchantment enchantment = entry.get();
				items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
			}

			//add placeholder potions
//			for (RegistryObject<Effect> effect : ModEffects.EFFECTS.getEntries()) {
//				ItemStack potionStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
//				CompoundNBT compoundnbt = potionStack.getOrCreateTag();
//				ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", Constants.NBT.TAG_LIST);
//				EffectInstance effectInstance = new EffectInstance(effect.get(), 20 * 30);
//				listnbt.add(effectInstance.write(new CompoundNBT()));
//				compoundnbt.put("CustomPotionEffects", listnbt);
//				potionStack.setDisplayName(new StringTextComponent("[PH] Sliver of ").appendSibling(new TranslationTextComponent(effect.get().getName())));
//				items.add(potionStack);
//			}
		}
	};

}
