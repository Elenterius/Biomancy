package com.github.elenterius.blightlings.init;

import com.github.elenterius.blightlings.BlightlingsMod;
import com.github.elenterius.blightlings.enchantment.BulletJumpEnchantment;
import com.github.elenterius.blightlings.enchantment.ClimbingEnchantment;
import com.github.elenterius.blightlings.item.ModSpawnEggItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class CommonSetupHandler
{
    @SubscribeEvent
    public static void onSetup(final FMLCommonSetupEvent event) {
        // do stuff after common setup event on single thread
        event.enqueueWork(() -> {
            ModEntityTypes.onPostSetup();
            ModFeatures.injectCarvableBlocks();
            ModBiomes.onPostSetupBiomes();
        });
    }

    @SubscribeEvent
    public static void onEnchantmentRegistry(final RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(
                new ClimbingEnchantment(Enchantment.Rarity.RARE).setRegistryName("climbing"),
                new BulletJumpEnchantment(Enchantment.Rarity.RARE).setRegistryName("bullet_jump")
        );
    }

    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
//                ModSoundEvents.createSoundEvent("impactsplat"),
                ModSoundEvents.createSoundEvent("wahwah")
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRegisterEntityType(final RegistryEvent.Register<EntityType<?>> event) {
        DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior()
        {
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
        BlightlingsMod.LOGGER.info("Injecting EntityType into SpawnEggs...");
        final Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
        assert EGGS != null;
        List<Field> spawn_eggs = Arrays.stream(ModItems.class.getFields()).filter(field -> field.getName().endsWith("SPAWN_EGG")).collect(Collectors.toList());
        spawn_eggs.forEach(field -> {
            try {
                @SuppressWarnings("unchecked") RegistryObject<ModSpawnEggItem> registryObject = (RegistryObject<ModSpawnEggItem>) field.get(null);
                ModSpawnEggItem spawnEggItem = registryObject.get();
                EGGS.put(spawnEggItem.getType(null), spawnEggItem);
                DispenserBlock.registerDispenseBehavior(spawnEggItem, behavior);
            }
            catch (Exception e) {
                BlightlingsMod.LOGGER.error("Failed to inject EntityType into " + field.getName() + " spawn egg.", e);
            }
        });
    }
}
