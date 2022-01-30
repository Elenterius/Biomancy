package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.world.item.BioExtractorItem;
import com.github.elenterius.biomancy.world.item.BioInjectorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonSetupHandler {

	private CommonSetupHandler() {}

	@SubscribeEvent
	public static void onSetup(final FMLCommonSetupEvent event) {
		ModNetworkHandler.register();

		// do stuff after common setup event on single thread
		event.enqueueWork(() -> {
			ModTags.init();
			ModTriggers.register();
			ModRecipes.register();
			registerDispenserBehaviors();
		});
	}

	private static void registerDispenserBehaviors() {
		DispenserBlock.registerBehavior(ModItems.BIO_EXTRACTOR.get(), new OptionalDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				setSuccess(BioExtractorItem.tryExtractEssence(source.getLevel(), pos, stack));
				return stack;
			}
		});

		DispenserBlock.registerBehavior(ModItems.BIO_INJECTOR.get(), new OptionalDispenseItemBehavior() {
			@Override
			protected ItemStack execute(BlockSource source, ItemStack stack) {
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				setSuccess(BioInjectorItem.tryInjectLivingEntity(source.getLevel(), pos, stack));
				return stack;
			}
		});
	}

}
