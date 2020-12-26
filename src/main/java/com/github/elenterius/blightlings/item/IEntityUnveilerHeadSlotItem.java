package com.github.elenterius.blightlings.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public interface IEntityUnveilerHeadSlotItem extends IKeyListener {

	@OnlyIn(Dist.CLIENT)
	static boolean canUnveilEntity(@Nullable PlayerEntity player, @Nullable Entity invisibleEntity) {
		if (player != null) {
			ItemStack stack = player.inventory.armorInventory.get(EquipmentSlotType.HEAD.getSlotIndex() - 1);
			if (!stack.isEmpty() && stack.getItem() instanceof IEntityUnveilerHeadSlotItem) {
				CompoundNBT nbt = stack.getOrCreateTag();
				if (!stack.hasTag() || !nbt.contains("BlightlingsItemAbilityEnabled")) {
					nbt.putBoolean("BlightlingsItemAbilityEnabled", true);
				}
				else if (!nbt.getBoolean("BlightlingsItemAbilityEnabled")) {
					return false;
				}
				return ((IEntityUnveilerHeadSlotItem) stack.getItem()).canUnveilEntity(stack, player, invisibleEntity);
			}
		}
		return false;
	}

	default boolean isItemAbilityActive(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean("BlightlingsItemAbilityEnabled"); //TODO: replace with capability?
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	default ActionResult<Byte> onClientKeyPress(ItemStack stack, World world, PlayerEntity player, byte flags) {
		boolean isActive = ((IEntityUnveilerHeadSlotItem) stack.getItem()).isItemAbilityActive(stack);
		byte flag = isActive ? (byte) 0 : (byte) 1;
		stack.getOrCreateTag().putBoolean("BlightlingsItemAbilityEnabled", flag == 1);

		SoundEvent soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
		Item item = stack.getItem();
		if (item instanceof ArmorItem) {
			soundEvent = ((ArmorItem) item).getArmorMaterial().getSoundEvent();
		}
		player.playSound(soundEvent, 1.0F, 1.0F);

		return ActionResult.resultSuccess(flag);
	}

	@Override
	default void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		stack.getOrCreateTag().putBoolean("BlightlingsItemAbilityEnabled", flags == 1);
	}

	/**
	 * only called on client side
	 */
	@OnlyIn(Dist.CLIENT)
	boolean canUnveilEntity(ItemStack stack, PlayerEntity player, @Nullable Entity invisibleEntity);
}
