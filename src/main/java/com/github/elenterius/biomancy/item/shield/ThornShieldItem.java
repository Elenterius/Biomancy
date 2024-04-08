package com.github.elenterius.biomancy.item.shield;

import com.github.elenterius.biomancy.client.render.item.shield.ThornShieldRenderer;
import com.github.elenterius.biomancy.item.ShieldBlockingListener;
import com.github.elenterius.biomancy.item.SimpleItem;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ThornShieldItem extends SimpleItem implements Equipable, ShieldBlockingListener, GeoItem {

	public static final String BLOCKING_KEY = "blocking";
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public ThornShieldItem(Properties properties) {
		super(properties);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltip, isAdvanced);
		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(TextComponentUtil.getTooltipText("ability.thorny_hide").withStyle(TextStyles.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getTooltipText("ability.thorny_hide.desc")).withStyle(TextStyles.DARK_GRAY));
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
	}

	@Override
	public void onShieldBlocking(ItemStack shield, LivingEntity user, LivingEntity attacker) {
		attacker.hurt(user.damageSources().thorns(user), 1.5f + user.getRandom().nextInt(4));
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.OFFHAND;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		if (level instanceof ServerLevel serverLevel && player.isUsingItem() && player.getUseItem() == stack) {
			GeoItem.getOrAssignId(stack, serverLevel);
			stack.getOrCreateTag().putBoolean(BLOCKING_KEY, true);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onStopUsing(ItemStack stack, LivingEntity livingEntity, int timeUsed) {
		if (!livingEntity.level().isClientSide()) {
			stack.getOrCreateTag().putBoolean(BLOCKING_KEY, false);
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final ThornShieldRenderer renderer = new ThornShieldRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<ThornShieldItem> controller = new AnimationController<>(this, "main", state -> {
			ItemStack stack = state.getData(DataTickets.ITEMSTACK);
			boolean isBlocking = stack.getOrCreateTag().getBoolean(BLOCKING_KEY);

			if (isBlocking) {
				if (state.isCurrentAnimationStage("retracted")) {
					return state.setAndContinue(Animations.TRANSITION_TO_EXTENDED);
				}
				return state.setAndContinue(Animations.EXTENDED);
			}

			if (state.isCurrentAnimationStage("extended")) {
				return state.setAndContinue(Animations.TRANSITION_TO_RETRACTED);
			}
			return state.setAndContinue(Animations.RETRACTED);
		});

		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static final class Animations {
		public static final RawAnimation TRANSITION_TO_RETRACTED = RawAnimation.begin().thenPlay("retract").thenPlay("retracted");
		public static final RawAnimation RETRACTED = RawAnimation.begin().thenPlay("retracted");
		public static final RawAnimation TRANSITION_TO_EXTENDED = RawAnimation.begin().thenPlay("extend").thenPlay("extended");
		public static final RawAnimation EXTENDED = RawAnimation.begin().thenPlay("extended");

		private Animations() {}

	}

}
