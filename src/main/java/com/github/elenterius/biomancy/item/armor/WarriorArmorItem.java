package com.github.elenterius.biomancy.item.armor;

import com.github.elenterius.biomancy.client.render.item.armor.WarriorArmorRenderer;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.misc.LivingEntityData;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.init.client.ModKeyBindings;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.item.KnowledgeReader;
import com.github.elenterius.biomancy.mixin.accessor.ArmorItemAccessor;
import com.github.elenterius.biomancy.sounds.ServerSoundHandler;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WarriorArmorItem extends LivingArmorGeoItem implements KnowledgeReader, ItemTooltipStyleProvider, KeyPressListener {

	public static final Predicate<ItemStack> ARMOR_SET_PREDICATE = itemStack -> itemStack.getItem() instanceof WarriorArmorItem;

	public static final int[] BULLET_JUMP_COST = {10, 15, 25, 40};
	public static final int BULLET_JUMP_COST_COOLDOWN = 5 * 20;

	public static final int IMPOSING_ROAR_COST = 100;
	public static final int IMPOSING_ROAR_DURATION = 30 * 20;
	public static final double IMPOSING_ROAR_RADIUS = 16d;

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	protected final ImmutableMultimap<Attribute, AttributeModifier> brokenAttributeModifiers;

	public WarriorArmorItem(ArmorMaterial material, Type type, int maxNutrients, Properties properties) {
		super(material, type, maxNutrients, properties);

		ArmorItemAccessor baseArmor = (ArmorItemAccessor) this;
		UUID uuid = ArmorItemAccessor.biomancy$ARMOR_MODIFIER_UUID_PER_TYPE().get(type);

		ImmutableMultimap.Builder<Attribute, AttributeModifier> defaultBuilder = ImmutableMultimap.builder();
		defaultBuilder.putAll(baseArmor.biomancy$getDefaultModifiers());

		ImmutableMultimap.Builder<Attribute, AttributeModifier> brokenBuilder = ImmutableMultimap.builder();

		if (type == Type.CHESTPLATE) {
			defaultBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Armor attack damage bonus", 0.10d, AttributeModifier.Operation.MULTIPLY_BASE));
			defaultBuilder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid, "Armor attack knockback bonus", 0.5d, AttributeModifier.Operation.ADDITION));

			AttributeModifier attackSpeedPenalty = new AttributeModifier(uuid, "Armor attack speed penalty", -0.1d, AttributeModifier.Operation.MULTIPLY_BASE);
			defaultBuilder.put(Attributes.ATTACK_SPEED, attackSpeedPenalty);
			brokenBuilder.put(Attributes.ATTACK_SPEED, attackSpeedPenalty);

			AttributeModifier movementSpeedPenalty = new AttributeModifier(uuid, "Armor movement speed penalty", -0.2d, AttributeModifier.Operation.MULTIPLY_BASE);
			defaultBuilder.put(Attributes.MOVEMENT_SPEED, movementSpeedPenalty);
			brokenBuilder.put(Attributes.MOVEMENT_SPEED, movementSpeedPenalty);
		}
		else if (type == Type.LEGGINGS) {
			defaultBuilder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Armor movement speed bonus", 0.2d, AttributeModifier.Operation.MULTIPLY_BASE));
		}

		baseArmor.biomancy$setDefaultModifiers(defaultBuilder.build());

		brokenAttributeModifiers = brokenBuilder.build();
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return getNutrients(stack) > 0 ? super.getAttributeModifiers(slot, stack) : getBrokenAttributeModifiers(slot);
	}

	protected ImmutableMultimap<Attribute, AttributeModifier> getBrokenAttributeModifiers(EquipmentSlot slot) {
		return slot == type.getSlot() ? brokenAttributeModifiers : ImmutableMultimap.of();
	}

	//	public double getJumpBoostPower(ItemStack stack) {
	//		return type == ArmorItem.Type.LEGGINGS && getNutrients(stack) > 0 ? 0.15d : 0d;
	//	}

	@Override
	public KeyPressResult onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		if (slot != EquipmentSlot.HEAD || type != Type.HELMET) return KeyPressResult.fail();
		if (!CombatUtil.hasFulLArmorSetEquipped(player, ARMOR_SET_PREDICATE)) return KeyPressResult.fail();

		int nutrients = getNutrientsFromEquippedArmor(player, ARMOR_SET_PREDICATE);
		if (nutrients < IMPOSING_ROAR_COST) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
			player.playSound(ModSoundEvents.FLESHKIN_NO.get(), 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
			return KeyPressResult.fail();
		}

		return KeyPressResult.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		if (type != Type.HELMET || player.getItemBySlot(EquipmentSlot.HEAD) != stack) return;
		if (!CombatUtil.hasFulLArmorSetEquipped(player, ARMOR_SET_PREDICATE)) return;

		int nutrients = getNutrientsFromEquippedArmor(player, ARMOR_SET_PREDICATE);
		if (nutrients >= IMPOSING_ROAR_COST) {
			consumeNutrientsFromEquippedArmor(player, IMPOSING_ROAR_COST, ARMOR_SET_PREDICATE);
			imposingRoar(level, player);

			int range = 256;
			float volume = range / 16f;
			level.playSound(null, player, ModSoundEvents.ARMOR_IMPOSING_ROAR.get(), SoundSource.PLAYERS, volume, 0.9f + player.getRandom().nextFloat() * 0.2f);
			player.gameEvent(GameEvent.ENTITY_ROAR);
		}
	}

	public float getFallReduction(ItemStack stack) {
		return type == Type.BOOTS && getNutrients(stack) > 0 ? 0.5f : 0f;
	}

	public boolean onJump(ItemStack stack, LivingEntity livingEntity, boolean isBulletJumpKeyDown) {
		if (livingEntity.level().isClientSide) return false;

		if (type != ArmorItem.Type.LEGGINGS) return false;
		if (livingEntity.isInWaterOrBubble()) return false;

		if (isBulletJumpKeyDown) {
			if (livingEntity instanceof ServerPlayer player) {
				int cost = getBulletJumpCost(player, stack);
				if (getNutrients(stack) >= cost) {
					if (bulletJump(player)) {
						consumeNutrients(stack, cost);
						increaseBulletJumpCost(player, stack);
						return true;
					}
				}
				else {
					player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
					ServerSoundHandler.triggerSoundForClientPlayer(player, ModSoundEvents.FLESHKIN_NO.get(), player.getSoundSource(), 1f, 1f + player.level().getRandom().nextFloat() * 0.4f);
				}
			}
			return false;
		}

		//      // if we reintroduce this we must set the delta movement on the client side as well due to syncing issues
		//		double jumpBoostPower = getJumpBoostPower(stack);
		//		if (jumpBoostPower != 0d) {
		//			if (getNutrients(stack) >= 1) {
		//				livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0d, jumpBoostPower, 0d));
		//				livingEntity.hasImpulse = true;
		//				livingEntity.hurtMarked = true;
		//				consumeNutrients(stack, 1);
		//				return true;
		//			}
		//			else if (livingEntity instanceof ServerPlayer player) {
		//				player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_nutrients"), true);
		//				SoundUtil.Server.sendSoundToClient(player, ModSoundEvents.FLESHKIN_NO.get(), player.getSoundSource(), 1f, 1f + player.level().getRandom().nextFloat() * 0.4f);
		//			}
		//		}

		return false;
	}

	private static int getBulletJumpCost(ServerPlayer player, ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		long timestamp = tag.getLong("bullet_jump_timestamp");
		long elapsedTime = player.level().getGameTime() - timestamp;

		if (elapsedTime > BULLET_JUMP_COST_COOLDOWN) {
			return BULLET_JUMP_COST[0];
		}

		int index = Mth.clamp(tag.getInt("bullet_jump_cost_index"), 0, BULLET_JUMP_COST.length - 1);
		return BULLET_JUMP_COST[index];
	}

	private static void increaseBulletJumpCost(ServerPlayer player, ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		long timestamp = tag.getLong("bullet_jump_timestamp");
		long gameTime = player.level().getGameTime();
		long elapsedTime = gameTime - timestamp;

		if (elapsedTime > BULLET_JUMP_COST_COOLDOWN) {
			tag.putInt("bullet_jump_cost_index", 0);
		}
		else {
			int index = Mth.clamp(tag.getInt("bullet_jump_cost_index") + 1, 0, BULLET_JUMP_COST.length - 1);
			tag.putInt("bullet_jump_cost_index", index);
		}

		tag.putLong("bullet_jump_timestamp", gameTime);
	}

	public void onFall(ItemStack stack, LivingFallEvent event) {
		if (type == Type.BOOTS && getNutrients(stack) >= 1) {
			if (event.getDistance() > 3f && event.getDamageMultiplier() > 0f) {
				consumeNutrients(stack, 1);
			}
			float multiplier = 1f - getFallReduction(stack);
			event.setDamageMultiplier(event.getDamageMultiplier() * multiplier);
			event.setDistance(event.getDistance() * multiplier);
		}
	}

	protected boolean bulletJump(ServerPlayer player) {
		Vec3 lookVec = player.getLookAngle();
		Vec3 velocity = player.getDeltaMovement();

		Vec3 UP = new Vec3(0, 1, 0);
		double verticalSimilarity = UP.dot(lookVec); //1 = up, 0 = horizontal, -1 = down

		double maxDistance = 32d;
		double minDistance = maxDistance * 0.7d;
		double horizontalSimilarity = 1d - Math.abs(verticalSimilarity);
		double lookDistance = Mth.lerp(horizontalSimilarity, minDistance, maxDistance);

		Vec3 eyePos = player.getEyePosition();
		Vec3 lookTarget = eyePos.add(lookVec.scale(lookDistance));

		if (verticalSimilarity < -(22.5d / 90d)) {
			ClipContext context = new ClipContext(player.position(), lookTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
			if (player.level().clip(context).getType() == HitResult.Type.BLOCK) {
				ServerSoundHandler.triggerSoundForClientPlayer(player, ModSoundEvents.FLESHKIN_NO.get(), player.getSoundSource(), 0.5f, 0.5f + player.level().getRandom().nextFloat() * 0.5f);
				return false;
			}
		}

		Vec3 jumpDirection = lookTarget.subtract(player.position()).normalize();

		if (canElytraFly(player)) {
			//free boosted elytra takeoff without rockets
			double maxPower = 0.75d + velocity.y;
			double power = Mth.lerp(horizontalSimilarity, maxPower * 0.54d, maxPower);

			player.setDeltaMovement(
					velocity.x + jumpDirection.x * power,
					jumpDirection.y * power,
					velocity.z + jumpDirection.z * power
			);

			player.startFallFlying();
		}
		else {
			double maxPower = 2d + velocity.y;
			double power = Mth.lerp(horizontalSimilarity, maxPower * 0.7d, maxPower);

			player.setDeltaMovement(
					velocity.x + jumpDirection.x * power,
					jumpDirection.y * power,
					velocity.z + jumpDirection.z * power
			);

			player.startAutoSpinAttack(20); //note: we can't pick longer duration because auto spin ticks don't reset when the player collides with the ground
			if (player.onGround()) {
				//hack to allow consecutive execution of spin attacks when the user holds down shift continuously
				player.move(MoverType.SELF, new Vec3(0d, 1.1999999f, 0d));
			}
		}

		if (player.onGround()) {
			//Fix warning "player moved wrongly!" caused by sneaking and trying to jump off block ledge (fall off prevention)
			player.setShiftKeyDown(false);

			//probably not needed
			if (player instanceof LivingEntityData.TransientDataProvider provider) {
				provider.biomancy$getData().setDiscardFriction(true);
			}
		}

		player.hasImpulse = true;
		player.hurtMarked = true;

		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.ARMOR_BULLET_JUMP.get(), player.getSoundSource(), 1f, 1f);

		return true;
	}

	protected static boolean canElytraFly(Player player) {
		if (!player.isFallFlying() && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION)) {
			return player.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(player);
		}
		return false;
	}

	protected void imposingRoar(ServerLevel level, Player player) {
		double radiusSqr = IMPOSING_ROAR_RADIUS * IMPOSING_ROAR_RADIUS;
		Vec3 center = player.getBoundingBox().getCenter();

		Predicate<Entity> predicate = EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR);
		List<LivingEntity> livingEntities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(IMPOSING_ROAR_RADIUS), predicate);

		for (LivingEntity livingEntity : livingEntities) {
			if (livingEntity == player) continue;
			if (center.distanceToSqr(livingEntity.getX(), livingEntity.getY(0.5d), livingEntity.getZ()) >= radiusSqr) continue;

			if (livingEntity.isAlliedTo(player)) {
				livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, IMPOSING_ROAR_DURATION, 1));
				livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, IMPOSING_ROAR_DURATION));
			}
			else {
				livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, IMPOSING_ROAR_DURATION));

				if (livingEntity instanceof Mob mob) {
					for (WrappedGoal wrappedGoal : mob.goalSelector.getAvailableGoals()) {
						if (wrappedGoal.getGoal() instanceof PanicGoal) {
							livingEntity.setLastHurtByMob(player);
							livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, IMPOSING_ROAR_DURATION));
						}
					}
				}
			}
		}

		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, IMPOSING_ROAR_DURATION));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> original) {
				if (renderer == null) {
					renderer = new WarriorArmorRenderer();
				}

				renderer.prepForRender(livingEntity, itemStack, slot, original);

				return renderer;
			}
		});
	}

	@Override
	public boolean canShowKnowledgeOverlay(ItemStack stack, Player player) {
		return AcolyteArmorUpgrades.hasUpgrade(stack, AcolyteArmorUpgrades.PRIMORDIAL_SIGHT) && hasNutrients(stack);
	}

	@Override
	public boolean canTranslatePrimordialRunes(ItemStack stack, Player player) {
		return false;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		//TODO: add idle animations?
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		AcolyteArmorUpgrades.appendHoverText(stack, tooltip);

		tooltip.add(ComponentUtil.EMPTY_LINE);
		tooltip.add(TextComponentUtil.getAbilityText("fleshkin_affinity").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.space().append(TextComponentUtil.getAbilityText("fleshkin_affinity.desc")).withStyle(ChatFormatting.DARK_GRAY));

		switch (type) {
			case HELMET -> {
				tooltip.add(ComponentUtil.EMPTY_LINE);
				tooltip.add(TextComponentUtil.getAbilityText("imposing_roar").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.space().append(TextComponentUtil.getAbilityText("imposing_roar.desc", IMPOSING_ROAR_COST, IMPOSING_ROAR_RADIUS, IMPOSING_ROAR_DURATION / 20)).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(ClientTextUtil.pressButtonTo(ComponentUtil.keybind(ModKeyBindings.EQUIPPED_ARMOR_ACTION), TextComponentUtil.getActionText("activate")).withStyle(TextStyles.DARK_GRAY));
			}
			case LEGGINGS -> {
				tooltip.add(ComponentUtil.EMPTY_LINE);
				tooltip.add(TextComponentUtil.getAbilityText("bullet_jump").withStyle(ChatFormatting.GRAY));
				tooltip.add(ComponentUtil.space().append(TextComponentUtil.getAbilityText("bullet_jump.desc", BULLET_JUMP_COST[0], abilityCostToString(BULLET_JUMP_COST), BULLET_JUMP_COST_COOLDOWN / 20)).withStyle(ChatFormatting.DARK_GRAY));

				//			double jumpBoostPower = getJumpBoostPower(stack);
				//			if (jumpBoostPower != 0d) {
				//				String prefix = jumpBoostPower > 0d ? "+" : "";
				//				tooltip.add(ComponentUtil.space().append(prefix + jumpBoostPower + " Jump Boost Power").withStyle(ChatFormatting.DARK_GRAY));
				//			}
			}
			case BOOTS -> {
				tooltip.add(ComponentUtil.EMPTY_LINE);
				tooltip.add(TextComponentUtil.getAbilityText("padded_soles").withStyle(ChatFormatting.GRAY));

				int pct = (int) (getFallReduction(stack) * 100f);
				tooltip.add(ComponentUtil.space().append(pct + "% Fall Reduction").withStyle(ChatFormatting.DARK_GRAY));
			}
		}

		tooltip.add(ComponentUtil.EMPTY_LINE);

		//		CompoundTag compoundTag = stack.getOrCreateTag().getCompound("damage_resistance_tracker");
		//		AdaptiveDamageResistanceHandler.DamageTypeResistanceTracker.appendTooltipText(compoundTag, tooltip);

		appendLivingToolTooltip(stack, tooltip);

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.EMPTY_LINE);
		}
	}

	public static String abilityCostToString(int[] array) {
		if (array.length == 0) return "0";

		StringBuilder builder = new StringBuilder();
		for (int value : array) {
			builder.append(value);
			builder.append("/");
		}
		return builder.toString();
	}

}