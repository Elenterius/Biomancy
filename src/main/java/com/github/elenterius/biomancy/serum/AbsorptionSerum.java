package com.github.elenterius.biomancy.serum;

import com.github.elenterius.biomancy.BiomancyConfig;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AbsorptionSerum extends BasicSerum {

	public AbsorptionSerum(int color) {
		super(color);
	}

	@Override
	public void affectEntity(ServerLevel level, CompoundTag tag, @Nullable LivingEntity source, LivingEntity target) {
		addAbsorption(target);
	}

	@Override
	public void affectPlayerSelf(ServerLevel level, CompoundTag tag, ServerPlayer targetSelf) {
		addAbsorption(targetSelf);
	}

	private void addAbsorption(LivingEntity target) {
		float absorptionAmount = target.getAbsorptionAmount();
		float maxHearts = getMaxHearts();
		if (absorptionAmount < maxHearts * 2f) {
			target.setAbsorptionAmount(Math.min(maxHearts * 2f, absorptionAmount + getHearts() * 2));
		}
	}

	@Override
	public void appendTooltip(CompoundTag tag, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(ComponentUtil.translatable(getDescriptionTranslationKey(), getHearts(), getMaxHearts()).withStyle(TextStyles.LORE));
	}

	protected float getHearts() {
		return BiomancyConfig.SERVER_SYNCED.absorptionHearts.get().floatValue();
	}

	protected float getMaxHearts() {
		return BiomancyConfig.SERVER_SYNCED.absorptionMaxHearts.get().floatValue();
	}

}
