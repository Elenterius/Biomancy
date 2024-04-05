package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.block.property.*;
import com.github.elenterius.biomancy.util.EnhancedIntegerProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class ModBlockProperties {
	public static final BooleanProperty IS_CRAFTING = BooleanProperty.create("crafting");
	public static final EnhancedIntegerProperty CHARGE = EnhancedIntegerProperty.create("charge", 0, 15);
	public static final EnumProperty<UserSensitivity> USER_SENSITIVITY = EnumProperty.create("sensitivity", UserSensitivity.class);
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final EnumProperty<DirectionalSlabType> DIRECTIONAL_SLAB_TYPE = EnumProperty.create("type", DirectionalSlabType.class);
	public static final BooleanProperty VIAL_0 = BooleanProperty.create("vial_0");
	public static final BooleanProperty VIAL_1 = BooleanProperty.create("vial_1");
	public static final BooleanProperty VIAL_2 = BooleanProperty.create("vial_2");
	public static final BooleanProperty VIAL_3 = BooleanProperty.create("vial_3");
	public static final BooleanProperty VIAL_4 = BooleanProperty.create("vial_4");
	public static final EnumProperty<DirectedConnection> DIRECTED_CONNECTION = EnumProperty.create("connection", DirectedConnection.class);
	public static final EnumProperty<VertexType> VERTEX_TYPE = EnumProperty.create("vertex", VertexType.class);
	public static final EnhancedIntegerProperty SPIKES = EnhancedIntegerProperty.create("spikes", 1, 3);
	public static final EnumProperty<MobSoundType> MOB_SOUND_TYPE = EnumProperty.create("mob_sound_type", MobSoundType.class);

	private ModBlockProperties() {}

}
