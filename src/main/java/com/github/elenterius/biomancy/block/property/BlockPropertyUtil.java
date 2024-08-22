package com.github.elenterius.biomancy.block.property;

import com.github.elenterius.biomancy.mixin.accessor.IntegerPropertyAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public final class BlockPropertyUtil {

	public static final String AGE_PROPERTY = "age";

	private BlockPropertyUtil() {}

	public static int getMaxAge(IntegerProperty property) {
		return ((IntegerPropertyAccessor) property).biomancy$getMax();
	}

	public static int getMinAge(IntegerProperty property) {
		return ((IntegerPropertyAccessor) property).biomancy$getMin();
	}

	public static Optional<IntegerProperty> getAgeProperty(BlockState state) {
		//		if (state.getBlock() instanceof CropBlock crop) {
		//			return Optional.of(crop.getAgeProperty());
		//		}
		for (Property<?> prop : state.getProperties()) {
			if (prop.getName().equals(AGE_PROPERTY) && prop instanceof IntegerProperty intProperty) {
				return Optional.of(intProperty);
			}
		}
		return Optional.empty();
	}

	public static int getAge(BlockState state) {
		return getAgeProperty(state).map(state::getValue).orElse(0);
	}

	public static <T extends Comparable<T>> T getPrevious(Property<T> property, T value) {
		return findPrevious(property.getPossibleValues(), value);
	}

	private static <T> T findPrevious(Collection<T> collection, T value) {
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

	public static <T extends Comparable<T>> T getLast(Property<T> property) {
		return findLast(property.getPossibleValues());
	}

	private static <T> T findLast(Collection<T> collection) {
		T value = collection.iterator().next();
		for (T t : collection) value = t;
		return value;
	}

}
