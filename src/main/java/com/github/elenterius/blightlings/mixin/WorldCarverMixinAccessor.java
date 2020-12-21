package com.github.elenterius.blightlings.mixin;

import net.minecraft.block.Block;
import net.minecraft.world.gen.carver.WorldCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(WorldCarver.class)
public interface WorldCarverMixinAccessor {
	@Accessor
	Set<Block> getCarvableBlocks();

	@Accessor
	void setCarvableBlocks(Set<Block> carvableBlocks);
}
