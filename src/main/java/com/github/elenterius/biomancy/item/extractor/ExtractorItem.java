package com.github.elenterius.biomancy.item.extractor;

import com.github.elenterius.biomancy.client.render.item.extractor.ExtractorRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.EssenceItem;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.item.armor.AcolyteArmorItem;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ExtractorItem extends Item implements KeyPressListener, ItemTooltipStyleProvider, GeoItem {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public ExtractorItem(Properties properties) {
		super(properties);
	}

	public static boolean tryExtractEssence(ServerLevel level, BlockPos pos, ItemStack stack) {
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos), EntitySelector.NO_SPECTATORS);
		if (!entities.isEmpty() && extractEssence(stack, null, entities.get(0))) {
			level.playSound(null, pos, ModSoundEvents.INJECTOR_INJECT.get(), SoundSource.BLOCKS, 0.8f, 1f / (level.random.nextFloat() * 0.5f + 1f) + 0.2f);
			return true;
		}
		return false;
	}

	private static boolean extractEssence(ItemStack stack, @Nullable Player player, LivingEntity targetEntity) {
		if (targetEntity.isAlive() && !targetEntity.hasEffect(ModMobEffects.ESSENCE_ANEMIA.get())) {
			if (CombatUtil.canPierceThroughArmor(stack, targetEntity, player)) {
				int lootingLevel = stack.getEnchantmentLevel(Enchantments.MOB_LOOTING);
				int surgicalPrecisionLevel = stack.getEnchantmentLevel(ModEnchantments.SURGICAL_PRECISION.get());

				ItemStack essenceStack = EssenceItem.fromEntity(targetEntity, surgicalPrecisionLevel, lootingLevel);

				if (!essenceStack.isEmpty()) {
					if (player != null) {
						if (!player.addItem(essenceStack)) {
							player.drop(essenceStack, false);
						}
						stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
					}
					else {
						Containers.dropItemStack(targetEntity.level(), targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), essenceStack);
					}

					float damagePct = 1f;
					for (ItemStack itemStack : targetEntity.getArmorSlots()) {
						if (itemStack.getItem() instanceof AcolyteArmorItem armor && armor.hasNutrients(itemStack)) {
							damagePct -= 0.25f;
						}
					}

					if (stack.getEnchantmentLevel(ModEnchantments.ANESTHETIC.get()) <= 0) {
						float damage = 0.5f * damagePct;
						if (damage > 0) {
							targetEntity.hurt(targetEntity.level().damageSources().sting(player), damage);
						}
					}

					targetEntity.addEffect(new MobEffectInstance(ModMobEffects.ESSENCE_ANEMIA.get(), 2400 * (lootingLevel + 1)));
					return true;
				}
			}
			else if (player != null) {
				stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
		}
		return false;
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		//TODO: add cooldown?
		if (!interactWithPlayerSelf(stack, player)) {
			SoundUtil.clientPlayItemSound(level, player, ModSoundEvents.INJECTOR_FAIL.get());
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}
		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		boolean hasInteracted = interactWithPlayerSelf(stack, player);
		SoundUtil.broadcastItemSound(level, player, hasInteracted ? ModSoundEvents.INJECTOR_INJECT.get() : ModSoundEvents.INJECTOR_FAIL.get());
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		//the device is empty
		if (interactionTarget.level() instanceof ServerLevel serverLevel && extractEssence(stack, player, interactionTarget)) {
			SoundUtil.broadcastItemSound(serverLevel, player, ModSoundEvents.INJECTOR_INJECT.get());

			//fix for creative mode (normally the stack is not modified in creative)
			if (player.isCreative()) player.setItemInHand(usedHand, stack);

			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public boolean interactWithPlayerSelf(ItemStack stack, Player player) {
		if (player.hasEffect(ModMobEffects.ESSENCE_ANEMIA.get())) return false;
		if (player.level().isClientSide()) return true;

		if (extractEssence(stack, player, player)) {
			//fix for creative mode (normally the stack is not modified in creative)
			if (player.isCreative()) player.setItemInHand(player.getUsedItemHand(), stack);
			return true;
		}
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.MOB_LOOTING || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.self_extract")).withStyle(ChatFormatting.DARK_GRAY));

		if (stack.isEnchanted()) tooltip.add(ComponentUtil.emptyLine());
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new IClientItemExtensions() {
			private final ExtractorRenderer renderer = new ExtractorRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
		AnimationController<ExtractorItem> controller = new AnimationController<>(this, "main", 5, state -> {

			if (state.isCurrentAnimation(Animations.EXTRACT) && state.getController().isPlayingTriggeredAnimation()) return PlayState.CONTINUE;

			boolean isFirstPerson = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE).firstPerson(); //only animate in first person view to mitigate animation of other items
			boolean isPlayerLookingAtMob = isFirstPerson && Minecraft.getInstance().crosshairPickEntity instanceof LivingEntity;

			if (isPlayerLookingAtMob) {
				state.setAnimation(Animations.TRANSITION_TO_ARMED);
			}
			else {
				state.setAnimation(Animations.TRANSITION_TO_IDLE);
			}

			return PlayState.CONTINUE;
		});

		controller.setAnimation(Animations.IDLE);
		registrar.add(controller);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	protected static class Animations {
		public static final RawAnimation TRANSITION_TO_IDLE = RawAnimation.begin().thenPlay("extractor.transition_to_idle").thenPlay("extractor.idle");
		public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("extractor.idle");
		public static final RawAnimation TRANSITION_TO_ARMED = RawAnimation.begin().thenPlay("extractor.transition_to_armed").thenPlay("extractor.armed");
		public static final RawAnimation ARMED = RawAnimation.begin().thenPlay("extractor.armed");
		public static final RawAnimation EXTRACT = RawAnimation.begin().thenPlay("extractor.extract");

		private Animations() {}
	}

}
