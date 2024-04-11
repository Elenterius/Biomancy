package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.client.render.item.BEWLItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BEWLBlockItem extends SimpleBlockItem {

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

}
