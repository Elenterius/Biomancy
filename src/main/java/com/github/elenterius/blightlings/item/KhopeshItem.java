package com.github.elenterius.blightlings.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static net.minecraft.item.ItemStack.DECIMALFORMAT;

public class KhopeshItem extends AxeItem
{
    public static UUID RIDING_ATTACK_DAMAGE_UUID = UUID.fromString("CBD1DE77-3F1D-4E8B-839A-AA471A93D424");
    public static AttributeModifier RIDING_ATTACK_DAMAGE_MODIFIER = new AttributeModifier(RIDING_ATTACK_DAMAGE_UUID, "riding_attack_modifier", 4f, AttributeModifier.Operation.ADDITION);

    public KhopeshItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(getTranslationKey(stack).replace("item", "tooltip")).setStyle(Style.EMPTY.applyFormatting(TextFormatting.GRAY)));
        tooltip.add(new StringTextComponent(" "));
        tooltip.add(new TranslationTextComponent("tooltip.blightlings.riding_bonus").setStyle(Style.EMPTY.applyFormatting(TextFormatting.GRAY)));
        tooltip.add((new StringTextComponent(" ")).append(new TranslationTextComponent("attribute.modifier.plus." + RIDING_ATTACK_DAMAGE_MODIFIER.getOperation().getId(), DECIMALFORMAT.format(RIDING_ATTACK_DAMAGE_MODIFIER.getAmount()), new TranslationTextComponent("attribute.name.generic.attack_damage"))).mergeStyle(TextFormatting.BLUE));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.isIn(Blocks.COBWEB) ? 15f : super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return blockIn.isIn(Blocks.COBWEB) || super.canHarvestBlock(blockIn);
    }
}
