package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.BiomancyMod;
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
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.EquipmentSlot;
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

	String LAST_USE_TIMESTAMP_KEY = "last_use_timestamp";

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

	protected long getLastUseTimestamp(ItemStack stack) {
		return stack.getOrCreateTag().getLong(LAST_USE_TIMESTAMP_KEY);
	}

	protected void setLastUseTimestamp(ItemStack stack, long timestamp) {
		stack.getOrCreateTag().putLong(LAST_USE_TIMESTAMP_KEY, timestamp);
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		broadcastAnimation(level, shooter, projectileWeapon, Animations.SHOOT);
		super.shoot(level, shooter, usedHand, projectileWeapon);
		setLastUseTimestamp(projectileWeapon, level.getGameTime());
	}

	@Override
	public InteractionResultHolder<ItemStack> useInMeleeMode(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
		if (level instanceof ServerLevel serverLevel) {
			if (getAmmo(stack) > 1 && !Abilities.ACID_COAT.isActive(stack)) {
				consumeAmmo(player, stack, 1);
				Abilities.ACID_COAT.setActive(serverLevel, stack, player);
				broadcastAnimation(serverLevel, player, stack, Animations.COAT_BLADES);
				setLastUseTimestamp(stack, serverLevel.getGameTime());
			}
		}

		return InteractionResultHolder.fail(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (!(entity instanceof LivingEntity shooter)) return;

		if (isSelected) {
			Abilities.ACID_COAT.tick(serverLevel, stack, shooter);

			if (getGunState(stack) == GunState.NONE && !Abilities.ACID_COAT.isActive(stack) && canReload(stack, shooter)) {
				startReload(stack, serverLevel, shooter);
				return;
			}
		}

		super.inventoryTick(stack, level, entity, slotId, isSelected);
	}

	@Override
	public boolean canReload(ItemStack stack, LivingEntity shooter) {
		long elapsedTime = shooter.level().getGameTime() - getLastUseTimestamp(stack);
		return elapsedTime > 5 * 20 && getAmmo(stack) < getMaxAmmo(stack) && stack.getDamageValue() < stack.getMaxDamage() - 5;
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
		if (attacker.level().isClientSide) return;
		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return;

		if (Abilities.ACID_COAT.isActive(stack)) {
			target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 4 * 20, 1));
		}
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level().isClientSide) return super.hurtEnemy(stack, target, attacker);

		setLastUseTimestamp(stack, attacker.level().getGameTime());

		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return super.hurtEnemy(stack, target, attacker);

		if (Abilities.ACID_COAT.isActive(stack)) {
			boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
			if (isFullAttackStrength) {
				playSwipeFX(attacker);
				target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE.get(), 2 * 20, 0));
				target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 4 * 20, 0));
			}
		}

		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public void onChangeGunbladeMode(ServerLevel level, LivingEntity shooter, ItemStack stack) {
		Abilities.ACID_COAT.cancel(level, stack, shooter);
		setLastUseTimestamp(stack, level.getGameTime());

		SoundEvent soundEvent = GunbladeMode.from(stack) == GunbladeMode.MELEE ? ModSoundEvents.FLESHKIN_BECOME_DORMANT.get() : ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get();
		playSFX(level, shooter, soundEvent);
	}

	@Override
	public void onReloadTick(ItemStack stack, ServerLevel level, LivingEntity shooter, long elapsedTime) {
		//if (elapsedTime % 20L == 0L) playSFX(level, shooter, SoundEvents.GENERIC_EAT);
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
		stack.hurtAndBreak(5, shooter, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		playSFX(level, shooter, SoundEvents.PLAYER_BURP);
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		return !Abilities.ACID_COAT.isActive(stack) ? displayName : ComponentUtil.mutable().append(displayName).append(" (").append(ComponentUtil.translatable(Abilities.ACID_COAT.getTranslationKey())).append(")");
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.emptyLine());

		if (GunbladeMode.from(stack) == GunbladeMode.MELEE) {
			Abilities.ACID_COAT.appendAbilityDescription(stack, tooltip);
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

	public interface ItemAbility {
		String name();

		void setActive(ServerLevel level, ItemStack stack, LivingEntity itemOwner);

		boolean isActive(ItemStack stack);

		void tick(Level level, ItemStack stack, LivingEntity itemOwner);

		void cancel(ServerLevel level, ItemStack stack, LivingEntity itemOwner);

		default String getTranslationKey() {
			return TextComponentUtil.getTranslationKey("ability", name());
		}

		default void appendAbilityDescription(ItemStack stack, List<Component> components) {
			String translationKey = getTranslationKey();
			components.add(ComponentUtil.translatable(translationKey).withStyle(TextStyles.GRAY));
			components.add(ComponentUtil.literal(" ").append(ComponentUtil.translatable(translationKey + ".desc")).withStyle(TextStyles.DARK_GRAY));
		}
	}

	protected static final class Animations {
		static final String MAIN_CONTROLLER = "main";
		static final String ACID_COAT_CONTROLLER = "acid_blades";

		static final RawAnimation IDLE_RANGED = RawAnimation.begin().thenPlay("idle_ranged");
		static final RawAnimation IDLE_MELEE = RawAnimation.begin().thenPlay("idle_melee");
		static final RawAnimation RANGED_TO_MELEE = RawAnimation.begin().thenPlay("ranged_to_melee").thenPlay("idle_melee");
		static final RawAnimation MELEE_TO_RANGED = RawAnimation.begin().thenPlay("melee_to_ranged").thenPlay("idle_ranged");
		static final RawAnimation COATED_BLADES = RawAnimation.begin().thenPlay("coated_blades");
		static final RawAnimation UNCOATED_BLADES = RawAnimation.begin().thenPlay("uncoated_blades");

		private static final List<TriggerableAnimation> TRIGGERABLE_ANIMATIONS = new ArrayList<>();
		static final TriggerableAnimation SHOOT = register(MAIN_CONTROLLER, "shoot", RawAnimation.begin().thenPlay("shoot"));
		static final TriggerableAnimation COAT_BLADES = register(MAIN_CONTROLLER, "coat_blades", RawAnimation.begin().thenPlay("coat_blades"));

		private Animations() {}

		static <T extends CausticGunbladeItem> PlayState handleMainAnimations(AnimationState<T> state) {

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

		static <T extends CausticGunbladeItem> PlayState handleAcidCoatAnimations(AnimationState<T> state) {
			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			boolean hasCoatedBlades = Abilities.ACID_COAT.isActive(itemStack);
			return state.setAndContinue(hasCoatedBlades ? Animations.COATED_BLADES : Animations.UNCOATED_BLADES);
		}

		static void registerControllers(CausticGunbladeItem animatable, AnimatableManager.ControllerRegistrar controllers) {
			AnimationController<CausticGunbladeItem> mainController = new AnimationController<>(animatable, MAIN_CONTROLLER, 0, Animations::handleMainAnimations);
			Animations.registerTriggerableAnimations(mainController);
			controllers.add(mainController);

			AnimationController<CausticGunbladeItem> acidBladesController = new AnimationController<>(animatable, ACID_COAT_CONTROLLER, 0, Animations::handleAcidCoatAnimations);
			Animations.registerTriggerableAnimations(acidBladesController);
			controllers.add(acidBladesController);
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

	protected static final class Abilities {
		public static final ItemAbility ACID_COAT = new ItemAbility() {
			static final String NAME = "acid_coat";
			static final String KEY = BiomancyMod.createRLString(NAME);
			static final String TIMESTAMP_KEY = "timestamp";

			@Override
			public String name() {
				return NAME;
			}

			@Override
			public boolean isActive(ItemStack stack) {
				CompoundTag tag = stack.getTagElement(KEY);
				return tag != null;
			}

			@Override
			public void setActive(ServerLevel level, ItemStack stack, LivingEntity itemOwner) {
				CompoundTag tag = stack.getOrCreateTagElement(KEY);
				tag.putLong(TIMESTAMP_KEY, level.getGameTime());
			}

			@Override
			public void tick(Level level, ItemStack stack, LivingEntity itemOwner) {
				CompoundTag tag = stack.getTagElement(KEY);
				if (tag == null) return;

				if (level.getGameTime() - tag.getLong(TIMESTAMP_KEY) > 10 * 20) {
					stack.removeTagKey(KEY);
				}
			}

			@Override
			public void cancel(ServerLevel level, ItemStack stack, LivingEntity itemOwner) {
				stack.removeTagKey(KEY);
			}
		};
	}

}
