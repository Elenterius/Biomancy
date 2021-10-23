package com.github.elenterius.biomancy.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public final class BlockPropertyUtil {

	public static final ImmutableMap<IntegerProperty, Integer> maxAgeMappings;
	public static final String AGE_PROPERTY = "age";

	static {
		//we can't know if someone might have manipulated the properties, so we search for the max value
		maxAgeMappings = ImmutableMap.<IntegerProperty, Integer>builder()
				.put(BlockStateProperties.AGE_1, findLast(BlockStateProperties.AGE_1.getPossibleValues()))
				.put(BlockStateProperties.AGE_3, findLast(BlockStateProperties.AGE_3.getPossibleValues()))
				.put(BlockStateProperties.AGE_5, findLast(BlockStateProperties.AGE_5.getPossibleValues()))
				.put(BlockStateProperties.AGE_7, findLast(BlockStateProperties.AGE_7.getPossibleValues()))
				.put(BlockStateProperties.AGE_15, findLast(BlockStateProperties.AGE_15.getPossibleValues()))
				.put(BlockStateProperties.AGE_25, findLast(BlockStateProperties.AGE_25.getPossibleValues()))
				.build();
	}

	private BlockPropertyUtil() {}

	public static int getMaxAge(IntegerProperty property) {
		if (maxAgeMappings.containsKey(property)) {
			return maxAgeMappings.get(property);
		}
		else {
			return findLast(property.getPossibleValues());
		}
	}

	public static int getMinAge(IntegerProperty property) {
		return property.getPossibleValues().iterator().next();
	}

	public static Optional<IntegerProperty> getAgeProperty(BlockState state) {
		if (state.getBlock() instanceof CropsBlock) {
			return Optional.of(((CropsBlock) state.getBlock()).getAgeProperty());
		}
		for (Property<?> prop : state.getProperties()) {
			if (prop.getName().equals(AGE_PROPERTY) && prop instanceof IntegerProperty) {
				return Optional.of((IntegerProperty) prop);
			}
		}
		return Optional.empty();
	}

	public static int getAge(BlockState state) {
		return getAgeProperty(state).map(state::getValue).orElse(0);
	}

	public static Optional<int[]> getCurrentAgeAndMaxAge(BlockState state) {
		if (state.getBlock() instanceof CropsBlock) {
			CropsBlock block = (CropsBlock) state.getBlock();
			return Optional.of(new int[]{state.getValue(block.getAgeProperty()), block.getMaxAge()});
		}

		for (Property<?> prop : state.getProperties()) {
			if (prop.getName().equals(AGE_PROPERTY) && prop instanceof IntegerProperty) {
				IntegerProperty ageProperty = (IntegerProperty) prop;
				return Optional.of(new int[]{state.getValue(ageProperty), getMaxAge(ageProperty)});
			}
		}

		return Optional.empty();
	}

	public static <T extends Comparable<T>> T getPrevious(Property<T> property, T value) {
		return findPrevious(property.getPossibleValues(), value);
	}

	public static <T> T findPrevious(Collection<T> collection, T value) {
		Iterator<T> iterator = collection.iterator();
		T previous = null;

		while (iterator.hasNext()) {
			T next = iterator.next();
			if (next.equals(value) && previous != null) {
				return previous;
			}
			previous = next;
		}

		return previous != null ? previous : value; //returns the last value of the collection if possible
	}

	public static <T> T findLast(Collection<T> collection) {
		T value = collection.iterator().next();
		for (T t : collection) value = t;
		return value;
	}

}
