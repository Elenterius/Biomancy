package com.github.elenterius.biomancy.entity.ownable;

import com.github.elenterius.biomancy.entity.ai.goal.controllable.FollowOwnerGoal;
import com.github.elenterius.biomancy.entity.ai.goal.controllable.*;
import com.github.elenterius.biomancy.ownable.IOwnableMob;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

@Deprecated
public class Fleshkin extends OwnableMonster implements IControllableMob<Fleshkin> {

	private static final EntityDataAccessor<Boolean> IS_CHILD = SynchedEntityData.defineId(Fleshkin.class, EntityDataSerializers.BOOLEAN);

	private static final EntityDataAccessor<Byte> BEHAVIOR_COMMAND = SynchedEntityData.defineId(Fleshkin.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Fleshkin.class, EntityDataSerializers.BOOLEAN);

	private static final UUID SPEED_BOOST_UUID = UUID.fromString("7ac9f8fa-3d7c-48e5-9690-fa7025723b04");
	private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(SPEED_BOOST_UUID, "speed boost", 0.2F, AttributeModifier.Operation.MULTIPLY_BASE);

	public Fleshkin(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		setCanPickUpLoot(true); //todo: replace with armor stand equip logic
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 35d)
				.add(Attributes.MAX_HEALTH, 20d)
				.add(Attributes.ARMOR, 2d)
				.add(Attributes.MOVEMENT_SPEED, 0.35d)
				.add(Attributes.ATTACK_DAMAGE, 4d);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(IS_CHILD, true);
		entityData.define(BEHAVIOR_COMMAND, Command.DEFEND_OWNER.serialize());
		entityData.define(IS_CHARGING_CROSSBOW, false);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 1d, true));
		goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9d, 36f));
		goalSelector.addGoal(3, new ReturnToHomePosGoal<>(this, 0.6d, false));
		goalSelector.addGoal(4, new PatrolAreaGoal<>(this, 0.6d));
		goalSelector.addGoal(5, new FollowOwnerGoal<>(this, 1d, 10f, 2f, false));
		goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8f));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));

		targetSelector.addGoal(1, new CopyOwnerRevengeTargetGoal<>(this));
		targetSelector.addGoal(2, new CopyOwnerAttackTargetGoal<>(this));
		targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
		targetSelector.addGoal(4, new FindAttackTargetGoal<>(this, Mob.class, 5, false, false, target -> {
			if (target instanceof Enemy) {
				if (target instanceof IOwnableMob) {
					Optional<Player> owner = getOwnerAsPlayer();
					if (owner.isPresent()) return shouldAttackEntity(target, owner.get());
				}
				return true;
			}
			return false;
		}));
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isInvulnerableTo(source)) {
			return false;
		}
		else if (getActiveCommand() == Command.SIT) {
			setActiveCommand(Command.PATROL_AREA);
		}
		return super.hurt(source, amount);
	}

	public static void displayCommandSetMsg(Player player, Component name, Command newCommand) {
		MutableComponent cmd = ComponentUtil.literal(newCommand.toString()).withStyle(ChatFormatting.DARK_AQUA);
		MutableComponent text = TextComponentUtil.getMsgText("set_behavior_command", name, cmd).withStyle(ChatFormatting.WHITE);
		player.displayClientMessage(text, true);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		//debug stuff
		if (player.isCreative()) {
			Item item = player.getMainHandItem().getItem();
			if (item == Items.DEBUG_STICK /*|| item == ModItems.CONTROL_STAFF.get()*/) {
				setOwner(player);
				player.displayClientMessage(ComponentUtil.literal("You are now the owner of this creature!").withStyle(ChatFormatting.RED), true);
			}
		}

		if (!player.getMainHandItem().isEmpty() || !isOwner(player)) return InteractionResult.PASS;

		if (!player.level().isClientSide() && player.isShiftKeyDown()) {
			Command newCommand = getActiveCommand().cycle();
			updateRestriction(newCommand);
			setActiveCommand(newCommand);
			displayCommandSetMsg(player, getName(), newCommand);
		}
		return InteractionResult.sidedSuccess(level().isClientSide());
	}

	@Override
	public Command getActiveCommand() {
		return Command.deserialize((byte) (entityData.get(BEHAVIOR_COMMAND) & 0xF));
	}

	@Override
	public void setActiveCommand(Command commandIn) {
		int prevCommand = (entityData.get(BEHAVIOR_COMMAND) & 0xF) << 4;
		int newCommand = commandIn.serialize();
		entityData.set(BEHAVIOR_COMMAND, (byte) (prevCommand | newCommand));
	}

	public Command getGolemCommand(byte packedCommands) {
		return Command.deserialize((byte) (packedCommands & 0xF));
	}

	public Command getPreviousGolemCommand(byte packedCommands) {
		return Command.deserialize((byte) (packedCommands >> 4));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("IsBaby", isBaby());
		tag.putByte("GolemCommand", entityData.get(BEHAVIOR_COMMAND));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setBaby(tag.getBoolean("IsBaby"));
		entityData.set(BEHAVIOR_COMMAND, tag.getByte("GolemCommand"));
	}

	//client side
	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (IS_CHILD.equals(key)) {
			refreshDimensions();
		}
	}

	@Override
	protected float getEquipmentDropChance(EquipmentSlot slot) {
		return 2f;
	}

	@Override
	public boolean isBaby() {
		return entityData.get(IS_CHILD);
	}

	@Override
	public void setBaby(boolean isChild) {
		entityData.set(IS_CHILD, isChild);
		if (!level().isClientSide) {
			AttributeInstance attribute = getAttribute(Attributes.MOVEMENT_SPEED);
			if (attribute != null) {
				attribute.removeModifier(SPEED_MODIFIER);
				if (isChild) {
					attribute.addTransientModifier(SPEED_MODIFIER);
				}
			}
		}
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntityDimensions dim) {
		return isBaby() ? 0.93f : 1.74f;
	}

	@Override
	public boolean wantsToPickUp(ItemStack stack) {
		Item item = stack.getItem();
		return (item instanceof ArmorItem || item instanceof TieredItem || item instanceof ShieldItem || item instanceof ProjectileWeaponItem);
	}

	@Override
	public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
		return item == Items.CROSSBOW;
	}

	private boolean isChargingCrossbow() {
		return entityData.get(IS_CHARGING_CROSSBOW);
	}

	public void setChargingCrossbow(boolean isCharging) {
		entityData.set(IS_CHARGING_CROSSBOW, isCharging);
	}

	public boolean isHoldingMeleeWeapon() {
		return getMainHandItem().getItem() instanceof TieredItem;
	}

	@Override
	public IControllableMob.Action getCurrentAction() {
		if (isAggressive() && isHoldingMeleeWeapon()) {
			return IControllableMob.Action.ATTACKING_WITH_MELEE_WEAPON;
		}
		else if (isChargingCrossbow()) {
			return IControllableMob.Action.CROSSBOW_CHARGE;
		}
		else {
			return isAggressive() && isHolding(Items.CROSSBOW) ? IControllableMob.Action.CROSSBOW_HOLD : IControllableMob.Action.IDLE;
		}
	}

	@Override
	public boolean tryToReturnIntoPlayerInventory() {
		return false;
	}

}
