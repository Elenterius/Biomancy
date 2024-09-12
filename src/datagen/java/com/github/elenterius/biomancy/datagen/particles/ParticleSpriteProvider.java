package com.github.elenterius.biomancy.datagen.particles;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class ParticleSpriteProvider implements DataProvider {

	private static final Logger LOGGER = LogManager.getLogger(ParticleSpriteProvider.class);
	private final PackOutput packOutput;
	private final String modId;
	private final ExistingFileHelper fileHelper;

	private final Map<ResourceLocation, ParticleSprite> particles = new LinkedHashMap<>();

	protected ParticleSpriteProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
		this.packOutput = packOutput;
		this.modId = modId;
		this.fileHelper = fileHelper;
	}

	private static <O extends ParticleOptions> ResourceLocation getId(ParticleType<O> particleType) {
		ResourceLocation key = ForgeRegistries.PARTICLE_TYPES.getKey(particleType);
		if (key == null) {
			throw new IllegalStateException("No registry key found for the particle type: " + particleType);
		}
		return key;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		particles.clear();
		registerParticles();
		validate();
		if (!particles.isEmpty()) {
			return generate(cachedOutput);
		}

		return CompletableFuture.allOf();
	}

	private CompletableFuture<?> generate(final CachedOutput cache) {
		List<CompletableFuture<?>> futures = new ArrayList<>();

		for (Map.Entry<ResourceLocation, ParticleSprite> entry : particles.entrySet()) {
			ResourceLocation particleId = entry.getKey();
			ParticleSprite particleSprite = entry.getValue();
			futures.add(DataProvider.saveStable(cache, particleSprite.toJson(), getPath(particleId)));
		}

		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
	}

	private Path getPath(ResourceLocation particleId) {
		return packOutput.getOutputFolder().resolve("assets/" + particleId.getNamespace() + "/particles/" + particleId.getPath() + ".json");
	}

	@Override
	public String getName() {
		return StringUtils.capitalize(modId) + " Particle Sprite Frames";
	}

	public abstract void registerParticles();

	protected <O extends ParticleOptions, T extends ParticleType<O>> void addParticle(final RegistryObject<T> registryObject, String texture) {
		ResourceLocation id = registryObject.getId();
		addParticle(id, ParticleSprite.create(texture));
	}

	protected <O extends ParticleOptions, T extends ParticleType<O>> void addParticle(final RegistryObject<T> registryObject, ResourceLocation texture) {
		ResourceLocation id = registryObject.getId();
		addParticle(id, ParticleSprite.create(texture));
	}

	protected <O extends ParticleOptions, T extends ParticleType<O>> void addParticle(final RegistryObject<T> registryObject, int frameCount, int frameStartNumber) {
		ResourceLocation id = registryObject.getId();
		addParticle(id, ParticleSprite.create(id, frameCount, frameStartNumber));
	}

	protected <O extends ParticleOptions, T extends ParticleType<O>> void addParticle(final RegistryObject<T> registryObject, int frameCount) {
		ResourceLocation id = registryObject.getId();
		addParticle(id, ParticleSprite.create(id, frameCount));
	}

	protected <O extends ParticleOptions> void addParticle(ParticleType<O> particleType, int frameCount) {
		ResourceLocation id = getId(particleType);
		addParticle(id, ParticleSprite.create(id, frameCount));
	}

	protected void addParticle(ResourceLocation key, ParticleSprite particleSprite) {
		if (particles.put(key, particleSprite) != null) {
			throw new IllegalStateException("Particle '%s' already exists for mod %s".formatted(key, modId));
		}
	}

	private void validate() {
		final List<String> invalidParticles = particles.entrySet().stream()
				.filter(entry -> !validate(entry.getKey(), entry.getValue()))
				.map(Map.Entry::getKey)
				.map(ResourceLocation::toString)
				.toList();

		if (!invalidParticles.isEmpty()) {
			throw new IllegalStateException("Found invalid Particle Sprite Frames: " + invalidParticles);
		}
	}

	private boolean validate(final ResourceLocation particleId, final ParticleSprite particleSprite) {
		boolean isValid = true;
		for (String sprite : particleSprite.spriteFrames) {
			ResourceLocation spriteResource = new ResourceLocation(sprite);
			boolean valid = fileHelper.exists(spriteResource, PackType.CLIENT_RESOURCES, ".png", "textures/particle");
			if (!valid) {
				final String path = "%s:textures/particle/%s.png".formatted(spriteResource.getNamespace(), spriteResource.getPath());
				LOGGER.warn("Unable to find sprite frame '{}' for particle '{}'", path, particleId);
				isValid = false;
			}
		}

		return isValid;
	}

	protected record ParticleSprite(ResourceLocation texture, int frameCount, String[] spriteFrames) {

		public static ParticleSprite create(String texture) {
			ResourceLocation rl = new ResourceLocation(texture);
			return new ParticleSprite(rl, 1, new String[]{rl.toString()});
		}

		public static ParticleSprite create(ResourceLocation texture) {
			return new ParticleSprite(texture, 1, new String[]{texture.toString()});
		}

		public static ParticleSprite create(ResourceLocation texture, int frameCount) {
			String[] sprites = createSpriteFrames(texture, frameCount, 0);
			return new ParticleSprite(texture, frameCount, sprites);
		}

		public static ParticleSprite create(ResourceLocation texture, int frameCount, int frameStartNumber) {
			String[] sprites = createSpriteFrames(texture, frameCount, frameStartNumber);
			return new ParticleSprite(texture, frameCount, sprites);
		}

		private static String[] createSpriteFrames(ResourceLocation texture, int frameCount, int frameStartNumber) {
			String[] sprites = new String[frameCount];
			String baseTexture = texture.toString();

			for (int i = 0; i < frameCount; i++) {
				int frameNumber = frameStartNumber + i;
				sprites[i] = baseTexture + "_" + frameNumber;
			}

			return sprites;
		}

		public JsonElement toJson() {
			JsonArray array = new JsonArray();
			Arrays.stream(spriteFrames).forEachOrdered(array::add);
			JsonObject json = new JsonObject();
			json.add("textures", array);
			return json;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof ParticleSprite that)) return false;
			return frameCount == that.frameCount && Objects.equals(texture, that.texture);
		}

		@Override
		public int hashCode() {
			return Objects.hash(texture, frameCount);
		}

		@Override
		public String toString() {
			return "ParticleSprite{texture=%s, frameCount=%d, spriteFrames=%s}".formatted(texture, frameCount, Arrays.toString(spriteFrames));
		}
	}
}
