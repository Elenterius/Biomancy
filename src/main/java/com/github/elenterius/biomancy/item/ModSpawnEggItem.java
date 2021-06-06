package com.github.elenterius.biomancy.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

public class ModSpawnEggItem extends SpawnEggItem {

	private final RegistryObject<EntityType<?>> typeRegistryObject;

	@SuppressWarnings({"ConstantConditions", "unchecked"})
	public ModSpawnEggItem(RegistryObject<? extends EntityType<?>> registryObject, int primaryColor, int secondaryColor, Properties properties) {
		super(null, primaryColor, secondaryColor, properties);
		typeRegistryObject = (RegistryObject<EntityType<?>>) registryObject;
	}

	@Override
	public EntityType<?> getType(@Nullable CompoundNBT nbt) {
		return typeRegistryObject.get();
	}

}
