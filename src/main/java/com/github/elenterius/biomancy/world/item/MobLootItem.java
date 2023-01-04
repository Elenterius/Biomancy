package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

public class MobLootItem extends Item implements ICustomTooltip {

	private final ITag<EntityType<?>> taggedEntities;

	public MobLootItem(TagKey<EntityType<?>> lootSource, Properties properties) {
		super(properties);

		taggedEntities = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags()).getTag(lootSource);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ComponentUtil.horizontalLine());
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));

		if (Screen.hasControlDown()) {
			tooltip.add(ComponentUtil.emptyLine());
			tooltip.add(TextComponentUtil.getTooltipText("drops_from").withStyle(TextStyles.LORE));

			List<EntityType<?>> mobs = taggedEntities.stream().limit(12).toList();
			int mobCount = mobs.size();
			if (mobCount > 0) {
				MutableComponent component = ComponentUtil.mutable().withStyle(TextStyles.ITALIC_GRAY);
				tooltip.add(component);

				for (int i = 0; i < mobCount; i++) {
					component.append(mobs.get(i).getDescription());
					if (mobCount > 1 && i < mobCount - 1) component.append(", ");
				}
				if (mobCount < taggedEntities.size()) component.append(" ").append(TextComponentUtil.getTooltipText("and_more"));
			}
		}
	}

}
