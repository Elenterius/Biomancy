package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.HrTooltipComponent;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class SerumItem extends Item implements ISerumProvider, IBiomancyItem {

	private static final Map<Serum, SerumItem> SERUM_MAP = new IdentityHashMap<>();
	private static final List<SerumItem> ITEMS = new ArrayList<>();

	@Nullable
	private final Supplier<? extends Serum> serumSupplier;

	public SerumItem(Properties properties, @Nullable Supplier<? extends Serum> serumSupplier) {
		super(properties);
		this.serumSupplier = serumSupplier;
		if (serumSupplier != null) ITEMS.add(this);
	}

	@Nullable
	public static SerumItem getItemForSerum(Serum serum) {
		return SERUM_MAP.get(serum);
	}

	@Nullable
	public Serum getSerum(ItemStack stack) {
		return serumSupplier != null ? serumSupplier.get() : null;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
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
				if (item.serumSupplier != null) {
					SERUM_MAP.put(item.serumSupplier.get(), item);
				}
			}
		}
	}

}
