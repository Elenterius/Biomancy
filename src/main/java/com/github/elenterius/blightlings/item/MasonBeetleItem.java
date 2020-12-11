package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.entity.MasonBeetleEntity;
import com.github.elenterius.blightlings.init.ModEntityTypes;
import com.github.elenterius.blightlings.util.BlockPlacementTarget;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MasonBeetleItem extends Item implements IHighlightRayTraceResultItem {
    public final float maxDistance;

    public MasonBeetleItem(Properties properties, float maxDistance) {
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
            String translationKey = stack.getTag().getString("BlockName");
            if (!translationKey.isEmpty())
                tooltip.add(new TranslationTextComponent(translationKey).setStyle(Style.EMPTY.setFormatting(TextFormatting.GRAY)));
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        if (!hasContainerItem(stack)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stack1 = stack.copy();
            stack1.removeChildTag("Block");
            stack1.removeChildTag("BlockName");
            return stack1;
        }
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return containsBlock(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!containsBlock(stack)) return ActionResult.resultFail(stack);

        BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) playerIn.pick(20d, 1f, false);
        if (rayTraceResult.getType() == RayTraceResult.Type.MISS) return ActionResult.resultPass(stack);
        if (rayTraceResult.getType() != RayTraceResult.Type.BLOCK) return ActionResult.resultPass(stack);

        BlockPlacementTarget placementTarget = new BlockPlacementTarget(rayTraceResult);
        if (!worldIn.getFluidState(placementTarget.targetPos).isEmpty()) return ActionResult.resultFail(stack);

        if (!worldIn.isRemote()) {
            MasonBeetleEntity entity = ModEntityTypes.MASON_BEETLE.get().create(worldIn);
            if (entity != null) {
                entity.enablePersistence();
                if (stack.hasDisplayName()) {
                    entity.setCustomName(stack.getDisplayName());
                    entity.setCustomNameVisible(true);
                }
                entity.setOwner(playerIn);
                entity.setPlacementBlock(getBlockItemStack(stack));
                entity.setBlockPlacementTarget(placementTarget);

                Vector3d posVec = playerIn.getEyePosition(1f).add(0d, -0.1d, 0d).add(playerIn.getLookVec().rotateYaw(-15f).normalize().scale(0.15d));
                entity.setPosition(posVec.x, posVec.y, posVec.z);
                entity.lookAt(EntityAnchorArgument.Type.EYES, Vector3d.copyCentered(placementTarget.targetPos));
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

    public ItemStack getBlockItemStack(ItemStack stack) {
        if (containsBlock(stack)) {
            CompoundNBT childTag = stack.getChildTag("Block");
            if (childTag != null) return ItemStack.read(childTag);
        }
        return ItemStack.EMPTY;
    }

    public boolean containsBlock(ItemStack stack) {
        return stack.hasTag() && stack.getTag() != null && stack.getTag().contains("Block");
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return containsBlock(stack);
    }
}
