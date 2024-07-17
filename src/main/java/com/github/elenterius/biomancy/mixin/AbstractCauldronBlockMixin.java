package com.github.elenterius.biomancy.mixin;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.SoundActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value= AbstractCauldronBlock.class)
public abstract class AbstractCauldronBlockMixin {
	@Inject(at=@At("TAIL"),method="Lnet/minecraft/world/level/block/AbstractCauldronBlock;use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
	public void biomancy$use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		//Allow the Acid bucket to fill Cauldrons. I have no idea why this should require a mixin, beyond "Just Vanilla Things".
		if (player.getItemInHand(hand).getItem().equals(ModItems.ACID_BUCKET.get())) {
			cir.setReturnValue(CauldronInteraction.emptyBucket(level,pos,player,hand,player.getItemInHand(hand), ModBlocks.ACID_CAULDRON.get().defaultBlockState(), Objects.requireNonNull(ModFluids.ACID_TYPE.get().getSound(SoundActions.BUCKET_FILL))));
		}
	}
}
