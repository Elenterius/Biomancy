package com.github.elenterius.biomancy.item.injector;

import com.github.elenterius.biomancy.api.serum.Serum;
import com.github.elenterius.biomancy.api.serum.SerumContainer;
import com.github.elenterius.biomancy.api.serum.SerumInjector;
import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.gui.InjectorScreen;
import com.github.elenterius.biomancy.client.render.item.injector.InjectorRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.inventory.InjectorItemInventory;
import com.github.elenterius.biomancy.inventory.itemhandler.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.item.ICustomTooltip;
import com.github.elenterius.biomancy.item.IKeyListener;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class InjectorItem extends Item implements SerumInjector, ICustomTooltip, IKeyListener, IAnimatable, ISyncable {

	public static final short MAX_SLOT_SIZE = 16;
	public static final String INVENTORY_TAG = "inventory";
	public static final int COOL_DOWN_TICKS = 25;
	public static final int SCHEDULE_TICKS = Mth.ceil(0.32f * 20);
	protected static final String CURRENT_VICTIM_KEY = "CurrentVictimId";
	protected static final String CURRENT_HOST_KEY = "CurrentHostId";
	protected static final String NEEDLE_ANIM_CONTROLLER = "needle";
	private static final String DEFAULT_ANIM_CONTROLLER = "controller";
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public InjectorItem(Properties properties) {
		super(properties);
		GeckoLibNetwork.registerSyncable(this);
	}

	public static boolean tryInjectLivingEntity(ServerLevel level, BlockPos pos, ItemStack stack) {
		if (!(stack.getItem() instanceof InjectorItem injectorItem) || stack.getDamageValue() >= stack.getMaxDamage() - 1) return false;

		Serum serum = injectorItem.getSerum(stack);
		if (!serum.isEmpty()) {
			List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos), EntitySelector.NO_SPECTATORS);
			if (entities.isEmpty()) return false;
			LivingEntity target = entities.get(0);
			if (target.isAlive() && dispenserAffectEntity(level, pos, serum, stack, injectorItem, target)) {
				level.playSound(null, pos, ModSoundEvents.INJECTOR_INJECT.get(), SoundSource.BLOCKS, 0.8f, 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f);
				level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, target.blockPosition(), 0);
				return true;
			}
		}

		return false;
	}

	private static boolean dispenserAffectEntity(ServerLevel level, BlockPos pos, Serum serum, ItemStack injectorStack, InjectorItem injectorItem, LivingEntity target) {
		if (CombatUtil.canPierceThroughArmor(injectorStack, target)) {
			CompoundTag dataTag = Serum.getDataTag(injectorStack);
			if (serum.canAffectEntity(dataTag, null, target)) {
				serum.affectEntity(level, dataTag, null, target);
				injectorItem.consumeSerum(injectorStack, null); //TODO: drop appropriate vials/container
				injectorStack.hurt(1, level.getRandom(), null);
				if (injectorStack.getEnchantmentLevel(ModEnchantments.ANESTHETIC.get()) <= 0) {
					target.hurt(new EntityDamageSource("sting", null), 0.5f);
				}
				return true;
			}
		}
		else {
			injectorStack.hurt(2, level.getRandom(), null);
			level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5f, 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f);
		}
		return false;
	}

	public static Optional<LargeSingleItemStackHandler> getItemHandler(ItemStack stack) {
		return stack.getCapability(ModCapabilities.ITEM_HANDLER).filter(LargeSingleItemStackHandler.class::isInstance).map(LargeSingleItemStackHandler.class::cast);
	}

	@OnlyIn(Dist.CLIENT)
	private void tryToOpenClientScreen(InteractionHand hand) {
		Screen currScreen = Minecraft.getInstance().screen;
		if (currScreen == null && Minecraft.getInstance().player != null) {
			Minecraft.getInstance().setScreen(new InjectorScreen(hand));
			Minecraft.getInstance().player.playNotifySound(ModSoundEvents.UI_RADIAL_MENU_OPEN.get(), SoundSource.PLAYERS, 1f, 1f);
		}
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (slot.getType() == EquipmentSlot.Type.HAND) {
			InteractionHand hand = slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			tryToOpenClientScreen(hand);
		}
		return InteractionResultHolder.fail(flags); //don't send button press to server
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (flags == -2) {
			clearInventory(stack, player);
		}

		if (flags >= 0) {
			setInventory(stack, player, flags);
		}
	}

	/**
	 * replace contents of injector inventory with new item from player inventory
	 */
	private void setInventory(ItemStack injector, Player player, int slotIndex) {
		ItemStack foundStack = player.getInventory().getItem(slotIndex);

		Item item = foundStack.getItem();
		if (!(item instanceof SerumContainer)) return;

		getItemHandler(injector).ifPresent(handler -> {
			ItemStack oldStack = ItemStack.EMPTY;
			if (!handler.getStack().isEmpty()) {
				oldStack = handler.extractItem(handler.getMaxAmount(), false);
			}

			ItemStack remainder = handler.insertItem(foundStack, false);
			player.getInventory().setItem(slotIndex, remainder);

			//eject old stuff
			if (!oldStack.isEmpty() && !player.addItem(oldStack)) {
				player.drop(oldStack, false);
			}
		});
	}

	private void clearInventory(ItemStack injector, Player player) {
		getItemHandler(injector).ifPresent(handler -> {
			if (!handler.getStack().isEmpty()) {
				ItemStack result = handler.extractItem(handler.getMaxAmount(), false);
				if (!player.addItem(result)) {
					player.drop(result, false);
				}
			}
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);

		if (!player.isShiftKeyDown()) return InteractionResultHolder.pass(stack);
		if (player.getCooldowns().isOnCooldown(this) || !canInteractWithPlayerSelf(stack, player)) {
			SoundUtil.playItemSoundEffect(level, player, ModSoundEvents.INJECTOR_FAIL);
			return InteractionResultHolder.fail(stack);
		}

		if (!level.isClientSide) {
			InjectionScheduler.schedule(this, stack, player, player, SCHEDULE_TICKS);
			broadcastAnimation((ServerLevel) level, player, stack, InjectorAnimationState.INJECT_SELF.id);
			player.getCooldowns().addCooldown(this, COOL_DOWN_TICKS);
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack copyOfStack, Player player, LivingEntity target, InteractionHand usedHand) {
		if (!canInteractWithLivingTarget(copyOfStack, player, target)) {
			if (player.level.isClientSide) SoundUtil.clientPlayItemSound(player.level, player, ModSoundEvents.INJECTOR_FAIL.get());
			return InteractionResult.FAIL;
		}

		if (player.level.isClientSide) return InteractionResult.CONSUME;
		if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;

		player.getCooldowns().addCooldown(this, COOL_DOWN_TICKS);

		ItemStack realStack = player.getAbilities().instabuild ? player.getItemInHand(usedHand) : copyOfStack;
		InjectionScheduler.schedule(this, realStack, player, target, SCHEDULE_TICKS);
		broadcastAnimation((ServerLevel) target.level, player, realStack, InjectorAnimationState.INJECT_OTHER.id);

		return InteractionResult.CONSUME;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (entity instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
			InjectionScheduler.tick(serverLevel, this, stack, player);
		}
	}

	@Override
	public Serum getSerum(ItemStack stack) {
		return getItemHandler(stack).map(LargeSingleItemStackHandler::getItem)
				.filter(SerumContainer.class::isInstance)
				.map(SerumContainer.class::cast)
				.map(SerumContainer::getSerum).orElse(Serum.EMPTY);
	}

	public ItemStack getSerumItemStack(ItemStack stack) {
		return getItemHandler(stack).map(LargeSingleItemStackHandler::getStack).orElse(ItemStack.EMPTY);
	}

	public void consumeSerum(ItemStack stack, @Nullable Player player) {
		if (player != null && player.isCreative()) return;
		getItemHandler(stack).ifPresent(handler -> consumeSerum(handler, player));
	}

	private void consumeSerum(LargeSingleItemStackHandler handler, @Nullable Player player) {
		if (handler.getStack().getItem() instanceof SerumContainer) {
			ItemStack stack = handler.extractItem(1, false);
			if (stack.hasCraftingRemainingItem()) {
				ItemStack containerItem = stack.getCraftingRemainingItem();
				if (!containerItem.isEmpty() && player != null && !player.addItem(containerItem)) {
					player.drop(containerItem, false);
				}
			}
		}
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new InventoryCapability(stack);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final InjectorRenderer renderer = new InjectorRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Nullable
	private InjectorRenderer getInjectorRenderer(InjectorItem item) {
		BlockEntityWithoutLevelRenderer renderer = IClientItemExtensions.of(this).getCustomRenderer();
		return renderer instanceof InjectorRenderer injectorRenderer ? injectorRenderer : null;
	}

	/**
	 * Stores the id of the entity that is holding/storing this ItemStack
	 */
	public void setEntityHost(ItemStack stack, Entity entity) {
		if (stack.isEmpty()) return;
		stack.getOrCreateTag().putInt(CURRENT_HOST_KEY, entity.getId());
	}

	public void setInjectionSuccess(ItemStack stack, boolean flag) {
		if (stack.isEmpty()) return;
		stack.getOrCreateTag().putBoolean("IsInjectionSuccess", flag);
	}

	public boolean getInjectionSuccess(ItemStack stack) {
		if (stack.isEmpty()) return false;
		return stack.getOrCreateTag().getBoolean("IsInjectionSuccess");
	}

	public void removeEntityHost(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null) {
				tag.remove(CURRENT_HOST_KEY);
			}
		}
	}

	@Nullable
	public Entity getEntityHost(ItemStack stack, Level level) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null && tag.contains(CURRENT_HOST_KEY, Tag.TAG_ANY_NUMERIC)) {
				return level.getEntity(tag.getInt(CURRENT_HOST_KEY));
			}
		}

		return null;

		//return stack.getEntityRepresentation(); //usually null, ItemFrame or ItemEntity
		//this would make it possible to play sounds/etc. via item animation at an ItemFrame or ItemEntity position
	}

	public void setEntityVictim(ItemStack stack, Entity entity) {
		if (stack.isEmpty()) return;
		stack.getOrCreateTag().putInt(CURRENT_VICTIM_KEY, entity.getId());
	}

	public void removeEntityVictim(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null) {
				tag.remove(CURRENT_VICTIM_KEY);
			}
		}
	}

	@Nullable
	public Entity getEntityVictim(ItemStack stack, Level level) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null && tag.contains(CURRENT_VICTIM_KEY, Tag.TAG_ANY_NUMERIC)) {
				return level.getEntity(tag.getInt(CURRENT_VICTIM_KEY));
			}
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void onSoundKeyFrame(ItemStack stack, String soundId, double animationTick) {
		Level level = Minecraft.getInstance().level;
		if (level == null) return;
		if (!getInjectionSuccess(stack)) return;

		Entity soundOrigin = getEntityHost(stack, level);
		if (soundOrigin == null) return;

		LocalPlayer client = Minecraft.getInstance().player;
		level.playSound(client, soundOrigin.getX(), soundOrigin.getY(0.5f), soundOrigin.getZ(), ModSoundEvents.INJECTOR_INJECT.get(), SoundSource.PLAYERS, 0.8f, 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f);

		Entity victim = getEntityVictim(stack, level);
		if (victim != null) {
			level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, victim.blockPosition(), 0);
		}
	}

	private void soundListener(SoundKeyframeEvent<InjectorItem> event) {
		if (IClientItemExtensions.of(this).getCustomRenderer() instanceof InjectorRenderer renderer) {
			onSoundKeyFrame(renderer.getCurrentItemStack(), event.sound, event.getAnimationTick());
		}
	}

	@Override
	public void registerControllers(AnimationData data) {
		//IMPORTANT: transitionLengthTicks needs to be larger than 0, or else the controller.currentAnimation might be null and a NPE is thrown
		AnimationController<InjectorItem> controller = new AnimationController<>(this, DEFAULT_ANIM_CONTROLLER, 1, event -> PlayState.CONTINUE);
		controller.registerSoundListener(this::soundListener);
		data.addAnimationController(controller);

		data.addAnimationController(new AnimationController<>(this, NEEDLE_ANIM_CONTROLLER, 1, event -> PlayState.CONTINUE));
	}

	@Override
	public AnimationFactory getFactory() {
		return factory;
	}

	void broadcastAnimation(ServerLevel level, Player player, ItemStack stack, int state) {
		int id = GeckoLibUtil.guaranteeIDForStack(stack, level);
		PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
		GeckoLibNetwork.syncAnimation(target, this, id, state);
	}

	@Override
	public void onAnimationSync(int controllerId, int animationStateId) {
		//client side for living entities: stack.setEntityRepresentation(); ?

		switch (InjectorAnimationState.from(animationStateId)) {
			case INJECT_OTHER -> playAnimation(controllerId, InjectorAnimationState.INJECT_OTHER, DEFAULT_ANIM_CONTROLLER);
			case INJECT_SELF -> playAnimation(controllerId, InjectorAnimationState.INJECT_SELF, DEFAULT_ANIM_CONTROLLER);
			case REGROW_NEEDLE -> playAnimation(controllerId, InjectorAnimationState.REGROW_NEEDLE, NEEDLE_ANIM_CONTROLLER);
			case INJECT_FAIL -> playAnimation(controllerId, InjectorAnimationState.INJECT_FAIL, DEFAULT_ANIM_CONTROLLER);
			case EMPTY -> { /* do nothing */ }
		}
	}

	private void playAnimation(int controllerId, InjectorAnimationState animationState, String animationController) {
		AnimationController<?> controller = GeckoLibUtil.getControllerForID(factory, controllerId, animationController);
		if (controller.getAnimationState() == AnimationState.Stopped) {
			controller.markNeedsReload(); //make sure animation can play more than once (animations are usually cached)
			controller.setAnimation(animationState.animation);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ComponentUtil.horizontalLine());
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.emptyLine());

		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(INVENTORY_TAG)) {
			Serum serum = getSerum(stack);
			if (!serum.isEmpty()) {
				short amount = tag.getCompound(INVENTORY_TAG).getShort(LargeSingleItemStackHandler.ITEM_AMOUNT_TAG);
				tooltip.add(ComponentUtil.literal(String.format("%dx ", amount)).append(serum.getDisplayName()).withStyle(ChatFormatting.GRAY));
				serum.appendTooltip(stack, level, tooltip, isAdvanced);
				tooltip.add(ComponentUtil.emptyLine());
			}
		}

		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.open_inventory")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getShiftKey().append(" + ").append(ClientTextUtil.getRightMouseKey()), TextComponentUtil.getTooltipText("action.self_inject")).withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		Serum serum = getSerum(stack);
		return serum.isEmpty() ? displayName : ComponentUtil.mutable().append(displayName).append(" (").append(serum.getDisplayName()).append(")");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.PIERCING || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	enum InjectorAnimationState {
		EMPTY(-1, ""),
		INJECT_OTHER(0, "injector.inject"),
		INJECT_SELF(1, "injector.inject_self"),
		INJECT_FAIL(3, "injector.inject_fail"),
		REGROW_NEEDLE(2, "injector.regrow_needle");

		private final int id;
		private final String nameId;
		private final AnimationBuilder animation;

		InjectorAnimationState(int id, String nameId) {
			this.id = id;
			this.nameId = nameId;
			animation = new AnimationBuilder().playOnce(nameId);
		}

		public static InjectorAnimationState from(int id) {
			if (id < 0 || id > 3) return EMPTY;
			for (InjectorAnimationState animationState : values()) {
				if (animationState.id == id) {
					return animationState;
				}
			}
			return EMPTY;
		}

		public int getId() {
			return id;
		}

		public String getNameId() {
			return nameId;
		}
	}

	private static class InventoryCapability implements ICapabilityProvider {

		private final InjectorItemInventory itemHandler;

		public InventoryCapability(ItemStack stack) {
			itemHandler = InjectorItemInventory.createServerContents(MAX_SLOT_SIZE, stack);
		}

		@NotNull
		@Override
		public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
			return ModCapabilities.ITEM_HANDLER.orEmpty(capability, itemHandler.getOptionalItemHandler());
		}

	}

}
