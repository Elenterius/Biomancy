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

	private final Function<K, Integer> intermediaryKeyFunc;
	private final Map<K, V> accessMap = new HashMap<>();
	private final Map<Integer, V> valueCache = new HashMap<>();

	/**
	 * @param intermediaryKeyFunc a function that computes the same intermediary key for all input keys that must point to the same cached value
	 */
	public IntermediaryKeyCache(Function<K, Integer> intermediaryKeyFunc) {
		this.intermediaryKeyFunc = intermediaryKeyFunc;
	}

	@Nullable
	public V put(K key, V value) {
		valueCache.put(intermediaryKeyFunc.apply(key), value);
		return accessMap.put(key, value);
	}

	public V computeIfAbsent(K key, Function<K, V> computeValue) {
		if (accessMap.containsKey(key)) return accessMap.get(key);

		V value = valueCache.computeIfAbsent(intermediaryKeyFunc.apply(key), intermediaryKey -> computeValue.apply(key));
		accessMap.put(key, value);
		return value;
	}

	@Nullable
	public V get(K key) {
		return accessMap.get(key);
	}

	public Optional<V> getOptional(K key) {
		return Optional.ofNullable(accessMap.get(key));
	}
}
