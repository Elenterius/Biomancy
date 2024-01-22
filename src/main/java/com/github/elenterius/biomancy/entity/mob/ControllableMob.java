package com.github.elenterius.biomancy.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

public interface ControllableMob<T extends Mob> {

	static <T extends Mob> ControllableMob<T> cast(T mob) {
		//noinspection unchecked
		return (ControllableMob<T>) mob;
	}

	private T getMobInstance() {
		//noinspection unchecked
		return (T) this;
	}

	default boolean canExecuteCommand() {
		return getActiveCommand() != Command.SIT;
	}

	Command getActiveCommand();

	void setActiveCommand(Command cmd);

	default void updateRestriction(Command cmd) {
		updateRestriction(cmd, getMobInstance().blockPosition());
	}

	default void updateRestriction(Command cmd, BlockPos pos) {
		T mob = getMobInstance();
		if (cmd == Command.SIT) {
			mob.restrictTo(pos, 4);
		}
		else if (cmd == Command.PATROL_AREA) {
			mob.restrictTo(pos, 24);
		}
		else if (cmd == Command.HOLD_POSITION) {
			mob.restrictTo(pos, 8);
		}
		else {
			mob.clearRestriction();
		}
	}

	default Action getCurrentAction() {
		return Action.IDLE;
	}

	enum Command {
		SIT, //do nothing
		HOLD_POSITION, //stand guard
		DEFEND_OWNER, //follows owner
		PATROL_AREA, //wander around home position
		SEEK_AND_DESTROY; //seeks out and attacks any hostile entity

		public static Command deserialize(byte b) {
			for (Command value : values()) {
				if (value.ordinal() == b) return value;
			}
			return SIT;
		}

		public Command cycle() {
			return deserialize((byte) (ordinal() + 1));
		}

		public byte serialize() {
			return (byte) ordinal();
		}
	}

	enum Action {
		IDLE,
		ATTACKING_WITH_MELEE_WEAPON,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE
	}
}
