package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.render.item.ravenousclaws.RavenousClawsRenderer;
import com.github.elenterius.biomancy.entity.MobUtil;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.item.livingtool.LivingClawsItem;
import com.github.elenterius.biomancy.item.livingtool.LivingToolState;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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

public class RavenousClawsItem extends LivingClawsItem implements IAnimatable {
	protected static final UUID BASE_ATTACK_KNOCKBACK_UUID = UUID.fromString("6175525b-56dd-4f87-b035-86b892afe7b3");
	private final Lazy<Multimap<Attribute, AttributeModifier>> brokenAttributes;
	private final Lazy<Multimap<Attribute, AttributeModifier>> dormantAttributes;
	private final Lazy<Multimap<Attribute, AttributeModifier>> awakenedAttributes;
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

	public RavenousClawsItem(Tier tier, int attackDamage, float attackSpeed, int maxNutrients, Properties properties) {
		super(tier, 0, 0, 0, maxNutrients, properties);

		float attackSpeedModifier = (float) (attackSpeed - Attributes.ATTACK_SPEED.getDefaultValue());
		brokenAttributes = Lazy.of(() -> createDefaultAttributeModifiers(0, 0, -0.5f).build());
		dormantAttributes = Lazy.of(() -> createDefaultAttributeModifiers(attackDamage, attackSpeedModifier - 1, -0.5f).build());
		awakenedAttributes = Lazy.of(() -> createDefaultAttributeModifiers(attackDamage + 1, attackSpeedModifier, 0.5f).build());
	}

