package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonSetupHandler {
	private CommonSetupHandler() {}

	@SubscribeEvent
	public static void onSetup(final FMLCommonSetupEvent event) {
//		ModNetworkHandler.register();

		// do stuff after common setup event on single thread
		event.enqueueWork(() -> {
			ModTags.init();
			ModTriggers.register();

			ModRecipes.registerRecipeTypes();
			ModRecipes.registerCustomItemPredicates();
			ModRecipes.registerComposterRecipes();

			registerDispenserBehaviors();
		});
	}

	@SubscribeEvent
	public static void registerModifierSerializers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
//		event.getRegistry().register(new AnimalDropStomachLootModifier.Serializer().setRegistryName(BiomancyMod.createRL("animal_drop_stomach")));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRegisterEntityType(final RegistryEvent.Register<EntityType<?>> event) {
//		DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
//			@Override
//			protected ItemStack execute(IBlockSource iBlockSource, ItemStack stack) {
//				EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
//				Direction direction = iBlockSource.getBlockState().getValue(DispenserBlock.FACING);
//				entityType.spawn(iBlockSource.getLevel(), stack, null, iBlockSource.getPos().relative(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
//				stack.shrink(1);
//				return stack;
//			}
//		};
	}

	private static void registerDispenserBehaviors() {
//		OptionalDispenseBehavior behavior = new OptionalDispenseBehavior() {
//			@Override
//			protected ItemStack execute(IBlockSource source, ItemStack stack) {
//				ServerWorld world = source.getLevel();
//				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
//				setSuccess(InjectionDeviceItem.dispenserInjectLivingEntity(world, pos, stack));
//				return stack;
//			}
//		};
//		DispenserBlock.registerBehavior(ModItems.INJECTION_DEVICE.get(), behavior);
	}

}
