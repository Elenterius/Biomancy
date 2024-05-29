package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.livingtool.SimpleLivingTool;
import com.github.elenterius.biomancy.client.render.item.caustic_gunblade.CausticGunbladeRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.item.CriticalHitListener;
import com.github.elenterius.biomancy.item.ItemAttackDamageSourceProvider;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.weapon.BladeProperties;
import com.github.elenterius.biomancy.styles.ColorStyles;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import com.github.elenterius.biomancy.util.function.FloatOperator;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
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
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
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

public class CausticGunbladeItem extends GunbladeItem implements SimpleLivingTool, CriticalHitListener, ItemAttackDamageSourceProvider, ItemTooltipStyleProvider, GeoItem {

	protected final Multimap<Attribute, AttributeModifier> disabledBladeModifiers;
	protected final Multimap<Attribute, AttributeModifier> disabledGunModifiers;
	private final int maxNutrients;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	String LAST_USE_TIMESTAMP_KEY = "last_use_timestamp";

	public CausticGunbladeItem(int maxNutrients, Properties itemProperties) {
		super(itemProperties,
				BladeProperties.builder().attackDamage(6).attackSpeed(1.2f).build(),
				GunProperties.builder()
						.fireRate(0.5f)
						.maxAmmo(10).reloadDuration(10 * 20).autoReload(true)
						.build(),
				ModProjectiles.ACID_BLOB);

		this.maxNutrients = maxNutrients;

		disabledBladeModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder().putAll(Attributes.ATTACK_SPEED, defaultBladeModifiers.get(Attributes.ATTACK_SPEED)).build();
		disabledGunModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder().putAll(Attributes.ATTACK_SPEED, defaultGunModifiers.get(Attributes.ATTACK_SPEED)).build();

		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			boolean isMeleeMode = GunbladeMode.from(stack).isBlade();

			if (hasNutrients(stack)) {
				return isMeleeMode ? defaultBladeModifiers : defaultGunModifiers;
			}
			return isMeleeMode ? disabledBladeModifiers : disabledGunModifiers;
		}

		return ImmutableMultimap.of();
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

		configuredProjectile.shoot(level, shooter,
				FloatOperator.IDENTITY,
				baseDamage -> modifyProjectileDamage(baseDamage, projectileWeapon),
				baseKnockBack -> modifyProjectileKnockBack(baseKnockBack, projectileWeapon),
				baseInaccuracy -> modifyProjectileInaccuracy(baseInaccuracy, projectileWeapon));

		consumeAmmo(shooter, projectileWeapon, 1);
		consumeNutrients(projectileWeapon, 1);

