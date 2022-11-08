package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.client.gui.InjectorScreen;
import com.github.elenterius.biomancy.client.renderer.item.InjectorRenderer;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.tooltip.HrTooltipComponent;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.inventory.InjectorItemInventory;
import com.github.elenterius.biomancy.world.inventory.itemhandler.LargeSingleItemStackHandler;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
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
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.PacketDistributor;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class InjectorItem extends Item implements ISerumProvider, IBiomancyItem, IKeyListener, IAnimatable, ISyncable {

	public static final short MAX_SLOT_SIZE = 16;
	public static final String INVENTORY_TAG = "inventory";
	public static final int COOL_DOWN_TICKS = 25;
	private static final String CONTROLLER_NAME = "controller";
	public static final int SCHEDULE_TICKS = Mth.ceil(0.32f * 20);

	private final AnimationFactory factory = new AnimationFactory(this);

	public InjectorItem(Properties properties) {
		super(properties);
		GeckoLibNetwork.registerSyncable(this);
	}

	public static boolean tryInjectLivingEntity(ServerLevel level, BlockPos pos, ItemStack stack) {
		if (!(stack.getItem() instanceof InjectorItem injectorItem) || stack.getDamageValue() >= stack.getMaxDamage() - 1) return false;

		Serum serum = injectorItem.getSerum(stack);
		if (serum != null) {
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
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).filter(LargeSingleItemStackHandler.class::isInstance).map(LargeSingleItemStackHandler.class::cast);
	}

	@OnlyIn(Dist.CLIENT)
	private void tryToOpenWheelMenu(InteractionHand hand) {
		Screen currScreen = Minecraft.getInstance().screen;
		if (currScreen == null && Minecraft.getInstance().player != null) {
			Minecraft.getInstance().setScreen(new InjectorScreen(hand));
			Minecraft.getInstance().player.playNotifySound(ModSoundEvents.UI_RADIAL_MENU_OPEN.get(), SoundSource.PLAYERS, 1f, 1f);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, ClientLevel level, Player player, EquipmentSlot slot, byte flags) {
		if (slot.getType() == EquipmentSlot.Type.HAND) {
			InteractionHand hand = slot == EquipmentSlot.MAINHAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
			tryToOpenWheelMenu(hand);
		}
		return InteractionResultHolder.fail(flags); //don't send button press to server
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (flags == -2) {
			//clear out whole inventory
			getItemHandler(stack).ifPresent(handler -> {
				if (!handler.getStack().isEmpty()) {
					ItemStack result = handler.extractItem(handler.getMaxAmount(), false);
					if (!player.addItem(result)) {
						player.drop(result, false);
					}
				}
			});
		}

		if (flags >= 0) {
			//replace whole inventory with new item
			final int idx = flags;
			ItemStack foundStack = player.getInventory().getItem(idx);
			Item item = foundStack.getItem();
			if (item instanceof ISerumProvider && !(item instanceof InjectorItem)) {
				getItemHandler(stack).ifPresent(handler -> {
					ItemStack oldStack = ItemStack.EMPTY;
					if (!handler.getStack().isEmpty()) {
						oldStack = handler.extractItem(handler.getMaxAmount(), false);
					}

					ItemStack remainder = handler.insertItem(foundStack, false);
					player.getInventory().setItem(idx, remainder);

					//eject old stuff
					if (!oldStack.isEmpty() && !player.addItem(oldStack)) {
						player.drop(oldStack, false);
					}
				});
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if (player.isShiftKeyDown()) {
			ItemStack stack = player.getItemInHand(usedHand);
			if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);

			if (interactWithPlayerSelf(stack, player)) {
				if (!level.isClientSide) {
					scheduleSerumInjection(stack, player, player, SCHEDULE_TICKS);
					broadcastAnimation((ServerLevel) level, player, stack, AnimState.INJECT_SELF.id);
					player.getCooldowns().addCooldown(this, COOL_DOWN_TICKS);
				}
				return InteractionResultHolder.consume(stack);
			}

			SoundUtil.playItemSoundEffect(level, player, ModSoundEvents.INJECTOR_FAIL);
			return InteractionResultHolder.fail(stack);

		}
		return InteractionResultHolder.pass(player.getItemInHand(usedHand));
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack copyOfStack, Player player, LivingEntity target, InteractionHand usedHand) {
		Serum serum = getSerum(copyOfStack);
		if (serum != null) {
			if (MobUtil.canPierceThroughArmor(copyOfStack, target)) {
				CompoundTag dataTag = Serum.getDataTag(copyOfStack);
				if (serum.canAffectEntity(dataTag, player, target)) {
					if (player.level.isClientSide) return InteractionResult.CONSUME;

					if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;
					player.getCooldowns().addCooldown(this, COOL_DOWN_TICKS);

					ItemStack realStack = player.getAbilities().instabuild ? player.getItemInHand(usedHand) : copyOfStack;
					scheduleSerumInjection(realStack, player, target, SCHEDULE_TICKS);
					broadcastAnimation((ServerLevel) target.level, player, realStack, AnimState.INJECT_OTHER.id);

					return InteractionResult.CONSUME;
				}
			}
			else if (!player.level.isClientSide) {
				copyOfStack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}

			if (player.level.isClientSide) SoundUtil.playLocalItemSound((ClientLevel) player.level, player, ModSoundEvents.INJECTOR_FAIL.get());
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (entity instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
			tickInjectionScheduler(serverLevel, stack, player);
		}
	}

	private void scheduleSerumInjection(ItemStack stack, Player player, LivingEntity target, int delayInTicks) {
		if (stack.isEmpty() || player.level.isClientSide || getSerum(stack) == null) return;

		setEntityHost(stack, player); //who is using the item
		setEntityVictim(stack, target); //who is the victim

		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt("DelayInTicks", delayInTicks);
		tag.putLong("ScheduleTimestamp", player.level.getGameTime());
	}

	private void tickInjectionScheduler(ServerLevel level, ItemStack stack, ServerPlayer player) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains("ScheduleTimestamp")) return;

		long delayInTicks = tag.getLong("DelayInTicks");
		long starTimestamp = tag.getLong("ScheduleTimestamp");
		if (player.level.getGameTime() - starTimestamp > delayInTicks) {
			performScheduledSerumInjection(level, stack, player);
			tag.remove("DelayInTicks");
			tag.remove("ScheduleTimestamp");
		}
	}

	private void performScheduledSerumInjection(ServerLevel level, ItemStack stack, ServerPlayer player) {
		Serum serum = getSerum(stack);
		if (serum != null) {
			Entity victim = getEntityVictim(stack, level);
			Entity host = getEntityHost(stack, level);
			if (victim instanceof LivingEntity target) {
				if (host == victim) {
					serum.affectPlayerSelf(Serum.getDataTag(stack), player);
				}
				else {
					serum.affectEntity(level, Serum.getDataTag(stack), player, target);
				}
			}
			consumeSerum(stack, player);
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
	}

	public boolean interactWithPlayerSelf(ItemStack stack, Player player) {
		Serum serum = getSerum(stack);
		if (serum != null) {
			if (MobUtil.canPierceThroughArmor(stack, player)) {
				return serum.canAffectPlayerSelf(Serum.getDataTag(stack), player);
			}
			else if (!player.level.isClientSide) {
				stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
		}
		return false;
	}

	@Override
	@Nullable
	public Serum getSerum(ItemStack stack) {
		Optional<LargeSingleItemStackHandler> optional = getItemHandler(stack);
		if (optional.isPresent()) {
			ItemStack foundStack = optional.get().getStack();
			if (foundStack.getItem() instanceof ISerumProvider provider) {
				return provider.getSerum(foundStack);
			}
		}
		return null;
	}

	@Override
	public int getSerumColor(ItemStack stack) {
		Serum serum = getSerum(stack);
		return serum != null ? serum.getColor() : -1;
	}

	public void consumeSerum(ItemStack stack, @Nullable Player player) {
		if (player != null && player.isCreative()) return;
		getItemHandler(stack).ifPresent(handler -> consumeSerum(handler, player));
	}

	private void consumeSerum(LargeSingleItemStackHandler handler, @Nullable Player player) {
		if (handler.getStack().getItem() instanceof ISerumProvider) {
			ItemStack stack = handler.extractItem(1, false);
			if (stack.hasContainerItem()) {
				ItemStack containerItem = stack.getContainerItem();
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
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IItemRenderProperties() {
			private final InjectorRenderer renderer = new InjectorRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return renderer;
			}
		});
	}

	@Nullable
	private InjectorRenderer getInjectorRenderer(InjectorItem item) {
		BlockEntityWithoutLevelRenderer renderer = RenderProperties.get(item).getItemStackRenderer();
		return renderer instanceof InjectorRenderer injectorRenderer ? injectorRenderer : null;
	}

	/**
	 * Stores the id of the entity that is holding/storing this ItemStack
	 */
	public void setEntityHost(ItemStack stack, Entity entity) {
		if (stack.isEmpty()) return;
		stack.getOrCreateTag().putInt("CurrentHostId", entity.getId());
	}

	public void removeEntityHost(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null) {
				tag.remove("CurrentHostId");
			}
		}
	}

	@Nullable
	public Entity getEntityHost(ItemStack stack, Level level) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null && tag.contains("CurrentHostId", Tag.TAG_ANY_NUMERIC)) {
				return level.getEntity(tag.getInt("CurrentHostId"));
			}
		}

		return null;

