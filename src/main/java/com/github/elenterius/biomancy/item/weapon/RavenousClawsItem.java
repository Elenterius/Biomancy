package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.render.item.ravenousclaws.RavenousClawsRenderer;
import com.github.elenterius.biomancy.entity.MobUtil;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.ItemCharge;
import com.github.elenterius.biomancy.item.livingtool.LivingClawsItem;
import com.github.elenterius.biomancy.item.livingtool.LivingToolState;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResultHolder;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RavenousClawsItem extends LivingClawsItem implements IAnimatable, ItemCharge {
	protected static final UUID BASE_ATTACK_KNOCKBACK_UUID = UUID.fromString("6175525b-56dd-4f87-b035-86b892afe7b3");
	private final Lazy<Multimap<Attribute, AttributeModifier>> brokenAttributes;
	private final Lazy<Multimap<Attribute, AttributeModifier>> dormantAttributes;
	private final Lazy<Multimap<Attribute, AttributeModifier>> awakenedAttributes;
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

	public RavenousClawsItem(Tier tier, float attackDamage, float attackSpeed, int maxNutrients, Properties properties) {
		super(tier, 0, 0, 0, maxNutrients, properties);

		float attackSpeedModifier = (float) (attackSpeed - Attributes.ATTACK_SPEED.getDefaultValue());
		brokenAttributes = Lazy.of(() -> createDefaultAttributeModifiers(0, 0, -0.5f).build());
		dormantAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage, attackSpeedModifier, -0.5f).build());
		awakenedAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage + 2, attackSpeedModifier, 0.5f).build());
	}

	private static void playBloodyClawsFX(LivingEntity attacker) {
		attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 0.85f, 0.9f + attacker.getRandom().nextFloat() * 0.5f);
		if (attacker.level instanceof ServerLevel serverLevel) {
			double xOffset = -Mth.sin(attacker.getYRot() * Mth.DEG_TO_RAD);
			double zOffset = Mth.cos(attacker.getYRot() * Mth.DEG_TO_RAD);
			serverLevel.sendParticles(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), attacker.getX() + xOffset, attacker.getY(0.52f), attacker.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
		}
	}

	private static void playBloodExplosionFX(LivingEntity target) {
		if (target.level instanceof ServerLevel serverLevel) {
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
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_charge"), true);
			player.playSound(SoundEvents.VILLAGER_NO, 0.8f, 0.8f + player.getLevel().getRandom().nextFloat() * 0.4f);
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
		GeckoLibUtil.writeIDToStack(livingTool, level);

		LivingToolState state = getLivingToolState(livingTool);
		boolean hasNutrients = hasNutrients(livingTool);

		if (!hasNutrients) {
			if (state != LivingToolState.BROKEN) {
				setLivingToolState(livingTool, LivingToolState.BROKEN);
				SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_BREAK.get());
			}
			return;
		}

		switch (state) {
			case BROKEN, AWAKENED -> {
				setLivingToolState(livingTool, LivingToolState.DORMANT);
				SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_HIT.get());
			}
			case DORMANT -> {
				if (hasCharge(livingTool)) {
					setLivingToolState(livingTool, LivingToolState.AWAKENED);
					SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_PLACE.get());
				}
			}
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack livingTool, Slot slot, ClickAction action, Player player) {
		if (player.level instanceof ServerLevel serverLevel) GeckoLibUtil.writeIDToStack(livingTool, serverLevel);
		return super.overrideStackedOnOther(livingTool, slot, action, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack livingTool, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (player.level instanceof ServerLevel serverLevel) GeckoLibUtil.writeIDToStack(livingTool, serverLevel);
		return super.overrideOtherStackedOnMe(livingTool, other, slot, action, player, access);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (toolAction == ToolActions.SWORD_SWEEP) return false;
		return super.canPerformAction(stack, toolAction);
	}

	@Override
	public int getNutrientFuelValue(ItemStack container, ItemStack food) {
		return super.getNutrientFuelValue(container, food) / 2;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker.level.isClientSide) return true;

		LivingToolState livingToolState = getLivingToolState(stack);
		boolean isFullAttackStrength = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) >= 0.9f;
		boolean isNotCreativePlayer = !MobUtil.isCreativePlayer(attacker);

		if (isNotCreativePlayer) {
			switch (livingToolState) {
				case BROKEN -> { /* do nothing */ }
				case DORMANT -> consumeNutrients(stack, 1);
				case AWAKENED -> consumeCharge(stack, 1);
			}
		}

		if (isFullAttackStrength) {
			switch (livingToolState) {
				case BROKEN -> { /* do nothing */ }
				case DORMANT -> {
					playBloodyClawsFX(attacker);
					if (attacker.getRandom().nextInt(12) == 0) { //8.3%
						attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1f, 1.5f);

						CombatUtil.applyBleedEffect(target, 20);
					}
				}
				case AWAKENED -> {
					playBloodyClawsFX(attacker);
					if (attacker.getRandom().nextInt(5) == 0) { //20%
						attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1f, 1.5f);

						playBloodExplosionFX(target);
						CombatUtil.hurtWithBleed(target, 0.1f * target.getMaxHealth());
						CombatUtil.applyBleedEffect(target, 20);
					}
				}
			}
		}

		if (target.isDeadOrDying()) {
			addCharge(stack, 2);
		}

		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltip, isAdvanced);
		switch (getLivingToolState(stack)) {
			case BROKEN -> {
				//do nothing
			}
			case DORMANT -> {
				tooltip.add(ComponentUtil.emptyLine());
				tooltip.add(ComponentUtil.literal("On Charged Hit:").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" 8% Bleed Chance").withStyle(ChatFormatting.DARK_GRAY));
			}
			case AWAKENED -> {
				tooltip.add(ComponentUtil.emptyLine());
				tooltip.add(ComponentUtil.literal("On Charged Hit:").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" 20% Bleed Chance").withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(ComponentUtil.literal(" 20% Blood Explosion Chance (deals 10% of max health as damage)").withStyle(ChatFormatting.DARK_GRAY));
			}
		}

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}

	@Override
	public int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ToolAction toolAction) {
		return switch (state) {
			case AWAKENED, DORMANT -> 1;
			case BROKEN -> 0;
		};
	}

	private PlayState onAnim(AnimationEvent<RavenousClawsItem> event) {
		List<ItemStack> extraData = event.getExtraDataOfType(ItemStack.class);
		LivingToolState state = !extraData.isEmpty() ? getLivingToolState(extraData.get(0)) : LivingToolState.BROKEN;

		AnimationController<RavenousClawsItem> controller = event.getController();
		switch (state) {
			case DORMANT -> Animations.setDormant(controller);
			case AWAKENED -> Animations.setAwakened(controller);
			case BROKEN -> Animations.setBroken(controller);
		}

		return PlayState.CONTINUE;
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

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 1, this::onAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	protected static class Animations {
		protected static final AnimationBuilder DORMANT = new AnimationBuilder().loop("ravenous_claws.dormant");
		protected static final AnimationBuilder TO_SLEEP_TRANSITION = new AnimationBuilder().playOnce("ravenous_claws.tosleep").loop("ravenous_claws.dormant");
		protected static final AnimationBuilder BROKEN = new AnimationBuilder().loop("ravenous_claws.broken");
		protected static final AnimationBuilder WAKEUP_TRANSITION = new AnimationBuilder().playOnce("ravenous_claws.wakeup").loop("ravenous_claws.awakened");
		protected static final AnimationBuilder AWAKENED = new AnimationBuilder().loop("ravenous_claws.awakened");

		private Animations() {}

		protected static void setDormant(AnimationController<?> controller) {
			Animation animation = controller.getCurrentAnimation();
			if (animation == null) {
				controller.setAnimation(DORMANT);
				return;
			}

			if (!animation.animationName.equals("ravenous_claws.dormant")) {
				controller.setAnimation(TO_SLEEP_TRANSITION);
				return;
			}

			controller.setAnimation(DORMANT);
		}

		protected static void setAwakened(AnimationController<?> controller) {
			Animation animation = controller.getCurrentAnimation();
			if (animation == null) {
				controller.setAnimation(AWAKENED);
				return;
			}

			if (!animation.animationName.equals("ravenous_claws.awakened")) {
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
