package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.api.serum.ISerum;
import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModSerums;
import com.github.elenterius.biomancy.styles.TextStyles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class Serum implements ISerum {

	private final int color;

	protected Serum(int color) {
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
	public String getTranslationKey() {
		return ISerum.makeTranslationKey(Objects.requireNonNull(ModSerums.REGISTRY.get().getKey(this)));
	}

	@Override
	public MutableComponent getDisplayName() {
		return ComponentUtil.translatable(getTranslationKey());
	}

	public void appendTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(ComponentUtil.translatable(getTooltipKey()).withStyle(TextStyles.LORE));
		}
	}

	public String getTooltipKey() {
		return getTranslationKey() + ".tooltip";
	}

	@Override
	public String toString() {
		return "Serum{name=%s, color=%s}".formatted(ModSerums.REGISTRY.get().getKey(this), Integer.toHexString(color));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Serum otherSerum = (Serum) other;
		return Objects.requireNonNull(ModSerums.REGISTRY.get().getKey(this)).equals(ModSerums.REGISTRY.get().getKey(otherSerum));
	}

}
