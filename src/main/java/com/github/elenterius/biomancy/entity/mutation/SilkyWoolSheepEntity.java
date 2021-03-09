package com.github.elenterius.biomancy.entity.mutation;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 8d).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23d);
	}

	@Override
	public DyeColor getFleeceColor() {
		return DyeColor.WHITE;
	}

	@Override
	public void setFleeceColor(DyeColor color) {
		super.setFleeceColor(DyeColor.WHITE);
	}

	@Override
	public ResourceLocation getLootTable() {
		if (getSheared()) {
			return getType().getLootTable();
		}
		else {
			return SILKY_WOOL_LOOT_TABLE;
		}
	}

	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
		world.playMovingSound(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1f, 1f);
		if (!world.isRemote) {
			setSheared(true);
			int n = 1 + rand.nextInt(3);
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
		world.playMovingSound(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, category, 1f, 1f);
		setSheared(true);
		int n = 1 + rand.nextInt(3);
		for (int i = 0; i < n; i++) {
			ItemEntity itemEntity = entityDropItem(Items.STRING, 1);
			if (itemEntity != null) {
				itemEntity.setMotion(itemEntity.getMotion().add((rand.nextFloat() - rand.nextFloat()) * 0.1f, rand.nextFloat() * 0.05f, (rand.nextFloat() - rand.nextFloat()) * 0.1f));
			}
		}
	}

}
