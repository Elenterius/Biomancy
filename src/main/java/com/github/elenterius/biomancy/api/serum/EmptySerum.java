package com.github.elenterius.biomancy.api.serum;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class EmptySerum extends ForgeRegistryEntry<Serum> implements Serum {

	static final Serum INSTANCE = new EmptySerum();

	private EmptySerum() {}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return false;
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag nbt, @Nullable LivingEntity source, LivingEntity target) {}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return false;
	}

	@Override
	public void affectPlayerSelf(CompoundTag nbt, ServerPlayer targetSelf) {}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public int getColor() {
		return 0xFF_FFFFFF;
	}

	@Override
	public String getNameTranslationKey() {
		return TRANSLATION_PREFIX + "biomancy.empty";
	}

}
