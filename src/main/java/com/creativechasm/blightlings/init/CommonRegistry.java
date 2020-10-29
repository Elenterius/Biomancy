package com.creativechasm.blightlings.init;

import com.creativechasm.blightlings.BlightlingsMod;
import com.creativechasm.blightlings.entity.BloblingEntity;
import com.creativechasm.blightlings.entity.BroodmotherEntity;
import com.creativechasm.blightlings.item.GogglesArmorItem;
import com.creativechasm.blightlings.item.ModArmorMaterial;
import com.creativechasm.blightlings.item.ModItemTier;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = BlightlingsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class CommonRegistry
{
    @SubscribeEvent
    public static void onSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CommonRegistry::onPostSetup); // do stuff after common setup event
    }

    public static void onPostSetup() {
        GlobalEntityTypeAttributes.put(EntityTypes.BLOBLING, BloblingEntity.createAttributes().func_233813_a_());
        GlobalEntityTypeAttributes.put(EntityTypes.BROOD_MOTHER, BroodmotherEntity.createAttributes().func_233813_a_());

        EntitySpawnPlacementRegistry.register(EntityTypes.BLOBLING, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CommonRegistry::monsterCondition);
        EntitySpawnPlacementRegistry.register(EntityTypes.BROOD_MOTHER, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CommonRegistry::monsterCondition);
    }

    @SubscribeEvent
    public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> registryEvent) {
        registryEvent.getRegistry().register(EntityTypes.BLOBLING);
        registryEvent.getRegistry().register(EntityTypes.BROOD_MOTHER);
    }

    private static boolean monsterCondition(EntityType<? extends MonsterEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && mobCondition(entityType, world, spawnReason, pos, random); // && lightCondition(world, pos, random)
    }

    private static boolean mobCondition(EntityType<? extends MobEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos blockpos = pos.down();
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(blockpos).canEntitySpawn(world, blockpos, entityType);
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(-1, BlightlingsMod.MOD_ID)
    {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(ModItems.TRUE_SIGHT_GOGGLES);
        }
    };

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(ITEM_GROUP);

        event.getRegistry().registerAll(
                new GogglesArmorItem(ModArmorMaterial.CRYSTAL, new Item.Properties().group(ITEM_GROUP).rarity(Rarity.EPIC)).setRegistryName("true_sight_goggles"),
                new ArmorItem(ModArmorMaterial.CRYSTAL, EquipmentSlotType.FEET, new Item.Properties().group(ITEM_GROUP).rarity(Rarity.EPIC)).setRegistryName("climbing_boots"),
                new ArmorItem(ModArmorMaterial.CRYSTAL, EquipmentSlotType.LEGS, new Item.Properties().group(ITEM_GROUP).rarity(Rarity.EPIC)).setRegistryName("jumping_pants"),
                new AxeItem(ModItemTier.CRYSTAL, 5F, -3.15F, new Item.Properties().group(ITEM_GROUP).rarity(Rarity.EPIC)).setRegistryName("blightbringer_axe"),

                createSpawnEggItem(EntityTypes.BLOBLING, 0x4B2277, 0xAF27E0, properties, "blobling"),
                createSpawnEggItem(EntityTypes.BROOD_MOTHER, 0x4B2277, 0xCF7DEC, properties, "brood_mother")
        );
    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {

    }

    private static <T extends Entity> Item createSpawnEggItem(EntityType<T> entityType, int primaryColor, int secondaryColor, Item.Properties properties, String name) {
        return new SpawnEggItem(entityType, primaryColor, secondaryColor, properties).setRegistryName("spawn_egg_" + name);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                ModSoundEvents.createSoundEvent("impactsplat"),
                ModSoundEvents.createSoundEvent("wahwah")
        );
    }

    public abstract static class EntityTypes
    {
        public final static EntityType<BloblingEntity> BLOBLING = createEntityType(EntityType.Builder.create(BloblingEntity::new, EntityClassification.MONSTER).size(0.4F, 0.35F), "blobling");
        public final static EntityType<BroodmotherEntity> BROOD_MOTHER = createEntityType(EntityType.Builder.create(BroodmotherEntity::new, EntityClassification.MONSTER).size(1.6F, 0.7F), "brood_mother");

        private static <T extends Entity> EntityType<T> createEntityType(EntityType.Builder<T> builder, String entityName) {
            EntityType<T> entityType = builder.build(entityName);
            entityType.setRegistryName(BlightlingsMod.MOD_ID, entityName);
            return entityType;
        }
    }
}
