package com.github.elenterius.blightlings.mixin;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(NoiseChunkGenerator.class)
public abstract class NoiseChunkGeneratorMixin extends ChunkGenerator
{
    @Shadow
    @Final
    protected BlockState defaultFluid;

    @Shadow
    @Final
    private static BlockState AIR;

    public NoiseChunkGeneratorMixin(BiomeProvider biomeProvider, DimensionStructuresSettings structuresSettings) {
        super(biomeProvider, structuresSettings);
    }

    public NoiseChunkGeneratorMixin(BiomeProvider biomeProvider, BiomeProvider biomeProvider1, DimensionStructuresSettings structuresSettings, long seed) {
        super(biomeProvider, biomeProvider1, structuresSettings, seed);
    }

    boolean chunkHasBlightBiome = false;

    @Inject(method = "func_230352_b_", at = @At("HEAD"))
    protected void injectFunc_230352_b_(IWorld world, StructureManager manager, IChunk chunk, CallbackInfo ci) {
        chunkHasBlightBiome = false;
        if (chunk.getBiomes() != null) {
            int[] ids = Arrays.stream(chunk.getBiomes().getBiomeIds()).distinct().toArray();
            for (int id : ids) {
                if (BiomeRegistry.getKeyFromID(id).getLocation().getNamespace().equals(BlightlingsMod.MOD_ID)) {
                    chunkHasBlightBiome = true;
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
        if (chunkHasBlightBiome && state == defaultFluid) return AIR;
        return state;
    }

}
