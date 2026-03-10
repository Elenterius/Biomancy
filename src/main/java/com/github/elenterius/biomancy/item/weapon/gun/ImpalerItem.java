package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.item.impaler.ImpalerRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.projectile.ImpalerProjectile;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.client.ModArmPoses;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import com.github.elenterius.biomancy.util.shooting.*;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
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

public class ImpalerItem extends LivingGunItem<ImpalerProjectile> implements ItemTooltipStyleProvider, GeoItem {

	public static final float CHARGE_DURATION = 1.79f + 0.17f; //based on animation length of "charging_shot" + "holding_shot"

	protected static final UUID BASE_MOVEMENT_SPEED_UUID = UUID.fromString("efc325ad-c747-4c0e-80c2-f3f0f4261e91");

	protected static final ResourceLocation CROSSHAIR = BiomancyMod.rl("textures/gui/impaler_crosshair.png");
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public ImpalerItem(int maxNutrients, Properties properties) {
		super(maxNutrients, properties,
				GunProperties.<ImpalerProjectile>builder()
						.shootBehavior(ShootBehavior.ON_RELEASE_INSTANT)
						.timeBetweenShots(Mth.ceil(CHARGE_DURATION * 20f))
						.damage(24f).accuracy(0.98f).spreadBias(SpreadBias.CENTER_HEAVY)
						.maxAmmo(1).reloadDuration(3 * 20).autoReload()
						.projectile(ModEntityTypes.IMPALER_PROJECTILE).velocity(2.85f)
						.localOffset(new Vector3f(0.25f, -0.2f, 0.5f))
						.sounds(GUN_SOUNDS.withShoot(ModSoundEvents.IMPALER_SHOOT.get()))
						.build()
		);

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.5f, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(BASE_MOVEMENT_SPEED_UUID, "Weapon modifier", -0.125f, AttributeModifier.Operation.MULTIPLY_BASE));
		defaultModifiers = builder.build();