//		return stack.getEntityRepresentation(); //usually null, ItemFrame or ItemEntity
		//this would make it possible to play sounds/etc. via item animation at an ItemFrame or ItemEntity position
	}

	public void setEntityVictim(ItemStack stack, Entity entity) {
		if (stack.isEmpty()) return;
		stack.getOrCreateTag().putInt("CurrentVictimId", entity.getId());
	}

	public void removeEntityVictim(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null) {
				tag.remove("CurrentVictimId");
			}
		}
	}

	@Nullable
	public Entity getEntityVictim(ItemStack stack, Level level) {
		if (stack.hasTag()) {
			CompoundTag tag = stack.getTag();
			if (tag != null && tag.contains("CurrentVictimId", Tag.TAG_ANY_NUMERIC)) {
				return level.getEntity(tag.getInt("CurrentVictimId"));
			}
		}
		return null;
	}

	private void onSoundKeyFrame(ItemStack stack, String soundId, double animationTick) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;

		Entity soundOrigin = getEntityHost(stack, level);
		if (soundOrigin != null) {
			LocalPlayer client = Minecraft.getInstance().player;
			level.playSound(client,
					soundOrigin.getX(), soundOrigin.getY(0.5f), soundOrigin.getZ(),
					ModSoundEvents.INJECTOR_INJECT.get(), SoundSource.PLAYERS,
					0.8f,
					1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f
			);

			Entity victim = getEntityVictim(stack, level);
			if (victim != null) {
				level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, victim.blockPosition(), 0);
			}

