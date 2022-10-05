package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.HrTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class VolatileGlandItem extends Item implements IBiomancyItem {

	public VolatileGlandItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		return player.isDeadOrDying() ? InteractionResultHolder.fail(player.getItemInHand(usedHand)) : super.use(level, player, usedHand);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
		ItemStack result = livingEntity.eat(level, stack);
		explode(level, livingEntity);
		return result;
	}

	private void explode(Level level, LivingEntity livingEntity) {
		if (level.isClientSide) return;

		float explosionRadius = 3 - livingEntity.getArmorCoverPercentage() * 1.5f;

		Explosion.BlockInteraction blockInteraction = ForgeEventFactory.getMobGriefingEvent(level, livingEntity) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
		DamageSource damageSource = DamageSource.explosion(livingEntity);
		level.explode(null, damageSource, null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), explosionRadius, false, blockInteraction);

		if (!livingEntity.isDeadOrDying()) {
			livingEntity.hurt(damageSource, 0.5f + livingEntity.getArmorCoverPercentage() * 2.5f);
		}

		if (livingEntity.isDeadOrDying()) {
			spawnFleshBits(level, livingEntity.getX(), livingEntity.getY(0.5f), livingEntity.getZ());
			spawnEffectCloud(level, livingEntity);
		}
	}

	private void spawnFleshBits(Level level, double x, double y, double z) {
		float force = 0.095f;
		int n = level.random.nextInt(1, 6 + 1);

		for (int i = 0; i < n; i++) {
			ItemEntity itemEntity = new ItemEntity(level, x, y, z, new ItemStack(ModItems.FLESH_BITS.get()));
			itemEntity.setDefaultPickUpDelay();
			double dX = level.random.nextGaussian() * force;
			double dY = level.random.nextGaussian() * force;
			double dZ = level.random.nextGaussian() * force;
			itemEntity.setDeltaMovement(dX, dY, dZ);
			level.addFreshEntity(itemEntity);
		}
	}

	private void spawnEffectCloud(Level level, LivingEntity livingEntity) {
		Collection<MobEffectInstance> activeEffects = livingEntity.getActiveEffects();
		if (activeEffects.isEmpty()) return;

		AreaEffectCloud effectCloud = new AreaEffectCloud(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
		effectCloud.setRadius(2.5f);
		effectCloud.setRadiusOnUse(-0.5f);
		effectCloud.setWaitTime(10);
		effectCloud.setDuration(effectCloud.getDuration() / 2);
		effectCloud.setRadiusPerTick(-effectCloud.getRadius() / effectCloud.getDuration());

		for (MobEffectInstance mobeffectinstance : activeEffects) {
			effectCloud.addEffect(new MobEffectInstance(mobeffectinstance));
		}

		level.addFreshEntity(effectCloud);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

}
