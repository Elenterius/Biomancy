package com.github.elenterius.biomancy.item.weapon.gun;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.api.livingtool.SimpleLivingTool;
import com.github.elenterius.biomancy.client.render.item.caustic_gunblade.CausticGunbladeRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.projectile.AcidSprayProjectile;
import com.github.elenterius.biomancy.init.*;
import com.github.elenterius.biomancy.item.CriticalHitListener;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.MeleeDamageSourceProviderItem;
import com.github.elenterius.biomancy.item.weapon.BladeProperties;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.util.animation.TriggerableAnimation;
import com.github.elenterius.biomancy.util.shooting.*;
import com.github.elenterius.geckolibextras.GLibExtras;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
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

public class CausticGunbladeItem extends LivingGunItem<AcidSprayProjectile> implements SimpleLivingTool, CriticalHitListener, MeleeDamageSourceProviderItem, ItemTooltipStyleProvider, GeoItem {

	public static final GunSounds GUN_SOUNDS = new GunSounds(
			null, ModSoundEvents.FLESHKIN_NO.get(),
			SoundEvents.WITCH_DRINK, SoundEvents.WITCH_DRINK,
			SoundEvents.TROPICAL_FISH_FLOP, SoundEvents.TROPICAL_FISH_FLOP, ModSoundEvents.FLESHKIN_BURP.get()
	);
	protected static final ResourceLocation CROSSHAIR = BiomancyMod.rl("textures/gui/gunblade_crosshair.png");
	protected final Multimap<Attribute, AttributeModifier> defaultModifiers;
	protected final Multimap<Attribute, AttributeModifier> disabledModifiers;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public CausticGunbladeItem(int maxNutrients, Properties itemProperties) {
		super(maxNutrients, itemProperties,
				GunProperties.<AcidSprayProjectile>builder()
						.fireRate(20f).damage(0.25f).accuracy(0.98f).spreadBias(SpreadBias.SLIGHTLY_CENTER_HEAVY)
						.maxAmmo(100).reloadDuration(10 * 20).autoReload()
						.projectile(ModEntityTypes.ACID_SPRAY_PROJECTILE).velocity(0.95f)
						.localOffset(new Vector3f(0.25f, -0.2f, 0.55f))
						.sounds(GUN_SOUNDS)
						.build()
		);

		defaultModifiers = createDefaultModifiers(BladeProperties.builder().attackDamage(6).attackSpeed(1.2f).build());
		disabledModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder().putAll(Attributes.ATTACK_SPEED, defaultModifiers.get(Attributes.ATTACK_SPEED)).build();

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

	protected Multimap<Attribute, AttributeModifier> createDefaultModifiers(BladeProperties bladeProperties) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", bladeProperties.attackDamageModifier(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", bladeProperties.attackSpeedModifier(), AttributeModifier.Operation.ADDITION));
		return builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			return hasNutrients(stack) ? defaultModifiers : disabledModifiers;
		}
		return ImmutableMultimap.of();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (Abilities.ACID_REFLUX.isActive(stack)) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("acid_reflux"), true);
			gunProperties.sounds().playLocalFail(level, player);
			return InteractionResultHolder.fail(stack);
		}

		return super.use(level, player, usedHand);
	}

	@Override
	public void onUseTick(Level level, LivingEntity shooter, ItemStack stack, int remainingUseDuration) {
		if (level.isClientSide) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		if (getGunState(stack) != GunState.SHOOTING_OR_CHARGING) return;

		if (Abilities.ACID_REFLUX.isActive(stack)) {
			shooter.releaseUsingItem();
			stopShooting(stack, serverLevel, shooter);
			if (shooter instanceof ServerPlayer player) {
				player.displayClientMessage(TextComponentUtil.getFailureMsgText("acid_reflux"), true);
			}
			return;
		}

		super.onUseTick(level, shooter, stack, remainingUseDuration);
	}

	@Override
	public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand usedHand, ItemStack projectileWeapon) {
		broadcastAnimation(level, shooter, projectileWeapon, Animations.SHOOT);

		super.shoot(level, shooter, usedHand, projectileWeapon);

		boolean hadReflux = Abilities.ACID_REFLUX.isActive(projectileWeapon);
		Abilities.ACID_REFLUX.setActive(level, projectileWeapon, shooter);
		if (!hadReflux && Abilities.ACID_REFLUX.isActive(projectileWeapon)) {
			broadcastAnimation(level, shooter, projectileWeapon, Animations.COAT_BLADES);
			GunSounds.play(level, shooter, ModSoundEvents.FLESHKIN_BECOME_DORMANT.get());
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
			Abilities.ACID_REFLUX.tick(serverLevel, stack, livingEntity);
		}

		super.inventoryTick(stack, level, entity, slotId, isSelected);
	}

	@Override
	public boolean canReload(ItemStack stack, LivingEntity shooter) {
		return !Abilities.ACID_REFLUX.isActive(stack) && super.canReload(stack, shooter);
	}

	@Override
	public int getReloadCost(ItemStack stack) {
		return (getMaxAmmo(stack) - getAmmo(stack)) / 2;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (!hasNutrients(stack)) return false;
		return toolAction != ToolActions.SWORD_SWEEP && ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level().isClientSide) return true;

		if (!MobUtil.isCreativePlayer(attacker)) {
			consumeNutrients(stack, 1);
		}

		if (Abilities.ACID_REFLUX.isActive(stack)) {
			boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
			if (isFullAttackStrength) {
				playSwipeFX(attacker);
				target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE.get(), 20 + 20 / 2, 0));
				target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 4 * 20, 0));
			}
			Abilities.ACID_REFLUX.use(attacker.level(), stack, attacker);
		}

		return true;
	}

	@Override
	public @Nullable DamageSource getMeleeDamageSource(ItemStack stack, Entity target, LivingEntity attacker, float attackStrengthScale) {
		if (!Abilities.ACID_REFLUX.isActive(stack)) return null;

		DamageSource damageSource = ModDamageSources.acid(attacker.level(), attacker);
		if (target.isInvulnerableTo(damageSource)) return null; //use default melee damagesource as fallback
		return damageSource;
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		if (attacker.level().isClientSide) return;

		if (Abilities.ACID_REFLUX.isActive(stack)) {
			target.addEffect(new MobEffectInstance(ModMobEffects.CORROSIVE.get(), 4 * 20, 0));
			target.addEffect(new MobEffectInstance(ModMobEffects.ARMOR_SHRED.get(), 6 * 20, 0));
			Abilities.ACID_REFLUX.use(attacker.level(), stack, attacker);
		}
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (!hasNutrients(stack)) return 1f;
		if (state.is(Blocks.COBWEB)) return 15f;
		return state.is(BlockTags.SWORD_EFFICIENT) ? 1.5f : 1f;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0f && !MobUtil.isCreativePlayer(miningEntity)) {
			consumeNutrients(stack, 2);
		}
		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState state) {
		return state.is(Blocks.COBWEB);
	}

	@Override
	public boolean isValidEnchantment(ItemStack livingTool, Enchantment enchantment) {
		return super.isValidEnchantment(livingTool, enchantment);
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		return !Abilities.ACID_REFLUX.isActive(stack) ? displayName : ComponentUtil.mutable().append(displayName).append(" (").append(ComponentUtil.translatable(Abilities.ACID_REFLUX.getTranslationKey())).append(")");
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));

		tooltip.add(ComponentUtil.EMPTY_LINE);
		appendGunStats(stack, tooltip);

		tooltip.add(ComponentUtil.EMPTY_LINE);
		Abilities.ACID_REFLUX.appendAbilityDescription(stack, tooltip);

		tooltip.add(ComponentUtil.EMPTY_LINE);
		appendLivingToolTooltip(stack, tooltip);

		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getActionText("switch_mode")).withStyle(TextStyles.DARK_GRAY));

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.EMPTY_LINE);
		}
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
			public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				return entityLiving.isUsingItem() ? HumanoidModel.ArmPose.CROSSBOW_HOLD : null;
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

	@Override
	public ResourceLocation getCrosshairTexture(ItemStack stack, Player player) {
		return CROSSHAIR;
	}

	public interface ItemAbility {
		String name();

		void setActive(ServerLevel level, ItemStack stack, LivingEntity itemOwner);

		boolean isActive(ItemStack stack);

		void tick(Level level, ItemStack stack, LivingEntity itemOwner);

		void use(Level level, ItemStack stack, LivingEntity itemOwner);

		void cancel(ServerLevel level, ItemStack stack, LivingEntity itemOwner);

		default String getTranslationKey() {
			return BiomancyMod.translationKey("ability", name());
		}

		default void appendAbilityDescription(ItemStack stack, List<Component> components) {
			String translationKey = getTranslationKey();
			components.add(ComponentUtil.translatable(translationKey).withStyle(TextStyles.GRAY));
			components.addAll(ClientTextUtil.splitLinesByNewLine(ComponentUtil.translatable(translationKey + ".desc").withStyle(TextStyles.DARK_GRAY)));
		}
	}

	protected static final class Abilities {

		public static final ItemAbility ACID_REFLUX = new ItemAbility() {
			static final String NAME = "acid_reflux";
			static final String KEY = BiomancyMod.rlStr(NAME);
			static final String ACID_LEVEL = "acid_level";
			static final String HAS_REFLUX = "has_reflux";
			static final String REFLUX_START_TIME = "reflux_start_time";

			static final byte MAX_ACID_LEVEL = 100;
			static final int RECOVERY_DELAY = SharedConstants.TICKS_PER_SECOND * 10;

			@Override
			public String name() {
				return NAME;
			}

			@Override
			public boolean isActive(ItemStack stack) {
				return stack.getOrCreateTagElement(KEY).getBoolean(HAS_REFLUX);
			}

			@Override
			public void setActive(ServerLevel level, ItemStack stack, LivingEntity itemOwner) {
				CompoundTag tag = stack.getOrCreateTagElement(KEY);

				byte acidLevel = (byte) Math.min(tag.getByte(ACID_LEVEL) + 2, MAX_ACID_LEVEL);
				tag.putByte(ACID_LEVEL, acidLevel);

				if (acidLevel >= MAX_ACID_LEVEL) {
					tag.putLong(REFLUX_START_TIME, level.getGameTime());
					tag.putBoolean(HAS_REFLUX, true);
				}
			}

			@Override
			public void tick(Level level, ItemStack stack, LivingEntity itemOwner) {
				CompoundTag tag = stack.getOrCreateTagElement(KEY);

				if (tag.getBoolean(HAS_REFLUX)) {
					long elapsedTime = level.getGameTime() - tag.getLong(REFLUX_START_TIME);
					if (elapsedTime < RECOVERY_DELAY) return;

					int acidLevel = tag.getByte(ACID_LEVEL) - 1;
					tag.putByte(ACID_LEVEL, (byte) acidLevel);

					if (acidLevel <= 0) {
						tag.putBoolean(HAS_REFLUX, false);
						GunSounds.play(level, itemOwner, ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get());
					}
				}
			}

			@Override
			public void use(Level level, ItemStack stack, LivingEntity itemOwner) {
				CompoundTag tag = stack.getOrCreateTagElement(KEY);

				int uses = tag.getByte(ACID_LEVEL) - 1;

				if (uses > 0) {
					tag.putByte(ACID_LEVEL, (byte) uses);
				}
				else {
					stack.removeTagKey(KEY);
				}
			}

			@Override
			public void cancel(ServerLevel level, ItemStack stack, LivingEntity itemOwner) {
				CompoundTag tag = stack.getOrCreateTagElement(KEY);
				tag.putBoolean(HAS_REFLUX, false);
				tag.putByte(REFLUX_START_TIME, (byte) 0);
				tag.putByte(ACID_LEVEL, (byte) 0);
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
			if (state.getData(GLibExtras.ITEM_HOST_TICKET) instanceof LivingEntity livingEntity) {
				if (livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack) {
					return state.setAndContinue(Animations.MELEE_TO_RANGED);
				}
			}

			return state.setAndContinue(Animations.RANGED_TO_MELEE);
		}

		static <T extends CausticGunbladeItem> PlayState handleAcidCoat(AnimationState<T> state) {
			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			boolean hasCoatedBlades = Abilities.ACID_REFLUX.isActive(itemStack);
			return state.setAndContinue(hasCoatedBlades ? Animations.COATED_BLADES : Animations.UNCOATED_BLADES);
		}

		static <T extends CausticGunbladeItem> PlayState handleAmmo(AnimationState<T> state) {
			ItemStack itemStack = state.getData(DataTickets.ITEMSTACK);
			Gun<?> gun = (Gun<?>) itemStack.getItem();

			int ammo = gun.getAmmo(itemStack);
			int maxAmmo = gun.getMaxAmmo(itemStack);

			if (ammo <= 0) {
				return state.setAndContinue(Animations.NO_AMMO);
			}

			return state.setAndContinue(ammo < maxAmmo ? Animations.HALF_AMMO : Animations.FULL_AMMO);
		}

		static void registerControllers(CausticGunbladeItem animatable, AnimatableManager.ControllerRegistrar controllers) {
			AnimationController<CausticGunbladeItem> mainController = new AnimationController<>(animatable, MAIN_CONTROLLER, 0, Animations::handleMain);
			registerTriggerableAnimations(mainController);
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
