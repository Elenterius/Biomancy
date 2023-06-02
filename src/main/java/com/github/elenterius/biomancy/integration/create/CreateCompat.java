package com.github.elenterius.biomancy.integration.create;

public final class CreateCompat {

	private CreateCompat() {}

	public static void onPostSetup() {
		InteractionBehaviors.register();
	}

}
