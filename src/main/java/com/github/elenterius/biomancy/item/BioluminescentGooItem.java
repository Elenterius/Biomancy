package com.github.elenterius.biomancy.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class BioluminescentGooItem extends SimpleItem implements SignApplicator {

	public BioluminescentGooItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean tryApplyToSign(Level level, SignBlockEntity sign, boolean isFrontText, Player player) {
		if (sign.updateText(signText -> signText.setHasGlowingText(true), isFrontText)) {
			level.playSound(null, sign.getBlockPos(), SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1f, 1f);
			return true;
		}
		return false;
	}

}
