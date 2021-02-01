package com.github.elenterius.biomancy.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;

public class ModSpawnEggItem extends SpawnEggItem {
	private final Lazy<? extends EntityType<?>> lazyEntityTypeSupplier;

	public ModSpawnEggItem(RegistryObject<? extends EntityType<?>> registryObject, int primaryColor, int secondaryColor, Properties properties) {
		//noinspection ConstantConditions
		super(null, primaryColor, secondaryColor, properties);
		lazyEntityTypeSupplier = Lazy.of(registryObject);
	}

	@Override
	public EntityType<?> getType(@Nullable CompoundNBT nbt) {
		return lazyEntityTypeSupplier.get();
	}

}
