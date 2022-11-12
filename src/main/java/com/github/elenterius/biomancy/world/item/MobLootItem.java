package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.tooltip.PlaceholderComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MobLootItem extends Item implements IBiomancyItem {

	private final ITag<EntityType<?>> taggedEntities;

	public MobLootItem(TagKey<EntityType<?>> lootSource, Properties properties) {
		super(properties);

		taggedEntities = Objects.requireNonNull(ForgeRegistries.ENTITIES.tags()).getTag(lootSource);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		PlaceholderComponent hrElement = new PlaceholderComponent(new HrTooltipComponent());

		tooltip.add(hrElement);
		ClientTextUtil.appendItemInfoTooltip(stack.getItem(), tooltip);

		if (Screen.hasControlDown()) {
			tooltip.add(hrElement);
			tooltip.add(new TextComponent("Drops from").withStyle(TextStyles.LORE));

			List<EntityType<?>> mobs = taggedEntities.stream().limit(12).toList();
			int mobCount = mobs.size();
			if (mobCount > 0) {
				MutableComponent component = new TextComponent("").withStyle(TextStyles.ItalicGray);
				tooltip.add(component);

				for (int i = 0; i < mobCount; i++) {
					component.append(mobs.get(i).getDescription());
					if (mobCount > 1 && i < mobCount - 1) component.append(", ");
				}
				if (mobCount < taggedEntities.size()) component.append(" and more...");
			}
		}
	}

}
