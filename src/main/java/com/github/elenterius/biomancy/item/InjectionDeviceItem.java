package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModReagents;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.reagent.BloodSampleReagent;
import com.github.elenterius.biomancy.reagent.Reagent;
import com.github.elenterius.biomancy.util.ClientTextUtil;
import com.github.elenterius.biomancy.util.TextUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class InjectionDeviceItem extends Item implements IKeyListener {

	public static final String NBT_KEY_REAGENT_AMOUNT = "ReagentAmount";

	public InjectionDeviceItem(Properties properties) {
		super(properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		CompoundNBT nbt = stack.getOrCreateTag();
		Reagent reagent = Reagent.deserialize(nbt);
		if (reagent != null) {
			byte amount = nbt.getByte(NBT_KEY_REAGENT_AMOUNT);
			tooltip.add(new StringTextComponent(String.format("Amount: %d/4", amount)).mergeStyle(TextFormatting.GRAY));
			reagent.addInfoToTooltip(stack, worldIn, tooltip, flagIn);
		}
		else tooltip.add(TextUtil.getTranslationText("tooltip", "contains_nothing").mergeStyle(TextFormatting.GRAY));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTranslationText("tooltip", "action_self_inject")).mergeStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		if (displayName instanceof IFormattableTextComponent) {
			Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
			if (reagent != null) {
				return ((IFormattableTextComponent) displayName).appendString(" (").appendSibling(new TranslationTextComponent(reagent.getTranslationKey()).mergeStyle(TextFormatting.AQUA)).appendString(")");
			}
		}
		return displayName;
	}

	public int getReagentColor(ItemStack stack) {
		return Reagent.getColor(stack.getOrCreateTag());
	}

	public byte getMaxReagentAmount() {
		return (byte) 4;
	}

	public void addReagentAmount(ItemStack stack, byte amount) {
		setReagentAmount(stack, (byte) (getReagentAmount(stack) + amount));
	}

	public void setReagentAmount(ItemStack stack, byte amount) {
		amount = (byte) MathHelper.clamp(amount, 0, getMaxReagentAmount());
		if (amount == 0) {
			Reagent.remove(stack.getOrCreateTag());
		}
		stack.getOrCreateTag().putByte(NBT_KEY_REAGENT_AMOUNT, amount);
	}

	public byte getReagentAmount(ItemStack stack) {
		return stack.getOrCreateTag().getByte(NBT_KEY_REAGENT_AMOUNT);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote && handIn == Hand.MAIN_HAND && playerIn.isSneaking()) {
			ItemStack heldStack = playerIn.getHeldItem(handIn);
			ItemStack offhandStack = playerIn.getHeldItemOffhand();
			if (!offhandStack.isEmpty()) {
				if (offhandStack.getItem() == ModItems.REAGENT.get()) {
					if (addReagent(offhandStack, heldStack)) {
						playSFX(worldIn, playerIn, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC);
						return ActionResult.resultFail(heldStack);
					}
				}
				else if (offhandStack.getItem() == ModItems.GLASS_VIAL.get()) {
					if (extractReagent(offhandStack, heldStack, playerIn)) {
						return ActionResult.resultFail(heldStack);
					}
				}
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	private boolean extractReagent(ItemStack containerStack, ItemStack gunStack, PlayerEntity playerIn) {
		byte amount = getReagentAmount(gunStack);
		if (amount < 1) return false;

		Reagent storedReagent = Reagent.deserialize(gunStack.getOrCreateTag());
		if (storedReagent != null) {
			ItemStack stack = new ItemStack(ModItems.REAGENT.get());
			Reagent.serialize(storedReagent, stack.getOrCreateTag());
			Reagent.copyAdditionalData(gunStack.getOrCreateTag(), stack.getOrCreateTag());
			setReagentAmount(gunStack, (byte) (amount - 1));
			containerStack.grow(-1);
			if (!playerIn.addItemStackToInventory(stack)) {
				playerIn.entityDropItem(stack);
			}
			return true;
		}

		return false;
	}

	private boolean addReagent(ItemStack ammoStack, ItemStack gunStack) {
		byte amount = getReagentAmount(gunStack);
		if (amount >= getMaxReagentAmount()) return false;

		Reagent reagentIn = Reagent.deserialize(ammoStack.getOrCreateTag());
		Reagent storedReagent = Reagent.deserialize(gunStack.getOrCreateTag());
		if (reagentIn != null) {
			if (storedReagent == null) {
				CompoundNBT nbt = gunStack.getOrCreateTag();
				Reagent.serialize(reagentIn, nbt);
				Reagent.copyAdditionalData(ammoStack.getOrCreateTag(), nbt);
				setReagentAmount(gunStack, (byte) 1);
				ammoStack.grow(-1);
				return true;
			}
			else {
				if (reagentIn == storedReagent) {
					CompoundNBT dataIn = ammoStack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA);
					CompoundNBT storedData = gunStack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA);
					if (storedData.isEmpty() || dataIn.equals(storedData)) {
						if (storedData.isEmpty()) {
							Reagent.copyAdditionalData(ammoStack.getOrCreateTag(), gunStack.getOrCreateTag());
						}
						setReagentAmount(gunStack, (byte) (amount + 1));
						ammoStack.grow(-1);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		ItemStack stack = context.getItem();
		if (context.getPlayer() != null && !context.getPlayer().canPlayerEdit(context.getPos().offset(context.getFace()), context.getFace(), stack))
			return ActionResultType.FAIL;

		Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			World world = context.getWorld();
			boolean success = reagent.affectBlock(stack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA), context.getPlayer(), world, context.getPos(), context.getFace());
			if (success) {
				if (!world.isRemote) {
					addReagentAmount(stack, (byte) -1);
					world.playEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, context.getPos().up(), 0);
					playSFX(world, context.getPlayer(), ModSoundEvents.INJECT.get());
				}
				return ActionResultType.func_233537_a_(world.isRemote);
			}

			if (world.isRemote) playSFX(world, context.getPlayer(), SoundEvents.BLOCK_DISPENSER_FAIL);
			return ActionResultType.FAIL;
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
		Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			if (reagent.affectEntity(stack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA), player, target)) {
				if (!target.world.isRemote) {
					if (reagent.isAttributeModifier()) reagent.applyAttributesModifiersToEntity(target);
					addReagentAmount(stack, (byte) -1);
					target.world.playEvent(Constants.WorldEvents.SPAWN_EXPLOSION_PARTICLE, target.getPosition(), 0);
					playSFX(target.world, player, ModSoundEvents.INJECT.get());
				}
				return ActionResultType.func_233537_a_(target.world.isRemote);
			}

			if (player.world.isRemote) playSFX(player.world, player, SoundEvents.BLOCK_DISPENSER_FAIL);
			return ActionResultType.FAIL;
		}
		else { //the device is empty
			if (!target.world.isRemote) {
				CompoundNBT reagentNbt = BloodSampleReagent.getBloodSampleFromEntity(player, target);
				if (reagentNbt != null && !reagentNbt.isEmpty()) {
					CompoundNBT nbt = stack.getOrCreateTag();
					Reagent.serialize(ModReagents.BLOOD_SAMPLE.get(), nbt);
					nbt.put(Reagent.NBT_KEY_DATA, reagentNbt);
					setReagentAmount(stack, getMaxReagentAmount());

					playSFX(target.world, player, ModSoundEvents.INJECT.get());
					target.attackEntityFrom(DamageSource.causeBeeStingDamage(player), 0.5f);

					if (player.isCreative()) {
						player.setHeldItem(hand, stack); //fix for creative mode (normally the stack is not modified in creative)
					}
					return ActionResultType.SUCCESS;
				}
			}
		}

		return ActionResultType.PASS;
	}

	public boolean interactWithPlayerSelf(ItemStack stack, PlayerEntity player) {
		Reagent reagent = Reagent.deserialize(stack.getOrCreateTag());
		if (reagent != null) {
			boolean success = reagent.affectPlayerSelf(stack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA), player);
			if (!player.world.isRemote && success && reagent.isAttributeModifier()) {
				reagent.applyAttributesModifiersToEntity(player);
			}
			return success;
		}
		else { //the device is empty
			if (!player.world.isRemote) {
				CompoundNBT reagentNbt = BloodSampleReagent.getBloodSampleFromEntityUnchecked(player);
				if (reagentNbt != null && !reagentNbt.isEmpty()) {
					CompoundNBT nbt = stack.getOrCreateTag();
					Reagent.serialize(ModReagents.BLOOD_SAMPLE.get(), nbt);
					nbt.put(Reagent.NBT_KEY_DATA, reagentNbt);
					setReagentAmount(stack, getMaxReagentAmount());

					playSFX(player.world, player, ModSoundEvents.INJECT.get());
					player.attackEntityFrom(DamageSource.causeBeeStingDamage(player), 0.5f);

					if (player.isCreative()) {
						player.setHeldItem(player.getActiveHand(), stack); //fix for creative mode (normally the stack is not modified in creative)
					}
					return true;
				}
				else return false;
			}
			else return true;
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ActionResult<Byte> onClientKeyPress(ItemStack stack, ClientWorld world, PlayerEntity player, byte flags) {
		if (!interactWithPlayerSelf(stack, player)) {
			playSFX(world, player, SoundEvents.BLOCK_DISPENSER_FAIL);
			return ActionResult.resultFail(flags); //don't send button press to server
		}
		return ActionResult.resultSuccess(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		if (interactWithPlayerSelf(stack, player)) {
			playSFX(world, player, ModSoundEvents.INJECT.get());
		}
		else {
			playSFX(world, player, SoundEvents.BLOCK_DISPENSER_FAIL);
		}
	}

	public void playSFX(World world, LivingEntity shooter, SoundEvent soundEvent) {
		SoundCategory soundcategory = shooter instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
		world.playSound(world.isRemote && shooter instanceof PlayerEntity ? (PlayerEntity) shooter : null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), soundEvent, soundcategory, 0.8f, 1f / (random.nextFloat() * 0.5f + 1f) + 0.2f);
	}
}
