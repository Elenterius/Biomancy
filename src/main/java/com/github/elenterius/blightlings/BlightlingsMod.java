package com.github.elenterius.blightlings;

import com.github.elenterius.blightlings.init.*;
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

@Mod(BlightlingsMod.MOD_ID)
public class BlightlingsMod {
	public static final String MOD_ID = "blightlings";
	public static final Logger LOGGER = LogManager.getLogger();

	public BlightlingsMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModAttributes.ATTRIBUTES.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
		ModEntityTypes.ENTITY_TYPE_REGISTRY.register(modEventBus);
		ModEffects.EFFECTS.register(modEventBus);
		ModEnchantments.ENCHANTMENTS.register(modEventBus);

		ModSoundEvents.SOUND_EVENTS.register(modEventBus);

//        ModFeatures.FEATURE_REGISTRY.register(modEventBus);
		ModFeatures.SURFACE_BUILDER_REGISTRY.register(modEventBus);
//        ModBiomes.BIOME_REGISTRY.register(modEventBus);
	}

	public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, MOD_ID) {
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(ModItems.TRUE_SIGHT_GOGGLES.get());
		}

		@Override
		public void fill(@Nonnull NonNullList<ItemStack> items) {
			super.fill(items);
			items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.CLIMBING.get(), 1)));
			items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.BULLET_JUMP.get(), 1)));
			items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.BULLET_JUMP.get(), 2)));
			items.add(EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(ModEnchantments.BULLET_JUMP.get(), 3)));

			for (RegistryObject<Effect> effect : ModEffects.EFFECTS.getEntries()) {
				ItemStack potionStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
				CompoundNBT compoundnbt = potionStack.getOrCreateTag();
				ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", Constants.NBT.TAG_LIST);
				EffectInstance effectInstance = new EffectInstance(effect.get(), 20 * 30);
				listnbt.add(effectInstance.write(new CompoundNBT()));
				compoundnbt.put("CustomPotionEffects", listnbt);
				potionStack.setDisplayName(new StringTextComponent("[PH] Sliver of ").append(new TranslationTextComponent(effect.get().getName())));
				items.add(potionStack);
			}
		}
	};

}
