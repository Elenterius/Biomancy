package com.github.elenterius.blightlings.world.gen.feature;

import com.github.elenterius.blightlings.util.WorldUtil;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class MoonMonolithFeature extends Feature<NoFeatureConfig> {

    public MoonMonolithFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockPos.Mutable currPos = pos.toMutable();
        while (reader.isAirBlock(currPos) && currPos.getY() > 2) { // reach bottom block
            currPos.move(0, -1, 0); //down
        }

        // moon rises in the east and sets in the west (x-axis) so we have to place the blocks along the z-axis
        int maxHeight = 10 + rand.nextInt(7);
        int width = 7;
        currPos.move(0, maxHeight, 0); //move up to max height of monolith (the new "origin" pos)
        BlockPos origin = currPos.toImmutable();

        if (WorldUtil.isAir(reader, currPos) && WorldUtil.isAir(reader, currPos.move(0, 0, width - 1))) { //moved south by width
            BlockState state = Blocks.OBSIDIAN.getDefaultState();

            reader.setBlockState(currPos.setPos(origin.getX(), origin.getY(), origin.getZ()), state, Constants.BlockFlags.DEFAULT); //return to origin
            for (int z = 0; z < width - 1; z++) {
                reader.setBlockState(currPos.move(0, 0, 1), state, Constants.BlockFlags.DEFAULT); //south
            }

            currPos.move(0, -1, 0); //down
            reader.setBlockState(currPos.setPos(origin.getX(), currPos.getY(), origin.getZ()), state, Constants.BlockFlags.DEFAULT); //return to origin z & x pos
            reader.setBlockState(currPos.move(0, 0, 1), state, Constants.BlockFlags.DEFAULT); //south
            reader.setBlockState(currPos.move(0, 0, width - 3), state, Constants.BlockFlags.DEFAULT); //south
            reader.setBlockState(currPos.move(0, 0, 1), state, Constants.BlockFlags.DEFAULT); //south
            maxHeight--;

            for (int n = 0; n < 3; n++) {
                currPos.move(0, -1, 0); //down
                reader.setBlockState(currPos.setPos(origin.getX(), currPos.getY(), origin.getZ()), state, Constants.BlockFlags.DEFAULT); //return to origin z & x pos
                reader.setBlockState(currPos.move(0, 0, width - 1), state, Constants.BlockFlags.DEFAULT);
                maxHeight--;
            }

            currPos.move(0, -1, 0); //down
            reader.setBlockState(currPos.setPos(origin.getX(), currPos.getY(), origin.getZ()), state, Constants.BlockFlags.DEFAULT); //return to origin z & x pos
            reader.setBlockState(currPos.move(0, 0, 1), state, Constants.BlockFlags.DEFAULT); //south
            reader.setBlockState(currPos.move(0, 0, width - 3), state, Constants.BlockFlags.DEFAULT); //south
            reader.setBlockState(currPos.move(0, 0, 1), state, Constants.BlockFlags.DEFAULT); //south
            maxHeight--;

            for (; maxHeight >= 0; maxHeight--) {
                currPos.move(0, -1, 0); //down
                reader.setBlockState(currPos.setPos(origin.getX(), currPos.getY(), origin.getZ()), state, Constants.BlockFlags.DEFAULT); //return to origin z & x pos
                for (int z = 0; z < width - 1; z++) {
                    reader.setBlockState(currPos.move(0, 0, 1), state, Constants.BlockFlags.DEFAULT); //south
                }
            }

            return true;
        }

        return false;
    }
}
