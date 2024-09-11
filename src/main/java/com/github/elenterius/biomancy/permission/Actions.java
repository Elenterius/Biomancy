package com.github.elenterius.biomancy.permission;

public final class Actions {

	private Actions() {}

	/**
	 * if at least one action can be performed
	 */
	public static final IAction ANY = type -> type.getAccessLevel() > UserType.NONE.getAccessLevel();

	/**
	 * if all actions can be performed
	 */
	public static final IAction ALL = type -> type.getAccessLevel() > UserType.DEFAULT.getAccessLevel();

	public static final IAction DESTROY_BLOCK = type -> type.getAccessLevel() > UserType.DEFAULT.getAccessLevel();
	public static final IAction PLACE_BLOCK = type -> type.getAccessLevel() > UserType.NONE.getAccessLevel();

	/**
	 * open containers, interact with block / block entity
	 */
	public static final IAction USE_BLOCK = type -> type.getAccessLevel() > UserType.NONE.getAccessLevel();

	public static final IAction CONFIGURE = type -> type.getAccessLevel() > UserType.DEFAULT.getAccessLevel();

}
