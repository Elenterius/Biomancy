package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.api.livingtool.LivingToolState;
import com.github.elenterius.biomancy.client.render.item.ravenousclaws.RavenousClawsRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.ItemAttackDamageSourceProvider;
import com.github.elenterius.biomancy.item.ItemCharge;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RavenousClawsItem extends LivingClawsItem implements GeoItem, ItemCharge, ItemAttackDamageSourceProvider {
	protected static final UUID BASE_ATTACK_KNOCKBACK_UUID = UUID.fromString("6175525b-56dd-4f87-b035-86b892afe7b3");
	private final Lazy<Multimap<Attribute, AttributeModifier>> brokenAttributes;
	private final Lazy<Multimap<Attribute, AttributeModifier>> dormantAttributes;
	private final Lazy<Multimap<Attribute, AttributeModifier>> awakenedAttributes;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public RavenousClawsItem(Tier tier, float attackDamage, float attackSpeed, int maxNutrients, Properties properties) {
		super(tier, 0, 0, 0, maxNutrients, properties);

		float attackSpeedModifier = (float) (attackSpeed - Attributes.ATTACK_SPEED.getDefaultValue());
		brokenAttributes = Lazy.of(() -> createDefaultAttributeModifiers(0, 0, -0.5f).build());
		dormantAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage, attackSpeedModifier, 0).build());
		awakenedAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage + 2.5f, attackSpeedModifier, 0.5f).build());
	}

	private static void playBloodyClawsFX(LivingEntity attacker) {
		attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_STRONG.get(), attacker.getSoundSource(), 1f, 1f + attacker.getRandom().nextFloat() * 0.5f);
		if (attacker.level() instanceof ServerLevel serverLevel) {
			double xOffset = -Mth.sin(attacker.getYRot() * Mth.DEG_TO_RAD);
			double zOffset = Mth.cos(attacker.getYRot() * Mth.DEG_TO_RAD);
			serverLevel.sendParticles(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), attacker.getX() + xOffset, attacker.getY(0.52f), attacker.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
		}
	}

	private static void playBloodExplosionFX(LivingEntity target) {
		if (target.level() instanceof ServerLevel serverLevel) {
			float w = target.getBbWidth() * 0.45f;
			double x = serverLevel.getRandom().nextGaussian() * w;
			double y = serverLevel.getRandom().nextGaussian() * 0.2d;
			double z = serverLevel.getRandom().nextGaussian() * w;
			serverLevel.sendParticles(ModParticleTypes.FALLING_BLOOD.get(), target.getX(), target.getY(0.5f), target.getZ(), 20, x, y, z, 0.25);
		}
	}

	@Override
	protected ImmutableMultimap.Builder<Attribute, AttributeModifier> createDefaultAttributeModifiers(float attackDamageModifier, float attackSpeedModifier, float attackRangeModifier) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = super.createDefaultAttributeModifiers(attackDamageModifier, attackSpeedModifier, attackRangeModifier);
		builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_UUID, "Weapon modifier", 0, AttributeModifier.Operation.MULTIPLY_TOTAL));
		return builder;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.MAINHAND ? dormantAttributes.get() : ImmutableMultimap.of();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			return switch (getLivingToolState(stack)) {
				case BROKEN -> brokenAttributes.get();
				case DORMANT -> dormantAttributes.get();
				case AWAKENED -> awakenedAttributes.get();
			};
		}
		return ImmutableMultimap.of();
	}

	@Override
	public boolean hasNutrients(ItemStack container) {
		return getNutrients(container) > getLivingToolActionCost(container, LivingToolState.AWAKENED, null);
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (!hasCharge(stack)) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_blood_charge"), true);
			player.playSound(ModSoundEvents.FLESHKIN_NO.get(), 1f, 1f + player.level().getRandom().nextFloat() * 0.4f);
			return InteractionResultHolder.fail(flags);
		}

		return InteractionResultHolder.success(flags);
	}

	@Override
	public int getMaxCharge(ItemStack container) {
		return 50;
	}

	@Override
	public void onChargeChanged(ItemStack livingTool, int oldValue, int newValue) {
		if (newValue <= 0 && getLivingToolState(livingTool) == LivingToolState.AWAKENED) {
			setLivingToolState(livingTool, LivingToolState.DORMANT);
		}
	}

	@Override
	public void onNutrientsChanged(ItemStack livingTool, int oldValue, int newValue) {
		LivingToolState prevState = getLivingToolState(livingTool);
		LivingToolState state = prevState;

		if (newValue <= 0) {
			if (state != LivingToolState.BROKEN) setLivingToolState(livingTool, LivingToolState.BROKEN);
			return;
		}

		if (state == LivingToolState.BROKEN) {
			state = LivingToolState.DORMANT;
		}

		int maxCost = getLivingToolMaxActionCost(livingTool, state);
		if (newValue < maxCost && state == LivingToolState.DORMANT) state = LivingToolState.BROKEN;

		if (state != prevState) setLivingToolState(livingTool, state);
	}

	@Override
	public void updateLivingToolState(ItemStack livingTool, ServerLevel level, Player player) {
		GeoItem.getOrAssignId(livingTool, level);

		LivingToolState state = getLivingToolState(livingTool);
		boolean hasNutrients = hasNutrients(livingTool);

		if (!hasNutrients) {
			if (state != LivingToolState.BROKEN) {
				setLivingToolState(livingTool, LivingToolState.BROKEN);
				SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESHKIN_BREAK.get());
			}
			return;
		}

		switch (state) {
			case BROKEN, AWAKENED -> {
				setLivingToolState(livingTool, LivingToolState.DORMANT);
				SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESHKIN_BECOME_DORMANT.get());
			}
			case DORMANT -> {
				if (hasCharge(livingTool)) {
					setLivingToolState(livingTool, LivingToolState.AWAKENED);
					SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESHKIN_BECOME_AWAKENED.get());
				}
			}
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack livingTool, Slot slot, ClickAction action, Player player) {
		if (player.level() instanceof ServerLevel serverLevel) GeoItem.getOrAssignId(livingTool, serverLevel);
		return super.overrideStackedOnOther(livingTool, slot, action, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack livingTool, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (player.level() instanceof ServerLevel serverLevel) GeoItem.getOrAssignId(livingTool, serverLevel);
		return super.overrideOtherStackedOnMe(livingTool, other, slot, action, player, access);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (toolAction == ToolActions.SWORD_SWEEP) return false;
		return super.canPerformAction(stack, toolAction);
	}

	@Override
	public @Nullable DamageSource getDamageSource(ItemStack stack, Entity target, LivingEntity attacker) {
		return ModDamageSources.bleed(attacker.level(), attacker);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level().isClientSide) return true;

		LivingToolState livingToolState = getLivingToolState(stack);
		boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
		boolean isNotCreativePlayer = !MobUtil.isCreativePlayer(attacker);

		switch (livingToolState) {
			case BROKEN -> { /* do nothing */ }
			case DORMANT -> {
				if (isNotCreativePlayer) {
					consumeNutrients(stack, 1);
				}

				if (isFullAttackStrength) {
					playBloodyClawsFX(attacker);
					if (attacker.getRandom().nextInt(12) == 0) { //8.3%
						attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_BLEED_PROC.get(), attacker.getSoundSource(), 1f, 1f);

						CombatUtil.applyBleedEffect(target, 20);
						if (isNotCreativePlayer) {
							consumeNutrients(stack, 1);
						}
					}

					target.invulnerableTime = 0; //make victims vulnerable the next attack regardless of the damage amount
				}

				if (target.isDeadOrDying()) {
					addCharge(stack, 5);
				}
			}
			case AWAKENED -> {
				if (isNotCreativePlayer) {
					consumeCharge(stack, 1);
				}

				if (isFullAttackStrength) {
					playBloodyClawsFX(attacker);
					if (attacker.getRandom().nextInt(5) == 0) { //20%
						attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), ModSoundEvents.CLAWS_ATTACK_BLEED_PROC.get(), attacker.getSoundSource(), 1f, 1f);

						if (CombatUtil.getBleedEffectLevel(target) < 2) {
							playBloodExplosionFX(target);
							CombatUtil.hurtWithBleed(target, 0.1f * target.getMaxHealth());

							if (isNotCreativePlayer) {
								consumeCharge(stack, 4);
							}
						}

						CombatUtil.applyBleedEffect(target, 20);
						if (isNotCreativePlayer) {
							consumeCharge(stack, 1);
						}
					}

					target.invulnerableTime = 0; //make victims vulnerable the next attack regardless of the damage amount
				}
			}
		}

		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.emptyLine());

		appendLivingToolTooltip(stack, tooltip);

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}

	@Override
	public void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		LivingToolState livingToolState = getLivingToolState(stack);

		switch (livingToolState) {
			case BROKEN -> {
				//do nothing
			}
			case DORMANT -> {
				tooltip.add(TextComponentUtil.getAbilityText("bleed_proc").append(" (8% chance)").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("bleed_proc.desc")).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(ComponentUtil.emptyLine());
			}
			case AWAKENED -> {
				tooltip.add(TextComponentUtil.getAbilityText("bleed_proc").append(" (20% chance)").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("bleed_proc.desc")).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(TextComponentUtil.getAbilityText("blood_explosion").append(" (20% chance)").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("blood_explosion.desc")).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(ComponentUtil.emptyLine());
			}
		}

		DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
		tooltip.add(TextComponentUtil.getTooltipText("nutrients_fuel").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" %s/%s".formatted(df.format(getNutrients(stack)), df.format(getMaxNutrients(stack)))).withStyle(TextStyles.NUTRIENTS));
		tooltip.add(TextComponentUtil.getTooltipText("blood_charge").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" %s/%s".formatted(df.format(getCharge(stack)), df.format(getMaxCharge(stack)))).withStyle(TextStyles.ERROR));

		switch (livingToolState) {
			case BROKEN -> {
				tooltip.add(ComponentUtil.emptyLine());
				tooltip.add(livingToolState.getTooltip());
			}
			case DORMANT -> {
				tooltip.add(ComponentUtil.emptyLine());
				tooltip.add(livingToolState.getTooltip().withStyle(TextStyles.ITALIC_GRAY));
				tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.enable_awakened_mode")));
			}
			case AWAKENED -> {
				tooltip.add(ComponentUtil.emptyLine());
				tooltip.add(livingToolState.getTooltip().withStyle(TextStyles.ITALIC_GRAY));
				tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.disable_awakened_mode")));
			}
		}
	}

	@Override
	public int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ToolAction toolAction) {
		return switch (state) {
			case AWAKENED, DORMANT -> 1;
			case BROKEN -> 0;
		};
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final RavenousClawsRenderer renderer = new RavenousClawsRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	private PlayState handleAnimationState(AnimationState<RavenousClawsItem> animationState) {
		ItemStack stack = animationState.getData(DataTickets.ITEMSTACK);
		LivingToolState toolState = stack != null ? getLivingToolState(stack) : LivingToolState.BROKEN;

		AnimationController<RavenousClawsItem> controller = animationState.getController();
		switch (toolState) {
			case DORMANT -> Animations.setDormant(controller);
			case AWAKENED -> Animations.setAwakened(controller);
			case BROKEN -> Animations.setBroken(controller);
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, Animations.MAIN_CONTROLLER, 1, this::handleAnimationState));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static class Animations {
		public static final String MAIN_CONTROLLER = "main";

		protected static final RawAnimation DORMANT = RawAnimation.begin().thenLoop("ravenous_claws.dormant");
		protected static final RawAnimation TO_SLEEP_TRANSITION = RawAnimation.begin().thenPlay("ravenous_claws.tosleep").thenLoop("ravenous_claws.dormant");
		protected static final RawAnimation BROKEN = RawAnimation.begin().thenLoop("ravenous_claws.broken");
		protected static final RawAnimation WAKEUP_TRANSITION = RawAnimation.begin().thenPlay("ravenous_claws.wakeup").thenLoop("ravenous_claws.awakened");
		protected static final RawAnimation AWAKENED = RawAnimation.begin().thenLoop("ravenous_claws.awakened");

		private Animations() {}

		protected static void setDormant(AnimationController<?> controller) {
			AnimationProcessor.QueuedAnimation queued = controller.getCurrentAnimation();
			if (queued == null) {
				controller.setAnimation(DORMANT);
				return;
			}

			if (!queued.animation().name().equals("ravenous_claws.dormant")) {
				controller.setAnimation(TO_SLEEP_TRANSITION);
				return;
			}

			controller.setAnimation(DORMANT);
		}

		protected static void setAwakened(AnimationController<?> controller) {
			AnimationProcessor.QueuedAnimation queued = controller.getCurrentAnimation();
			if (queued == null) {
				controller.setAnimation(AWAKENED);
				return;
			}

			if (!queued.animation().name().equals("ravenous_claws.awakened")) {
				controller.setAnimation(WAKEUP_TRANSITION);
				return;
			}

			controller.setAnimation(AWAKENED);
		}

		public static void setBroken(AnimationController<RavenousClawsItem> controller) {
			controller.setAnimation(BROKEN);
		}
	}

}
