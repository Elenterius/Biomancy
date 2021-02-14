package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class SyringeItem extends Item {

	public static final String NBT_KEY = "Entity";

	public SyringeItem(Properties properties) {
		super(properties);
	}

	public static void cleanEntityNBTData(CompoundNBT entityData) {
		entityData.remove("UUID");
		entityData.remove("Passengers");
		entityData.remove("ActiveEffects");
		entityData.remove("FallFlying");
		entityData.remove("Team");
		entityData.remove("SleepingX");
		entityData.remove("SleepingY");
		entityData.remove("SleepingZ");
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		CompoundNBT parentNBT = stack.getOrCreateTag();
		if (parentNBT.contains(NBT_KEY)) {
			CompoundNBT childNBT = parentNBT.getCompound(NBT_KEY);
			tooltip.add(new TranslationTextComponent(BiomancyMod.getTranslationKey("tooltip", "contains_dna"), new TranslationTextComponent(childNBT.getString("Name"))).mergeStyle(TextFormatting.GRAY));
		}
		else tooltip.add(BiomancyMod.getTranslationText("tooltip", "contains_nothing"));
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stackIn) {
		ItemStack stack = new ItemStack(this);
		CompoundNBT nbt = stackIn.getOrCreateTag().copy();
		stack.setTag(nbt);
		return stack;
	}

	@Override
	public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity targetEntity, Hand hand) {
		if (playerIn.world.isRemote()) return ActionResultType.PASS;

		boolean isValidEntity = targetEntity.isAlive() && (targetEntity.isNonBoss() || playerIn.isCreative());
		if (isValidEntity) {
			CompoundNBT nbt = stack.getOrCreateTag();
			if (!nbt.contains(NBT_KEY)) {
				CompoundNBT parentNbt = new CompoundNBT();

				CompoundNBT entityData = new CompoundNBT();
				targetEntity.writeUnlessPassenger(entityData);
				cleanEntityNBTData(entityData);
				parentNbt.put("Data", entityData);

				parentNbt.putString("Name", targetEntity.getType().getTranslationKey());
				parentNbt.putBoolean("IsPlayer", targetEntity instanceof PlayerEntity);
				parentNbt.putUniqueId("EntityUUID", targetEntity.getUniqueID());

				nbt.put(NBT_KEY, parentNbt);

				playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_HURT, SoundCategory.PLAYERS, 0.75F, 1f + playerIn.world.rand.nextFloat() * 0.25f);

				if (playerIn.isCreative()) {
					playerIn.setHeldItem(hand, stack); //fix for creative mode (normally the stack is not modified in creative)
				}
				return ActionResultType.SUCCESS;
			}
			else {
				playerIn.sendStatusMessage(BiomancyMod.getTranslationText("msg", "already_full").mergeStyle(TextFormatting.RED), true);
			}
		}

		return ActionResultType.PASS;
	}
}
