package com.github.elenterius.biomancy.datagen.recipes;

import com.github.elenterius.biomancy.api.nutrients.NutrientsContainerItem;
import com.github.elenterius.biomancy.datagen.recipes.builder.BioForgeRecipeBuilder;
import com.github.elenterius.biomancy.datagen.recipes.builder.ItemData;
import com.github.elenterius.biomancy.init.ModBioForgeTabs;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.item.EssenceItem;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.PartialNBTIngredient;

import java.util.function.Consumer;

public class BioForgeRecipeProvider extends RecipeProvider {

	static final int blockCost = 4;
	static final int slabCost = blockCost / 2;
	static final int stairsCost = blockCost - 1;
	static final int wallCost = blockCost;

	protected BioForgeRecipeProvider(PackOutput output) {
		super(output);
	}

	private static <T extends Item & NutrientsContainerItem> ItemStack withMaxNutrients(T item) {
		ItemStack itemStack = item.getDefaultInstance();
		item.setNutrients(itemStack, Integer.MAX_VALUE);
		return itemStack;
	}

	private static Ingredient createMobEssenceIngredient(EntityType<?> entityType) {
		CompoundTag essenceTag = new CompoundTag();
		essenceTag.putString(EssenceItem.ENTITY_TYPE_KEY, EntityType.getKey(entityType).toString());
		essenceTag.putString(EssenceItem.ENTITY_NAME_KEY, entityType.getDescriptionId());
		CompoundTag tag = new CompoundTag();
		tag.put(EssenceItem.ESSENCE_DATA_KEY, essenceTag);
		tag.putInt(EssenceItem.ESSENCE_TIER_KEY, 1);
		return PartialNBTIngredient.of(ModItems.ESSENCE.get(), tag);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		buildMachineRecipes(consumer);
		buildBlockRecipes(consumer);
		buildToolRecipes(consumer);
		buildComponentRecipes(consumer);
		buildMiscRecipes(consumer);

		/* --------- Testing --------- */

		//		BioForgeRecipeBuilder.create(Items.PAPER)
		//				.addIngredient(ItemTags.PLANKS, 11)
		//				.addIngredient(ModItems.ORGANIC_MATTER.get(), 8)
		//				.addIngredient(ItemTags.SMALL_FLOWERS, 3)
		//				.setCategory(ModBioForgeTabs.REPLICAS)
		//				.unlockedBy(Items.PAPER).save(consumer);
	}

