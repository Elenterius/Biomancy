package com.github.elenterius.biomancy.block.cauldron;

import com.github.elenterius.biomancy.init.AcidInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;

public class AcidCauldron extends LayeredCauldronBlock {

	public AcidCauldron(Properties properties) {
		super(properties, precipitation -> false, AcidInteractions.ACID_CAULDRON);
	}

	@Override
	protected boolean canReceiveStalactiteDrip(Fluid fluid) {
		return false;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity livingEntity && isEntityInsideContent(state, pos, entity)) {
			if (livingEntity.tickCount % 5 != 0) return;
			AcidInteractions.handleEntityInsideAcid(livingEntity);
		}
	}

	@Override
	protected void handleEntityOnFireInside(BlockState state, Level level, BlockPos pos) {
		//do nothing
	}

	@Override
	public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
		//do nothing
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			Block convertedBlock = AcidInteractions.convertBlock(block);
			if (convertedBlock != null) {
				if (!level.isClientSide()) {
					player.setItemInHand(hand, new ItemStack(convertedBlock.asItem(), stack.getCount()));
					player.awardStat(Stats.USE_CAULDRON);

					SoundType soundType = block.getSoundType(block.defaultBlockState(), level, pos, null);
					level.playSound(null, pos, soundType.getHitSound(), SoundSource.BLOCKS, soundType.volume, soundType.pitch);
					level.levelEvent(LevelEvent.LAVA_FIZZ, pos, 0);
				}

				return InteractionResult.sidedSuccess(level.isClientSide());
			}
		}

		return super.use(state, level, pos, player, hand, hit);
	}
}
