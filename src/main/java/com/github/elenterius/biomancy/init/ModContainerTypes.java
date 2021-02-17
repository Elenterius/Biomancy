package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.inventory.DecomposerContainer;
import com.github.elenterius.biomancy.inventory.EvolutionPoolContainer;
import com.github.elenterius.biomancy.inventory.FleshChestContainer;
import com.github.elenterius.biomancy.inventory.GulgeContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModContainerTypes {
	private ModContainerTypes() {}

	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BiomancyMod.MOD_ID);

	public static final RegistryObject<ContainerType<GulgeContainer>> GULGE = CONTAINERS.register("gulge", () -> IForgeContainerType.create(GulgeContainer::createClientContainer));
	public static final RegistryObject<ContainerType<FleshChestContainer>> FLESH_CHEST = CONTAINERS.register("bioflesh_chest", () -> IForgeContainerType.create(FleshChestContainer::createClientContainer));
	public static final RegistryObject<ContainerType<DecomposerContainer>> DECOMPOSER = CONTAINERS.register("decomposer", () -> IForgeContainerType.create(DecomposerContainer::createClientContainer));
	public static final RegistryObject<ContainerType<EvolutionPoolContainer>> EVOLUTION_POOL = CONTAINERS.register("evolution_pool", () -> IForgeContainerType.create(EvolutionPoolContainer::createClientContainer));
}
