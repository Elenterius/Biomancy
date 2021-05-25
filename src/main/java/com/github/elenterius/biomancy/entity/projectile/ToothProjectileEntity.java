package com.github.elenterius.biomancy.entity.projectile;

import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ToothProjectileEntity extends DamagingProjectileEntity implements IRendersAsItem, IEntityAdditionalSpawnData {

	public ToothProjectileEntity(EntityType<? extends DamagingProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketBuffer buffer) {
		Entity shooter = getShooter();
		buffer.writeVarInt(shooter == null ? 0 : shooter.getEntityId());
	}

	@Override
	public void readSpawnData(PacketBuffer additionalData) {
		Entity shooter = world.getEntityByID(additionalData.readVarInt());
		setShooter(shooter);
	}

	@Override
	public void tick() {
		Entity shooter = getShooter();
		System.out.println("flag: " + (world.isRemote || (shooter == null || !shooter.removed) && world.isBlockLoaded(getPosition())));
		super.tick();
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		super.onImpact(result);
		if (!world.isRemote) {
			setShooter(null);
			remove();
		}
	}

	@Override
	protected void onEntityHit(EntityRayTraceResult result) {
		super.onEntityHit(result);
		if (!world.isRemote) {
			Entity victim = result.getEntity();
			Entity shooter = getShooter();
			boolean success = victim.attackEntityFrom(ModDamageSources.createToothProjectileDamage(this, shooter), 2.5f);
			if (success) {
				if (shooter instanceof LivingEntity) {
					applyEnchantments((LivingEntity) shooter, victim); //thorn & arthropod damage
				}
				if (!isSilent() && victim != shooter && victim instanceof PlayerEntity && shooter instanceof ServerPlayerEntity) {
					((ServerPlayerEntity) shooter).connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.HIT_PLAYER_ARROW, 0f));
				}
			}
		}
		playSound(SoundEvents.ENTITY_ARROW_HIT, 1f, 1.2f / (rand.nextFloat() * 0.2f + 0.9f));
	}

	//onBlockHit
	@Override
	protected void func_230299_a_(BlockRayTraceResult result) {
		super.func_230299_a_(result);
		playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean isBurning() {
		return false;
	}

	@Override
	protected boolean isFireballFiery() {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.BONE_SCRAPS.get());
	}

}
