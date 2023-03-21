package com.github.elenterius.biomancy.world.item;

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
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
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

public class GuideBookItem extends SimpleItem implements IAnimatable {

	public static final ResourceLocation GUIDE_BOOK_ID = BiomancyMod.createRL("guide_book");
	protected static final String BOOK_OPEN_KEY = "IsBookOpen";
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public GuideBookItem(Properties properties) {
		super(properties);
	}

	private static ItemStack getItemStack(AnimationEvent<?> event) {
		List<Object> extraData = event.getExtraData();
		if (!extraData.isEmpty() && extraData.get(0) instanceof ItemStack stack) return stack;
		return ItemStack.EMPTY;
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
			boolean canOpenBook = ModsCompatHandler.getModonomiconHelper().openBook(GUIDE_BOOK_ID);
			if (!canOpenBook && player instanceof LocalPlayer localPlayer) {
				Minecraft.getInstance().setScreen(new AdvancementsScreen(localPlayer.connection.getAdvancements())); //fallback
			}
		}

		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (level.isClientSide) return;

		CompoundTag tag = stack.getOrCreateTag();
		boolean isBookOpen = tag.getBoolean(BOOK_OPEN_KEY);

		if (isSelected) {
			if (!isBookOpen) {
				GeckoLibUtil.writeIDToStack(stack, (ServerLevel) level);
				tag.putBoolean(BOOK_OPEN_KEY, true);
			}
		}
		else {
			if (isBookOpen) {
				GeckoLibUtil.writeIDToStack(stack, (ServerLevel) level);
				tag.putBoolean(BOOK_OPEN_KEY, false);
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity) {
		if (itemEntity.level.isClientSide) return super.onEntityItemUpdate(stack, itemEntity);

		CompoundTag tag = stack.getOrCreateTag();
		if (tag.getBoolean(BOOK_OPEN_KEY)) {
			GeckoLibUtil.writeIDToStack(stack, (ServerLevel) itemEntity.level);
			tag.putBoolean(BOOK_OPEN_KEY, false);

			itemEntity.setItem(stack.copy()); //we need to update ItemEntity with a new ItemStack instance to force the sync to the client
		}

		return super.onEntityItemUpdate(stack, itemEntity);
	}

	private PlayState handleAnim(AnimationEvent<GuideBookItem> event) {
		AnimationController<GuideBookItem> controller = event.getController();
		ItemStack stack = getItemStack(event);
		boolean isBookOpen = stack.getOrCreateTag().getBoolean(BOOK_OPEN_KEY);

		if (isBookOpen) {
			controller.setAnimation(new AnimationBuilder().playOnce("opening").loop("open_idle"));
		}
		else {
			controller.setAnimation(new AnimationBuilder().playOnce("closing").loop("closed_idle"));
		}

		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<GuideBookItem> controller = new AnimationController<>(this, "controller", 10, this::handleAnim);
		controller.setAnimation(new AnimationBuilder().loop("closed_idle"));
		data.addAnimationController(controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

}
