package com.github.elenterius.biomancy.reagent;

import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class BloodSampleReagent extends Reagent {

	public BloodSampleReagent(int colorIn) {
		super(colorIn);
	}

	private static boolean isNonBoss(LivingEntity target) {
		return target.canChangeDimensions(); //TODO: use boss entity tag
	}

	@Nullable
	public static CompoundNBT getBloodSampleFromEntity(PlayerEntity player, LivingEntity target) {
		boolean isValidEntity = target.isAlive() && (isNonBoss(target) || player.isCreative());
		return isValidEntity ? getBloodSampleFromEntityUnchecked(target) : null;
	}

	@Nullable
	public static CompoundNBT getBloodSampleFromEntityUnchecked(LivingEntity target) {
		CompoundNBT nbt = new CompoundNBT();
		String typeId = target instanceof PlayerEntity ? getPlayerTypeId((PlayerEntity) target) : target.getEncodeId();
		if (typeId != null) {
			nbt.putString("EntityTypeId", typeId);
			nbt.putString("Name", target.getType().getDescriptionId());
			nbt.putBoolean("IsPlayer", target instanceof PlayerEntity);
			nbt.putUUID("EntityUUID", target.getUUID());
			return nbt;
		}
		return null;
	}

	private static String getPlayerTypeId(PlayerEntity playerEntity) {
		return EntityType.getKey(playerEntity.getType()).toString();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInfoToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains(NBT_KEY_DATA)) {
			CompoundNBT reagentNbt = nbt.getCompound(NBT_KEY_DATA);
			if (reagentNbt.getBoolean("IsPlayer")) {
				String playerName = " " + ClientTextUtil.tryToGetPlayerNameOnClientSide(reagentNbt.getUUID("EntityUUID"));
				tooltip.add(ClientTextUtil.getTooltipText("contains_dna", new TranslationTextComponent(reagentNbt.getString("Name")).append(playerName)).withStyle(TextFormatting.GRAY));
			}
			else
				tooltip.add(ClientTextUtil.getTooltipText("contains_dna", new TranslationTextComponent(reagentNbt.getString("Name"))).withStyle(TextFormatting.GRAY));
		}
		if (ClientTextUtil.showExtraInfo(tooltip)) {
			tooltip.add(new TranslationTextComponent(getTranslationKey().replace("reagent", "tooltip")).withStyle(ClientTextUtil.LORE_STYLE));
		}
	}

	@Override
	public boolean affectBlock(CompoundNBT nbt, @Nullable LivingEntity source, World world, BlockPos pos, Direction facing) {
		return false;
	}

	@Override
	public boolean affectEntity(CompoundNBT nbt, @Nullable LivingEntity source, LivingEntity target) {
		if (target instanceof FleshBlobEntity) {
			if (!nbt.isEmpty()) {
				EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(nbt.getString("EntityTypeId")));
				if (entityType != null) {
					if (!target.level.isClientSide) {
						((FleshBlobEntity) target).addForeignEntityDNA(entityType);
					}
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean affectPlayerSelf(CompoundNBT nbt, PlayerEntity targetSelf) {
		return false;
	}
}
