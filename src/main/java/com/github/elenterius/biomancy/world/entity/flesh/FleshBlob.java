package com.github.elenterius.biomancy.world.entity.flesh;

import com.github.elenterius.biomancy.init.ModLootTables;
import com.github.elenterius.biomancy.world.entity.ai.goal.FindItemGoal;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FleshBlob extends Monster {

	public static final byte JUMPING_STATE_ID = 60;
	public static final byte EATING_STATE_ID = 61;
	public static final Predicate<ItemEntity> ITEM_ENTITY_FILTER = itemEntity -> FindItemGoal.ITEM_ENTITY_FILTER.test(itemEntity) && itemEntity.getItem().isEdible();

	private static final EntityDataAccessor<Byte> BLOB_SIZE = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> FLESH_BLOB_DATA = SynchedEntityData.defineId(FleshBlob.class, EntityDataSerializers.BYTE);
	public static final int MAX_SIZE = 10;

	private int eatTimer;
	private final DNAStorage storedDNA = new DNAStorage(4);

	public FleshBlob(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
		setCanPickUpLoot(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 10)
				.add(Attributes.MOVEMENT_SPEED, 0.2f)
				.add(Attributes.ARMOR, 8)
				.add(Attributes.ATTACK_DAMAGE, 3);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new PanicGoal(this, 1.5d));
		goalSelector.addGoal(2, new CustomAvoidEntityGoal<>(this, Player.class, 6f, 1d, 1.5d));
		goalSelector.addGoal(2, new CustomAttackGoal(this, 1.2d));
		goalSelector.addGoal(3, new FindItemGoal(this, 8f, ITEM_ENTITY_FILTER));
		goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1d));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AgeableMob.class, true));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(FLESH_BLOB_DATA, (byte) 0);
		entityData.define(BLOB_SIZE, (byte) 1);
	}

	public void setAttributeBaseValue(Attribute attribute, double value) {
		AttributeInstance instance = getAttribute(attribute);
		if (instance != null) instance.setBaseValue(value);
	}

	public void setBlobSize(byte size, boolean resetHealth) {
		size = (byte) Mth.clamp(size, 1, MAX_SIZE);
		entityData.set(BLOB_SIZE, size);
		reapplyPosition();
		refreshDimensions();
		setAttributeBaseValue(Attributes.MAX_HEALTH, size * 10d);
		setAttributeBaseValue(Attributes.MOVEMENT_SPEED, 0.2f + 0.1f * size);
		setAttributeBaseValue(Attributes.ATTACK_DAMAGE, size + (getFleshBlobData() == 1 ? 8d : 3d));
		setAttributeBaseValue(Attributes.ARMOR, size * 3d);
		if (resetHealth) setHealth(getMaxHealth());
		xpReward = size;
	}

	public byte getBlobSize() {
		return entityData.get(BLOB_SIZE);
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale(0.5f + getBlobSize() * 0.5f);
	}

	public byte getFleshBlobData() {
		return entityData.get(FLESH_BLOB_DATA);
	}

	public boolean isHangry() {
		return entityData.get(FLESH_BLOB_DATA) == 1;
	}

	public void setHangry() {
		if (!isHangry()) setCustomEntityData((byte) 1);
	}

	public void setCustomEntityData(byte flag) {
		if (flag == 1) {
			setAttributeBaseValue(Attributes.ATTACK_DAMAGE, getBlobSize() + 8d);
		}
		else if (flag == 0) {
			setAttributeBaseValue(Attributes.ATTACK_DAMAGE, getBlobSize() + 3d);
		}

		entityData.set(FLESH_BLOB_DATA, flag);
	}

	@Override
	public void refreshDimensions() {
		double x = getX();
		double y = getY();
		double z = getZ();
		super.refreshDimensions();
		setPos(x, y, z);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (BLOB_SIZE.equals(key)) {
			refreshDimensions();
			setYRot(yHeadRot);
			setYBodyRot(yHeadRot);
			if (isInWater() && random.nextFloat() < 0.05f) {
				doWaterSplashEffect();
			}
		}
		super.onSyncedDataUpdated(key);
	}

	public void addMobDNA(EntityType<LivingEntity> entityType) {
		if (!storedDNA.addDNA(entityType)) {
			Explosion.BlockInteraction mode = ForgeEventFactory.getMobGriefingEvent(level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
			level.explode(this, getX(), getY(), getZ(), getBlobSize() + 4f * 1.25f, mode);
			remove(RemovalReason.KILLED);
		}
	}

	public DNAStorage getStoredDNA() {
		return storedDNA;
	}

	@Override
	public void aiStep() {
		if (isEffectiveAi() && isAlive()) {
			eatTimer++;
			if (eatTimer > 300) {
				ItemStack heldStack = getItemBySlot(EquipmentSlot.MAINHAND);
				if (heldStack.getItem().isEdible() && getTarget() == null) {
					if (eatTimer > 400) {
						FoodProperties food = heldStack.getItem().getFoodProperties();
						ItemStack eatenStack = heldStack.finishUsingItem(level, this);
						if (!eatenStack.isEmpty()) {
							setItemSlot(EquipmentSlot.MAINHAND, eatenStack);
						}
						float health = getHealth();
						if (health < getMaxHealth()) {
							heal(getFoodHealAmount(food));
						}
						else {
							byte blobSize = getBlobSize();
							if (blobSize < MAX_SIZE && random.nextFloat() < getGrowChance(food)) {
								setBlobSize((byte) (blobSize + 1), true);
							}
						}
						eatTimer = 0;
					}
					else if (random.nextFloat() < 0.1f) {
						playSound(getEatingSound(heldStack), 1f, 1f);
						level.broadcastEntityEvent(this, EATING_STATE_ID);
					}
				}
			}
		}

		super.aiStep();
	}

	private float getFoodHealAmount(@Nullable FoodProperties food) {
		if (food == null) return 0.5f;
		return food.getNutrition() * (food.isMeat() ? 1.25f : 0.75f);
	}

	private float getGrowChance(@Nullable FoodProperties food) {
		if (food == null) return 0.4f;
		return 0.4f + (food.getNutrition() * (food.isMeat() ? 0.5f : 0.25f)) / MAX_SIZE;
	}

	@Override
	public boolean canTakeItem(ItemStack stack) {
		if (!canPickUpLoot()) return false;
		EquipmentSlot slotType = LivingEntity.getEquipmentSlotForItem(stack);
		return slotType == EquipmentSlot.MAINHAND && getItemBySlot(slotType).isEmpty();
	}

	@Override
	public boolean canHoldItem(ItemStack stack) {
		Item item = stack.getItem();
		ItemStack heldStack = getItemBySlot(EquipmentSlot.MAINHAND);
		return eatTimer > 0 && item.isEdible() && (heldStack.isEmpty() || !heldStack.getItem().isEdible());
	}

	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canHoldItem(stack)) {
			onItemPickup(itemEntity);
			setItemSlot(EquipmentSlot.MAINHAND, ItemHandlerHelper.copyStackWithSize(stack, 1));
			handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2f;
			take(itemEntity, 1);
			itemEntity.discard();
			eatTimer = 0;
		}
	}

	@Override
	protected void dropEquipment() {
		ItemStack stack = getItemBySlot(EquipmentSlot.MAINHAND);
		if (!stack.isEmpty()) {
			spawnAtLocation(stack);
			setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EATING_STATE_ID) {
			ItemStack stack = getItemBySlot(EquipmentSlot.MAINHAND);
			if (!stack.isEmpty()) {
				float pitch = -getXRot() * Mth.DEG_TO_RAD;
				float yaw = -getYRot() * Mth.DEG_TO_RAD;
				for (int i = 0; i < 8; ++i) {
					Vec3 vector3d = new Vec3((random.nextFloat() - 0.5d) * 0.1d, random.nextFloat() * 0.1d + 0.1d, 0).xRot(pitch).yRot(yaw);
					level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), getX() + getLookAngle().x / 2d, getY(), getZ() + getLookAngle().z / 2d, vector3d.x, vector3d.y + 0.05d, vector3d.z);
				}
			}
			return;
		}
		super.handleEntityEvent(id);
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		return size.height * 0.5f;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("FleshBlobData", getFleshBlobData());
		compound.putByte("Size", getBlobSize());
		compound.put("StoredDNA", storedDNA.toJson());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		setCustomEntityData(compound.getByte("FleshBlobData"));
		setBlobSize(compound.getByte("Size"), false);
		storedDNA.fromJson(compound.getCompound("StoredDNA"));
	}

	@Override
	public SoundSource getSoundSource() {
		return isHangry() ? SoundSource.HOSTILE : SoundSource.NEUTRAL;
	}

	@Override
	public boolean canSpawnSprintParticle() {
		return false;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return switch (getBlobSize()) {
			case 2 -> ModLootTables.FLESH_BLOB_SIZE_2;
			case 3 -> ModLootTables.FLESH_BLOB_SIZE_3;
			case 4 -> ModLootTables.FLESH_BLOB_SIZE_4;
			case 5 -> ModLootTables.FLESH_BLOB_SIZE_5;
			case 6 -> ModLootTables.FLESH_BLOB_SIZE_6;
			case 7 -> ModLootTables.FLESH_BLOB_SIZE_7;
			case 8 -> ModLootTables.FLESH_BLOB_SIZE_8;
			case 9 -> ModLootTables.FLESH_BLOB_SIZE_9;
			case 10 -> ModLootTables.FLESH_BLOB_SIZE_10;
			default -> getType().getDefaultLootTable();
		};
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
		if (spawnData instanceof FleshBlobData data) {
			setCustomEntityData(data.flag);
		}
		else {
			byte flag = (byte) (random.nextFloat() < 0.45 ? 1 : 0);
			spawnData = new FleshBlobData(flag);
			setCustomEntityData(flag);
		}

		return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
	}

	public static class FleshBlobData implements SpawnGroupData {
		public final byte flag;

		public FleshBlobData(byte flagIn) {
			flag = flagIn;
		}
	}

	static class CustomAttackGoal extends MeleeAttackGoal {

		public CustomAttackGoal(FleshBlob mob, double speed) {
			super(mob, speed, true);
		}

		@Override
		public boolean canContinueToUse() {
			if (!((FleshBlob) mob).isHangry() && mob.getRandom().nextFloat() < 0.2f) {
				mob.setTarget(null);
				return false;
			}
			else {
				return super.canContinueToUse();
			}
		}

		@Override
		protected double getAttackReachSqr(LivingEntity attackTarget) {
			return 2f + attackTarget.getBbWidth();
		}
	}

	static class CustomAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

		public CustomAvoidEntityGoal(FleshBlob fleshBlob, Class<T> clazz, float avoidDist, double farSpeed, double nearSpeed) {
			super(fleshBlob, clazz, avoidDist, farSpeed, nearSpeed);
		}

		@Override
		public boolean canUse() {
			return !((FleshBlob) mob).isHangry() && super.canUse();
		}

		@Override
		public boolean canContinueToUse() {
			return !((FleshBlob) mob).isHangry() && super.canContinueToUse();
		}

	}

	static final class DNAStorage {

		private final List<EntityType<?>> entities;
		private int maxSize;

		DNAStorage(int maxSize) {
			this.maxSize = maxSize;
			entities = new ArrayList<>(maxSize);
		}

		public boolean addDNA(EntityType<?> entityType) {
			if (entities.size() >= maxSize || !entityType.canSerialize()) return false;
			entities.add(entityType);
			return true;
		}

		public boolean isEmpty() {
			return entities.isEmpty();
		}

		public void clear() {
			entities.clear();
		}

		public List<EntityType<?>> entities() {return entities;}

		public int maxSize() {return maxSize;}

		public CompoundTag toJson() {
			CompoundTag tag = new CompoundTag();
			ListTag listTag = new ListTag();
			if (!entities.isEmpty()) {
				for (EntityType<?> entityType : entities) {
					listTag.add(StringTag.valueOf(EntityType.getKey(entityType).toString()));
				}
			}
			tag.putInt("MaxSize", maxSize);
			tag.put("Entities", listTag);
			return tag;
		}

		public void fromJson(CompoundTag tag) {
			entities.clear();
			maxSize = tag.getInt("MaxSize");
			ListTag listTag = tag.getList("Entities", Tag.TAG_STRING);
			if (!listTag.isEmpty()) {
				for (int i = 0; i < listTag.size(); i++) {
					String entityTypeId = listTag.getString(i);
					if (!entityTypeId.isEmpty()) {
						EntityType.byString(entityTypeId).ifPresent(this::addDNA);
					}
				}
			}
		}

		@Override
		public String toString() {
			return "DNAStorage[" +
					"entities=" + entities + ", " +
					"maxSize=" + maxSize + ']';
		}

	}

}
