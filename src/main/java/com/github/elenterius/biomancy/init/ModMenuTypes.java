package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.gui.DecomposerScreen;
import com.github.elenterius.biomancy.world.inventory.DecomposerMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenuTypes {

	private ModMenuTypes() {}

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BiomancyMod.MOD_ID);

	public static final RegistryObject<MenuType<DecomposerMenu>> DECOMPOSER = MENUS.register("decomposer", () -> IForgeMenuType.create(DecomposerMenu::createClientMenu));
//	public static final RegistryObject<ContainerType<GulgeContainer>> GULGE = CONTAINERS.register("gulge", () -> IForgeContainerType.create(GulgeContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<FleshChestContainer>> FLESHBORN_CHEST = CONTAINERS.register("fleshborn_chest", () -> IForgeContainerType.create(FleshChestContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<ChewerContainer>> CHEWER = CONTAINERS.register("chewer", () -> IForgeContainerType.create(ChewerContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<DigesterContainer>> DIGESTER = CONTAINERS.register("digester", () -> IForgeContainerType.create(DigesterContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<SolidifierContainer>> SOLIDIFIER = CONTAINERS.register("solidifier", () -> IForgeContainerType.create(SolidifierContainer::createClientContainer));
//	public static final RegistryObject<ContainerType<EvolutionPoolContainer>> EVOLUTION_POOL = CONTAINERS.register("evolution_pool", () -> IForgeContainerType.create(EvolutionPoolContainer::createClientContainer));

	@OnlyIn(Dist.CLIENT)
	static void registerMenuScreens() {
		MenuScreens.register(DECOMPOSER.get(), DecomposerScreen::new);
//		MenuScreens.register(GULGE.get(), GulgeContainerScreen::new);
//		MenuScreens.register(FLESHBORN_CHEST.get(), FleshChestContainerScreen::new);
//		MenuScreens.register(CHEWER.get(), ChewerContainerScreen::new);
//		MenuScreens.register(DIGESTER.get(), DigesterContainerScreen::new);
//		MenuScreens.register(SOLIDIFIER.get(), SolidifierContainerScreen::new);
//		MenuScreens.register(EVOLUTION_POOL.get(), EvolutionPoolContainerScreen::new);
	}

}
