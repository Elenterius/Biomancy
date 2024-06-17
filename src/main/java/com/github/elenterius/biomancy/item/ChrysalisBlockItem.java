package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.block.chrysalis.Chrysalis;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.MobUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

import static com.github.elenterius.biomancy.block.chrysalis.Chrysalis.*;

public class ChrysalisBlockItem extends SimpleBlockItem {

	public ChrysalisBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity) {
		if (itemEntity.level().isClientSide) return;

		CompoundTag compoundTag = BlockItem.getBlockEntityData(itemEntity.getItem());
		if (compoundTag == null || !compoundTag.contains(ENTITY_KEY)) return;

		CompoundTag entityTag = compoundTag.getCompound(ENTITY_KEY);
		Chrysalis.spawnEntity((ServerLevel) itemEntity.level(), itemEntity.getEyePosition(), entityTag);
	}

	public boolean isEmpty(ItemStack stack) {
		CompoundTag compoundTag = BlockItem.getBlockEntityData(stack);
		return compoundTag == null || !compoundTag.contains(ENTITY_KEY);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		if (player.level().isClientSide()) return InteractionResult.PASS;

		if (!isEmpty(stack)) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("already_full"), true);
			return InteractionResult.PASS;
		}

		boolean isValidTarget = isValidEntity(interactionTarget) && (!MobUtil.isBoss(interactionTarget) || player.isCreative()); // blame creative player if something breaks due to storing boss mobs
		if (!isValidTarget) {
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("mob_too_old"), true);
			return InteractionResult.PASS;
		}

		if (stack.getCount() > 1) {
			ItemStack copy = stack.copy();
			copy.setCount(1);

			CompoundTag compoundTag = new CompoundTag();

			if (storeEntity(compoundTag, interactionTarget, true)) {
				BlockItem.setBlockEntityData(copy, ModBlockEntities.CHRYSALIS.get(), compoundTag);

				playInsertSound(player);

				if (!player.isCreative()) stack.shrink(1);
				if (!player.getInventory().add(copy)) {
					player.drop(copy, true);
				}

				return InteractionResult.SUCCESS;
			}

			player.displayClientMessage(TextComponentUtil.getFailureMsgText("failed_to_store_mob"), true);
			return InteractionResult.PASS;
		}

		CompoundTag compoundTag = new CompoundTag();
		if (storeEntity(compoundTag, interactionTarget, true)) {
			BlockItem.setBlockEntityData(stack, ModBlockEntities.CHRYSALIS.get(), compoundTag);

			playInsertSound(player);

			if (player.isCreative()) {
				player.setItemInHand(usedHand, stack); //fix for creative mode (normally the stack is not modified in creative)
			}
			return InteractionResult.SUCCESS;
		}

		player.displayClientMessage(TextComponentUtil.getFailureMsgText("failed_to_store_mob"), true);
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null || !player.isSecondaryUseActive()) return InteractionResult.PASS;

		CompoundTag compoundTag = BlockItem.getBlockEntityData(stack);
		if (compoundTag == null || !compoundTag.contains(ENTITY_KEY)) return InteractionResult.PASS;

		Level level = context.getLevel();
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		if (level.mayInteract(player, context.getClickedPos()) && player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {

			CompoundTag entityTag = compoundTag.getCompound(ENTITY_KEY);

			if (spawnEntity((ServerLevel) level, context, entityTag)) {
				compoundTag.remove(ENTITY_KEY);
				BlockItem.setBlockEntityData(stack, ModBlockEntities.CHRYSALIS.get(), compoundTag);

				playRemoveSound(player);
				return InteractionResult.CONSUME;
			}

			MutableComponent entityName = ComponentUtil.translatable(entityTag.getString(ENTITY_NAME_KEY));
			player.displayClientMessage(TextComponentUtil.getFailureMsgText("failed_to_spawn_mob", entityName), true);
			return InteractionResult.FAIL;
		}

		player.displayClientMessage(TextComponentUtil.getFailureMsgText("item_interaction_not_allowed"), true);
		return InteractionResult.FAIL;
	}

	private boolean spawnEntity(ServerLevel level, UseOnContext context, CompoundTag entityTag) {
		Entity entityToSpawn = EntityType.loadEntityRecursive(entityTag.getCompound(ENTITY_DATA_KEY), level, entity -> {
			Vec3 pos = MobUtil.getAdjustedSpawnPositionFor(context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), entity);
			entity.moveTo(pos.x, pos.y, pos.z, Mth.wrapDegrees(level.random.nextFloat() * 360), 0);

			if (entity instanceof LivingEntity living) {
				living.yHeadRot = living.getYRot();
				living.yBodyRot = living.getYRot();
			}

			entity.setDeltaMovement(0, 0, 0);
			entity.fallDistance = 0;
			return entity;
		});

		if (entityToSpawn != null) {
			if (!MobUtil.isEntityIdUnique(level, entityToSpawn)) {
				//reset UUID to prevent "Trying to add entity with duplicated UUID" issue
				//this only happens if the item stack was copied (e.g. in creative mode) or if the original mob wasn't removed from the world
				MobUtil.randomizeUUID(entityToSpawn);
				//TODO: trigger secret achievement: Paradox! - There can't be two identical entities in the same world.
			}
			return level.addFreshEntity(entityToSpawn);
		}

		return false;
	}

	private void playRemoveSound(Player player) {
		playSound(player, SoundEvents.FROG_LAY_SPAWN);
	}

	private void playInsertSound(Player player) {
		playSound(player, SoundEvents.FROG_EAT);
	}

	private void playSound(Player player, SoundEvent soundEvent) {
		player.playSound(soundEvent, 0.9f, 0.3f + player.level().getRandom().nextFloat() * 0.25f);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);

		CompoundTag compoundTag = BlockItem.getBlockEntityData(stack);
		if (compoundTag == null || !compoundTag.contains(ENTITY_KEY)) return;

		CompoundTag tag = compoundTag.getCompound(ENTITY_KEY);
		MutableComponent entityName = ComponentUtil.translatable(tag.getString(ENTITY_NAME_KEY));

		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(TextComponentUtil.getTooltipText("contains", entityName).withStyle(TextStyles.ITALIC_GRAY));

		float volume = tag.getFloat(ENTITY_VOLUME_KEY);
		tooltip.add(ComponentUtil.literal(String.format("%.1f m\u00B3", volume)).withStyle(ChatFormatting.DARK_GRAY));
	}

	@Override
	public Component getName(ItemStack stack) {
		Component itemName = super.getName(stack);
		MutableComponent entityName = getEntityTypeName(stack);

		if (entityName == null) {
			return itemName;
		}

		return entityName.append(" ").append(itemName);
	}

	@Nullable
	private MutableComponent getEntityTypeName(ItemStack stack) {
		CompoundTag compoundTag = BlockItem.getBlockEntityData(stack);
		if (compoundTag == null || !compoundTag.contains(ENTITY_KEY)) return null;

		CompoundTag tag = compoundTag.getCompound(ENTITY_KEY);
		return ComponentUtil.translatable(tag.getString(ENTITY_NAME_KEY));
	}

}
