package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.renderer.item.LongClawsRenderer;
import com.github.elenterius.biomancy.mixin.SwordItemMixinAccessor;
import com.github.elenterius.biomancy.world.item.LivingToolState;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.UUID;
import java.util.function.Consumer;

public class LongClawsItem extends LivingSwordItem implements IClaws, ICriticalHitEntity, IAnimatable {

	public static final AttributeModifier EXTENDED_ATTACK_RANGE_MODIFIER = new AttributeModifier(UUID.fromString("29ace568-4e32-4809-840c-3c9a0e1ebcd4"), "Weapon modifier", 1.5f, AttributeModifier.Operation.ADDITION);

	private final Lazy<Multimap<Attribute, AttributeModifier>> dormantAttributeModifiers;
	private final Lazy<Multimap<Attribute, AttributeModifier>> awakenedAttributeModifiers;
	private final Lazy<Multimap<Attribute, AttributeModifier>> exaltedAttributeModifiers;
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

	public LongClawsItem(Tier tier, int attackDamage, float attackSpeed, int maxNutrients, Properties properties) {
		super(tier, attackDamage, attackSpeed, maxNutrients, properties);

		final float baseDamage = getDamage();
		dormantAttributeModifiers = Lazy.of(this::createDormantAttributeModifiers);
		awakenedAttributeModifiers = Lazy.of(() -> createAwakenedAttributeModifiers(baseDamage, attackSpeed));
		exaltedAttributeModifiers = Lazy.of(() -> createExaltedAttributeModifiers(baseDamage, attackSpeed));
	}

	protected Multimap<Attribute, AttributeModifier> createDormantAttributeModifiers() {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		Multimap<Attribute, AttributeModifier> swordAttributes = ((SwordItemMixinAccessor) this).biomancy_getDefaultModifiers();
		builder.putAll(swordAttributes);
		builder.put(ForgeMod.ATTACK_RANGE.get(), CLAWS_ATTACK_RANGE_MODIFIER);
		return builder.build();
	}

	protected Multimap<Attribute, AttributeModifier> createAwakenedAttributeModifiers(float baseDamage, float attackSpeed) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", baseDamage + 4, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed + 0.4f, AttributeModifier.Operation.ADDITION));
		builder.put(ForgeMod.ATTACK_RANGE.get(), CLAWS_ATTACK_RANGE_MODIFIER);
		return builder.build();
	}

	protected Multimap<Attribute, AttributeModifier> createExaltedAttributeModifiers(float baseDamage, float attackSpeed) {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", baseDamage + 8, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed + 1f, AttributeModifier.Operation.ADDITION));
		builder.put(ForgeMod.ATTACK_RANGE.get(), EXTENDED_ATTACK_RANGE_MODIFIER);
		return builder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			LivingToolState livingToolState = getLivingToolState(stack);
			return switch (livingToolState) {
				case DORMANT -> dormantAttributeModifiers.get();
				case AWAKE -> awakenedAttributeModifiers.get();
				case EXALTED -> exaltedAttributeModifiers.get();
			};
		}
		return ImmutableMultimap.of();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? dormantAttributeModifiers.get() : ImmutableMultimap.of();
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (toolAction == ToolActions.SWORD_SWEEP) return isClawExtended(stack);
		return super.canPerformAction(stack, toolAction);
	}

	public boolean isClawExtended(ItemStack stack) {
		return getLivingToolState(stack) == LivingToolState.EXALTED;
	}

	@Override
	public void onCriticalHitEntity(ItemStack stack, LivingEntity attacker, LivingEntity target) {
		if (!attacker.level.isClientSide()) {
			addNutrients(stack, 1);
		}
		else {
			attacker.playSound(SoundEvents.GOAT_SCREAMING_RAM_IMPACT, 0.75f, 1f / (attacker.getRandom().nextFloat() * 0.5f + 1f) + 0.2f);
		}
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		LivingToolState toolState = getLivingToolState(stack);
		return toolState == LivingToolState.DORMANT ? 1f : super.getDestroySpeed(stack, state);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		if (player.level.isClientSide()) return InteractionResult.PASS;
		if (shearTarget(stack, player, interactionTarget, usedHand)) return InteractionResult.SUCCESS;
		return InteractionResult.PASS;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide()) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForStack(animationFactory, stack, "controller");
			if (isClawExtended(stack)) {
				controller.setAnimation(new AnimationBuilder().playOnce("long_claws.extend").loop("long_claws.extended"));
			}
			else {
				controller.setAnimation(new AnimationBuilder().loop("long_claws.idle"));
			}
		}
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

}
