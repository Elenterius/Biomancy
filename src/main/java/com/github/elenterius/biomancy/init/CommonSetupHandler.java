package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonSetupHandler {

	private CommonSetupHandler() {}

	@SubscribeEvent
	public static void onSetup(final FMLCommonSetupEvent event) {
		ModNetworkHandler.register();
		ModRecipeBookTypes.init();

		// if not thread safe do it after the common setup event on a single thread
		event.enqueueWork(() -> {
			ModTriggers.register();
			ModPredicates.registerItemPredicates();

			registerDispenserBehaviors();
			ModRecipes.registerComposterRecipes();

			AcidInteractions.register();

			//dumpBiomeTemperatureAndHumidity();
		});

		ModFluids.registerInteractions();
		ModRecipes.registerBrewingRecipes();
		ModsCompatHandler.onBiomancyCommonSetup(event);
	}

	public static void dumpBiomeTemperatureAndHumidity() {
		BiomancyMod.LOGGER.info("dumping biome default temperatures to biome_temperatures.csv...");
		try {
			Stream<String> stringStream = ForgeRegistries.BIOMES.getEntries().stream()
					.map(keyEntry -> "%s,%s,%s".formatted(keyEntry.getKey().location(), keyEntry.getValue().getBaseTemperature(), keyEntry.getValue().getModifiedClimateSettings().downfall()));

			Files.write(Paths.get("biome_temperatures.csv"), (Iterable<String>) stringStream::iterator);
		}
		catch (IOException e) {
			BiomancyMod.LOGGER.error("Failed to dump biome temps!", e);
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegisterEvent event) {
		if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
			ModRecipes.registerIngredientSerializers();
		}
	}

	private static void registerDispenserBehaviors() {
		DispenserBlock.registerBehavior(ModItems.ESSENCE_EXTRACTOR.get(), new OptionalDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				setSuccess(ExtractorItem.tryExtractEssence(source.getLevel(), pos, stack));
				if (isSuccess() && stack.hurt(1, source.getLevel().getRandom(), null)) {
					stack.setCount(0);
				}
				return stack;
			}
		});

		DispenserBlock.registerBehavior(ModItems.INJECTOR.get(), new OptionalDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				setSuccess(InjectorItem.tryInjectLivingEntity(source.getLevel(), pos, stack));
				if (isSuccess() && stack.hurt(1, source.getLevel().getRandom(), null)) {
					stack.setCount(0);
				}
				return stack;
			}
		});

		DispenserBlock.registerBehavior(ModItems.ACID_BUCKET.get(), new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			public ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				Level level = source.getLevel();

				DispensibleContainerItem containerItem = (DispensibleContainerItem) stack.getItem();
				if (containerItem.emptyContents(null, level, pos, null, stack)) {
					containerItem.checkExtraContent(null, level, stack, pos);
					return new ItemStack(Items.BUCKET);
				}
				else {
					return defaultDispenseItemBehavior.dispense(source, stack);
				}
			}
		});
	}

}
