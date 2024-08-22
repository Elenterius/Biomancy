package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.block.membrane.Membrane;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {FlyNodeEvaluator.class, SwimNodeEvaluator.class, WalkNodeEvaluator.class})
public abstract class NodeEvaluatorMixin {

	@Inject(method = "getBlockPathType(Lnet/minecraft/world/level/BlockGetter;IIILnet/minecraft/world/entity/Mob;)Lnet/minecraft/world/level/pathfinder/BlockPathTypes;", at = @At(value = "HEAD"), cancellable = true)
	private void onGetBlockPathType(BlockGetter level, int x, int y, int z, Mob mob, @NotNull CallbackInfoReturnable<BlockPathTypes> cir) {
		BlockPos pos = new BlockPos(x, y, z);
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof Membrane membrane) {
			cir.setReturnValue(membrane.shouldIgnoreEntityCollisionAt(state, level, pos, mob) ? BlockPathTypes.DOOR_OPEN : BlockPathTypes.BLOCKED);
		}
	}

}
