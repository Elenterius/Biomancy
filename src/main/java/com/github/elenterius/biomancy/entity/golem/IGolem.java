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
		SIT,
		DEFEND_POSITION,
		DEFEND_OWNER,
		HUNT;

		public byte serialize() {
			return (byte) ordinal();
		}

		public static Command deserialize(byte b) {
			for (Command value : values()) {
				if (value.ordinal() == b) return value;
			}
			return DEFEND_OWNER;
		}
	}

	enum Action {
		IDLE,
		ATTACKING_WITH_MELEE_WEAPON,
		CROSSBOW_HOLD,
		CROSSBOW_CHARGE
	}
}
