package com.github.elenterius.biomancy.util;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Models a many-to-one relationship -> "Multi Key" Map
 * <br>
 * i.e. different Keys will point to the same Value
 */
public class IntermediaryKeyCache<K, V> {
	protected static final Map<Integer, Integer> INTEGER_CACHE = new HashMap<>();

	private final Function<K, Integer> computeKey;
	private final Map<K, Integer> computedKeys = new HashMap<>();
	private final HashMap<Integer, V> cachedValues = new HashMap<>();

	/**
	 * @param computeKey a function that computes the same intermediary key for all input keys that must point to the same cached value
	 */
	public IntermediaryKeyCache(Function<K, Integer> computeKey) {
		this.computeKey = computeKey;
	}

	private Integer getIntermediaryKey(K key) {
		return computedKeys.computeIfAbsent(key, k -> INTEGER_CACHE.computeIfAbsent(computeKey.apply(k), computedKey -> computedKey));
	}

	@Nullable
	public V put(K key, V value) {
		return cachedValues.put(getIntermediaryKey(key), value);
	}

	public V computeIfAbsent(K key, Function<K, V> computeValue) {
		return cachedValues.computeIfAbsent(getIntermediaryKey(key), k -> computeValue.apply(key));
	}

	@Nullable
	public V get(K key) {
		return computedKeys.containsKey(key) ? cachedValues.get(computedKeys.get(key)) : null;
	}

	public Optional<V> getOptional(K key) {
		return computedKeys.containsKey(key) ? Optional.ofNullable(cachedValues.get(computedKeys.get(key))) : Optional.empty();
	}
}
