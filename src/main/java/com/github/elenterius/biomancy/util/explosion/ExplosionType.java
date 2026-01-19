package com.github.elenterius.biomancy.util.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public enum ExplosionType {
	VANILLA(Explosion::new, Explosion::new),
	DECAY(DecayExplosion::new, DecayExplosion::new),
	VOLATILE(VolatileExplosion::new, VolatileExplosion::new);

	public final ServerExplosionFactory<? extends Explosion> serverFactory;
	public final ClientExplosionFactory<? extends Explosion> clientFactory;

	<T extends Explosion> ExplosionType(ServerExplosionFactory<T> serverFactory, ClientExplosionFactory<T> clientFactory) {
		this.clientFactory = clientFactory;
		this.serverFactory = serverFactory;
	}

	public interface ServerExplosionFactory<T extends Explosion> {
		T create(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction interaction);
	}

	public interface ClientExplosionFactory<T extends Explosion> {
		T create(Level level, @Nullable Entity source, double toBlowX, double toBlowY, double toBlowZ, float radius, List<BlockPos> positions);
	}

}
