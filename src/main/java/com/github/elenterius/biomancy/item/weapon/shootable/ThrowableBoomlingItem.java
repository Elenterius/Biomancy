package com.github.elenterius.biomancy.item.weapon.shootable;

import com.github.elenterius.biomancy.entity.golem.BoomlingEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.item.IHighlightRayTraceResultItem;
import com.github.elenterius.biomancy.util.RayTraceUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class ThrowableBoomlingItem extends Item implements IHighlightRayTraceResultItem {
	public final float maxDistance;

	public ThrowableBoomlingItem(Properties properties, float maxDistance) {
		super(properties);
		this.maxDistance = maxDistance;
	}

	@Override
	public double getMaxRayTraceDistance() {
		return maxDistance;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag() && stack.getTag() != null) {
			String potionTranslationKey = stack.getTag().getString("PotionName");
			if (!potionTranslationKey.isEmpty())
				tooltip.add(new TranslationTextComponent(potionTranslationKey).setStyle(Style.EMPTY.setFormatting(TextFormatting.GRAY)));
		}
		PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canHighlightBlocks(ItemStack stack) {
		return containsPotion(stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public int getColorForBlock(ItemStack stack, BlockPos blockPos) {
		return 0xCE0018;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		if (!hasContainerItem(stack)) {
			return ItemStack.EMPTY;
		}
		else {
			ItemStack stack1 = stack.copy();
			stack1.removeChildTag("Potion");
			stack1.removeChildTag("PotionItem");
			return stack1;
		}
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return containsPotion(stack);
	}

	public boolean containsPotion(ItemStack stack) {
		return PotionUtils.getPotionFromItem(stack) != Potions.EMPTY;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!containsPotion(stack)) return ActionResult.resultFail(stack);

//        BlockRayTraceResult rayTrace = (BlockRayTraceResult) playerIn.pick(20d, 1f, false);
		RayTraceResult rayTraceResult = RayTraceUtil.rayTrace(playerIn, target -> !target.isSpectator() && target.isAlive() && target.canBeCollidedWith() && target instanceof LivingEntity && !playerIn.isRidingSameEntity(target), getMaxRayTraceDistance());
		if (rayTraceResult.getType() == RayTraceResult.Type.MISS) return ActionResult.resultPass(stack);

		BlockPos targetBlockPos = null;
		LivingEntity targetEntity = null;
		if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK && rayTraceResult instanceof BlockRayTraceResult) {
			BlockRayTraceResult rayTrace = (BlockRayTraceResult) rayTraceResult;
			targetBlockPos = rayTrace.getPos().offset(rayTrace.getFace());
			if (!worldIn.getFluidState(targetBlockPos).isEmpty()) return ActionResult.resultFail(stack);
		}
		else if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && rayTraceResult instanceof EntityRayTraceResult) {
			EntityRayTraceResult rayTrace = (EntityRayTraceResult) rayTraceResult;
			if (rayTrace.getEntity() instanceof LivingEntity) {
				targetEntity = (LivingEntity) rayTrace.getEntity();
			}
			else return ActionResult.resultFail(stack);
		}

		if (!worldIn.isRemote()) {
			BoomlingEntity entity = ModEntityTypes.BOOMLING.get().create(worldIn);
			if (entity != null) {
				entity.enablePersistence();
				if (stack.hasDisplayName()) {
					entity.setCustomName(stack.getDisplayName());
					entity.setCustomNameVisible(true);
				}
				entity.setOwner(playerIn);
				entity.setStoredPotion(getPotionItemStack(stack));
				entity.setTargetBlockPos(targetBlockPos);
				entity.setAttackTarget(targetEntity);

				Vector3d posVec = playerIn.getEyePosition(1f).add(0d, -0.1d, 0d).add(playerIn.getLookVec().rotateYaw(-15f).normalize().scale(0.15d));
				entity.setPosition(posVec.x, posVec.y, posVec.z);
				entity.lookAt(EntityAnchorArgument.Type.EYES, targetBlockPos != null ? Vector3d.copyCentered(targetBlockPos) : targetEntity != null ? targetEntity.getPositionVec() : rayTraceResult.getHitVec());
				Vector3d direction = entity.getLookVec().normalize().scale(0.55f);
				entity.setMotion(direction);
				entity.isAirBorne = true;
				Vector3d playerMotion = playerIn.getMotion();
				entity.setMotion(entity.getMotion().add(playerMotion.x, playerIn.isOnGround() ? 0d : playerMotion.y, playerMotion.z));

				if (worldIn.addEntity(entity)) {
					entity.playAmbientSound();
					stack.shrink(1);
					return ActionResult.resultConsume(stack);
				}
			}
			return ActionResult.resultFail(stack);
		}
		return ActionResult.resultSuccess(stack);
	}

	public ItemStack getPotionItemStack(ItemStack stackIn) {
		Potion potion = PotionUtils.getPotionFromItem(stackIn);
		if (potion != Potions.EMPTY) {
			List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(stackIn);
			Item potionItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(stackIn.getOrCreateTag().getString("PotionItem")));
			ItemStack stack = new ItemStack(potionItem instanceof PotionItem ? potionItem : Items.POTION);
			PotionUtils.addPotionToItemStack(stack, potion);
			PotionUtils.appendEffects(stack, effects);
			return stack;
		}
		return ItemStack.EMPTY;
	}

	public ItemStack setPotionItemStack(ItemStack beetleStack, ItemStack potionStack) {
		if (!potionStack.isEmpty() && potionStack.getItem() instanceof ThrowablePotionItem) {
			Potion potion = PotionUtils.getPotionFromItem(potionStack);
			List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(potionStack);
			PotionUtils.addPotionToItemStack(beetleStack, potion);
			PotionUtils.appendEffects(beetleStack, effects);
			ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(potionStack.getItem());
			if (registryKey != null) beetleStack.getOrCreateTag().putString("PotionItem", registryKey.toString());
			beetleStack.getOrCreateTag().putString("PotionName", potionStack.getTranslationKey());
		}
		return beetleStack;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return PotionUtils.getPotionFromItem(stack) != Potions.EMPTY;
	}

}
