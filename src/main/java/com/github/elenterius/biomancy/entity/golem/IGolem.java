package com.github.elenterius.biomancy.entity.golem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IGolem {

	default boolean isGolemInactive() {
		return getGolemCommand() == Command.SIT;
	}

	Command getGolemCommand();

	void setGolemCommand(Command cmd);

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
