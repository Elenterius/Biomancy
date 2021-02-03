package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.init.ModBlocks;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class FleshBlock extends Block {

	public FleshBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
		PlantType type = plantable.getPlantType(world, pos.offset(facing));
		return type == ModBlocks.FLESH_PLANT_TYPE;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (this == ModBlocks.NECROTIC_FLESH_BLOCK.get() && state.isIn(ModBlocks.NECROTIC_FLESH_BLOCK.get())) {
			ItemStack stack = player.getHeldItem(handIn);
			if (stack.getItem() == ModItems.REJUVENATING_MUCUS.get()) {
				if (!worldIn.isRemote) {
					if (!player.abilities.isCreativeMode) {
						stack.shrink(1);
					}
					if (worldIn.rand.nextFloat() < 0.45f) {
						worldIn.setBlockState(pos, ModBlocks.FLESH_BLOCK.get().getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
					}
				}
				else {
					spawnHealingParticles(worldIn, pos, hit.getFace(), worldIn.rand);
				}

				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
		}

		return ActionResultType.PASS;
	}

	@OnlyIn(Dist.CLIENT)
	public static void spawnHealingParticles(World worldIn, BlockPos pos, Direction face, Random random) {
		for (int i = 0; i < 15; i++) {
			double xSpeed = random.nextGaussian() * 0.02D;
			double ySpeed = random.nextGaussian() * 0.02D;
			double zSpeed = random.nextGaussian() * 0.02D;
			double x = pos.getX() + 0.5d + face.getXOffset() * 0.55d + face.getZOffset() * random.nextGaussian() * 0.3d + face.getYOffset() * random.nextGaussian() * 0.3d;
			double y = pos.getY() + 0.5d + face.getYOffset() * 0.55d + face.getZOffset() * random.nextGaussian() * 0.3d + face.getXOffset() * random.nextGaussian() * 0.3d;
			double z = pos.getZ() + 0.5d + face.getZOffset() * 0.55d + face.getXOffset() * random.nextGaussian() * 0.3d + face.getYOffset() * random.nextGaussian() * 0.3d;
			worldIn.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}
}