	@Override
	protected ImmutableMultimap.Builder<Attribute, AttributeModifier> createDefaultAttributeModifiers(float attackDamageModifier, float attackSpeedModifier, float attackRangeModifier) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = super.createDefaultAttributeModifiers(attackDamageModifier, attackSpeedModifier, attackRangeModifier);
		builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_UUID, "Weapon modifier", 0, AttributeModifier.Operation.MULTIPLY_TOTAL));
		return builder;
	}

	private static void playChargedHitFX(Player player) {
		player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1f, 1f);
		if (player.level instanceof ServerLevel serverLevel) {
			double xOffset = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
			double zOffset = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
			serverLevel.sendParticles(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), player.getX() + xOffset, player.getY(0.52d), player.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.MAINHAND ? dormantAttributes.get() : super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			LivingToolState livingToolState = getLivingToolState(stack);
			return switch (livingToolState) {
				case BROKEN -> brokenAttributes.get();
				case DORMANT -> dormantAttributes.get();
				case AWAKENED -> awakenedAttributes.get();
			};
		}
		return super.getAttributeModifiers(slot, stack);
	}

	@Override
	public boolean hasNutrients(ItemStack container) {
		return getNutrients(container) > getLivingToolActionCost(container, LivingToolState.AWAKENED, null);
	}

	@Override
	public void updateLivingToolState(ItemStack livingTool, ServerLevel level, Player player) {
		GeckoLibUtil.writeIDToStack(livingTool, level);
		super.updateLivingToolState(livingTool, level, player);
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
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!MobUtil.isCreativePlayer(attacker)) {
			consumeNutrients(stack, getLivingToolActionCost(stack, null));
		}

		if (attacker.level.isClientSide) return true;

		if (target.isDeadOrDying()) {
			addNutrients(stack, 5);
		}

		if (attacker instanceof Player player) {
			if (player.getAttackStrengthScale(0.5f) < 0.9f) return true;

			switch (getLivingToolState(stack)) {
				case BROKEN -> {
					//do nothing
				}
				case DORMANT -> {
					playChargedHitFX(player);
					if (player.getRandom().nextFloat() < 0.06f) {
						player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1f, 1.5f);
						CombatUtil.applyBleedEffect(target, 20);
						player.sendSystemMessage(Component.literal("6% Bleed Proc!").withStyle(Style.EMPTY.withColor(ModMobEffects.BLEED.get().getColor())));
					}
				}
				case AWAKENED -> {
					playChargedHitFX(player);
					if (player.getRandom().nextFloat() < 0.12f) {
						player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1f, 1.5f);
						//bloodExplosionFX()
						CombatUtil.hurtWithBleed(target, 0.1f * target.getMaxHealth());

						CombatUtil.applyBleedEffect(target, 20);
						player.sendSystemMessage(Component.literal("12% Bleed Proc!").withStyle(Style.EMPTY.withColor(ModMobEffects.BLEED.get().getColor())));
					}
				}
			}
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
				tooltip.add(ComponentUtil.literal(" 6% Bleed Chance").withStyle(ChatFormatting.DARK_GRAY));
			}
			case AWAKENED -> {
				tooltip.add(ComponentUtil.emptyLine());
				tooltip.add(ComponentUtil.literal("On Charged Hit:").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.literal(" 12% Bleed Chance").withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(ComponentUtil.literal(" 12% Blood Explosion Chance (deals 10% of max health as damage)").withStyle(ChatFormatting.DARK_GRAY));
			}
		}
		tooltip.add(ComponentUtil.emptyLine());
	}

	@Override
	public int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ToolAction toolAction) {
		return switch (state) {
			case AWAKENED -> 20;
			case DORMANT -> 2;
			case BROKEN -> 0;
		};
	}

	private PlayState onAnim(AnimationEvent<RavenousClawsItem> event) {
		List<ItemStack> extraData = event.getExtraDataOfType(ItemStack.class);
		LivingToolState state = !extraData.isEmpty() ? getLivingToolState(extraData.get(0)) : LivingToolState.BROKEN;

		AnimationController<RavenousClawsItem> controller = event.getController();
		switch (state) {
			case DORMANT -> controller.setAnimation(Animations.getDormant(controller));
			case AWAKENED -> controller.setAnimation(Animations.getAwakened(controller));
			case BROKEN -> controller.setAnimation(Animations.BROKEN_ANIMATION);
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
		data.addAnimationController(new AnimationController<>(this, "controller", 10, this::onAnim));
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	protected static class Animations {
		protected static final AnimationBuilder DORMANT_ANIMATION = new AnimationBuilder().loop("ravenous_claws.dormant");
		protected static final AnimationBuilder TO_SLEEP_TRANSITION_ANIMATION = new AnimationBuilder().playOnce("ravenous_claws.tosleep").loop("ravenous_claws.dormant");
		protected static final AnimationBuilder BROKEN_ANIMATION = new AnimationBuilder().loop("ravenous_claws.broken");
		protected static final AnimationBuilder WAKEUP_TRANSITION_ANIMATION = new AnimationBuilder().playOnce("ravenous_claws.wakeup").loop("ravenous_claws.awakened");
		protected static final AnimationBuilder AWAKENED_ANIMATION = new AnimationBuilder().loop("ravenous_claws.awakened");

		private Animations() {}

		protected static AnimationBuilder getDormant(AnimationController<?> controller) {
			Animation animation = controller.getCurrentAnimation();
			if (animation == null) return DORMANT_ANIMATION;

			String animationName = animation.animationName;
			if (animationName.equals("ravenous_claws.awakened") || animationName.equals("ravenous_claws.wakeup")) {
				return TO_SLEEP_TRANSITION_ANIMATION;
			}

			return DORMANT_ANIMATION;
		}

		protected static AnimationBuilder getAwakened(AnimationController<?> controller) {
			Animation animation = controller.getCurrentAnimation();
			if (animation == null) return AWAKENED_ANIMATION;

			String animationName = animation.animationName;
			if (animationName.equals("ravenous_claws.dormant") || animationName.equals("ravenous_claws.tosleep")) {
				return WAKEUP_TRANSITION_ANIMATION;
			}

			return AWAKENED_ANIMATION;
		}
	}

}
