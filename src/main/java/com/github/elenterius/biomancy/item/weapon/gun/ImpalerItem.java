package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.client.render.item.impaler.ImpalerRenderer;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.client.ModArmPoses;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ImpalerItem extends GunItem implements ItemTooltipStyleProvider, GeoItem {

	public static final Predicate<ItemStack> AMMO_PREDICATE = stack -> stack.getItem() == ModItems.NUTRIENT_PASTE.get();
	protected static final UUID BASE_MOVEMENT_SPEED_UUID = UUID.fromString("efc325ad-c747-4c0e-80c2-f3f0f4261e91");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public ImpalerItem(Properties properties) {
		super(properties,
				GunProperties.builder()
						.shootBehavior(GunProperties.ShootBehavior.ON_FULL_CHARGE)
						.timeBetweenShots(2 * 20)
						//						.accuracy(0.5f)
						//						.damageModifier(15f)
						.maxAmmo(1).reloadDuration(10 * 20).autoReload()
						.build(),
				ModProjectiles.IMPALER_PROJECTILE);

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.5f, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(BASE_MOVEMENT_SPEED_UUID, "Weapon modifier", -0.25f, AttributeModifier.Operation.MULTIPLY_BASE));
		defaultModifiers = builder.build();

		GeoItem.registerSyncedAnimatable(this);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot.getType() == EquipmentSlot.Type.HAND ? defaultModifiers : ImmutableMultimap.of();
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {
		if (elapsedTime % 20L == 0L) playSFX(level, shooter, ModSoundEvents.FLESHKIN_EAT.get());
	}

	@Override
	public void onReloadStarted(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get());
	}

	@Override
	public void onReloadCanceled(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_BREAK.get());
	}

	@Override
	public void onReloadStopped(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_NO.get());
	}

	@Override
	public void onReloadFinished(ItemStack stack, ServerLevel level, LivingEntity shooter) {
		playSFX(level, shooter, ModSoundEvents.FLESHKIN_BURP.get());
	}

	@Override
	public int getAmmoReloadCost() {
		return 4;
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return AMMO_PREDICATE;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 32;
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		broadcastAnimation(level, shooter, projectileWeapon, Animations.SHOOT);
		super.shoot(level, shooter, usedHand, projectileWeapon);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private ImpalerRenderer renderer = null;

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (renderer == null) {
					renderer = new ImpalerRenderer();
				}
				return renderer;
			}

			@Override
			public @Nullable HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				//	getUseAnimation(ItemStack stack) needs to return NONE or CUSTOM for this method to be called
				return ModArmPoses.HOLD_AND_AIM_GUN_TWO_HANDED;
			}

			@Override
			public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
				if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && isHandPartOfArm(player, arm, player.getUsedItemHand())) {
					float elapsedDuration = (float) itemInHand.getUseDuration() - ((float) player.getUseItemRemainingTicks() - partialTick + 1f);
					float aimProgress = elapsedDuration / 2.5f;
					if (aimProgress > 1f) {
						aimProgress = 1f;
					}

					applyArmTransform(poseStack, arm, aimProgress, player.tickCount + partialTick);
					return true;
				}

				if (!player.isAutoSpinAttack() && !player.swinging) {
					applyArmTransform(poseStack, arm, 0f, player.tickCount + partialTick);
					return true;
				}

				return false;
			}

			/// workaround for forge not providing the interaction hand to the method
			private static boolean isHandPartOfArm(LocalPlayer player, HumanoidArm arm, InteractionHand hand) {
				InteractionHand handOfCurrentArm = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
				return hand == handOfCurrentArm;
			}

			private static void applyArmTransform(PoseStack poseStack, HumanoidArm arm, float progress, float ticks) {
				float direction = arm == HumanoidArm.RIGHT ? 1f : -1f;
				float invProgress = 1f - progress;
				//				float yOffset = 0.1f * -0.6f;
				poseStack.mulPose(Axis.YP.rotationDegrees(10f * invProgress + direction * Mth.cos(ticks * 0.09f) * 1f));
				poseStack.mulPose(Axis.XP.rotationDegrees(-15f * invProgress + direction * Mth.sin(ticks * 0.067f) * 1f));
				poseStack.translate(0.56f * direction, -0.52f, -0.72f); //align item to "item holding position of hand" on screen
			}
		});
	}

	protected void broadcastAnimation(ServerLevel level, Entity relatedEntity, ItemStack stack, TriggerableAnimation animation) {
		triggerAnim(relatedEntity, GeoItem.getOrAssignId(stack, level), animation.controller(), animation.name());
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		Animations.registerControllers(this, controllers);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static final class Animations {
		static final String MAIN_CONTROLLER = "main";

		private static final List<TriggerableAnimation> TRIGGERABLE_ANIMATIONS = new ArrayList<>();
		static final TriggerableAnimation SHOOT = register(MAIN_CONTROLLER, "shoot", RawAnimation.begin().thenPlay("barrel_recoil"));
		static final RawAnimation CHARGE_UP_SHOT = RawAnimation.begin().thenPlay("charging_shot");
		static final RawAnimation NO_PROJECTILE = RawAnimation.begin().thenPlay("no_projectile");
		static final RawAnimation GROW_PROJECTILE = RawAnimation.begin().thenPlay("grow_projectile");

		private Animations() {}

		static void registerControllers(ImpalerItem animatable, AnimatableManager.ControllerRegistrar controllers) {
			AnimationController<ImpalerItem> mainController = new AnimationController<>(animatable, MAIN_CONTROLLER, state -> PlayState.STOP);
			registerTriggerableAnimations(mainController);
			controllers.add(mainController);

			controllers.add(new AnimationController<>(animatable, "charge_up", state -> {
				ImpalerItem impalerItem = state.getAnimatable();
				ItemStack stack = state.getData(DataTickets.ITEMSTACK);

				if (impalerItem.getGunState(stack) == GunState.SHOOTING) {
					return state.setAndContinue(CHARGE_UP_SHOT);
				}

				return PlayState.STOP;
			}));

			controllers.add(new AnimationController<>(animatable, "projectile", state -> {
				ImpalerItem impalerItem = state.getAnimatable();
				ItemStack stack = state.getData(DataTickets.ITEMSTACK);

				if (impalerItem.getGunState(stack) == GunState.RELOADING) {
					return state.setAndContinue(GROW_PROJECTILE);
				}

				if (!impalerItem.hasAmmo(stack)) {
					return state.setAndContinue(NO_PROJECTILE);
				}

				return PlayState.STOP; //show projectile
			}));
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
