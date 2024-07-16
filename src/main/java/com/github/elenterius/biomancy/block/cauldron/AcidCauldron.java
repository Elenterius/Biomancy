package com.github.elenterius.biomancy.block.cauldron;

import com.github.elenterius.biomancy.fluid.AcidFluid;
import com.github.elenterius.biomancy.init.ModFluids;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class AcidCauldron extends AbstractCauldronBlock {
	public AcidCauldron() {
		super(Properties.copy(Blocks.CAULDRON),getInteractions());
	}

	private static Map<Item, CauldronInteraction> getInteractions() {
		Map<Item,CauldronInteraction> map = CauldronInteraction.newInteractionMap();
		map.put(Items.BUCKET, (pBlockState, pLevel, pPos, pPlayer, pHand, pEmptyStack) -> CauldronInteraction.fillBucket(pBlockState, pLevel, pPos, pPlayer, pHand, pEmptyStack, ModItems.ACID_BUCKET.get().getDefaultInstance(), (_stack)->true, Objects.requireNonNull(ModFluids.ACID_TYPE.get().getSound(SoundActions.BUCKET_FILL))));
		return map;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!(entity instanceof LivingEntity)) return;
		AcidFluid.onEntityInside((LivingEntity)entity);
	}

	@Override
	public boolean isFull(BlockState pState) {
		return true;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		//No idea why, but MC freaks out without this line. super.use() should cover it, but it doesn't so...
		if (getInteractions().containsKey(player.getItemInHand(hand).getItem())) return getInteractions().get(player.getItemInHand(hand).getItem()).interact(state,level,pos,player,hand,player.getItemInHand(hand));

		//An attempt to allow the player to do all the erosion interactions inside the cauldron.
		//It mostly works, copper being a weird exception.
		Block block = ForgeRegistries.BLOCKS.getValue(ForgeRegistries.ITEMS.getKey(player.getItemInHand(hand).getItem()));
		if (!Objects.isNull(block)) {
			BlockState eroded = AcidFluid.getEroded(block.defaultBlockState());
			if (!Objects.isNull(eroded)) {
				player.setItemInHand(hand,new ItemStack(eroded.getBlock().asItem(),player.getItemInHand(hand).getCount()));
				return InteractionResult.SUCCESS;
			}
		}

		return super.use(state, level, pos, player, hand, hit);
	}
}
