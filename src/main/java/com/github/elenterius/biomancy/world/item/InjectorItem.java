package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.client.renderer.item.InjectorRenderer;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextComponentUtil;
import com.github.elenterius.biomancy.world.entity.MobUtil;
import com.github.elenterius.biomancy.world.inventory.ItemInventory;
import com.github.elenterius.biomancy.world.inventory.menu.BioInjectorMenu;
import com.github.elenterius.biomancy.world.serum.Serum;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
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
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
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

	public static final int SLOTS = 5;
	public static final int MAX_SLOT_SIZE = 1;
	public static final String INVENTORY_TAG = "inventory";
	private static final String CONTROLLER_NAME = "controller";
	public static final int COOL_DOWN_TICKS = 25;
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
			if (target.isAlive() && dispenserAffectEntity(level, serum, stack, injectorItem, target)) {
				level.playSound(null, pos, ModSoundEvents.INJECT.get(), SoundSource.BLOCKS, 0.8f, 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f);
				level.levelEvent(LevelEvent.PARTICLES_DRAGON_BLOCK_BREAK, target.blockPosition(), 0);
				return true;
			}
		}

		return false;
	}

	private static boolean dispenserAffectEntity(ServerLevel level, Serum serum, ItemStack stack, InjectorItem injectorItem, LivingEntity target) {
		if (MobUtil.canPierceThroughArmor(stack, target)) {
			CompoundTag dataTag = Serum.getDataTag(stack);
			if (serum.canAffectEntity(dataTag, null, target)) {
				serum.affectEntity(dataTag, null, target);
				if (serum.isAttributeModifier()) serum.applyAttributesModifiersToEntity(target);
				injectorItem.consumeSerum(stack, serum, null);
				stack.hurt(1, level.getRandom(), null);
				return true;
			}
		}
		else {
			stack.hurt(2, level.getRandom(), null);
			//TODO: play breaking sound
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, ClientLevel level, Player player, byte flags) {
		if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(flags);

		if (!interactWithPlayerSelf(stack, player)) {
			ModSoundEvents.localItemSFX(level, player, ModSoundEvents.ACTION_FAIL.get());
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}
		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (player.getCooldowns().isOnCooldown(this)) return;

		if (interactWithPlayerSelf(stack, player)) {
			setEntityHost(stack, player);
			setEntityVictim(stack, player);
			broadcastAnimation(level, player, stack, AnimState.INJECT_SELF.id);
			player.getCooldowns().addCooldown(this, COOL_DOWN_TICKS);
		}
		else {
			ModSoundEvents.broadcastItemSFX(level, player, ModSoundEvents.ACTION_FAIL.get());
		}
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack copyOfStack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		Serum serum = getSerum(copyOfStack);
		if (serum != null) {
			if (MobUtil.canPierceThroughArmor(copyOfStack, interactionTarget)) {
				CompoundTag dataTag = Serum.getDataTag(copyOfStack);
				if (serum.canAffectEntity(dataTag, player, interactionTarget)) {
					serum.affectEntity(dataTag, player, interactionTarget);

					if (interactionTarget.level.isClientSide) return InteractionResult.CONSUME;

					if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.PASS;
					player.getCooldowns().addCooldown(this, COOL_DOWN_TICKS);

					if (serum.isAttributeModifier()) serum.applyAttributesModifiersToEntity(interactionTarget);
					if (!player.isCreative()) consumeSerum(copyOfStack, serum, player);

					copyOfStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));

					ItemStack realStack = player.getAbilities().instabuild ? player.getItemInHand(usedHand) : copyOfStack;
					setEntityHost(realStack, player); //who is using the item
					setEntityVictim(realStack, interactionTarget); //who is the victim
					broadcastAnimation((ServerLevel) interactionTarget.level, player, realStack, AnimState.INJECT_OTHER.id);

					return InteractionResult.CONSUME;
				}
			}
			else if (!player.level.isClientSide) {
				copyOfStack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}

			if (player.level.isClientSide) ModSoundEvents.localItemSFX((ClientLevel) player.level, player, ModSoundEvents.ACTION_FAIL.get());
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	public boolean interactWithPlayerSelf(ItemStack stack, Player player) {
		Serum serum = getSerum(stack);
		if (serum != null) {
			if (MobUtil.canPierceThroughArmor(stack, player)) {
				CompoundTag dataTag = Serum.getDataTag(stack);
				if (serum.canAffectPlayerSelf(dataTag, player)) {
					serum.affectPlayerSelf(dataTag, player);
					if (!player.level.isClientSide) {
						if (serum.isAttributeModifier()) serum.applyAttributesModifiersToEntity(player);
						if (!player.isCreative()) consumeSerum(stack, serum, player);
						stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
					}
					return true;
				}
			}

			if (!player.level.isClientSide) {
				stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
		}
		return false;
	}

	@Override
	@Nullable
	public Serum getSerum(ItemStack stack) {
		//TODO: cache selected serum, instead of looking through the whole inventory
		Optional<IItemHandler> optional = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (optional.isPresent()) {
			IItemHandler itemHandler = optional.get();
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stackInSlot = itemHandler.getStackInSlot(i);
				if (stackInSlot.getItem() instanceof ISerumProvider serumItem) {
					return serumItem.getSerum(stackInSlot);
				}
			}
		}
		return null;
	}

	@Override
	public int getSerumColor(ItemStack stack) {
		Serum serum = getSerum(stack);
		return serum != null ? serum.getColor() : -1;
	}

	public void consumeSerum(ItemStack stack, Serum serum, @Nullable Player player) {
		Optional<IItemHandler> optional = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
		if (optional.isPresent()) {
			IItemHandler itemHandler = optional.get();
			for (int i = 0; i < itemHandler.getSlots(); i++) {
				ItemStack stackInSlot = itemHandler.getStackInSlot(i);
				if (stackInSlot.getItem() instanceof ISerumProvider serumItem) {
					Serum foundSerum = serumItem.getSerum(stackInSlot);
					if (foundSerum == serum) {
						ItemStack serumStack = itemHandler.extractItem(i, 1, false);
						if (serumStack.hasContainerItem()) {
							ItemStack containerItem = serumStack.getContainerItem();
							ItemStack remainder = itemHandler.insertItem(i, containerItem, false);
							if (!remainder.isEmpty() && player != null && !player.addItem(remainder)) {
								player.drop(remainder, false);
							}
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if (player.isShiftKeyDown()) {
			if (!level.isClientSide) {
				ItemStack stack = player.getItemInHand(usedHand);
				ItemInventory inventory = ItemInventory.createServerContents(SLOTS, MAX_SLOT_SIZE, stack);
				MenuProvider container = new SimpleMenuProvider((id, playerInv, p) -> BioInjectorMenu.createServerMenu(id, playerInv, inventory), stack.getHoverName());
				NetworkHooks.openGui((ServerPlayer) player, container, byteBuf -> {
					byteBuf.writeByte(SLOTS);
					byteBuf.writeByte(MAX_SLOT_SIZE);
					byteBuf.writeItem(stack);
				});
			}
			else {
				player.playSound(SoundEvents.ARMOR_EQUIP_IRON, 1f, 1f);
			}
			return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
		}
		return InteractionResultHolder.pass(player.getItemInHand(usedHand));
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
					ModSoundEvents.INJECT.get(), SoundSource.PLAYERS,
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
			CompoundTag inventory = tag.getCompound(INVENTORY_TAG);
			ListTag tagList = inventory.getList("Items", Tag.TAG_COMPOUND);
			tooltip.add(new TextComponent(String.format("Amount: %d/" + SLOTS, tagList.size())).withStyle(ChatFormatting.GRAY));

			Serum serum = getSerum(stack);
			if (serum != null) {
				serum.addInfoToTooltip(stack, level, tooltip, isAdvanced);
			}
		}
		else tooltip.add(TextComponentUtil.getTooltipText("contains_nothing").withStyle(ChatFormatting.GRAY));

		tooltip.add(TextComponent.EMPTY);
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action_self_inject")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getShiftKey().append(" + ").append(ClientTextUtil.getRightMouseKey()), TextComponentUtil.getTooltipText("action_open_inventory")).withStyle(ChatFormatting.DARK_GRAY));
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

		private final ItemInventory itemHandler;

		public InventoryCapability(ItemStack stack) {
			itemHandler = ItemInventory.createServerContents(SLOTS, MAX_SLOT_SIZE, stack);
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, itemHandler.getOptionalItemHandler());
		}

	}

}
