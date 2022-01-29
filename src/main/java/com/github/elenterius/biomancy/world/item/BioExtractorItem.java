package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.init.ModEnchantments;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModMobEffects;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BioExtractorItem extends Item implements IKeyListener {

	public BioExtractorItem(Properties properties) {
		super(properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, ClientLevel level, Player player, byte flags) {
		//TODO: add cooldown?
		if (!interactWithPlayerSelf(stack, player)) {
			playSFX(level, player, SoundEvents.DISPENSER_FAIL);
			return InteractionResultHolder.fail(flags); //don't send button press to server
		}
		return InteractionResultHolder.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		playSFX(level, player, interactWithPlayerSelf(stack, player) ? ModSoundEvents.INJECT.get() : SoundEvents.DISPENSER_FAIL);
	}

	private static boolean extractEssence(ItemStack stack, @Nullable Player player, LivingEntity targetEntity) {
		if (targetEntity.isAlive() && !targetEntity.hasEffect(ModMobEffects.ESSENCE_ANEMIA.get())) {
			EssenceItem essenceItem = ModItems.ESSENCE.get();
			int lootingLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, stack);
			ItemStack essenceStack = new ItemStack(essenceItem, 1 + targetEntity.getRandom().nextInt(0, 1 + lootingLevel));

			if (essenceItem.setEntityType(essenceStack, targetEntity)) {
				if (player != null) {
					if (!player.addItem(essenceStack)) {
						player.drop(essenceStack, false);
					}
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
				}
				else {
					Containers.dropItemStack(targetEntity.level, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), essenceStack);
				}

				if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ANESTHETIC.get(), stack) <= 0) {
					targetEntity.hurt(new EntityDamageSource("sting", player), 0.5f);
				}

				targetEntity.addEffect(new MobEffectInstance(ModMobEffects.ESSENCE_ANEMIA.get(), 2400));
				return true;
			}
		}
		return false;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
		//the device is empty
		if (!interactionTarget.level.isClientSide && extractEssence(stack, player, interactionTarget)) {
			playSFX(interactionTarget.level, player, ModSoundEvents.INJECT.get());

			//fix for creative mode (normally the stack is not modified in creative)
			if (player.isCreative()) player.setItemInHand(usedHand, stack);

			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public boolean interactWithPlayerSelf(ItemStack stack, Player player) {
		if (player.hasEffect(ModMobEffects.ESSENCE_ANEMIA.get())) return false;
		if (player.level.isClientSide) return true;

		if (extractEssence(stack, player, player)) {
			//fix for creative mode (normally the stack is not modified in creative)
			if (player.isCreative()) player.setItemInHand(player.getUsedItemHand(), stack);
			return true;
		}
		return false;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.MOB_LOOTING || super.canApplyAtEnchantingTable(stack, enchantment);
	}

	public void playSFX(Level world, LivingEntity shooter, SoundEvent soundEvent) {
		SoundSource soundSource = shooter instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
		Random random = world.random;
		world.playSound(world.isClientSide && shooter instanceof Player player ? player : null, shooter.getX(), shooter.getY(), shooter.getZ(), soundEvent, soundSource, 0.8f, 1f / (random.nextFloat() * 0.5f + 1f) + 0.2f);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
	}

}
