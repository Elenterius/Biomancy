package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.client.renderer.item.BEWLRenderer;
import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.HrTooltipComponent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

//implements IAnimatable
public class BEWLBlockItem extends BlockItem implements IBiomancyItem {

	public BEWLBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return BEWLRenderer.INSTANCE;
			}
		});
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		ClientTextUtil.appendItemInfoTooltip(stack.getItem(), tooltip);
		super.appendHoverText(stack, level, tooltip, isAdvanced);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

}
