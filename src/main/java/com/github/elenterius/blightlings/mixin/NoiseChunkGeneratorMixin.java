package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.init.ModBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(NoiseChunkGenerator.class)
public abstract class NoiseChunkGeneratorMixin
{
    @Shadow
    @Final
    protected BlockState defaultFluid;

    @Shadow
    @Final
    private static BlockState AIR;

    private boolean hasBlightBiome = false;

    @Inject(method = "func_230352_b_", at = @At("HEAD"))
    protected void onFunc_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk chunk, CallbackInfo ci) {
        hasBlightBiome = false;
        if (chunk.getBiomes() != null) {
            int[] ids = Arrays.stream(chunk.getBiomes().getBiomeIds()).distinct().toArray();
            for (int id : ids) {
                if (id == ModBiomes.BLIGHT_BIOME_ID || id == ModBiomes.BLIGHT_BIOME_INNER_EDGE_ID) {
                    hasBlightBiome = true;
                    break;
                }
            }
        }
    }

    @ModifyVariable(
            method = "func_230352_b_",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/gen/NoiseChunkGenerator;func_236086_a_(DI)Lnet/minecraft/block/BlockState;")
    )
    protected BlockState modifyBlockStateVariable(BlockState state, IWorld world, StructureManager manager, IChunk chunk) {
        if (hasBlightBiome && state == defaultFluid) {
            return AIR; //remove "ocean", this is important because the blight biome has a negative depth and would normally generate with water below the sea level
        }
        return state;
    }
}
