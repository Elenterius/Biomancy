package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.renderer.item.LongClawsRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.item.IBiomancyItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.Lazy;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class LongClawsItem extends ClawWeaponItem implements ICriticalHitEntity, IBiomancyItem, IAnimatable /*implements IAreaHarvestingItem*/ {

	public static final String DURATION_KEY = "LongClawTimeLeft";

	public static final AttributeModifier NO_KNOCK_BACK_MODIFIER = new AttributeModifier(UUID.fromString("0f497472-0e93-4dc5-8a2c-c2033afbfed5"), "Weapon modifier", 0, AttributeModifier.Operation.MULTIPLY_TOTAL);
	public static final AttributeModifier RETRACTED_CLAW_REACH_MODIFIER = new AttributeModifier(UUID.fromString("d76adb08-2bb3-4e88-997d-766a919f0f6b"), "Weapon modifier", 0.5f, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier EXTENDED_CLAW_REACH_MODIFIER = new AttributeModifier(UUID.fromString("29ace568-4e32-4809-840c-3c9a0e1ebcd4"), "Weapon modifier", 1.5f, AttributeModifier.Operation.ADDITION);

	private final Lazy<Multimap<Attribute, AttributeModifier>> lazyAttributeModifiersV2;

	private final int abilityDuration; // in "seconds"

	private final AnimationFactory animationFactory = new AnimationFactory(this);

	public LongClawsItem(Tier tier, int attackDamage, float attackSpeed, int abilityDuration, Properties properties) {
		super(tier, attackDamage, attackSpeed, properties);
		lazyAttributeModifiersV2 = Lazy.of(this::createAttributeModifiersV2);
		this.abilityDuration = abilityDuration;
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			private final LongClawsRenderer renderer = new LongClawsRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer;
			}
		});
	}

	public static boolean isClawExtended(ItemStack stack) {
		return stack.getOrCreateTag().getInt(DURATION_KEY) > 0;
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (toolAction == ToolActions.SWORD_SWEEP) return false;
		return super.canPerformAction(stack, toolAction);
	}

	protected Multimap<Attribute, AttributeModifier> createAttributeModifiersV2() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> clawAttributes = lazyAttributeModifiers.get();
		clawAttributes.forEach((attribute, attributeModifier) -> {
			if (attributeModifier != RETRACTED_CLAW_REACH_MODIFIER) {
				builder.put(attribute, attributeModifier);
			}
		});
		builder.put(ForgeMod.ATTACK_RANGE.get(), EXTENDED_CLAW_REACH_MODIFIER);
		return builder.build();
	}

	@Override
	protected void addAdditionalAttributeModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addAdditionalAttributeModifiers(builder);
		builder.put(ForgeMod.ATTACK_RANGE.get(), RETRACTED_CLAW_REACH_MODIFIER);
		builder.put(Attributes.ATTACK_KNOCKBACK, NO_KNOCK_BACK_MODIFIER);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return slot == EquipmentSlot.MAINHAND && isClawExtended(stack) ? lazyAttributeModifiersV2.get() : super.getAttributeModifiers(slot, stack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.isAttackable() && !livingEntity.skipAttackInteraction(player) && player.getAttackStrengthScale(0.5f) > 0.9f) {
			livingEntity.getCapability(ModCapabilities.NO_KNOCKBACK_FLAG_CAP).ifPresent(ModCapabilities.IFlagCap::enable);

			boolean isAttackerHeavier = MobUtil.getWeight(player) > MobUtil.getWeight(livingEntity);

			LivingEntity anchorEntity;
			LivingEntity pulledEntity;

			if (isAttackerHeavier) {
				anchorEntity = player;
				pulledEntity = livingEntity;
			} else {
				anchorEntity = livingEntity;
				pulledEntity = player;
			}

			Vec3 movement = pulledEntity.getDeltaMovement();
			Vec3 diff = new Vec3(anchorEntity.getX() - pulledEntity.getX(), anchorEntity.getY(0.5D) - pulledEntity.getEyeY(), anchorEntity.getZ() - pulledEntity.getZ());
			double strength = diff.length() * 0.5f;
			Vec3 direction = diff.normalize().scale(strength);
			pulledEntity.setDeltaMovement(movement.x / 2d + direction.x, movement.y / 2d + direction.y * 0.5d, movement.z / 2d + direction.z);
			pulledEntity.hasImpulse = true;
		}
		return false;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		CompoundTag tag = stack.getOrCreateTag();
		int prevDuration = tag.getInt(DURATION_KEY);
		int maxDuration = Math.max(prevDuration, abilityDuration);
		int duration = prevDuration + Math.max(abilityDuration / 4, 1);
		tag.putInt(DURATION_KEY, Math.min(duration, maxDuration));

		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		if (!attacker.level.isClientSide()) {
			stack.getOrCreateTag().putInt(DURATION_KEY, abilityDuration * 2);
		} else {
			attacker.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1f, 1f / (attacker.getRandom().nextFloat() * 0.5f + 1f) + 0.2f);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.getGameTime() % 20L == 0L) {
			if (!level.isClientSide()) {
				CompoundTag tag = stack.getOrCreateTag();
				int timeLeft = tag.getInt(DURATION_KEY);
				if (timeLeft > 0) {
					tag.putInt(DURATION_KEY, timeLeft - 1);
				}
			}
		}

		if (level.isClientSide()) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForStack(animationFactory, stack, "controller");
			if (stack.getOrCreateTag().getInt(DURATION_KEY) > 0) {
				controller.setAnimation(new AnimationBuilder().playOnce("long_claws.extend").loop("long_claws.extended"));
			}
			else {
				controller.setAnimation(new AnimationBuilder().loop("long_claws.idle"));
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	//	@Override
	//	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
	//		byte harvestRange = getBlockHarvestRange(stack);
	//		if (!player.isShiftKeyDown() && harvestRange > 0 && !player.level.isClientSide && player instanceof ServerPlayer serverPlayer) {
	//			ServerLevel level = serverPlayer.getLevel();
	//			BlockState blockState = level.getBlockState(pos);
	//			HitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
	//			if (PlayerInteractionUtil.harvestBlock(level, serverPlayer, blockState, pos) && getDestroySpeed(stack, blockState) > 1f) {
	//				List<BlockPos> blockNeighbors = PlayerInteractionUtil.findBlockNeighbors(level, hitResult, blockState, pos, harvestRange, getHarvestShape(stack));
	//				for (BlockPos neighborPos : blockNeighbors) {
	//					PlayerInteractionUtil.harvestBlock(level, serverPlayer, blockState, neighborPos);
	//				}
	//			}
	//			return true;
	//		}
	//
	//		//only called on client side
	//		return super.onBlockStartBreak(stack, pos, player);
	//	}

	//	@Override
	//	public boolean isAreaSelectionVisibleFor(ItemStack stack, BlockPos pos, BlockState state) {
	//		return super.getDestroySpeed(stack, state) > 1f;
	//	}
	//
	//	@Override
	//	public byte getBlockHarvestRange(ItemStack stack) {
	//		return (byte) 1;
	//	}

	//	@Override
	//	public GeometricShape getHarvestShape(ItemStack stack) {
	//		return GeometricShape.CUBE;
	//	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<LongClawsItem> controller = new AnimationController<>(this, "controller", 10, event -> PlayState.CONTINUE);
		controller.setAnimation(new AnimationBuilder().loop("long_claws.idle"));
		data.addAnimationController(controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this));
		tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());

		int timeLeft = stack.getOrCreateTag().getInt(DURATION_KEY);
		if (timeLeft > 0) {
			tooltip.add(TextComponentUtil.getTooltipText("item_is_excited").append(" (" + timeLeft + ")").withStyle(ChatFormatting.GRAY));
		}
		else {
			tooltip.add(TextComponentUtil.getTooltipText("item_is_dormant").withStyle(ChatFormatting.GRAY));
		}
		if (stack.isEnchanted()) tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		if (displayName instanceof MutableComponent mutableComponent) {
			String keySuffix = stack.getOrCreateTag().getInt(DURATION_KEY) > 0 ? "excited" : "dormant";
			return mutableComponent.append(" (").append(TextComponentUtil.getTooltipText(keySuffix)).append(")");
		}
		return displayName;
	}

}
