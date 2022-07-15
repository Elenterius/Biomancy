package com.github.elenterius.biomancy.world.entity.ownable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IControllableMob<T extends Mob> {

	private T getMobInstance() {
		//noinspection unchecked
		return (T) this;
	}

	static <T extends Mob> IControllableMob<T> cast(T mob) {
		//noinspection unchecked
		return (IControllableMob<T>) mob;
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

	@OnlyIn(Dist.CLIENT)
	default Action getCurrentAction() {
		return Action.IDLE;
	}

	enum Command {
		SIT, //do nothing
		HOLD_POSITION, //stand guard
		DEFEND_OWNER, //follows owner
		PATROL_AREA, //wander around home position
		SEEK_AND_DESTROY; //seeks out and attacks any hostile entity

		public Command cycle() {
			return deserialize((byte) (ordinal() + 1));
		}

		public static Command deserialize(byte b) {
			for (Command value : values()) {
				if (value.ordinal() == b) return value;
			}
			return SIT;
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
