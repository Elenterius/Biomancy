package com.github.elenterius.biomancy.world.entity.projectile;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.util.Lazy;

import java.util.Objects;

public class AntiGravityProjectile extends BaseProjectile implements ItemSupplier {

    private static final Lazy<ItemStack> ITEM_TO_RENDER = Lazy.of(() -> new ItemStack(ModItems.CREATOR_MIX.get()));

    public AntiGravityProjectile(EntityType<? extends BaseProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public AntiGravityProjectile(Level level, double x, double y, double z) {
        super(ModEntityTypes.ANTI_GRAVITY_PROJECTILE.get(), level, x, y, z);
    }

    public AntiGravityProjectile(Level level, LivingEntity shooter) {
        super(ModEntityTypes.ANTI_GRAVITY_PROJECTILE.get(), level, shooter);
    }

    @Override
    public float getGravity() {
        return 0.01f;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        playSound(SoundEvents.GLOW_SQUID_SQUIRT, 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level.isClientSide) {
            Entity victim = result.getEntity();
            Entity owner = getOwner();
            if (victim instanceof LivingEntity livingVictim) {
                //                AttributeInstance gravityAttribute = livingVictim.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
                //                if (gravityAttribute != null) {
                //                    gravityAttribute.addTransientModifier(new AttributeModifier("AntiGravity modifier", -0.081d, AttributeModifier.Operation.ADDITION));
                //                }
                livingVictim.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20 * 240, livingVictim.hasEffect(MobEffects.LEVITATION) ? 1 : 0), Objects.requireNonNullElse(owner, this));
            }

            if (owner instanceof LivingEntity shooter) {
                doEnchantDamageEffects(shooter, victim);
            }
        }
        playSound(SoundEvents.GLOW_SQUID_SQUIRT, 1, 1.2f / (random.nextFloat() * 0.2f + 0.9f));
    }

    @Override
    public ItemStack getItem() {
        return ITEM_TO_RENDER.get();
    }

}
