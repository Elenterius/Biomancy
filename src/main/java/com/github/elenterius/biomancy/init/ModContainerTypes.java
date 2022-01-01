package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModContainerTypes {
	private ModContainerTypes() {}

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BiomancyMod.MOD_ID);

//	public static final RegistryObject<ContainerType<GulgeContainer>> GULGE = CONTAINERS.register("gulge", () -> IForgeContainerType.create(GulgeContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<FleshChestContainer>> FLESHBORN_CHEST = CONTAINERS.register("fleshborn_chest", () -> IForgeContainerType.create(FleshChestContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<DecomposerContainer>> DECOMPOSER = CONTAINERS.register("decomposer", () -> IForgeContainerType.create(DecomposerContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<ChewerContainer>> CHEWER = CONTAINERS.register("chewer", () -> IForgeContainerType.create(ChewerContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<DigesterContainer>> DIGESTER = CONTAINERS.register("digester", () -> IForgeContainerType.create(DigesterContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<SolidifierContainer>> SOLIDIFIER = CONTAINERS.register("solidifier", () -> IForgeContainerType.create(SolidifierContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<EvolutionPoolContainer>> EVOLUTION_POOL = CONTAINERS.register("evolution_pool", () -> IForgeContainerType.create(EvolutionPoolContainer::createClientContainer));

	@OnlyIn(Dist.CLIENT)
	static void registerContainerScreens() {
//		ScreenManager.register(GULGE.get(), GulgeContainerScreen::new);
//		ScreenManager.register(FLESHBORN_CHEST.get(), FleshChestContainerScreen::new);
//		ScreenManager.register(CHEWER.get(), ChewerContainerScreen::new);
//		ScreenManager.register(DIGESTER.get(), DigesterContainerScreen::new);
//		ScreenManager.register(SOLIDIFIER.get(), SolidifierContainerScreen::new);
//		ScreenManager.register(DECOMPOSER.get(), DecomposerContainerScreen::new);
//		ScreenManager.register(EVOLUTION_POOL.get(), EvolutionPoolContainerScreen::new);
	}

}
