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
		return !entity.canChangeDimension(); //TODO: use boss entity tag instead
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_ENTITY)) {
			TranslationTextComponent entityName = new TranslationTextComponent(nbt.getCompound(NBT_KEY_ENTITY).getString(NBT_KEY_ENTITY_NAME));
			tooltip.add(TextUtil.getTooltipText("contains", entityName).mergeStyle(TextFormatting.GRAY));
		}
		else tooltip.add(TextUtil.getTooltipText("contains_nothing").mergeStyle(TextFormatting.GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_ENTITY)) {
			TranslationTextComponent entityName = new TranslationTextComponent(nbt.getCompound(NBT_KEY_ENTITY).getString(NBT_KEY_ENTITY_NAME));
			return new StringTextComponent("").appendSibling(displayName).appendString(" (").appendSibling(entityName).appendString(")");
		}
		return super.getHighlightTip(stack, displayName);
	}

	@Override
	public float getFullness(ItemStack stack) {
		return stack.getOrCreateTag().contains(NBT_KEY_ENTITY) ? 1f : 0f;
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getWorld().isRemote()) return ActionResultType.PASS;

		PlayerEntity player = context.getPlayer();
		if (player != null) {
			onPlayerInteractWithItem(stack, player);
			CompoundNBT nbt = stack.getOrCreateTag();
			if (nbt.contains(NBT_KEY_ENTITY)) {
				CompoundNBT entityNbt = nbt.getCompound(NBT_KEY_ENTITY);
				if (context.getWorld().isBlockModifiable(player, context.getPos()) && player.canPlayerEdit(context.getPos(), context.getFace(), stack)) {

					Entity newEntity = EntityType.loadEntityAndExecute(entityNbt.getCompound(NBT_KEY_ENTITY_DATA), context.getWorld(), entity -> {
						Vector3d pos = MobUtil.getSimpleOffsetPosition(context.getHitVec(), context.getFace(), entity);
						entity.setLocationAndAngles(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(context.getWorld().rand.nextFloat() * 360f), 0f);

						if (entity instanceof MobEntity) {
							MobEntity mobentity = (MobEntity) entity;
							mobentity.rotationYawHead = mobentity.rotationYaw;
							mobentity.renderYawOffset = mobentity.rotationYaw;
						}

						entity.setMotion(0, 0, 0);
						entity.fallDistance = 0;
						return entity;
					});

					if (newEntity != null) {
						if (MobUtil.hasDuplicateEntity((ServerWorld) context.getWorld(), newEntity)) {
							//reset UUID to prevent "Trying to add entity with duplicated UUID" issue
							//this only happens if the item stack was copied (e.g. in creative mode) or if the original mob wasn't removed from the world
							newEntity.setUniqueId(MathHelper.getRandomUUID(((EntityAccessor) newEntity).biomancy_rand()));
							//TODO: trigger secret achievement: Paradox! - There can't be two identical entities in the same world.
						}

						if (context.getWorld().addEntity(newEntity)) {
							nbt.remove(NBT_KEY_ENTITY);
							context.getWorld().playSound(null, context.getPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.75f, 0.35f + context.getWorld().rand.nextFloat() * 0.25f);
							return ActionResultType.SUCCESS;
						}
					}

					TranslationTextComponent entityName = new TranslationTextComponent(entityNbt.getString(NBT_KEY_ENTITY_NAME));
					player.sendStatusMessage(TextUtil.getFailureMsgText("failed_to_spawn", entityName), true);
					return ActionResultType.FAIL;
				}
				player.sendStatusMessage(TextUtil.getFailureMsgText("not_allowed"), true);
				return ActionResultType.FAIL;
			}
		}
		return ActionResultType.PASS;
	}

	public boolean canStoreEntity(Entity entity) {
		return entity.isAlive() && !(entity instanceof PlayerEntity) && entity.canChangeDimension() && entity.getType().isSummonable() && entity.getType().isSerializable();
	}

	@Override
	public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		if (playerIn.world.isRemote()) return ActionResultType.PASS;
		onPlayerInteractWithItem(stack, playerIn);

		boolean isValidTarget = canStoreEntity(target) && (!isBossMob(target) || playerIn.isCreative()); // blame creative player if something breaks due to storing boss mobs
		if (isValidTarget) {
			boolean isAnyEntityStored = stack.getOrCreateTag().contains(NBT_KEY_ENTITY);
			if (!isAnyEntityStored) {
				if (setStoredEntity(stack, target, true, true)) {
					playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.9F, 0.3f + playerIn.world.rand.nextFloat() * 0.25f);
					if (playerIn.isCreative()) {
						playerIn.setHeldItem(hand, stack); //fix for creative mode (normally the stack is not modified in creative)
					}
					return ActionResultType.SUCCESS;
				}
				else {
					playerIn.sendStatusMessage(TextUtil.getFailureMsgText("failed_to_store"), true);
				}
			}
			else {
				playerIn.sendStatusMessage(TextUtil.getFailureMsgText("already_full"), true);
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
			entityToStore.dismount();
			if (entityToStore.isPassenger()) return false;
		}

		List<Entity> cachedPassengers = null;
		if (entityToStore.isBeingRidden()) {
			if (storePassengers) {
				boolean hasPlayerPassengers = entityToStore.getRecursivePassengers().stream().anyMatch(PlayerEntity.class::isInstance);
				if (hasPlayerPassengers) return false;
			}
			else {
				cachedPassengers = entityToStore.getPassengers();
				entityToStore.removePassengers();
			}
		}

		CompoundNBT entityData = new CompoundNBT();
		if (entityToStore.writeUnlessRemoved(entityData)) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.put(NBT_KEY_ENTITY_DATA, entityData);
			nbt.putString(NBT_KEY_ENTITY_NAME, entityToStore.getType().getTranslationKey());
			stack.getOrCreateTag().put(NBT_KEY_ENTITY, nbt);

			if (removeEntity) {
				if (storePassengers) {
					entityToStore.getRecursivePassengers().forEach(Entity::remove);
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