	private void buildMachineRecipes(Consumer<FinishedRecipe> consumer) {
		BioForgeRecipeBuilder.create(ModItems.DECOMPOSER.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 8)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 5)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 5)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 8)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.DIGESTER.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 10)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 8)
				.addIngredient(ModItems.BILE.get(), 4)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BIO_LAB.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 4)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 2)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BIO_FORGE.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 6)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 2)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		//		BioForgeRecipeBuilder.create(ModItems.PRIMORDIAL_CRADLE.get())
		//				.addIngredient(ModItems.PRIMORDIAL_CORE.get())
		//				.addIngredient(ModItems.FLESH_BITS.get(), 5)
		//				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
		//				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
		//				.setCategory(ModBioForgeTabs.MACHINES)
		//				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.TONGUE.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 10)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.MAW_HOPPER.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 10)
				.setCategory(ModBioForgeTabs.MACHINES)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);
	}

	private void buildBlockRecipes(Consumer<FinishedRecipe> consumer) {
		BioForgeRecipeBuilder.create(ModItems.FLESH_BLOCK.get()).addIngredient(ModItems.FLESH_BITS.get(), blockCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FLESH_STAIRS.get()).addIngredient(ModItems.FLESH_BITS.get(), stairsCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FLESH_SLAB.get()).addIngredient(ModItems.FLESH_BITS.get(), slabCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FLESH_WALL.get()).addIngredient(ModItems.FLESH_BITS.get(), wallCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_BLOCK.get()).addIngredient(ModItems.FLESH_BITS.get(), blockCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), blockCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_STAIRS.get()).addIngredient(ModItems.FLESH_BITS.get(), stairsCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), stairsCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_SLAB.get()).addIngredient(ModItems.FLESH_BITS.get(), slabCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), slabCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.PACKED_FLESH_WALL.get()).addIngredient(ModItems.FLESH_BITS.get(), wallCost * 2).addIngredient(ModItems.TOUGH_FIBERS.get(), wallCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FIBROUS_FLESH_BLOCK.get()).addIngredient(ModItems.FLESH_BITS.get(), blockCost / 2).addIngredient(ModItems.ELASTIC_FIBERS.get(), blockCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FIBROUS_FLESH_STAIRS.get()).addIngredient(ModItems.FLESH_BITS.get(), stairsCost / 2).addIngredient(ModItems.ELASTIC_FIBERS.get(), stairsCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FIBROUS_FLESH_SLAB.get()).addIngredient(ModItems.FLESH_BITS.get(), slabCost / 2).addIngredient(ModItems.ELASTIC_FIBERS.get(), slabCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);
		BioForgeRecipeBuilder.create(ModItems.FIBROUS_FLESH_WALL.get()).addIngredient(ModItems.FLESH_BITS.get(), wallCost / 2).addIngredient(ModItems.ELASTIC_FIBERS.get(), wallCost).setCategory(ModBioForgeTabs.BUILDING_BLOCKS).unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.CHISELED_FLESH_BLOCK.get())
				.addIngredient(ModItems.FLESH_BITS.get(), blockCost)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.BUILDING_BLOCKS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.ORNATE_FLESH_BLOCK.get())
				.addIngredient(ModItems.FLESH_BITS.get(), blockCost)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
				.setCategory(ModBioForgeTabs.BUILDING_BLOCKS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.ORNATE_FLESH_SLAB.get())
				.addIngredient(ModItems.FLESH_BITS.get(), slabCost)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.BUILDING_BLOCKS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.TUBULAR_FLESH_BLOCK.get())
				.addIngredient(ModItems.FLESH_BITS.get(), blockCost / 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), blockCost)
				.setCategory(ModBioForgeTabs.BUILDING_BLOCKS)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_PILLAR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), blockCost / 2)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), blockCost / 2)
				.setCategory(ModBioForgeTabs.BUILDING_BLOCKS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);
	}

	private void buildToolRecipes(Consumer<FinishedRecipe> consumer) {
		BioForgeRecipeBuilder.create(withMaxNutrients(ModItems.RAVENOUS_CLAWS.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.MOB_CLAW.get(), 3)
				.addIngredient(ModItems.FLESH_BITS.get(), 16)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 6 * 3)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 5 * 3)
				.setCraftingCost(250)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(withMaxNutrients(ModItems.THORN_SHIELD.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.MOB_FANG.get(), 8)
				.addIngredient(ModItems.FLESH_BITS.get(), 10)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 32)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 24)
				.setCraftingCost(250)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(withMaxNutrients(ModItems.ACOLYTE_ARMOR_HELMET.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 8)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 30)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 22)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 6)
				.setCraftingCost(250)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(withMaxNutrients(ModItems.ACOLYTE_ARMOR_CHESTPLATE.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 10)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 32)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 24)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.setCraftingCost(250)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(withMaxNutrients(ModItems.ACOLYTE_ARMOR_LEGGINGS.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 10)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 32)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 24)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.setCraftingCost(250)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(withMaxNutrients(ModItems.ACOLYTE_ARMOR_BOOTS.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 16)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 12)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
				.setCraftingCost(250)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(withMaxNutrients(ModItems.CAUSTIC_GUNBLADE.get()))
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 16)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.addIngredient(ModItems.GENERIC_MOB_GLAND.get(), 2)
				.addIngredient(ModItems.PRIMAL_ORIFICE.get(), 4)
				.setCraftingCost(250)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.INJECTOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 20)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 10)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 3)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 5)
				.setCraftingCost(5)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.VIAL.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 1)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.ESSENCE_EXTRACTOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 20)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 10)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 25)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 6)
				.setCraftingCost(20)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.EXOTIC_DUST.get()).save(consumer);
	}

	private void buildComponentRecipes(Consumer<FinishedRecipe> consumer) {
		BioForgeRecipeBuilder.create(ModItems.CREATOR_MIX.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.EXOTIC_DUST.get(), 2)
				.addIngredient(ModItems.NUTRIENTS.get(), 15)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(ModItems.EXOTIC_DUST.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FERTILIZER.get())
				.addIngredient(ModItems.NUTRIENTS.get(), 30)
				.addIngredient(ModItems.ORGANIC_MATTER.get(), 4)
				.addIngredient(ModItems.HORMONE_SECRETION.get(), 6)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 4)
				.addIngredient(ModItems.REGENERATIVE_FLUID.get(), 4)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(ModItems.HORMONE_SECRETION.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.MOB_FANG.get())
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 6)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 8)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(ModItems.MOB_FANG.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.MOB_CLAW.get())
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 7)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 8)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(ModItems.MOB_CLAW.get()).save(consumer);

		BioForgeRecipeBuilder.create(Items.BONE)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 5)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 1)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(Items.LEATHER)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 5)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 1)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.LEATHER).save(consumer);

		BioForgeRecipeBuilder.create(Items.STRING)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 1)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.STRING).save(consumer);

		BioForgeRecipeBuilder.create(Items.COBWEB)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2 * 4)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 4)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.STRING).save(consumer);

		BioForgeRecipeBuilder.create(Items.SCUTE)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 6)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 4)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.SCUTE).save(consumer);

		BioForgeRecipeBuilder.create(Items.NAUTILUS_SHELL)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 11)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 8)
				.addIngredient(ModItems.STONE_POWDER.get(), 1)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.NAUTILUS_SHELL).save(consumer);

		BioForgeRecipeBuilder.create(Items.SKELETON_SKULL)
				.setCraftingCost(4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 48 + 2)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 7 + 2)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.SKELETON_SKULL).save(consumer);

		BioForgeRecipeBuilder.create(Items.WITHER_SKELETON_SKULL)
				.setCraftingCost(4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 48 + 2)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 7 + 2)
				.addIngredient(ModItems.WITHERING_OOZE.get(), 16 + 2)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.WITHER_SKELETON_SKULL).save(consumer);

		BioForgeRecipeBuilder.create(Items.PLAYER_HEAD)
				.setCraftingCost(4)
				.addIngredient(Items.SKELETON_SKULL)
				.addIngredient(createMobEssenceIngredient(EntityType.PLAYER))
				.addIngredient(ModItems.FLESH_BITS.get(), 32 + 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 9 + 2)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.SKELETON_SKULL).save(consumer);

		BioForgeRecipeBuilder.create(Items.PIGLIN_HEAD)
				.setCraftingCost(4)
				.addIngredient(Items.SKELETON_SKULL)
				.addIngredient(createMobEssenceIngredient(EntityType.PIGLIN))
				.addIngredient(ModItems.FLESH_BITS.get(), 36 + 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 12 + 2)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.SKELETON_SKULL).save(consumer);

		//		DecomposerRecipeBuilder.create().setIngredient(Items.ZOMBIE_HEAD).addOutput(ModItems.FLESH_BITS.get(), 14, 24).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.ZOMBIE_HEAD).save(consumer);
		//		DecomposerRecipeBuilder.create().setIngredient(Items.CREEPER_HEAD).addOutput(ModItems.FLESH_BITS.get(), 19, 32).addOutput(ModItems.ELASTIC_FIBERS.get(), 5, 9).addOutput(Items.SKELETON_SKULL, 1).unlockedBy(Items.CREEPER_HEAD).save(consumer);
		//		DecomposerRecipeBuilder.create().setIngredient(Items.DRAGON_HEAD).addOutput(ModItems.FLESH_BITS.get(), 50).addOutput(ModItems.EXOTIC_DUST.get(), 50).addOutput(ModItems.TOUGH_FIBERS.get(), 25).addOutput(ModItems.MINERAL_FRAGMENT.get(), 20).addOutput(ModItems.BONE_FRAGMENTS.get(), 50).unlockedBy(Items.DRAGON_HEAD).save(consumer);

	}

	private void buildMiscRecipes(Consumer<FinishedRecipe> consumer) {
		BioForgeRecipeBuilder.create(ModItems.FLESHKIN_PRESSURE_PLATE.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 3)
				.addIngredient(ModItems.FLESH_BITS.get(), 5)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 10)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.MODULAR_LARYNX.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), 6)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 12)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 16)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.STORAGE_SAC.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 4)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 6)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 12)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 4)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESHKIN_CHEST.get())
				.addIngredient(ModItems.LIVING_FLESH.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 10)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 12)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 8)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 32)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.CHRYSALIS.get())
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 6)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 12)
				.addIngredient(Items.EGG, 4)
				.addIngredient(ModItems.LIVING_FLESH.get())
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.IMPERMEABLE_MEMBRANE.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2)
				.addIngredient(ModItems.BILE.get(), 4)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BABY_PERMEABLE_MEMBRANE.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.REGENERATIVE_FLUID.get(), 4)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.ADULT_PERMEABLE_MEMBRANE.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.HORMONE_SECRETION.get(), 4)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.UNDEAD_PERMEABLE_MEMBRANE.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.BILE.get(), 4)
				.addIngredient(Items.PHANTOM_MEMBRANE, 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(Items.PHANTOM_MEMBRANE).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.IMPERMEABLE_MEMBRANE_PANE.get(), 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2)
				.addIngredient(ModItems.BILE.get(), 4)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BABY_PERMEABLE_MEMBRANE_PANE.get(), 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.REGENERATIVE_FLUID.get(), 4)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.ADULT_PERMEABLE_MEMBRANE_PANE.get(), 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.HORMONE_SECRETION.get(), 4)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.UNDEAD_PERMEABLE_MEMBRANE_PANE.get(), 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.BILE.get(), 4)
				.addIngredient(Items.PHANTOM_MEMBRANE, 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(Items.PHANTOM_MEMBRANE).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BIOMETRIC_MEMBRANE.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2)
				.addIngredient(ModItems.BILE.get(), 4)
				.addIngredient(ModItems.GEM_FRAGMENTS.get(), 2)
				.addIngredient(ModItems.LIVING_FLESH.get())
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.LIVING_FLESH.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_FENCE.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), blockCost * 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 2)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_SPIKE.get(), 1)
				.addIngredient(ModItems.MOB_FANG.get())
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 2)
				.addIngredient(ModItems.FLESH_BITS.get(), 1)
				.setCategory(ModBioForgeTabs.TOOLS)
				.unlockedBy(ModItems.MOB_FANG.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_FENCE_GATE.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), slabCost * 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), blockCost * 4)
				.addIngredient(ModItems.MINERAL_FRAGMENT.get(), 4)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_DOOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 6)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 6)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FULL_FLESH_DOOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 6)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 6)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 2)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ItemData.from("dramaticdoors", "tall_flesh_door"))
				.ifModLoaded("dramaticdoors")
				.addIngredient(ModItems.FLESH_BITS.get(), 9)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 9)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 6)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 3)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ItemData.from("dramaticdoors", "tall_full_flesh_door"))
				.ifModLoaded("dramaticdoors")
				.addIngredient(ModItems.FLESH_BITS.get(), 9)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 9)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 6)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 3)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_IRIS_DOOR.get())
				.addIngredient(ModItems.FLESH_BITS.get(), 4)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 4)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 2)
				.addIngredient(ModItems.TOUGH_FIBERS.get(), 1)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.FLESH_BITS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.FLESH_LADDER.get(), 4)
				.addIngredient(ModItems.FLESH_BITS.get(), 2)
				.addIngredient(ModItems.BONE_FRAGMENTS.get(), 10)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.BONE_FRAGMENTS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.TENDON_CHAIN.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 8)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.YELLOW_BIO_LANTERN.get())
				.addIngredient(ModItems.BIO_LUMENS.get(), 10)
				.addIngredient(Items.YELLOW_DYE, 1)
				.addIngredient(ModItems.FLESH_BITS.get(), 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.BIO_LUMENS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.BLUE_BIO_LANTERN.get())
				.addIngredient(ModItems.BIO_LUMENS.get(), 10)
				.addIngredient(ModItems.FLESH_BITS.get(), 2)
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 4)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.BIO_LUMENS.get()).save(consumer);

		BioForgeRecipeBuilder.create(Items.SHROOMLIGHT)
				.addIngredient(ModItems.BIO_LUMENS.get(), 10)
				.addIngredient(Items.YELLOW_DYE, 2)
				.addIngredient(ModItems.ORGANIC_MATTER.get(), 4)
				.setCategory(ModBioForgeTabs.COMPONENTS)
				.unlockedBy(Items.SHROOMLIGHT).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.VIAL_HOLDER.get())
				.addIngredient(ModItems.ELASTIC_FIBERS.get(), 8)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ELASTIC_FIBERS.get()).save(consumer);

		BioForgeRecipeBuilder.create(ModItems.MASCOT_BANNER_PATTERNS.get())
				.addIngredient(ModItems.ORGANIC_MATTER.get(), 8)
				.addIngredient(Items.SPIDER_EYE, 1)
				.addIngredient(ModItems.BILE.get(), 4)
				.setCategory(ModBioForgeTabs.MISC)
				.unlockedBy(ModItems.ORGANIC_MATTER.get()).save(consumer);
	}

}
