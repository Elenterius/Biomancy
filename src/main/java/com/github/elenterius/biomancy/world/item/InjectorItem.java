package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.gui.InjectorScreen;
import com.github.elenterius.biomancy.client.render.item.injector.InjectorRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModCapabilities;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.inventory.InjectorItemInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.world.serum.Serum;
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

public class InjectorItem extends Item implements ISerumProvider, ICustomTooltip, IKeyListener, IAnimatable, ISyncable {

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
		if (MobUtil.canPierceThroughArmor(injectorStack, target)) {
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
		if (item instanceof InjectorItem) return;
		if (!(item instanceof ISerumProvider)) return;

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
			broadcastAnimation((ServerLevel) level, player, stack, AnimState.INJECT_SELF.id);
			player.getCooldowns().addCooldown(this, COOL_DOWN_TICKS);
		}
		return InteractionResultHolder.consume(stack);
	}

	public boolean canInteractWithPlayerSelf(ItemStack stack, Player player) {
		return getSerum(stack).canAffectPlayerSelf(Serum.getDataTag(stack), player);
	}

	public boolean canInteractWithLivingTarget(ItemStack stack, Player player, LivingEntity target) {
		return getSerum(stack).canAffectEntity(Serum.getDataTag(stack), player, target);
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
		broadcastAnimation((ServerLevel) target.level, player, realStack, AnimState.INJECT_OTHER.id);

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
		Optional<LargeSingleItemStackHandler> optional = getItemHandler(stack);
		if (optional.isPresent()) {
			ItemStack foundStack = optional.get().getStack();
			if (foundStack.getItem() instanceof ISerumProvider provider) {
				return provider.getSerum(foundStack);
			}
		}
		return Serum.EMPTY;
	}

	public void consumeSerum(ItemStack stack, @Nullable Player player) {
		if (player != null && player.isCreative()) return;
		getItemHandler(stack).ifPresent(handler -> consumeSerum(handler, player));
	}

	private void consumeSerum(LargeSingleItemStackHandler handler, @Nullable Player player) {
		if (handler.getStack().getItem() instanceof ISerumProvider) {
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

	private void broadcastAnimation(ServerLevel level, Player player, ItemStack stack, int state) {
		int id = GeckoLibUtil.guaranteeIDForStack(stack, level);
		PacketDistributor.PacketTarget target = PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player);
		GeckoLibNetwork.syncAnimation(target, this, id, state);
	}

	@Override
	public void onAnimationSync(int id, int state) {
		//client side for living entities: stack.setEntityRepresentation(); ?

		if (AnimState.INJECT_OTHER.is(state)) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(factory, id, DEFAULT_ANIM_CONTROLLER);
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload(); //make sure animation can play more than once (animations are usually cached)
				controller.setAnimation(new AnimationBuilder().playOnce(AnimState.INJECT_OTHER.animationName));
			}
		}
		else if (AnimState.INJECT_SELF.is(state)) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(factory, id, DEFAULT_ANIM_CONTROLLER);
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload(); //make sure animation can play more than once (animations are usually cached)
				controller.setAnimation(new AnimationBuilder().playOnce(AnimState.INJECT_SELF.animationName));
			}
		}
		else if (AnimState.REGROW_NEEDLE.is(state)) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(factory, id, NEEDLE_ANIM_CONTROLLER);
			controller.markNeedsReload(); //make sure animation can play more than once (animations are usually cached)
			controller.setAnimation(new AnimationBuilder().playOnce(AnimState.REGROW_NEEDLE.animationName));
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

	enum AnimState {
		INJECT_OTHER(0, "injector.inject"),
		INJECT_SELF(1, "injector.inject.self"),
		REGROW_NEEDLE(2, "injector.regrow_needle");

		private final int id;
		private final String animationName;

		AnimState(int id, String animationName) {
			this.id = id;
			this.animationName = animationName;
		}

		public boolean is(int otherId) {
			return otherId == id;
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

	static class InjectionScheduler {

		public static final String DELAY_KEY = "DelayInTicks";
		public static final String TIMESTAMP_KEY = "ScheduleTimestamp";
		private InjectionScheduler() {}

		public static void schedule(InjectorItem injector, ItemStack stack, Player player, LivingEntity target, int delayInTicks) {
			if (stack.isEmpty() || player.level.isClientSide || injector.getSerum(stack).isEmpty()) return;

			injector.setEntityHost(stack, player); //who is using the item
			injector.setEntityVictim(stack, target); //who is the victim

			injector.setInjectionSuccess(stack, MobUtil.canPierceThroughArmor(stack, target)); //precompute injection success

			CompoundTag tag = stack.getOrCreateTag();
			tag.putInt(DELAY_KEY, delayInTicks);
			tag.putLong(TIMESTAMP_KEY, player.level.getGameTime());
		}

		public static void tick(ServerLevel level, InjectorItem injector, ItemStack stack, ServerPlayer player) {
			CompoundTag tag = stack.getOrCreateTag();
			if (!tag.contains(TIMESTAMP_KEY)) return;

			long delayInTicks = tag.getLong(DELAY_KEY);
			long starTimestamp = tag.getLong(TIMESTAMP_KEY);
			if (player.level.getGameTime() - starTimestamp > delayInTicks) {
				performScheduledSerumInjection(level, injector, stack, player);
				tag.remove(DELAY_KEY);
				tag.remove(TIMESTAMP_KEY);
			}
		}

		public static void performScheduledSerumInjection(ServerLevel level, InjectorItem injector, ItemStack stack, ServerPlayer player) {
			Serum serum = injector.getSerum(stack);
			if (serum.isEmpty()) return;

			Entity victim = injector.getEntityVictim(stack, level);
			Entity host = injector.getEntityHost(stack, level);
			boolean injectionSuccess = injector.getInjectionSuccess(stack);

			if (victim instanceof LivingEntity target) {
				if (!injectionSuccess) {
					stack.hurtAndBreak(2, player, p -> {});
					player.broadcastBreakEvent(EquipmentSlot.MAINHAND); //break needle
					injector.broadcastAnimation(level, player, stack, AnimState.REGROW_NEEDLE.id);
					player.getCooldowns().addCooldown(stack.getItem(), COOL_DOWN_TICKS * 2);
					return;
				}

				if (host == victim) {
					serum.affectPlayerSelf(Serum.getDataTag(stack), player);
				}
				else {
					serum.affectEntity(level, Serum.getDataTag(stack), player, target);
				}

				injector.consumeSerum(stack, player);
				stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

				if (stack.getEnchantmentLevel(ModEnchantments.ANESTHETIC.get()) <= 0) {
					target.hurt(new EntityDamageSource("sting", player), 0.5f);
				}
			}
		}
	}

}
