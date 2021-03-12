package com.github.elenterius.biomancy;

import com.github.elenterius.biomancy.init.*;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
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

		ModSoundEvents.SOUND_EVENTS.register(modEventBus);
	}

	public static ResourceLocation createRL(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static String getTranslationKey(String prefix, String suffix) {
		return prefix + "." + MOD_ID + "." + suffix;
	}

	public static TranslationTextComponent getTranslationText(String prefix, String suffix) {
		return new TranslationTextComponent(getTranslationKey(prefix, suffix));
	}

	public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(ModItems.OCULUS.get());
		}

		@Override
		public void fill(@Nonnull NonNullList<ItemStack> items) {
			super.fill(items);
			items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.CLIMBING.get(), 1)));
			items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.BULLET_JUMP.get(), 3)));
			items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.ATTUNED_BANE.get(), 5)));

			//add placeholder potions
			for (RegistryObject<Effect> effect : ModEffects.EFFECTS.getEntries()) {
				ItemStack potionStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
				CompoundNBT compoundnbt = potionStack.getOrCreateTag();
				ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", Constants.NBT.TAG_LIST);
				EffectInstance effectInstance = new EffectInstance(effect.get(), 20 * 30);
				listnbt.add(effectInstance.write(new CompoundNBT()));
				compoundnbt.put("CustomPotionEffects", listnbt);
				potionStack.setDisplayName(new StringTextComponent("[PH] Sliver of ").appendSibling(new TranslationTextComponent(effect.get().getName())));
				items.add(potionStack);
			}
		}
	};

}
