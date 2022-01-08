package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.client.renderer.item.BEWLRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

//implements IAnimatable
public class CreatorBlockItem extends BlockItem {

	public CreatorBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
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

}
