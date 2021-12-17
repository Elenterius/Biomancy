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
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class EntityStorageBagItem extends BagItem {

	public static final String NBT_KEY_ENTITIES = "StoredEntities";
	public static final String NBT_KEY_ENTITY_NAME = "Name";
	public static final String NBT_KEY_ENTITY_DATA = "Data";
	public static final String NBT_KEY_FULLNESS = "Fullness";
	public static final String NBT_KEY_ENTITY_VOLUME = "Volume";

	public final float maxVolume;
	public final byte maxEntities;

	public EntityStorageBagItem(float maxVolume, byte maxEntities, Properties properties) {
		super(properties);
		this.maxVolume = maxVolume;
		this.maxEntities = maxEntities;
	}

	public EntityStorageBagItem(Properties properties) {
		super(properties);
		maxVolume = Float.MAX_VALUE;
		maxEntities = 1;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		CompoundNBT nbt = stack.getOrCreateTag();
		tooltip.add(new TranslationTextComponent(String.format("%.1f/%.1f m\u00B3", nbt.getFloat(NBT_KEY_FULLNESS), maxVolume)).withStyle(TextFormatting.DARK_GRAY));

		if (nbt.contains(NBT_KEY_ENTITIES)) {
			ListNBT storedEntities = getStoredEntities(nbt);

			tooltip.add(new TranslationTextComponent(storedEntities.size() + "/" + maxEntities + " E").withStyle(TextFormatting.DARK_GRAY));

			if (!storedEntities.isEmpty()) {
				IFormattableTextComponent names = new StringTextComponent("");
				int size = storedEntities.size();
				for (int i = 0; i < size; i++) {
					CompoundNBT entityNbt = storedEntities.getCompound(i);
					TranslationTextComponent entityName = new TranslationTextComponent(entityNbt.getString(NBT_KEY_ENTITY_NAME));
					names.append(entityName);
					if (i < size - 1) names.append(", ");
				}
				tooltip.add(TextUtil.getTooltipText("contains", names).withStyle(TextFormatting.GRAY));
			}
			else tooltip.add(TextUtil.getTooltipText("contains_nothing").withStyle(TextFormatting.GRAY));
		}
		else {
			tooltip.add(new TranslationTextComponent("0/" + maxEntities + " E").withStyle(TextFormatting.DARK_GRAY));
			tooltip.add(TextUtil.getTooltipText("contains_nothing").withStyle(TextFormatting.GRAY));
		}
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_ENTITIES)) {
			ListNBT storedEntities = getStoredEntities(nbt);
			if (!storedEntities.isEmpty()) {
				IFormattableTextComponent textComponent = new StringTextComponent("").append(displayName).append(" (");
				int size = storedEntities.size();
				for (int i = 0; i < size; i++) {
					CompoundNBT entityNbt = storedEntities.getCompound(i);
					TranslationTextComponent entityName = new TranslationTextComponent(entityNbt.getString(NBT_KEY_ENTITY_NAME));
					textComponent.append(entityName);
					if (i < size - 1) textComponent.append(", ");
				}
				return textComponent.append(")");
			}
		}
		return super.getHighlightTip(stack, displayName);
	}

	@Override
	public float getFullness(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_ENTITIES)) {
			ListNBT storedEntities = getStoredEntities(nbt);
			if (!storedEntities.isEmpty()) {
				if (storedEntities.size() == maxEntities) return 1f;
				return MathHelper.clamp(stack.getOrCreateTag().getFloat(NBT_KEY_FULLNESS) / maxVolume, 0f, 1f);
			}
		}
		return 0f;
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getLevel().isClientSide()) return ActionResultType.PASS;

		PlayerEntity player = context.getPlayer();
		if (player != null) {
			onPlayerInteractWithItem(stack, player);
			CompoundNBT nbt = stack.getOrCreateTag();
			ListNBT storedEntities = getStoredEntities(nbt);
			if (!storedEntities.isEmpty()) {
				if (context.getLevel().mayInteract(player, context.getClickedPos()) && player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {

					CompoundNBT entityNbt = storedEntities.getCompound(storedEntities.size() - 1);
					if (spawnEntity(context, entityNbt)) {
						nbt.putFloat(NBT_KEY_FULLNESS, nbt.getFloat(NBT_KEY_FULLNESS) - entityNbt.getFloat(NBT_KEY_ENTITY_VOLUME));
						storedEntities.remove(entityNbt);
						context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.75f, 0.35f + context.getLevel().random.nextFloat() * 0.25f);
						return ActionResultType.SUCCESS;
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

	private boolean spawnEntity(ItemUseContext context, CompoundNBT entityNbt) {
		Entity newEntity = EntityType.loadEntityRecursive(entityNbt.getCompound(NBT_KEY_ENTITY_DATA), context.getLevel(), entity -> {
			Vector3d pos = MobUtil.getAdjustedSpawnPositionFor(context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), entity);
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
			return context.getLevel().addFreshEntity(newEntity);
		}

		return false;
	}

	public boolean isValidEntity(Entity entity) {
		return entity.isAlive() && !(entity instanceof PlayerEntity) && entity.canChangeDimensions() && entity.getType().canSummon() && entity.getType().canSerialize();
	}

	public boolean canStoreEntity(ItemStack stack, Entity entity) {
		return hasSpaceForEntity(stack, entity) && hasSlotForEntity(stack, entity);
	}

	public boolean hasSlotForEntity(ItemStack stack, Entity entity) {
		return getStoredEntities(stack.getOrCreateTag()).size() < maxEntities;
	}

	public boolean hasSpaceForEntity(ItemStack stack, Entity entity) {
		return stack.getOrCreateTag().getFloat(NBT_KEY_FULLNESS) + entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() <= maxVolume;
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		if (playerIn.level.isClientSide()) return ActionResultType.PASS;
		onPlayerInteractWithItem(stack, playerIn);

		boolean isValidTarget = isValidEntity(target) && (!MobUtil.isBoss(target) || playerIn.isCreative()); // blame creative player if something breaks due to storing boss mobs
		if (isValidTarget) {
			boolean hasSpaceForEntity = hasSpaceForEntity(stack, target);
			boolean hasSlotForEntity = hasSlotForEntity(stack, target);
			if (hasSpaceForEntity && hasSlotForEntity) {
				if (storeEntity(stack, target, true, true)) {
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
				playerIn.displayClientMessage(TextUtil.getFailureMsgText(hasSlotForEntity ? "entity_too_large" : "already_full"), true);
			}
		}

		return ActionResultType.PASS;
	}

	public boolean storeEntity(ItemStack stack, Entity entity, boolean removeEntity, boolean storePassengers) {

		if (removeEntity && entity.isPassenger()) {
			entity.removeVehicle();
			if (entity.isPassenger()) return false;
		}

		List<Entity> cachedPassengers = null;
		if (entity.isVehicle()) {
			if (storePassengers) {
				boolean hasPlayerPassengers = entity.getIndirectPassengers().stream().anyMatch(PlayerEntity.class::isInstance);
				if (hasPlayerPassengers) return false;
			}
			else {
				cachedPassengers = entity.getPassengers();
				entity.ejectPassengers();
			}
		}

		if (saveEntity(stack, entity)) {
			if (removeEntity) {
				if (storePassengers) entity.getIndirectPassengers().forEach(Entity::remove);
				entity.remove();
			}
			else if (cachedPassengers != null) { //if we don't remove the entity from the world we restore the passengers
				cachedPassengers.forEach(passenger -> passenger.startRiding(entity));
			}

			return true;
		}

		return false;
	}

	private boolean saveEntity(ItemStack stack, Entity entity) {
		CompoundNBT dataNbt = new CompoundNBT();
		if (entity.saveAsPassenger(dataNbt)) {
			float volume = entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight();

			CompoundNBT entityNbt = new CompoundNBT();
			entityNbt.put(NBT_KEY_ENTITY_DATA, dataNbt);
			entityNbt.putString(NBT_KEY_ENTITY_NAME, entity.getType().getDescriptionId());
			entityNbt.putFloat(NBT_KEY_ENTITY_VOLUME, volume);

			CompoundNBT stackNbt = stack.getOrCreateTag();
			ListNBT listNbt = getStoredEntities(stackNbt);
			listNbt.add(entityNbt);
//			stackNbt.put(NBT_KEY_ENTITIES, listNbt); //override old list
			stackNbt.putFloat(NBT_KEY_FULLNESS, stackNbt.getFloat(NBT_KEY_FULLNESS) + volume);
			return true;
		}
		return false;
	}

	public boolean containsEntities(ItemStack stack) {
		return !stack.getOrCreateTag().getList(NBT_KEY_ENTITIES, Constants.NBT.TAG_COMPOUND).isEmpty();
	}

	protected ListNBT getStoredEntities(CompoundNBT nbt) {
		if (!nbt.contains(NBT_KEY_ENTITIES)) {
			ListNBT listNbt = new ListNBT();
			nbt.put(NBT_KEY_ENTITIES, listNbt);
			return listNbt;
		}
		return nbt.getList(NBT_KEY_ENTITIES, Constants.NBT.TAG_COMPOUND);
	}

	protected ListNBT getStoredEntities(ItemStack stack) {
		return getStoredEntities(stack.getOrCreateTag());
	}

}
