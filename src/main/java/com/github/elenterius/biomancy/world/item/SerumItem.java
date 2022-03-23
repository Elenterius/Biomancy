package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SerumItem extends Item {

	private static final Map<Serum, SerumItem> SERUM_MAP = new IdentityHashMap<>();
	private static final List<SerumItem> ITEMS = new ArrayList<>();

	private final Supplier<? extends Serum> serumSupplier;

	public SerumItem(Properties properties, @Nullable Supplier<? extends Serum> serumSupplier) {
		super(properties);
		this.serumSupplier = serumSupplier;
		if (serumSupplier != null) ITEMS.add(this);
	}

	public static ItemStack getSerumItemStack(Serum serum) {
		SerumItem item = SERUM_MAP.get(serum);
		if (item == null) item = ModItems.GENERIC_SERUM.get();

		ItemStack stack = new ItemStack(item);
		Serum.serialize(serum, stack.getOrCreateTag());
		return stack;
	}

	public static int getSerumColor(ItemStack stack) {
		return Serum.getColor(stack.getOrCreateTag());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stackIn) {
		return new ItemStack(ModItems.GLASS_VIAL.get());
	}

	@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class CommonHandler {
		@SubscribeEvent
		public static void onCommonSetup(FMLCommonSetupEvent event) {
			ITEMS.forEach(item -> SERUM_MAP.put(item.serumSupplier.get(), item));
		}
	}

	public static final class Generic extends SerumItem {

		public Generic(Properties properties) {
			super(properties, null);
		}

		@Override
		public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
			Serum serum = Serum.deserialize(stack.getOrCreateTag());
			if (serum != null) {
				serum.addInfoToTooltip(stack, level, tooltip, isAdvanced);
			}
			else tooltip.add(TextComponentUtil.getTooltipText("contains_nothing"));
		}

		@Override
		public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
			if (allowdedIn(category)) {
				for (Serum serum : ModSerums.REGISTRY.get()) {
					if (!SERUM_MAP.containsKey(serum)) {
						ItemStack stack = SerumItem.getSerumItemStack(serum);
						if (!stack.isEmpty()) items.add(stack);
					}
				}
			}
		}

		@Override
		public String getDescriptionId(ItemStack stack) {
			Serum serum = Serum.deserialize(stack.getOrCreateTag());
			if (serum != null) {
				return serum.getTranslationKey();
			}
			return super.getDescriptionId(stack);
		}

	}

}
