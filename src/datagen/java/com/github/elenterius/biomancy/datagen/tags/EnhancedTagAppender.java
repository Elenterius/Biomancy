package com.github.elenterius.biomancy.datagen.tags;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public record EnhancedTagAppender<T extends IForgeRegistryEntry<?>>(TagsProvider.TagAppender<T> delegate) {

	public EnhancedTagAppender<T> addTag(TagKey<T> tagKey) {
		if (isValidNamespace(tagKey.location().getNamespace())) {
			delegate.addTag(tagKey);
		}
		else {
			addOptionalTag(tagKey.location());
		}
		return this;
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> addTag(TagKey<T>... tagKeys) {
		for (TagKey<T> tagKey : tagKeys) {
			addTag(tagKey);
		}
		return this;
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> add(T... entries) {
		for (T entry : entries) {
			add(entry);
		}
		return this;
	}

	public EnhancedTagAppender<T> add(T entry) {
		if (isValidNamespace(Objects.requireNonNull(entry.getRegistryName()).getNamespace())) {
			delegate.add(entry);
		}
		else {
			addOptional(entry.getRegistryName());
		}
		return this;
	}

	private boolean isValidNamespace(String namespace) {
		return "minecraft".equals(namespace) || delegate.getModID().equals(namespace);
	}

	@SafeVarargs
	public final EnhancedTagAppender<T> add(ResourceKey<T>... resourceKeys) {
		for (ResourceKey<T> resourcekey : resourceKeys) {
			if (isValidNamespace(resourcekey.location().getNamespace())) {
				delegate.add(resourcekey);
			}
			else {
				addOptional(resourcekey.location());
			}
		}
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

	public EnhancedTagAppender<T> addOptional(RegistryObject<?>... entries) {
		for (RegistryObject<?> registryObject : entries) {
			delegate.addOptional(registryObject.getId());
		}
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

	public Tag.Builder getInternalBuilder() {
		return delegate.getInternalBuilder();
	}

	public String getModId() {
		return delegate.getModID();
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
