package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.mixin.EntityAccessor;
import com.github.elenterius.biomancy.util.MobUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EntityStorageBagItem extends BagItem {

	public static final String NBT_KEY_ENTITY = "StoredEntity";
	public static final String NBT_KEY_ENTITY_NAME = "Name";
	public static final String NBT_KEY_ENTITY_DATA = "Data";

	public EntityStorageBagItem(Properties properties) {
		super(properties);
	}

	public static boolean isBossMob(Entity entity) {
		return !entity.canChangeDimensions(); //TODO: use boss entity tag instead
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_ENTITY)) {
			TranslationTextComponent entityName = new TranslationTextComponent(nbt.getCompound(NBT_KEY_ENTITY).getString(NBT_KEY_ENTITY_NAME));
			tooltip.add(TextUtil.getTooltipText("contains", entityName).withStyle(TextFormatting.GRAY));
		}
		else tooltip.add(TextUtil.getTooltipText("contains_nothing").withStyle(TextFormatting.GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_ENTITY)) {
			TranslationTextComponent entityName = new TranslationTextComponent(nbt.getCompound(NBT_KEY_ENTITY).getString(NBT_KEY_ENTITY_NAME));
			return new StringTextComponent("").append(displayName).append(" (").append(entityName).append(")");
		}
		return super.getHighlightTip(stack, displayName);
	}

	@Override
	public float getFullness(ItemStack stack) {
		return stack.getOrCreateTag().contains(NBT_KEY_ENTITY) ? 1f : 0f;
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getLevel().isClientSide()) return ActionResultType.PASS;

		PlayerEntity player = context.getPlayer();
		if (player != null) {
			onPlayerInteractWithItem(stack, player);
			CompoundNBT nbt = stack.getOrCreateTag();
			if (nbt.contains(NBT_KEY_ENTITY)) {
				CompoundNBT entityNbt = nbt.getCompound(NBT_KEY_ENTITY);
				if (context.getLevel().mayInteract(player, context.getClickedPos()) && player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {

					Entity newEntity = EntityType.loadEntityRecursive(entityNbt.getCompound(NBT_KEY_ENTITY_DATA), context.getLevel(), entity -> {
						Vector3d pos = MobUtil.getSimpleOffsetPosition(context.getClickLocation(), context.getClickedFace(), entity);
						entity.moveTo(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(context.getLevel().random.nextFloat() * 360f), 0f);

						if (entity instanceof MobEntity) {
							MobEntity mobentity = (MobEntity) entity;
							mobentity.yHeadRot = mobentity.yRot;
							mobentity.yBodyRot = mobentity.yRot;
						}

						entity.setDeltaMovement(0, 0, 0);
						entity.fallDistance = 0;
						return entity;
					});

					if (newEntity != null) {
						if (MobUtil.hasDuplicateEntity((ServerWorld) context.getLevel(), newEntity)) {
							//reset UUID to prevent "Trying to add entity with duplicated UUID" issue
							//this only happens if the item stack was copied (e.g. in creative mode) or if the original mob wasn't removed from the world
							newEntity.setUUID(MathHelper.createInsecureUUID(((EntityAccessor) newEntity).biomancy_rand()));
							//TODO: trigger secret achievement: Paradox! - There can't be two identical entities in the same world.
						}

						if (context.getLevel().addFreshEntity(newEntity)) {
							nbt.remove(NBT_KEY_ENTITY);
							context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.75f, 0.35f + context.getLevel().random.nextFloat() * 0.25f);
							return ActionResultType.SUCCESS;
						}
					}

					TranslationTextComponent entityName = new TranslationTextComponent(entityNbt.getString(NBT_KEY_ENTITY_NAME));
					player.displayClientMessage(TextUtil.getFailureMsgText("failed_to_spawn", entityName), true);
					return ActionResultType.FAIL;
				}
				player.displayClientMessage(TextUtil.getFailureMsgText("not_allowed"), true);
				return ActionResultType.FAIL;
			}
		}
		return ActionResultType.PASS;
	}

	public boolean canStoreEntity(Entity entity) {
		return entity.isAlive() && !(entity instanceof PlayerEntity) && entity.canChangeDimensions() && entity.getType().canSummon() && entity.getType().canSerialize();
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		if (playerIn.level.isClientSide()) return ActionResultType.PASS;
		onPlayerInteractWithItem(stack, playerIn);

		boolean isValidTarget = canStoreEntity(target) && (!isBossMob(target) || playerIn.isCreative()); // blame creative player if something breaks due to storing boss mobs
		if (isValidTarget) {
			boolean isAnyEntityStored = stack.getOrCreateTag().contains(NBT_KEY_ENTITY);
			if (!isAnyEntityStored) {
				if (setStoredEntity(stack, target, true, true)) {
					playerIn.level.playSound(null, playerIn.blockPosition(), SoundEvents.GENERIC_EAT, SoundCategory.PLAYERS, 0.9F, 0.3f + playerIn.level.random.nextFloat() * 0.25f);
					if (playerIn.isCreative()) {
						playerIn.setItemInHand(hand, stack); //fix for creative mode (normally the stack is not modified in creative)
					}
					return ActionResultType.SUCCESS;
				}
				else {
					playerIn.displayClientMessage(TextUtil.getFailureMsgText("failed_to_store"), true);
				}
			}
			else {
				playerIn.displayClientMessage(TextUtil.getFailureMsgText("already_full"), true);
			}
		}

		return ActionResultType.PASS;
	}

	public boolean storeEntity(ItemStack stack, Entity entity, boolean removeEntity) {
		if (!canStoreEntity(entity) || isBossMob(entity)) return false;
		return setStoredEntity(stack, entity, removeEntity, false);
	}

	protected boolean setStoredEntity(ItemStack stack, Entity entityToStore, boolean removeEntity, boolean storePassengers) {

		if (removeEntity && entityToStore.isPassenger()) {
			entityToStore.removeVehicle();
			if (entityToStore.isPassenger()) return false;
		}

		List<Entity> cachedPassengers = null;
		if (entityToStore.isVehicle()) {
			if (storePassengers) {
				boolean hasPlayerPassengers = entityToStore.getIndirectPassengers().stream().anyMatch(PlayerEntity.class::isInstance);
				if (hasPlayerPassengers) return false;
			}
			else {
				cachedPassengers = entityToStore.getPassengers();
				entityToStore.ejectPassengers();
			}
		}

		CompoundNBT entityData = new CompoundNBT();
		if (entityToStore.saveAsPassenger(entityData)) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put(NBT_KEY_ENTITY_DATA, entityData);
			nbt.putString(NBT_KEY_ENTITY_NAME, entityToStore.getType().getDescriptionId());
			stack.getOrCreateTag().put(NBT_KEY_ENTITY, nbt);

			if (removeEntity) {
				if (storePassengers) {
					entityToStore.getIndirectPassengers().forEach(Entity::remove);
				}
				entityToStore.remove();
			}
			else if (cachedPassengers != null) {
				cachedPassengers.forEach(passenger -> passenger.startRiding(entityToStore));
			}
			return true;
		}

		return false;
	}
}
