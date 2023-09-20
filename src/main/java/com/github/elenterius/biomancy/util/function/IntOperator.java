package com.github.elenterius.biomancy.util.function;

@FunctionalInterface
public interface IntOperator {

	IntOperator IDENTITY = v -> v;

	int apply(int operand);

	default IntOperator compose(IntOperator before) {
		return v -> apply(before.apply(v));
	}

	default IntOperator andThen(IntOperator after) {
		return v -> after.apply(apply(v));
	}

}
