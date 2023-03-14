package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.client.render.item.dev.BileSpitterRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModProjectiles;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.world.item.IArmPoseProvider;
import com.github.elenterius.biomancy.world.item.ICustomTooltip;
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
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BileSpitterItem extends ProjectileWeaponItem implements ICustomTooltip, IAnimatable, ISyncable, IArmPoseProvider {

	private static final String CONTROLLER_NAME = "controller";
	public final float drawTime = 50f;
	private final AnimationFactory animationFactory = new AnimationFactory(this);

	public BileSpitterItem(Properties properties) {
		super(properties);
		GeckoLibNetwork.registerSyncable(this);
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
			broadcastAnimation((ServerLevel) level, player, stack, Animation.CHARGE);
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
		ClientTextUtil.appendItemInfoTooltip(stack.getItem(), tooltip);
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
	public void registerControllers(AnimationData data) {
		//IMPORTANT: transitionLengthTicks needs to be larger than 0, or else the controller.currentAnimation might be null and a NPE is thrown
		AnimationController<BileSpitterItem> controller = new AnimationController<>(this, CONTROLLER_NAME, 1, event -> PlayState.CONTINUE);
		controller.setAnimation(new AnimationBuilder().loop(Animation.IDLE.name()));
		data.addAnimationController(controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	private void broadcastAnimation(ServerLevel level, Player player, ItemStack stack, Animation state) {
		int stackId = GeckoLibUtil.guaranteeIDForStack(stack, level);
		PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
		GeckoLibNetwork.syncAnimation(target, this, stackId, state.id);
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

	@Override
	public void onAnimationSync(int id, int state) {
		if (Animation.CHARGE.is(state)) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(animationFactory, id, CONTROLLER_NAME);
			controller.markNeedsReload(); //make sure animation can play more than once (animations are usually cached)
			controller.setAnimation(new AnimationBuilder().addAnimation(Animation.CHARGE.name).loop(Animation.HOLD_CHARGE.name()));
		} else if (Animation.IDLE.is(state)) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(animationFactory, id, CONTROLLER_NAME);
			controller.setAnimation(new AnimationBuilder().loop(Animation.IDLE.name()));
		}
	}

	protected record Animation(int id, String name) {
		static Animation IDLE = new Animation(0, "bile_spitter.anim.idle");
		static Animation CHARGE = new Animation(1, "bile_spitter.anim.charge");
		static Animation HOLD_CHARGE = new Animation(2, "bile_spitter.anim.hold_charge");

		public boolean is(int otherId) {
			return otherId == id;
		}
	}

}
