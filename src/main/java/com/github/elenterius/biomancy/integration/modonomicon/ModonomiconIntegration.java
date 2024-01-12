package com.github.elenterius.biomancy.integration.modonomicon;

import java.util.function.Consumer;

public final class ModonomiconIntegration {

	private ModonomiconIntegration() {}

	public static void init(Consumer<ModonomiconHelper> helperSetter) {
		helperSetter.accept(new ModonomiconHelperImpl());
	}

}