		GeoItem.registerSyncedAnimatable(this);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot.getType() == EquipmentSlot.Type.HAND ? defaultModifiers : ImmutableMultimap.of();
	}

	@Override
	public int getReloadCost(ItemStack stack) {
		return 5;
	}

	@Override
	public int getDurabilityCost(ItemStack projectileWeapon) {
		return 35;
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		super.onUseTick(level, shooter, stack, remainingUseDuration);

		if (level.isClientSide) return;
		if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

		int elapsedTime = getUseDuration(stack) - remainingUseDuration;
		int delayBetweenShots = getDelayBetweenShots(stack);

		if (elapsedTime % delayBetweenShots == 0) {
			GunSounds.play(level, shooter, ModSoundEvents.IMPALER_CHARGE.get());
		}
	}

	@Override
	public float getProjectileVelocity(ItemStack stack) {
		return gunProperties.velocity() + 0.12f * stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
	}

	@Override
	public float getProjectileDamage(ItemStack stack) {
		return gunProperties.damage() + stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		float elapsedDuration = (float) projectileWeapon.getUseDuration() - ((float) shooter.getUseItemRemainingTicks());
		float maxChargeDuration = getDelayBetweenShots(projectileWeapon);
		float chargePercentage = Mth.clamp(elapsedDuration / maxChargeDuration, 0.1f, 1f);
		float velocity = getProjectileVelocity(projectileWeapon) * chargePercentage;

		boolean success = ProjectileUtil.shoot(level, shooter,
				velocity,
				getProjectileDamage(projectileWeapon),
				getProjectileKnockBack(projectileWeapon),
				getAccuracy(projectileWeapon),
				gunProperties.spreadBias(),
				gunProperties.localOffset(),
				gunProperties.projectileType().get(),
				getProjectileCount(projectileWeapon),
				projectile -> projectile.setPierceLevel(projectileWeapon.getEnchantmentLevel(Enchantments.PIERCING)));

		if (!success) return;

		broadcastAnimation(level, shooter, projectileWeapon, Animations.SHOOT);
		gunProperties.sounds().playShoot(level, shooter, 1.5f, 0.8f + shooter.getRandom().nextFloat() * 0.5f);

		projectileWeapon.hurtAndBreak(1, shooter, entity -> entity.broadcastBreakEvent(usedHand));
		consumeAmmo(shooter, projectileWeapon, getAmmoCost(projectileWeapon));
		consumeNutrients(projectileWeapon, getDurabilityCost(projectileWeapon));

		boolean isAnchored = shooter.onGround() && shooter.isCrouching();
		double reduction = isAnchored ? 0.25d : 0.5d;

		Vec3 recoil = shooter.getLookAngle().normalize().scale(-1d).scale(velocity * reduction);
		shooter.push(recoil.x, recoil.y, recoil.z); //sets hasImpulse to true
		shooter.fallDistance = 0f;

		// "self" damage
		//DamageSource damageSource = level.damageSources().explosion(shooter, shooter);
		//shooter.hurt(damageSource, velocity * 0.5f);

		if (!shooter.hurtMarked && shooter instanceof ServerPlayer serverPlayer) {
			// Important:
			// hasImpulse only broadcasts entity motion to OTHER players that track the entity
			// instead of using hurtMarked we send the motion packet to the client of the shooter ourselves
			serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
			//level.getChunkSource().broadcastAndSend(serverPlayer, new ClientboundSetEntityMotionPacket(serverPlayer)); // not necessary because hasImpulse already broadcasts
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment == Enchantments.PIERCING) return true;
		if (enchantment == Enchantments.PUNCH_ARROWS) return false;
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.EMPTY_LINE);
		super.appendHoverText(stack, level, tooltip, isAdvanced);
	}

	@Override
	public void appendGunStats(ItemStack stack, List<Component> tooltip) {
		if (Screen.hasControlDown()) return;
		super.appendGunStats(stack, tooltip);
	}

	@Override
	public int getDefaultTooltipHideFlags(ItemStack stack) {
		return ItemStack.TooltipPart.MODIFIERS.getMask();
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.CUSTOM;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private @Nullable ImpalerRenderer renderer = null;

			/// workaround for forge not providing the interaction hand to the method
			private static boolean isHandPartOfArm(LocalPlayer player, HumanoidArm arm, InteractionHand hand) {
				InteractionHand handOfCurrentArm = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
				return hand == handOfCurrentArm;
			}

			private static void applyArmTransform(PoseStack poseStack, HumanoidArm arm, float progress, float ticks) {
				float direction = arm == HumanoidArm.RIGHT ? 1f : -1f;
				float invProgress = 1f - progress;
				//float yOffset = 0.1f * -0.6f;
				poseStack.mulPose(Axis.YP.rotationDegrees(10f * invProgress + direction * Mth.cos(ticks * 0.09f) * 1f));
				poseStack.mulPose(Axis.XP.rotationDegrees(-15f * invProgress + direction * Mth.sin(ticks * 0.067f) * 1f));
				poseStack.translate(0.56f * direction, -0.52f, -0.72f); //align item to "item holding position of hand" on screen
			}

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (renderer == null) {
					renderer = new ImpalerRenderer();
				}
				return renderer;
			}

			@Override
			public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				//	getUseAnimation(ItemStack stack) needs to return NONE or CUSTOM for this method to be called
				return ModArmPoses.HOLD_AND_AIM_GUN_TWO_HANDED;
			}

			@Override
			public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
				if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && isHandPartOfArm(player, arm, player.getUsedItemHand())) {
					float elapsedDuration = (float) itemInHand.getUseDuration() - ((float) player.getUseItemRemainingTicks() - partialTick + 1f);
					float maxChargeDuration = getDelayBetweenShots(itemInHand);
					float aimProgress = elapsedDuration / maxChargeDuration;
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

			public boolean applyCrosshair(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
				return false;
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

	@Override
	public ResourceLocation getCrosshairTexture(ItemStack stack, Player player) {
		return CROSSHAIR;
	}

	protected static final class Animations {
		static final String MAIN_CONTROLLER = "main";

		private static final List<TriggerableAnimation> TRIGGERABLE_ANIMATIONS = new ArrayList<>();

		static final TriggerableAnimation SHOOT = register(MAIN_CONTROLLER, "shoot", RawAnimation.begin().thenPlay("barrel_recoil"));
		static final RawAnimation CHARGE_UP_SHOT = RawAnimation.begin().thenPlay("charging_shot").thenPlay("holding_shot");
		static final RawAnimation DEFAULT_STATE = RawAnimation.begin().thenPlay("default_state");

		private Animations() {}

		static void registerControllers(ImpalerItem animatable, AnimatableManager.ControllerRegistrar controllers) {
			AnimationController<ImpalerItem> mainController = new AnimationController<>(animatable, MAIN_CONTROLLER, state -> PlayState.STOP);
			registerTriggerableAnimations(mainController);
			controllers.add(mainController);

			controllers.add(new AnimationController<>(animatable, "charging", state -> {
				ImpalerItem impalerItem = state.getAnimatable();
				ItemStack stack = state.getData(DataTickets.ITEMSTACK);

				if (impalerItem.getGunState(stack) == GunState.SHOOTING_OR_CHARGING) {
					return state.setAndContinue(CHARGE_UP_SHOT);
				}

				return state.setAndContinue(DEFAULT_STATE);
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
