package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.tileentity.IOwnableTile;
import com.github.elenterius.biomancy.util.ClientTextUtil;
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
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		CompoundNBT nbt = stack.getOrCreateTagElement(NBT_KEY);
		if (nbt.hasUUID("OwnerUUID")) {
			tooltip.add(ClientTextUtil.EMPTY_LINE_HACK());
			StringTextComponent ownerName = new StringTextComponent(ClientTextUtil.tryToGetPlayerNameOnClientSide(nbt.getUUID("OwnerUUID")));
			ownerName.withStyle(TextFormatting.WHITE);
			tooltip.add(new TranslationTextComponent(TextUtil.getTranslationKey("tooltip", "owner"), ownerName).withStyle(TextFormatting.GRAY));

			if (nbt.contains("UserList")) {
				ListNBT nbtList = nbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
				tooltip.add(new StringTextComponent("Users: ").withStyle(TextFormatting.GRAY));
				int limit = Screen.hasControlDown() ? Math.min(5, nbtList.size()) : nbtList.size();
				for (int i = 0; i < limit; i++) {
					CompoundNBT userNbt = nbtList.getCompound(i);
					String userName = ClientTextUtil.tryToGetPlayerNameOnClientSide(userNbt.getUUID("UserUUID"));
					UserAuthorization.AuthorityLevel level = UserAuthorization.AuthorityLevel.deserialize(userNbt);
					tooltip.add(new StringTextComponent(String.format(" - %s (%s)", userName, level.name().toLowerCase(Locale.ROOT))).withStyle(TextFormatting.GRAY));
				}
				int remainder = nbtList.size() - limit;
				if (remainder > 0) {
					tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getCtrlKey(), "show " + remainder + " more users..."));
				}
			}
		}
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isShiftKeyDown()) {
			ItemStack stack = playerIn.getItemInHand(handIn);
			CompoundNBT nbt = stack.getOrCreateTagElement(NBT_KEY);
			if (!nbt.contains("OwnerUUID")) {
				if (!worldIn.isClientSide()) {
					nbt.putUUID("OwnerUUID", playerIn.getUUID());
				}
				return ActionResult.sidedSuccess(stack, worldIn.isClientSide);
			}
		}
		return ActionResult.pass(playerIn.getItemInHand(handIn));
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (!context.getLevel().isClientSide && context.getPlayer() != null) {
			TileEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if (tileEntity instanceof IOwnableTile) {
				UserAuthorization.AuthorityLevel userAuthority = ((IOwnableTile) tileEntity).getUserAuthorityLevel(context.getPlayer().getUUID());
				if (userAuthority.isAdminLevel()) {
					CompoundNBT nbt = stack.getOrCreateTagElement(NBT_KEY);
					if (nbt.contains("UserList")) {
						ListNBT nbtList = nbt.getList("UserList", Constants.NBT.TAG_COMPOUND);
						for (int i = 0; i < nbtList.size(); i++) {
							CompoundNBT nbtEntry = nbtList.getCompound(i);
							UUID userUUID = nbtEntry.getUUID("UserUUID");
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