//			removeEntityHost(stack);
//			removeEntityVictim(stack);
		}
	}

	private <T extends InjectorItem> void soundListener(SoundKeyframeEvent<T> event) {
		if (RenderProperties.get(event.getEntity()).getItemStackRenderer() instanceof InjectorRenderer renderer) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(factory, renderer.getUniqueID(event.getEntity()), CONTROLLER_NAME);
			if (event.getController() == controller) { //sanity check
				onSoundKeyFrame(renderer.getCurrentItemStack(), event.sound, event.getAnimationTick());
			}
		}
	}

	@Override
	public void registerControllers(AnimationData data) {
		//IMPORTANT: transitionLengthTicks needs to be larger than 0, or else the controller.currentAnimation might be null and a NPE is thrown
		AnimationController<InjectorItem> controller = new AnimationController<>(this, CONTROLLER_NAME, 1, event -> PlayState.CONTINUE);
		controller.registerSoundListener(this::soundListener);
		data.addAnimationController(controller);

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
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(factory, id, CONTROLLER_NAME);
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload(); //make sure animation can play more than once (animations are usually cached)
				controller.setAnimation(new AnimationBuilder().addAnimation(AnimState.INJECT_OTHER.animationName, false));
			}
		}
		else if (AnimState.INJECT_SELF.is(state)) {
			AnimationController<?> controller = GeckoLibUtil.getControllerForID(factory, id, CONTROLLER_NAME);
			if (controller.getAnimationState() == AnimationState.Stopped) {
				controller.markNeedsReload(); //make sure animation can play more than once (animations are usually cached)
				controller.setAnimation(new AnimationBuilder().addAnimation(AnimState.INJECT_SELF.animationName, false));
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));

		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(INVENTORY_TAG)) {
			Serum serum = getSerum(stack);
			if (serum != null) {
				short amount = tag.getCompound(INVENTORY_TAG).getShort(LargeSingleItemStackHandler.ITEM_AMOUNT_TAG);
				tooltip.add(new TextComponent(String.format("Amount: %dx", amount)).withStyle(ChatFormatting.GRAY));
				serum.addInfoToTooltip(stack, level, tooltip, isAdvanced);
			}
		} else tooltip.add(TextComponentUtil.getTooltipText("contains_nothing").withStyle(ChatFormatting.GRAY));

		tooltip.add(TextComponent.EMPTY);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action_open_inventory")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getShiftKey().append(" + ").append(ClientTextUtil.getRightMouseKey()), TextComponentUtil.getTooltipText("action_self_inject")).withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

	@Override
	public Component getHighlightTip(ItemStack stack, Component displayName) {
		if (displayName instanceof MutableComponent mutableComponent) {
			Serum serum = getSerum(stack);
			if (serum != null) {
				return mutableComponent.append(" (").append(new TranslatableComponent(serum.getTranslationKey()).withStyle(ChatFormatting.AQUA)).append(")");
			}
		}
		return displayName;
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
		INJECT_OTHER(0, "injector.anim.inject"),
		INJECT_SELF(1, "injector.anim.inject.self");

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

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, itemHandler.getOptionalItemHandler());
		}

	}

}
