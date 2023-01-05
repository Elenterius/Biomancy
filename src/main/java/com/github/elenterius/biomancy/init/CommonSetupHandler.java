package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.world.item.BioExtractorItem;
import com.github.elenterius.biomancy.world.item.InjectorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonSetupHandler {

	private CommonSetupHandler() {}

	@SubscribeEvent
	public static void onSetup(final FMLCommonSetupEvent event) {
		ModNetworkHandler.register();
		ModRecipeBookTypes.init();

		// if not thread safe do it after the common setup event on a single thread
		event.enqueueWork(() -> {
			ModTags.init();
			ModTriggers.register();
			registerDispenserBehaviors();
			ModRecipes.registerComposterRecipes();
		});

		ModsCompatHandler.onBiomancyCommonSetup(event);
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		ModRecipes.registerIngredientSerializers();
	}

	private static void registerDispenserBehaviors() {
		DispenserBlock.registerBehavior(ModItems.BIO_EXTRACTOR.get(), new OptionalDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				setSuccess(BioExtractorItem.tryExtractEssence(source.getLevel(), pos, stack));
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
	}

}
