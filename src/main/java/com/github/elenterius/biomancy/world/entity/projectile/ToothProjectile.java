package com.github.elenterius.biomancy.world.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.Lazy;

public class ToothProjectile extends BaseProjectile implements ItemSupplier {

	public ToothProjectile(EntityType<? extends BaseProjectile> entityType, Level world) {
		super(entityType, world);
	}

	public ToothProjectile(Level world, double x, double y, double z) {
		super(ModEntityTypes.TOOTH_PROJECTILE.get(), world, x, y, z);
	}

	public ToothProjectile(Level world, LivingEntity shooter) {
		super(ModEntityTypes.TOOTH_PROJECTILE.get(), world, shooter);
	}

	@Override
	public float getGravity() {
		return 0.01f;
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		Entity victim = result.getEntity();
		Entity shooter = getOwner();
		if (shooter instanceof LivingEntity livingEntity) {
			livingEntity.setLastHurtMob(victim);
		}

		boolean success = victim.hurt(ModDamageSources.createToothProjectileDamage(this, shooter != null ? shooter : this), getDamage());
		if (success && victim instanceof LivingEntity && !level.isClientSide) {
			if (getKnockback() > 0) {
				Vec3 vector3d = getDeltaMovement().multiply(1d, 0d, 1d).normalize().scale((double) getKnockback() * 0.6d);
				if (vector3d.lengthSqr() > 0d) {
					victim.push(vector3d.x, 0.1d, vector3d.z);
				}
			}

			if (shooter instanceof LivingEntity livingEntity) {
				doEnchantDamageEffects(livingEntity, victim); //thorn & arthropod damage
			}

			if (!isSilent() && victim != shooter && victim instanceof Player && shooter instanceof ServerPlayer serverPlayer) {
				serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0f));
			}
		}
		playSound(SoundEvents.ARROW_HIT, 1f, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	protected ParticleOptions getParticle() {
		return ParticleTypes.POOF;
	}

	private static final Lazy<ItemStack> ITEM_TO_RENDER = Lazy.of(() -> new ItemStack(ModItems.MOB_FANG.get()));

	public ItemStack getItem() {
		return ITEM_TO_RENDER.get();
	}

}
