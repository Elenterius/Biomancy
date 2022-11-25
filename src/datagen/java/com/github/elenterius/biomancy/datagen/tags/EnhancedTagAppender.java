package com.github.elenterius.biomancy.datagen.tags;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

public record EnhancedTagAppender<T>(TagsProvider.TagAppender<T> delegate) {

	public EnhancedTagAppender<T> add(T entry) {
		delegate.add(entry);
		return this;
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> add(ResourceKey<T>... resourceKeys) {
		delegate.add(resourceKeys);
		return this;
	}

	public EnhancedTagAppender<T> addOptional(ResourceLocation resourceLocation) {
		delegate.addOptional(resourceLocation);
		return this;
	}

	public EnhancedTagAppender<T> addOptional(ResourceLocation... resourceLocations) {
		for (ResourceLocation resourceLocation : resourceLocations) {
			delegate.addOptional(resourceLocation);
		}
		return this;
	}

	public EnhancedTagAppender<T> addOptional(String... resourceLocations) {
		for (String resourceLocation : resourceLocations) {
			delegate.addOptional(new ResourceLocation(resourceLocation));
		}
		return this;
	}


	public EnhancedTagAppender<T> addTag(TagKey<T> tagKey) {
		delegate.addTag(tagKey);
		return this;
	}

	public EnhancedTagAppender<T> addOptionalTag(ResourceLocation resourceLocation) {
		delegate.addOptionalTag(resourceLocation);
		return this;
	}

	public EnhancedTagAppender<T> addOptionalTag(ResourceLocation... resourceLocations) {
		for (ResourceLocation resourceLocation : resourceLocations) {
			delegate.addOptionalTag(resourceLocation);
		}
		return this;
	}

	public EnhancedTagAppender<T> addOptionalTag(String... resourceLocations) {
		for (String resourceLocation : resourceLocations) {
			delegate.addOptionalTag(new ResourceLocation(resourceLocation));
		}
		return this;
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> add(T... entries) {
		delegate.add(entries);
		return this;
	}

	public EnhancedTagAppender<T> add(Tag.Entry tagEntry) {
		delegate.add(tagEntry);
		return this;
	}

	public Tag.Builder getInternalBuilder() {
		return delegate.getInternalBuilder();
	}

	public String getModId() {
		return delegate.getModID();
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> addTag(TagKey<T>... tagKeys) {
		delegate.addTags(tagKeys);
		return this;
	}

	public EnhancedTagAppender<T> replace() {
		delegate.replace();
		return this;
	}

	public EnhancedTagAppender<T> replace(boolean bool) {
		delegate.replace(bool);
		return this;
	}

	public EnhancedTagAppender<T> remove(T entry) {
		delegate.remove(entry);
		return this;
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> remove(T first, T... entries) {
		delegate.remove(first, entries);
		return this;
	}

	public EnhancedTagAppender<T> remove(ResourceLocation resourceLocation) {
		delegate.remove(resourceLocation);
		return this;
	}

	public EnhancedTagAppender<T> remove(ResourceLocation first, ResourceLocation... resourceLocations) {
		delegate.remove(first, resourceLocations);
		return this;
	}

	public EnhancedTagAppender<T> remove(TagKey<T> tagKey) {
		delegate.remove(tagKey);
		return this;
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> remove(TagKey<T> first, TagKey<T>... tagKeys) {
		delegate.remove(first, tagKeys);
		return this;
	}

}
