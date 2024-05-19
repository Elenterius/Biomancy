package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.client.render.item.caustic_gunblade.CausticGunbladeRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.item.CriticalHitListener;
import com.github.elenterius.biomancy.item.ItemAttackDamageSourceProvider;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.weapon.BladeProperties;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CausticGunbladeItem extends GunbladeItem implements CriticalHitListener, ItemAttackDamageSourceProvider, ItemTooltipStyleProvider, GeoItem {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public CausticGunbladeItem(Properties itemProperties) {
		super(itemProperties,
				BladeProperties.builder().attackDamage(6).attackSpeed(1).build(),
				GunProperties.builder()
						.fireRate(0.5f)
						.maxAmmo(10).reloadDuration(10 * 20).autoReload(true)
						.build(),
				ModProjectiles.ACID_BLOB);

		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	private static void playSwipeFX(LivingEntity attacker) {
		attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_STRONG.get(), attacker.getSoundSource(), 1f, 1f + attacker.getRandom().nextFloat() * 0.5f);
		if (attacker.level() instanceof ServerLevel serverLevel) {
			double xOffset = -Mth.sin(attacker.getYRot() * Mth.DEG_TO_RAD);
			double zOffset = Mth.cos(attacker.getYRot() * Mth.DEG_TO_RAD);
			serverLevel.sendParticles(ModParticleTypes.CORROSIVE_SWIPE_ATTACK.get(), attacker.getX() + xOffset, attacker.getY(0.52f), attacker.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
		}
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return itemStack -> false;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 16;
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		broadcastAnimation(level, shooter, projectileWeapon, Animations.SHOOT);
		super.shoot(level, shooter, usedHand, projectileWeapon);
	}

	@Override
	public InteractionResultHolder<ItemStack> useInMeleeMode(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
		if (level instanceof ServerLevel serverLevel) {
			broadcastAnimation(serverLevel, player, stack, Animations.COAT_BLADES);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (!(entity instanceof LivingEntity shooter)) return;

		GunState gunState = getGunState(stack);
		if (gunState == GunState.NONE && serverLevel.getGameTime() - getShootTimestamp(stack) > 5 * 20 && canReload(stack, shooter)) {
			startReload(stack, serverLevel, shooter);
		}

		super.inventoryTick(stack, level, entity, slotId, isSelected);
	}

	@Override
	public boolean canReload(ItemStack stack, LivingEntity shooter) {
		return getAmmo(stack) < getMaxAmmo(stack);
	}

	@Override
	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		return new ItemStack(Items.ARROW, 64);
	}

	@Override
	public @Nullable DamageSource getDamageSource(ItemStack stack, Entity target, LivingEntity attacker) {
		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return null;

		return ModDamageSources.acid(attacker.level(), attacker);
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return;

		int seconds = 4;
		target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE.get(), seconds * 20, 0));
		target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), seconds * 20, 1));
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level().isClientSide) return super.hurtEnemy(stack, target, attacker);
		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return super.hurtEnemy(stack, target, attacker);

		boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
		if (isFullAttackStrength) {
			playSwipeFX(attacker);
			target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 4 * 20, 0));
		}

		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public void onChangeGunbladeMode(ServerLevel level, LivingEntity shooter, ItemStack stack) {
		SoundEvent soundEvent = GunbladeMode.from(stack) == GunbladeMode.MELEE ? ModSoundEvents.FLESHKIN_BECOME_DORMANT.get() : ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get();
		playSFX(level, shooter, soundEvent);
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {
		if (elapsedTime % 20L == 0L) playSFX(level, shooter, SoundEvents.GENERIC_EAT);
	}

	@Override
	public void onReloadStarted(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.GENERIC_EAT);
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.TROPICAL_FISH_FLOP);
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.TROPICAL_FISH_FLOP);
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, SoundEvents.PLAYER_BURP);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.emptyLine());

		if (GunbladeMode.from(stack) == GunbladeMode.MELEE) {
			tooltip.add(TextComponentUtil.getTooltipText("ability.shredding_strike").withStyle(ChatFormatting.GRAY));
			tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getTooltipText("ability.shredding_strike.desc")).withStyle(ChatFormatting.DARK_GRAY));
			tooltip.add(TextComponentUtil.getTooltipText("ability.corrosive_proc").withStyle(ChatFormatting.GRAY));
			tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getTooltipText("ability.corrosive_proc.desc")).withStyle(ChatFormatting.DARK_GRAY));
		}
		else {
			appendGunStats(stack, tooltip);
		}

		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.switch_mode")).withStyle(TextStyles.DARK_GRAY));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final CausticGunbladeRenderer renderer = new CausticGunbladeRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}

			@Override
			public @Nullable HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				if (entityLiving.getUseItemRemainingTicks() > 0) {
					return HumanoidModel.ArmPose.CROSSBOW_HOLD;
				}
				return null;
			}
		});
	}

	protected void broadcastAnimation(ServerLevel level, Entity relatedEntity, ItemStack stack, TriggerableAnimation animation) {
		long id = GeoItem.getOrAssignId(stack, level);
		triggerAnim(relatedEntity, id, animation.controller(), animation.name());
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		Animations.registerControllers(this, controllers);
	}

	protected static class Animations {
		private static final List<TriggerableAnimation> TRIGGERABLE_ANIMATIONS = new ArrayList<>();
		static final String MAIN_CONTROLLER = "main";

		static final TriggerableAnimation SHOOT = register(MAIN_CONTROLLER, "shoot", RawAnimation.begin().thenPlay("shoot"));
		static final TriggerableAnimation COAT_BLADES = register(MAIN_CONTROLLER, "coat_blades", RawAnimation.begin().thenPlay("coat_blades"));
		static final RawAnimation IDLE_RANGED = RawAnimation.begin().thenPlay("idle_ranged");
		static final RawAnimation IDLE_MELEE = RawAnimation.begin().thenPlay("idle_melee");
		static final RawAnimation RANGED_TO_MELEE = RawAnimation.begin().thenPlay("ranged_to_melee").thenPlay("idle_melee");
		static final RawAnimation MELEE_TO_RANGED = RawAnimation.begin().thenPlay("melee_to_ranged").thenPlay("idle_ranged");
		;

		private Animations() {}

		static <T extends CausticGunbladeItem> PlayState handleAnimationState(AnimationState<T> state) {

			if (state.getController().isPlayingTriggeredAnimation()) return PlayState.CONTINUE;

			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			GunbladeMode gunbladeMode = GunbladeMode.from(itemStack);

			if (gunbladeMode == GunbladeMode.MELEE) {
				return state.setAndContinue(Animations.RANGED_TO_MELEE);
			}
			else {
				return state.setAndContinue(Animations.MELEE_TO_RANGED);
			}
		}

		static void registerControllers(CausticGunbladeItem animatable, AnimatableManager.ControllerRegistrar controllers) {
			AnimationController<CausticGunbladeItem> controller = new AnimationController<>(animatable, MAIN_CONTROLLER, 1, Animations::handleAnimationState);
			Animations.registerTriggerableAnimations(controller);
			controllers.add(controller);
		}

		private static TriggerableAnimation register(String controller, String name, RawAnimation rawAnimation) {
			TriggerableAnimation animation = new TriggerableAnimation(controller, name, rawAnimation);
			TRIGGERABLE_ANIMATIONS.add(animation);
			return animation;
		}

		private static void registerTriggerableAnimations(AnimationController<?> controller) {
			for (TriggerableAnimation animation : TRIGGERABLE_ANIMATIONS) {
				if (animation.controller().equals(controller.getName())) {
					controller.triggerableAnim(animation.name(), animation.rawAnimation());
				}
			}
		}
	}

}
