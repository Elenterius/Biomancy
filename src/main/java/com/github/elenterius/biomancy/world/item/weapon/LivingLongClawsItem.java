package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.render.item.longclaws.LongClawsRenderer;
import com.github.elenterius.biomancy.world.item.state.LivingToolState;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.util.Lazy;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
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
		if (toolAction == ToolActions.SWORD_SWEEP && getLivingToolState(stack) == LivingToolState.EXALTED) return true;
		return super.canPerformAction(stack, toolAction);
	}

	private PlayState onAnim(AnimationEvent<LivingLongClawsItem> event) {
		List<ItemStack> extraData = event.getExtraDataOfType(ItemStack.class);
		LivingToolState state = !extraData.isEmpty() ? getLivingToolState(extraData.get(0)) : LivingToolState.DORMANT;

		AnimationController<LivingLongClawsItem> controller = event.getController();

		if (state == LivingToolState.AWAKE) {
			controller.setAnimation(new AnimationBuilder().loop("long_claws.awake"));
		}
		else if (state == LivingToolState.EXALTED) {
			controller.setAnimation(new AnimationBuilder().playOnce("long_claws.extend").loop("long_claws.extended"));
		}
		else {
			controller.setAnimation(new AnimationBuilder().loop("long_claws.dormant"));
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final LongClawsRenderer renderer = new LongClawsRenderer();

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
