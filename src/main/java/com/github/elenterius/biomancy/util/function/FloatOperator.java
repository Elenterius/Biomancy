package com.github.elenterius.biomancy.util.function;

@FunctionalInterface
public interface FloatOperator {

	FloatOperator IDENTITY = v -> v;

	float apply(float operand);

	default FloatOperator compose(FloatOperator before) {
		return v -> apply(before.apply(v));
	}

	default FloatOperator andThen(FloatOperator after) {
		return v -> after.apply(apply(v));
	}

}
