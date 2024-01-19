package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.render.item.guidebook.GuideBookRenderer;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GuideBookItem extends SimpleItem implements GeoItem {

	public static final String MAIN_ANIM_CONTROLLER = "main";
	public static final RawAnimation CLOSED_IDLE_ANIM = RawAnimation.begin().thenLoop("closed_idle");
	public static final RawAnimation OPEN_THEN_IDLE_ANIM = RawAnimation.begin().thenPlay("opening").thenLoop("open_idle");
	public static final RawAnimation CLOSE_THEN_IDLE_ANIM = RawAnimation.begin().thenPlay("closing").thenLoop("closed_idle");

	public static final ResourceLocation GUIDE_BOOK_ID = BiomancyMod.createRL("guide_book");
	protected static final String BOOK_OPEN_KEY = "IsBookOpen";
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public GuideBookItem(Properties properties) {
		super(properties);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final GuideBookRenderer renderer = new GuideBookRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (level.isClientSide) {
			tryToOpenClientScreen(player);
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@OnlyIn(Dist.CLIENT)
	private void tryToOpenClientScreen(Player player) {
		boolean canOpenBook = ModsCompatHandler.getModonomiconHelper().openBook(GUIDE_BOOK_ID);
		if (!canOpenBook && player instanceof LocalPlayer localPlayer) {
			Minecraft.getInstance().setScreen(new AdvancementsScreen(localPlayer.connection.getAdvancements())); //fallback
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level.isClientSide) return;

		CompoundTag tag = stack.getOrCreateTag();
		boolean isBookOpen = tag.getBoolean(BOOK_OPEN_KEY);

		if (isSelected) {
			if (!isBookOpen) {
				GeoItem.getOrAssignId(stack, (ServerLevel) level);
				tag.putBoolean(BOOK_OPEN_KEY, true);
			}
		}
		else {
			if (isBookOpen) {
				GeoItem.getOrAssignId(stack, (ServerLevel) level);
				tag.putBoolean(BOOK_OPEN_KEY, false);
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity) {
		if (itemEntity.level().isClientSide) return super.onEntityItemUpdate(stack, itemEntity);

		CompoundTag tag = stack.getOrCreateTag();
		if (tag.getBoolean(BOOK_OPEN_KEY)) {
			GeoItem.getOrAssignId(stack, (ServerLevel) itemEntity.level());
			tag.putBoolean(BOOK_OPEN_KEY, false);

			itemEntity.setItem(stack.copy()); //we need to update ItemEntity with a new ItemStack instance to force the sync to the client
		}

		return super.onEntityItemUpdate(stack, itemEntity);
	}

	private PlayState handleAnimation(AnimationState<GuideBookItem> state) {
		AnimationController<GuideBookItem> controller = state.getController();
		ItemStack stack = state.getData(DataTickets.ITEMSTACK);
		boolean isBookOpen = stack.getOrCreateTag().getBoolean(BOOK_OPEN_KEY);

		if (isBookOpen) {
			controller.setAnimation(OPEN_THEN_IDLE_ANIM);
		}
		else {
			controller.setAnimation(CLOSE_THEN_IDLE_ANIM);
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		AnimationController<GuideBookItem> controller = new AnimationController<>(this, MAIN_ANIM_CONTROLLER, 10, this::handleAnimation);
		controller.setAnimation(CLOSED_IDLE_ANIM);
		controllers.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

}
