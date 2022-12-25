package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.styles.TooltipHacks;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.network.chat.Component;
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

public class SerumItem extends Item implements ISerumProvider, IBiomancyItem {

	private static final Map<Serum, SerumItem> SERUM_MAP = new IdentityHashMap<>();
	private static final List<SerumItem> ITEMS = new ArrayList<>();

	private final Supplier<? extends Serum> serumSupplier;

	public SerumItem(Properties properties, Supplier<? extends Serum> serumSupplier) {
		super(properties);
		this.serumSupplier = serumSupplier;
		ITEMS.add(this);
	}

	@Nullable
	public static SerumItem getItemForSerum(Serum serum) {
		return SERUM_MAP.get(serum);
	}

	public Serum getSerum(ItemStack stack) {
		return serumSupplier.get();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(TooltipHacks.HR_COMPONENT);
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));
	}

	@Override
	public String getTooltipKey(ItemStack stack) {
		return getSerum(stack).getTooltipKey();
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
			for (SerumItem item : ITEMS) {
				SERUM_MAP.put(item.serumSupplier.get(), item);
			}
		}
	}

}
