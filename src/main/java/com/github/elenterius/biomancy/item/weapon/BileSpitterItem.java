package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.client.render.item.dev.BileSpitterRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.item.IArmPoseProvider;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Deprecated(forRemoval = true)
public class BileSpitterItem extends ProjectileWeaponItem implements ItemTooltipStyleProvider, GeoItem, IArmPoseProvider {

	public final float drawTime = 50f;
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public BileSpitterItem(Properties properties) {
		super(properties);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return itemStack -> false;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 15;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final BileSpitterRenderer renderer = new BileSpitterRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	public float getPullProgress(ItemStack stack, LivingEntity livingEntity) {
		return (stack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / drawTime;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 3600 * 20;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide) {
			broadcastAnimation((ServerLevel) level, player, stack, Animation.CHARGE_THEN_HOLD);
		}
		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player player) {
			if (!level.isClientSide) {
				ModProjectiles.CORROSIVE.shoot(level, entityLiving);
				broadcastAnimation((ServerLevel) level, player, stack, Animation.IDLE);
			}
			player.awardStat(Stats.ITEM_USED.get(this));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<BileSpitterItem> controller = new AnimationController<>(this, Animation.MAIN_CONTROLLER, 1, event -> PlayState.CONTINUE);
		controller.setAnimation(Animation.IDLE.rawAnimation);
		Animation.registerTriggerableAnimations(controller);
		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected void broadcastAnimation(ServerLevel level, Player player, ItemStack stack, Animation animation) {
		long id = GeoItem.getOrAssignId(stack, level);
		triggerAnim(player, id, animation.controller, animation.name);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public HumanoidModel.ArmPose getArmPose(Player player, InteractionHand usedHand, ItemStack stack) {
		if (player.getUseItemRemainingTicks() > 0) {
			return HumanoidModel.ArmPose.CROSSBOW_HOLD;
		}
		return HumanoidModel.ArmPose.ITEM;
	}

	protected record Animation(String controller, String name, RawAnimation rawAnimation) {
		private static final List<Animation> ANIMATIONS = new ArrayList<>();
		static final String MAIN_CONTROLLER = "main";
		static final Animation IDLE = register(MAIN_CONTROLLER, "idle", RawAnimation.begin().thenLoop("bile_spitter.anim.idle"));
		static final Animation CHARGE_THEN_HOLD = register(MAIN_CONTROLLER, "charge_then_hold", RawAnimation.begin().thenPlay("bile_spitter.anim.charge").thenLoop("bile_spitter.anim.hold_charge"));

		static Animation register(String controller, String name, RawAnimation rawAnimation) {
			Animation animation = new Animation(controller, name, rawAnimation);
			ANIMATIONS.add(animation);
			return animation;
		}

		static void registerTriggerableAnimations(AnimationController<BileSpitterItem> controller) {
			for (Animation animation : ANIMATIONS) {
				if (animation.controller.equals(controller.getName())) {
					controller.triggerableAnim(animation.name, animation.rawAnimation);
				}
			}
		}

	}

}
