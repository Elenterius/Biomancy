package com.github.elenterius.biomancy.entity.mutation;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SilkyWoolSheepEntity extends SheepEntity {

	public static final ResourceLocation SILKY_WOOL_LOOT_TABLE = BiomancyMod.createRL("entities/sheep/silky_wool");

	public SilkyWoolSheepEntity(EntityType<? extends SheepEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 8d).add(Attributes.MOVEMENT_SPEED, 0.23d);
	}

//	@Override
//	public DyeColor getFleeceColor() {
//		return DyeColor.WHITE;
//	}
//
//	@Override
//	public void setFleeceColor(DyeColor color) {
//		super.setFleeceColor(DyeColor.WHITE);
//	}

	@Override
	public ResourceLocation getDefaultLootTable() {
		if (isSheared()) {
			return getType().getDefaultLootTable();
		}
		else {
			return SILKY_WOOL_LOOT_TABLE;
		}
	}

	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
		world.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1f, 1f);
		if (!world.isClientSide) {
			setSheared(true);
			int n = 1 + random.nextInt(3);
			List<ItemStack> items = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				items.add(new ItemStack(Items.STRING));
			}
			return items;
		}
		return Collections.emptyList();
	}

	@Override
	public void shear(SoundCategory category) {
		level.playSound(null, this, SoundEvents.SHEEP_SHEAR, category, 1f, 1f);
		setSheared(true);
		int n = 1 + random.nextInt(3);
		for (int i = 0; i < n; i++) {
			ItemEntity itemEntity = spawnAtLocation(Items.STRING, 1);
			if (itemEntity != null) {
				itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add((random.nextFloat() - random.nextFloat()) * 0.1f, random.nextFloat() * 0.05f, (random.nextFloat() - random.nextFloat()) * 0.1f));
			}
		}
	}

	@Override
	public SilkyWoolSheepEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
		return ModEntityTypes.SILKY_WOOL_SHEEP.get().create(world);
	}

}
