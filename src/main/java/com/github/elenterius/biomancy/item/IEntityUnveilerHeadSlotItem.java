package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.client.world.ClientWorld;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public interface IEntityUnveilerHeadSlotItem extends IKeyListener {

	String NBT_KEY = StringUtils.capitalize(BiomancyMod.MOD_ID) + "ItemAbilityEnabled";

	@OnlyIn(Dist.CLIENT)
	static boolean canUnveilEntity(@Nullable PlayerEntity player, @Nullable Entity invisibleEntity) {
		if (player != null) {
			ItemStack stack = player.inventory.armor.get(EquipmentSlotType.HEAD.getFilterFlag() - 1);
			if (!stack.isEmpty() && stack.getItem() instanceof IEntityUnveilerHeadSlotItem) {
				CompoundNBT nbt = stack.getOrCreateTag();
				if (!stack.hasTag() || !nbt.contains(NBT_KEY)) {
					nbt.putBoolean(NBT_KEY, true);
				}
				else if (!nbt.getBoolean(NBT_KEY)) {
					return false;
				}
				return ((IEntityUnveilerHeadSlotItem) stack.getItem()).canUnveilEntity(stack, player, invisibleEntity);
			}
		}
		return false;
	}

	default boolean isItemAbilityActive(ItemStack stack) {
		return stack.getOrCreateTag().getBoolean(NBT_KEY); //TODO: replace with capability?
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	default ActionResult<Byte> onClientKeyPress(ItemStack stack, ClientWorld world, PlayerEntity player, byte flags) {
		boolean isActive = ((IEntityUnveilerHeadSlotItem) stack.getItem()).isItemAbilityActive(stack);
		byte flag = isActive ? (byte) 0 : (byte) 1;
		stack.getOrCreateTag().putBoolean(NBT_KEY, flag == 1);

		SoundEvent soundEvent = SoundEvents.ARMOR_EQUIP_GENERIC;
		Item item = stack.getItem();
		if (item instanceof ArmorItem) {
			soundEvent = ((ArmorItem) item).getMaterial().getEquipSound();
		}
		player.playSound(soundEvent, 1.0F, 1.0F);

		return ActionResult.success(flag);
	}

	@Override
	default void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		stack.getOrCreateTag().putBoolean(NBT_KEY, flags == 1);
	}

	/**
	 * only called on client side
	 */
	@OnlyIn(Dist.CLIENT)
	boolean canUnveilEntity(ItemStack stack, PlayerEntity player, @Nullable Entity invisibleEntity);
}
