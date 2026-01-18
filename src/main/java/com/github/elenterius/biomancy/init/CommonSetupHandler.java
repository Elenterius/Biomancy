package com.github.elenterius.biomancy.init;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.entity.projectile.GrenadeProjectile;
import com.github.elenterius.biomancy.integration.ModsCompatHandler;
import com.github.elenterius.biomancy.item.extractor.ExtractorItem;
import com.github.elenterius.biomancy.item.injector.InjectorItem;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
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
		});

		ModFluids.registerInteractions();
		ModRecipes.registerBrewingRecipes();
		ModsCompatHandler.onBiomancyCommonSetup(event);
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

		DispenseItemBehavior grenadeDispenseBehavior = new DispenseItemBehavior() {
			private final AbstractProjectileDispenseBehavior behavior = new AbstractProjectileDispenseBehavior() {

				protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
					GrenadeProjectile grenade = new GrenadeProjectile(level, position.x(), position.y(), position.z());
					grenade.setItem(stack);
					return grenade;
				}

				protected float getUncertainty() {
					return super.getUncertainty() * 0.5F;
				}

				protected float getPower() {
					return super.getPower() * 1.25F;
				}

			};

			public ItemStack dispense(BlockSource source, ItemStack itemStack) {
				return behavior.dispense(source, itemStack);
			}
		};

		DispenserBlock.registerBehavior(ModItems.TOXIN_GRENADE.get(), grenadeDispenseBehavior);
		DispenserBlock.registerBehavior(ModItems.ACID_GRENADE.get(), grenadeDispenseBehavior);
		DispenserBlock.registerBehavior(ModItems.DECAY_GRENADE.get(), grenadeDispenseBehavior);
		DispenserBlock.registerBehavior(ModItems.VOLATILE_GRENADE.get(), grenadeDispenseBehavior);
	}

}
