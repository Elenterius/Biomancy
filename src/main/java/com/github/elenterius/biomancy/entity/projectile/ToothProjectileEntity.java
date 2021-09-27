package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Lazy;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ToothProjectileEntity extends AbstractProjectileEntity implements IRendersAsItem {

	public ToothProjectileEntity(EntityType<? extends AbstractProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public ToothProjectileEntity(World world, double x, double y, double z) {
		super(ModEntityTypes.TOOTH_PROJECTILE.get(), world, x, y, z);
	}

	public ToothProjectileEntity(World world, LivingEntity shooter) {
		super(ModEntityTypes.TOOTH_PROJECTILE.get(), world, shooter);
	}

	@Override
	public float getGravity() {
		return 0.01f;
	}

	@Override
	protected void onHitEntity(EntityRayTraceResult result) {
		super.onHitEntity(result);
		Entity victim = result.getEntity();
		Entity shooter = getOwner();
		if (shooter instanceof LivingEntity) {
			((LivingEntity) shooter).setLastHurtMob(victim);
		}
		boolean success = victim.hurt(ModDamageSources.createToothProjectileDamage(this, shooter != null ? shooter : this), getDamage());
		if (success && victim instanceof LivingEntity) {
			if (!level.isClientSide) {
				if (getKnockback() > 0) {
					Vector3d vector3d = getDeltaMovement().multiply(1d, 0d, 1d).normalize().scale((double) getKnockback() * 0.6d);
					if (vector3d.lengthSqr() > 0d) {
						victim.push(vector3d.x, 0.1d, vector3d.z);
					}
				}
				if (shooter instanceof LivingEntity) {
					doEnchantDamageEffects((LivingEntity) shooter, victim); //thorn & arthropod damage
				}
				if (!isSilent() && victim != shooter && victim instanceof PlayerEntity && shooter instanceof ServerPlayerEntity) {
					((ServerPlayerEntity) shooter).connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0f));
				}
			}
		}
		playSound(SoundEvents.ARROW_HIT, 1f, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
	}

	//onBlockHit
	@Override
	protected void onHitBlock(BlockRayTraceResult result) {
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
	protected IParticleData getParticle() {
		return ParticleTypes.POOF;
	}

	private static final Lazy<ItemStack> ITEM_TO_RENDER = Lazy.of(() -> {
		ItemStack stack = new ItemStack(ModItems.BONE_SCRAPS.get());
		stack.getOrCreateTag().putInt("ScrapType", 1);
		return stack;
	});

	@OnlyIn(Dist.CLIENT)
	public static ItemStack getItemForRendering() {
		return ITEM_TO_RENDER.get();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack getItem() {
		return ITEM_TO_RENDER.get();
	}

}
