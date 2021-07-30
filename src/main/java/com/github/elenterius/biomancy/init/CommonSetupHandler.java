package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.capabilities.IItemDecayTracker;
import com.github.elenterius.biomancy.capabilities.ItemDecayImpl;
import com.github.elenterius.biomancy.handler.AnimalDropStomachLootModifier;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = BiomancyMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonSetupHandler {
	private CommonSetupHandler() {}

	@SubscribeEvent
	public static void onSetup(final FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IItemDecayTracker.class, new ItemDecayImpl.DecayTrackerStorage(), ItemDecayImpl.DecayTrackerDefaultImpl::new);

		ModNetworkHandler.register();

		// do stuff after common setup event on single thread
		event.enqueueWork(() -> {
			ModTags.init();
			ModTriggers.register();

			ModRecipes.registerRecipeTypes();
			ModRecipes.registerCustomItemPredicates();
			ModRecipes.registerComposterRecipes();

			ModEntityTypes.onPostSetup();
		});
	}

	@SubscribeEvent
	public static void registerModifierSerializers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		event.getRegistry().register(new AnimalDropStomachLootModifier.Serializer().setRegistryName(BiomancyMod.createRL("animal_drop_stomach")));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRegisterEntityType(final RegistryEvent.Register<EntityType<?>> event) {
		DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
			@Override
			protected ItemStack dispenseStack(IBlockSource iBlockSource, ItemStack stack) {
				EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());
				Direction direction = iBlockSource.getBlockState().get(DispenserBlock.FACING);
				entityType.spawn(iBlockSource.getWorld(), stack, null, iBlockSource.getBlockPos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
				stack.shrink(1);
				return stack;
			}
		};

		//hacky fix for spawn eggs and deferred entity types
		BiomancyMod.LOGGER.info("Injecting EntityType into SpawnEggs...");
		final Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
		Objects.requireNonNull(EGGS);
		for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
			if (entry.get() instanceof SpawnEggItem) {
				SpawnEggItem item = (SpawnEggItem) entry.get();
				EGGS.put(item.getType(null), item);
				DispenserBlock.registerDispenseBehavior(item, behavior);
			}
		}
	}
}
