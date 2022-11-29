package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.renderer.item.LongClawsRenderer;
import com.github.elenterius.biomancy.world.item.LivingToolState;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
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

import java.util.function.Consumer;

public class LivingLongClawsItem extends LivingClawsItem implements IAnimatable {

	private final Lazy<Multimap<Attribute, AttributeModifier>> awakenedAttributeModifiers;
	private final Lazy<Multimap<Attribute, AttributeModifier>> exaltedAttributeModifiers;
	private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

	public LivingLongClawsItem(Tier tier, int baseAttackDamage, float attackSpeed, float attackRange, int maxNutrients, Properties properties) {
		super(tier, baseAttackDamage, attackSpeed, attackRange, maxNutrients, properties);
		float attackDamageModifier = baseAttackDamage + tier.getAttackDamageBonus();
		awakenedAttributeModifiers = Lazy.of(() -> createDefaultAttributeModifiers(attackDamageModifier + 4, attackSpeed + 0.4f, attackRange));
		exaltedAttributeModifiers = Lazy.of(() -> createDefaultAttributeModifiers(attackDamageModifier + 8, attackSpeed + 1f, attackRange + 1f));
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			LivingToolState livingToolState = getLivingToolState(stack);
			return switch (livingToolState) {
				case DORMANT -> defaultAttributeModifiers.get();
				case AWAKE -> awakenedAttributeModifiers.get();
				case EXALTED -> exaltedAttributeModifiers.get();
			};
		}
		return super.getAttributeModifiers(slot, stack);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (toolAction == ToolActions.SWORD_SWEEP && getLivingToolState(stack) == LivingToolState.EXALTED) return true;
		return super.canPerformAction(stack, toolAction);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide()) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForStack(animationFactory, stack, "controller");
			LivingToolState state = getLivingToolState(stack);
			switch (state) {
				case AWAKE -> controller.setAnimation(new AnimationBuilder().loop("long_claws.awake"));
				case EXALTED -> controller.setAnimation(new AnimationBuilder().playOnce("long_claws.extend").loop("long_claws.extended"));
				default -> controller.setAnimation(new AnimationBuilder().loop("long_claws.dormant"));
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
		AnimationController<LivingLongClawsItem> controller = new AnimationController<>(this, "controller", 10, event -> PlayState.CONTINUE);
		controller.setAnimation(new AnimationBuilder().loop("long_claws.dormant"));
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
