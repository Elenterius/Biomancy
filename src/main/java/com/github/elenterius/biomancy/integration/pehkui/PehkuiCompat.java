package com.github.elenterius.biomancy.integration.pehkui;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.*;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class PehkuiCompat {

	private PehkuiCompat() {}

	public static void init(Consumer<PehkuiHelper> helperSetter) {
		ScaleTypes.BASE.getDefaultBaseValueModifiers().add(SCALE_MODIFIER);
		helperSetter.accept(new PehkuiHelperImpl());
	}

	private static ScaleModifier registerScaleModifier(String name, Supplier<ScaleModifier> factory) {
		return ScaleRegistries.register(ScaleRegistries.SCALE_MODIFIERS, BiomancyMod.createRL(name), factory.get());
	}

	private static ScaleType registerScaleType(String name, UnaryOperator<ScaleType.Builder> builder) {
		return ScaleRegistries.register(ScaleRegistries.SCALE_TYPES, BiomancyMod.createRL(name), builder.apply(ScaleType.Builder.create()).build());
	}

	static final class PehkuiHelperImpl implements PehkuiHelper {

		@Override
		public void setScale(LivingEntity livingEntity, float scale) {
			PehkuiCompat.SCALE_TYPE.getScaleData(livingEntity).setTargetScale(scale);
		}

		@Override
		public void resetSize(LivingEntity livingEntity) {
			//PehkuiCompat.SCALE_TYPE.getScaleData(livingEntity).resetScale();
			setScale(livingEntity, 1f);
		}

		@Override
		public void resize(LivingEntity livingEntity, float multiplier) {
			ScaleData scaleData = PehkuiCompat.SCALE_TYPE.getScaleData(livingEntity);
			scaleData.setTargetScale(scaleData.getScale() * multiplier);
		}

		@Override
		public float getScale(LivingEntity livingEntity) {
			ScaleData scaleData = PehkuiCompat.SCALE_TYPE.getScaleData(livingEntity);
			return scaleData.getScale();
		}

	}

	private static final ScaleModifier SCALE_MODIFIER = registerScaleModifier("scale", () -> new TypedScaleModifier(() -> PehkuiCompat.SCALE_TYPE));

	private static final ScaleType SCALE_TYPE = registerScaleType("size", builder -> builder.affectsDimensions().addDependentModifier(SCALE_MODIFIER));

}