		setLastUseTimestamp(projectileWeapon, level.getGameTime());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!hasNutrients(stack)) {
			if (level.isClientSide()) {
				player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
				playSound(player, ModSoundEvents.FLESHKIN_NO.get());
			}
			return InteractionResultHolder.fail(stack);
		}

		return super.use(level, player, hand);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (getGunState(stack) != GunState.SHOOTING) return;

		if (!hasNutrients(stack)) {
			shooter.releaseUsingItem();
			stopShooting(stack, serverLevel, shooter);
		}
		else {
			super.onUseTick(level, shooter, stack, remainingUseDuration);
		}
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
		return elapsedTime > 5 * 20 && getAmmo(stack) < getMaxAmmo(stack) && getNutrients(stack) >= getAmmoReloadCost();
	}

	@Override
	public int getAmmoReloadCost() {
		return 5;
	}

	@Override
	public ItemStack findAmmoInInv(ItemStack stack, LivingEntity shooter) {
		return new ItemStack(Items.ARROW, 64);
	}

	@Override
	public @Nullable DamageSource getDamageSource(ItemStack stack, Entity target, LivingEntity attacker) {
		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return null;
		if (!Abilities.ACID_COAT.isActive(stack)) return null;

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
		if (attacker.level().isClientSide) return true;

		setLastUseTimestamp(stack, attacker.level().getGameTime());

		consumeNutrients(stack, 2);

		if (GunbladeMode.from(stack) != GunbladeMode.MELEE) return true;

		if (Abilities.ACID_COAT.isActive(stack)) {
			boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
			if (isFullAttackStrength) {
				playSwipeFX(attacker);
				target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE.get(), 2 * 20, 0));
				target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 4 * 20, 0));
			}
		}

		return true;
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
		consumeNutrients(stack, getAmmoReloadCost());
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

		appendLivingToolTooltip(stack, tooltip);
		tooltip.add(ComponentUtil.emptyLine());

		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.switch_mode")).withStyle(TextStyles.DARK_GRAY));

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}

	@Override
	public int getMaxNutrients(ItemStack stack) {
		return maxNutrients;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (handleOverrideStackedOnOther(stack, slot, action, player)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (handleOverrideOtherStackedOnMe(stack, other, slot, action, player, access)) {
			playSound(player, ModSoundEvents.FLESHKIN_EAT.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return super.canPerformAction(stack, toolAction) && hasNutrients(stack);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return isValidEnchantment(stack, enchantment) && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getNutrients(stack) < getMaxNutrients(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(getNutrientsPct(stack) * 13f);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return ColorStyles.NUTRIENTS_FUEL_BAR;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		//do nothing
	}

	@Override
	public int getDamage(ItemStack stack) {
		return 0;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return 0;
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	protected void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
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
				if (GunbladeMode.from(itemStack) == GunbladeMode.RANGED) {
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
			components.addAll(ClientTextUtil.splitLinesByNewLine(ComponentUtil.translatable(translationKey + ".desc").withStyle(TextStyles.DARK_GRAY)));
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

	protected static final class Animations {
		static final String MAIN_CONTROLLER = "main";
		static final String ACID_COAT_CONTROLLER = "acid_blades";
		static final String AMMO_CONTROLLER = "ammo";

		static final RawAnimation IDLE_RANGED = RawAnimation.begin().thenPlay("idle_ranged");
		static final RawAnimation IDLE_MELEE = RawAnimation.begin().thenPlay("idle_melee");
		static final RawAnimation RANGED_TO_MELEE = RawAnimation.begin().thenPlay("ranged_to_melee").thenPlay("idle_melee");
		static final RawAnimation MELEE_TO_RANGED = RawAnimation.begin().thenPlay("melee_to_ranged").thenPlay("idle_ranged");
		static final RawAnimation COATED_BLADES = RawAnimation.begin().thenPlay("coated_blades");
		static final RawAnimation UNCOATED_BLADES = RawAnimation.begin().thenPlay("uncoated_blades");
		static final RawAnimation FULL_AMMO = RawAnimation.begin().thenPlay("full_ammo");
		static final RawAnimation HALF_AMMO = RawAnimation.begin().thenPlay("half_ammo");
		static final RawAnimation NO_AMMO = RawAnimation.begin().thenPlay("no_ammo");

		private static final List<TriggerableAnimation> TRIGGERABLE_ANIMATIONS = new ArrayList<>();
		static final TriggerableAnimation SHOOT = register(MAIN_CONTROLLER, "shoot", RawAnimation.begin().thenPlay("shoot"));
		static final TriggerableAnimation COAT_BLADES = register(MAIN_CONTROLLER, "coat_blades", RawAnimation.begin().thenPlay("coat_blades"));

		private Animations() {}

		static <T extends CausticGunbladeItem> PlayState handleMain(AnimationState<T> state) {

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

		static <T extends CausticGunbladeItem> PlayState handleAcidCoat(AnimationState<T> state) {
			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			boolean hasCoatedBlades = Abilities.ACID_COAT.isActive(itemStack);
			return state.setAndContinue(hasCoatedBlades ? Animations.COATED_BLADES : Animations.UNCOATED_BLADES);
		}

		static <T extends CausticGunbladeItem> PlayState handleAmmo(AnimationState<T> state) {
			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			CausticGunbladeItem item = (CausticGunbladeItem) itemStack.getItem();

			int ammo = item.getAmmo(itemStack);
			int maxAmmo = item.getMaxAmmo(itemStack);

			if (ammo <= 0) {
				return state.setAndContinue(Animations.NO_AMMO);
			}

			return state.setAndContinue(ammo < maxAmmo ? Animations.HALF_AMMO : Animations.FULL_AMMO);
		}

		static void registerControllers(CausticGunbladeItem animatable, AnimatableManager.ControllerRegistrar controllers) {
			AnimationController<CausticGunbladeItem> mainController = new AnimationController<>(animatable, MAIN_CONTROLLER, 0, Animations::handleMain);
			Animations.registerTriggerableAnimations(mainController);
			controllers.add(mainController);

			controllers.add(new AnimationController<>(animatable, ACID_COAT_CONTROLLER, 0, Animations::handleAcidCoat));
			controllers.add(new AnimationController<>(animatable, AMMO_CONTROLLER, 0, Animations::handleAmmo));
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
