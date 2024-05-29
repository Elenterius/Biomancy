package com.github.elenterius.biomancy.item.shield;

import com.github.elenterius.biomancy.client.render.item.shield.ThornShieldRenderer;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.ShieldBlockingListener;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ThornShieldItem extends LivingShieldItem implements Equipable, ShieldBlockingListener, GeoItem {

	public static final String BLOCKING_TAG_KEY = "blocking";
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public ThornShieldItem(int maxNutrients, Properties properties) {
		super(maxNutrients, properties);
	}

	@Override
	public void appendLivingToolTooltip(ItemStack stack, List<Component> tooltip) {
		tooltip.add(TextComponentUtil.getAbilityText("thorny_hide").withStyle(TextStyles.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("thorny_hide.desc")).withStyle(TextStyles.DARK_GRAY));

		super.appendLivingToolTooltip(stack, tooltip);
	}

	@Override
	public void onShieldBlocking(ItemStack shield, LivingEntity user, LivingEntity attacker) {
		attacker.hurt(user.damageSources().thorns(user), 1.5f + user.getRandom().nextInt(4));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!hasNutrients(stack)) {
			if (level.isClientSide()) {
				player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
				player.playSound(ModSoundEvents.FLESHKIN_NO.get(), 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
			}
			return InteractionResultHolder.fail(stack);
		}

		player.startUsingItem(hand);
		if (level instanceof ServerLevel serverLevel && player.isUsingItem() && player.getUseItem() == stack) {
			GeoItem.getOrAssignId(stack, serverLevel);
			stack.getOrCreateTag().putBoolean(BLOCKING_TAG_KEY, true);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onStopUsing(ItemStack stack, LivingEntity livingEntity, int timeUsed) {
		if (livingEntity.level() instanceof ServerLevel serverLevel) {
			GeoItem.getOrAssignId(stack, serverLevel);
			stack.getOrCreateTag().putBoolean(BLOCKING_TAG_KEY, false);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
		if (!isSelected) return;
		if (level.isClientSide) return;

		//fallback, because in some rare cases the `IForgeItem.onStopUsing()` method isn't called
		if (entity instanceof LivingEntity livingEntity && livingEntity.getUseItem() != stack) {
			boolean isBlocking = stack.getOrCreateTag().getBoolean(BLOCKING_TAG_KEY);
			if (isBlocking) {
				GeoItem.getOrAssignId(stack, (ServerLevel) level);
				stack.getOrCreateTag().putBoolean(BLOCKING_TAG_KEY, false);
			}
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
			boolean isBlocking = stack.getOrCreateTag().getBoolean(BLOCKING_TAG_KEY);

			if (isBlocking) {
				return state.setAndContinue(Animations.TRANSITION_TO_EXTENDED);
			}

			if (!state.isCurrentAnimationStage("retracted")) {
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
		public static final RawAnimation RETRACTED = RawAnimation.begin().thenPlay("retracted");
		public static final RawAnimation TRANSITION_TO_RETRACTED = RawAnimation.begin().thenPlay("retract").thenPlay("retracted");
		public static final RawAnimation TRANSITION_TO_EXTENDED = RawAnimation.begin().thenPlay("extend").thenPlay("extended");

		private Animations() {}

	}

}
