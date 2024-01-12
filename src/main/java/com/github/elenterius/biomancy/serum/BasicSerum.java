package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class BasicSerum extends ForgeRegistryEntry<Serum> implements Serum {

	private final int color;

	protected BasicSerum(int color) {
		this.color = color;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public boolean canAffectEntity(CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		return true;
	}

	@Override
	public boolean canAffectPlayerSelf(CompoundTag tag, Player targetSelf) {
		return true;
	}

	@Override
	public String getNameTranslationKey() {
		return Serum.makeTranslationKey(Objects.requireNonNull(ModSerums.REGISTRY.get().getKey(this)));
	}

	@Override
	public MutableComponent getDisplayName() {
		return ComponentUtil.translatable(getNameTranslationKey());
	}

	public void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(ComponentUtil.translatable(getDescriptionTranslationKey()).withStyle(TextStyles.LORE));
		}
	}

	@Override
	public String toString() {
		return "Serum{name=%s, color=%s}".formatted(ModSerums.REGISTRY.get().getKey(this), Integer.toHexString(color));
	}

}
