package com.github.elenterius.biomancy.util;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class IntermediaryKeyCache<K, V> {
	private final Function<K, Integer> computeKey;
	private final Map<K, Integer> computedKeys = new HashMap<>();
	private final HashMap<Integer, V> cachedValues = new HashMap<>();

	/**
	 * many-to-one relationship -> "Multi Key" Map
	 */
	public IntermediaryKeyCache(Function<K, Integer> computeKey) {
		this.computeKey = computeKey;
	}

	@Nullable
	public V put(K key, V value) {
		Integer intermediaryKey = computedKeys.computeIfAbsent(key, computeKey);
		return cachedValues.put(intermediaryKey, value);
	}

	public V computeIfAbsent(K key, Function<K, V> computeValue) {
		Integer intermediaryKey = computedKeys.computeIfAbsent(key, computeKey);
		return cachedValues.computeIfAbsent(intermediaryKey, k -> computeValue.apply(key));
	}

	@Nullable
	public V get(K key) {
		return computedKeys.containsKey(key) ? cachedValues.get(computedKeys.get(key)) : null;
	}

	public Optional<V> getOptional(K key) {
		return computedKeys.containsKey(key) ? Optional.ofNullable(cachedValues.get(computedKeys.get(key))) : Optional.empty();
	}
}
