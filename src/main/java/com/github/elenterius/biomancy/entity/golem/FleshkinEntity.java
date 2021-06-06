package com.github.elenterius.biomancy.entity.golem;

import com.github.elenterius.biomancy.entity.ai.goal.golem.FollowOwnerGoal;
import com.github.elenterius.biomancy.entity.ai.goal.golem.*;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.UUID;

public class FleshkinEntity extends OwnableMonsterEntity implements IGolem {

	private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(FleshkinEntity.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Byte> GOLEM_COMMAND = EntityDataManager.createKey(FleshkinEntity.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.createKey(FleshkinEntity.class, DataSerializers.BOOLEAN);

	private static final UUID SPEED_BOOST_UUID = UUID.fromString("7ac9f8fa-3d7c-48e5-9690-fa7025723b04");
	private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(SPEED_BOOST_UUID, "speed boost", 0.2F, AttributeModifier.Operation.MULTIPLY_BASE);

	public FleshkinEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
		setCanPickUpLoot(true);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MonsterEntity.func_234295_eP_()
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 35d)
				.createMutableAttribute(Attributes.MAX_HEALTH, 20d)
				.createMutableAttribute(Attributes.ARMOR, 2d)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35d)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 4d);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(IS_CHILD, true);
		dataManager.register(GOLEM_COMMAND, Command.DEFEND_OWNER.serialize());
		dataManager.register(IS_CHARGING_CROSSBOW, false);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(2, new MeleeAttackGoal(this, 1d, true));
		goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9d, 36f));
		goalSelector.addGoal(3, new ReturnToHomePosGoal<>(this, 0.6d, false));
		goalSelector.addGoal(4, new PatrolAreaGoal<>(this, 0.6d));
		goalSelector.addGoal(5, new FollowOwnerGoal<>(this, 1d, 10f, 2f, false));
		goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8f));
		goalSelector.addGoal(6, new LookRandomlyGoal(this));

		targetSelector.addGoal(1, new CopyOwnerRevengeTargetGoal<>(this));
		targetSelector.addGoal(2, new CopyOwnerAttackTargetGoal<>(this));
		targetSelector.addGoal(3, new HurtByTargetGoal(this).setCallsForHelp());
		targetSelector.addGoal(4, new FindAttackTargetGoal<>(this, MobEntity.class, 5, false, false, (target) -> {
			if (target instanceof IMob) {
				if (target instanceof IOwnableCreature) {
					Optional<PlayerEntity> owner = getOwner();
					if (owner.isPresent()) {
						return shouldAttackEntity(target, owner.get());
					}
				}
				return true;
			}
			return false;
		}));
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isInvulnerableTo(source)) {
			return false;
		}
		else if (getGolemCommand() == Command.SIT) {
			setGolemCommand(Command.PATROL_AREA);
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
		if (player.isCreative() && player.getHeldItemMainhand().getItem() == Items.DEBUG_STICK) {
			setOwner(player);
			player.sendStatusMessage(new StringTextComponent("You are now the owner of this creature!").mergeStyle(TextFormatting.RED), true);
		}

		if (!player.getHeldItemMainhand().isEmpty() || !isOwner(player)) return ActionResultType.PASS;

		if (!player.world.isRemote() && player.isSneaking()) {
			Command newCommand = getGolemCommand().cycle();
			if (newCommand == Command.SIT) {
				setHomePosAndDistance(getPosition(), 4);
			}
			else if (newCommand == Command.PATROL_AREA) {
				setHomePosAndDistance(getPosition(), 24);
			}
			else if (newCommand == Command.HOLD_POSITION) {
				setHomePosAndDistance(getPosition(), 8);
			}

			setGolemCommand(newCommand);
			IFormattableTextComponent cmd = new StringTextComponent(newCommand.toString()).mergeStyle(TextFormatting.DARK_AQUA);
			TranslationTextComponent text = new TranslationTextComponent(TextUtil.getTranslationKey("msg", "set_golem_command"), getName(), cmd);
			text.mergeStyle(TextFormatting.WHITE);
			player.sendStatusMessage(text, true);
		}
		return ActionResultType.func_233537_a_(world.isRemote());
	}

	@Override
	public Command getGolemCommand() {
		return Command.deserialize((byte) (dataManager.get(GOLEM_COMMAND) & 0xF));
	}

	@Override
	public void setGolemCommand(Command commandIn) {
		int prevCommand = (dataManager.get(GOLEM_COMMAND) & 0xF) << 4;
		int newCommand = commandIn.serialize();
		dataManager.set(GOLEM_COMMAND, (byte) (prevCommand | newCommand));
	}

	public Command getGolemCommand(byte packedCommands) {
		return Command.deserialize((byte) (packedCommands & 0xF));
	}

	public Command getPreviousGolemCommand(byte packedCommands) {
		return Command.deserialize((byte) (packedCommands >> 4));
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("IsBaby", isChild());
		compound.putByte("GolemCommand", dataManager.get(GOLEM_COMMAND));
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		setChild(compound.getBoolean("IsBaby"));
		dataManager.set(GOLEM_COMMAND, compound.getByte("GolemCommand"));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (IS_CHILD.equals(key)) {
			recalculateSize();
		}
	}

	@Override
	protected float getDropChance(EquipmentSlotType slotIn) {
		return 2f;
	}

	@Override
	public boolean isChild() {
		return dataManager.get(IS_CHILD);
	}

	@Override
	public void setChild(boolean isChild) {
		dataManager.set(IS_CHILD, isChild);
		if (!world.isRemote) {
			ModifiableAttributeInstance attribute = getAttribute(Attributes.MOVEMENT_SPEED);
			if (attribute != null) {
				attribute.removeModifier(SPEED_MODIFIER);
				if (isChild) {
					attribute.applyNonPersistentModifier(SPEED_MODIFIER);
				}
			}
		}
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return isChild() ? 0.93f : 1.74f;
	}

	@Override
	public boolean func_230293_i_(ItemStack stack) {
		Item item = stack.getItem();
		return (item instanceof ArmorItem || item instanceof TieredItem || item instanceof ShieldItem || item instanceof ShootableItem);
	}

	@Override
	public boolean func_230280_a_(ShootableItem item) {
		return item == Items.CROSSBOW;
	}

	private boolean isChargingCrossbow() {
		return dataManager.get(IS_CHARGING_CROSSBOW);
	}

	public void setChargingCrossbow(boolean isCharging) {
		dataManager.set(IS_CHARGING_CROSSBOW, isCharging);
	}

	public boolean isHoldingMeleeWeapon() {
		return getHeldItemMainhand().getItem() instanceof TieredItem;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public IGolem.Action getCurrentAction() {
		if (isAggressive() && isHoldingMeleeWeapon()) {
			return IGolem.Action.ATTACKING_WITH_MELEE_WEAPON;
		}
		else if (isChargingCrossbow()) {
			return IGolem.Action.CROSSBOW_CHARGE;
		}
		else {
			return isAggressive() && canEquip(Items.CROSSBOW) ? IGolem.Action.CROSSBOW_HOLD : IGolem.Action.IDLE;
		}
	}

	@Override
	public boolean tryToReturnIntoPlayerInventory() {
		return false;
	}

}
