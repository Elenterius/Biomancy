package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.render.item.BEWLItemRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BEWLBlockItem extends BlockItem implements ICustomTooltip {

	private final Lazy<BlockEntity> cachedBlockEntityWithoutLevel;

	public <T extends Block & EntityBlock> BEWLBlockItem(T block, Properties properties) {
		super(block, properties);
		cachedBlockEntityWithoutLevel = Lazy.of(() -> block.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()));
	}

	@Nullable
	public BlockEntity getCachedBEWL() {
		return cachedBlockEntityWithoutLevel.get();
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return BEWLItemRenderer.INSTANCE;
			}
		});
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ComponentUtil.horizontalLine());
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));
		super.appendHoverText(stack, level, tooltip, isAdvanced);
	}

}
