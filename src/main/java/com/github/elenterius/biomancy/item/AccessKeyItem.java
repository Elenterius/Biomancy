package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.client.util.TooltipUtil;
import com.github.elenterius.biomancy.tileentity.IOwnableTile;
import com.github.elenterius.biomancy.util.TextUtil;
import com.github.elenterius.biomancy.util.UserAuthorization;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AccessKeyItem extends Item {

	public static final String NBT_KEY = StringUtils.capitalize(BiomancyMod.MOD_ID) + "Key";

	public AccessKeyItem(Properties properties) {
		super(properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		CompoundNBT nbt = stack.getOrCreateChildTag(NBT_KEY);
		if (nbt.hasUniqueId("OwnerUUID")) {
			tooltip.add(TooltipUtil.EMPTY_LINE_HACK());
			StringTextComponent ownerName = new StringTextComponent(TooltipUtil.tryToGetPlayerNameOnClientSide(nbt.getUniqueId("OwnerUUID")));
			ownerName.mergeStyle(TextFormatting.WHITE);
			tooltip.add(new TranslationTextComponent(TextUtil.getTranslationKey("tooltip", "owner"), ownerName).mergeStyle(TextFormatting.GRAY));

			if (nbt.contains("UserList")) {
				ListNBT nbtList = nbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
				tooltip.add(new StringTextComponent("Users: ").mergeStyle(TextFormatting.GRAY));
				int limit = Screen.hasControlDown() ? Math.min(5, nbtList.size()) : nbtList.size();
				for (int i = 0; i < limit; i++) {
					CompoundNBT userNbt = nbtList.getCompound(i);
					String userName = TooltipUtil.tryToGetPlayerNameOnClientSide(userNbt.getUniqueId("UserUUID"));
					UserAuthorization.AuthorityLevel level = UserAuthorization.AuthorityLevel.deserialize(userNbt);
					tooltip.add(new StringTextComponent(String.format(" - %s (%s)", userName, level.name().toLowerCase(Locale.ROOT))).mergeStyle(TextFormatting.GRAY));
				}
				int remainder = nbtList.size() - limit;
				if (remainder > 0) {
					tooltip.add(TooltipUtil.pressButtonTo(TooltipUtil.getCtrlKey(), "show " + remainder + " more users..."));
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isSneaking()) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			CompoundNBT nbt = stack.getOrCreateChildTag(NBT_KEY);
			if (!nbt.contains("OwnerUUID")) {
				if (!worldIn.isRemote()) {
					nbt.putUniqueId("OwnerUUID", playerIn.getUniqueID());
				}
				return ActionResult.func_233538_a_(stack, worldIn.isRemote);
			}
		}
		return ActionResult.resultPass(playerIn.getHeldItem(handIn));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (!context.getWorld().isRemote && context.getPlayer() != null) {
			TileEntity tileEntity = context.getWorld().getTileEntity(context.getPos());
			if (tileEntity instanceof IOwnableTile) {
				UserAuthorization.AuthorityLevel userAuthority = ((IOwnableTile) tileEntity).getUserAuthorityLevel(context.getPlayer().getUniqueID());
				if (userAuthority.isAdminLevel()) {
					CompoundNBT nbt = stack.getOrCreateChildTag(NBT_KEY);
					if (nbt.contains("UserList")) {
						ListNBT nbtList = nbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
						for (int i = 0; i < nbtList.size(); i++) {
							CompoundNBT nbtEntry = nbtList.getCompound(i);
							UUID userUUID = nbtEntry.getUniqueId("UserUUID");
							UserAuthorization.AuthorityLevel authority = UserAuthorization.AuthorityLevel.deserialize(nbtEntry);
							((IOwnableTile) tileEntity).addUser(userUUID, authority);
						}
					}
				}
			}
		}
		return ActionResultType.PASS;
	}
}
