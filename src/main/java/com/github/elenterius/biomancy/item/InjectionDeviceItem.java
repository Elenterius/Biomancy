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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

	public static boolean dispenserInjectLivingEntity(ServerWorld level, BlockPos pos, ItemStack stack) {
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos), EntityPredicates.NO_SPECTATORS);
		if (!entities.isEmpty() && dispenserAffectEntity(level, stack, entities.get(0))) {
			level.playSound(null, pos, ModSoundEvents.INJECT.get(), SoundCategory.BLOCKS, 0.8f, 1f / (random.nextFloat() * 0.5f + 1f) + 0.2f);
			return true;
		}
		return false;
	}

	private static boolean dispenserAffectEntity(ServerWorld level, ItemStack stack, LivingEntity target) {
		InjectionDeviceItem item = ModItems.INJECTION_DEVICE.get();
		Reagent reagent = item.getReagent(stack);
		if (reagent != null) {
			if (reagent.affectEntity(stack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA), null, target)) {
				if (reagent.isAttributeModifier()) reagent.applyAttributesModifiersToEntity(target);
				item.addReagentAmount(stack, (byte) -1);
				level.levelEvent(Constants.WorldEvents.SPAWN_EXPLOSION_PARTICLE, target.blockPosition(), 0);
				return true;
			}
		}
		else { //the device is empty
			if (target.isAlive() && BloodSampleReagent.isNonBoss(target)) {
				CompoundNBT reagentNbt = BloodSampleReagent.getBloodSampleFromEntityUnchecked(target);
				if (reagentNbt != null && !reagentNbt.isEmpty()) {
					CompoundNBT nbt = stack.getOrCreateTag();
					Reagent.serialize(ModReagents.BLOOD_SAMPLE.get(), nbt);
					nbt.put(Reagent.NBT_KEY_DATA, reagentNbt);
					item.setReagentAmount(stack, item.getMaxReagentAmount());

					target.hurt(new EntityDamageSource("sting", null), 0.5f);
					return true;
				}
			}
		}

		return false;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(this).setStyle(ClientTextUtil.LORE_STYLE));
		Reagent reagent = getReagent(stack);
		if (reagent != null) {
			byte amount = getReagentAmount(stack);
			tooltip.add(new StringTextComponent(String.format("Amount: %d/4", amount)).withStyle(TextFormatting.GRAY));
			reagent.addInfoToTooltip(stack, worldIn, tooltip, flagIn);
		}
		else tooltip.add(TextUtil.getTranslationText("tooltip", "contains_nothing").withStyle(TextFormatting.GRAY));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextUtil.getTranslationText("tooltip", "action_self_inject")).withStyle(TextFormatting.DARK_GRAY));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		if (displayName instanceof IFormattableTextComponent) {
			Reagent reagent = getReagent(stack);
			if (reagent != null) {
				return ((IFormattableTextComponent) displayName).append(" (").append(new TranslationTextComponent(reagent.getTranslationKey()).withStyle(TextFormatting.AQUA)).append(")");
			}
		}
		return displayName;
	}

	@Nullable
	public Reagent getReagent(ItemStack stack) {
		return Reagent.deserialize(stack.getOrCreateTag());
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
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isClientSide && handIn == Hand.MAIN_HAND && playerIn.isShiftKeyDown()) {
			ItemStack heldStack = playerIn.getItemInHand(handIn);
			ItemStack offhandStack = playerIn.getOffhandItem();
			if (!offhandStack.isEmpty()) {
				if (offhandStack.getItem() == ModItems.REAGENT.get()) {
					if (addReagent(offhandStack, heldStack, playerIn)) {
						playSFX(worldIn, playerIn, SoundEvents.ARMOR_EQUIP_GENERIC);
						return ActionResult.fail(heldStack);
					}
				}
				else if (offhandStack.getItem() == ModItems.GLASS_VIAL.get()) {
					if (extractReagent(offhandStack, heldStack, playerIn)) {
						return ActionResult.fail(heldStack);
					}
				}
			}
		}
		return super.use(worldIn, playerIn, handIn);
	}

	private boolean extractReagent(ItemStack containerStack, ItemStack gunStack, PlayerEntity playerIn) {
		byte amount = getReagentAmount(gunStack);
		if (amount < 1) return false;

		Reagent storedReagent = getReagent(gunStack);
		if (storedReagent != null) {
			ItemStack stack = new ItemStack(ModItems.REAGENT.get());
			Reagent.serialize(storedReagent, stack.getOrCreateTag());
			Reagent.copyAdditionalData(gunStack.getOrCreateTag(), stack.getOrCreateTag());
			setReagentAmount(gunStack, (byte) (amount - 1));
			containerStack.grow(-1);
			if (!playerIn.addItem(stack)) {
				playerIn.spawnAtLocation(stack);
			}
			return true;
		}

		return false;
	}

	private boolean addReagent(ItemStack ammoStack, ItemStack gunStack, PlayerEntity playerIn) {
		byte amount = getReagentAmount(gunStack);
		if (amount >= getMaxReagentAmount()) return false;

		Reagent reagentIn = getReagent(ammoStack);
		Reagent storedReagent = getReagent(gunStack);
		if (reagentIn != null) {
			if (storedReagent == null) {
				CompoundNBT gunNbt = gunStack.getOrCreateTag();
				Reagent.serialize(reagentIn, gunNbt);
				Reagent.copyAdditionalData(ammoStack.getOrCreateTag(), gunNbt);
				setReagentAmount(gunStack, (byte) 1);
				ammoStack.grow(-1);

				ItemStack stack = new ItemStack(ModItems.GLASS_VIAL.get());
				if (!playerIn.addItem(stack)) {
					playerIn.spawnAtLocation(stack);
				}
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

						ItemStack stack = new ItemStack(ModItems.GLASS_VIAL.get());
						if (!playerIn.addItem(stack)) {
							playerIn.spawnAtLocation(stack);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack stack = context.getItemInHand();
		if (context.getPlayer() != null && !context.getPlayer().mayUseItemAt(context.getClickedPos().relative(context.getClickedFace()), context.getClickedFace(), stack))
			return ActionResultType.FAIL;

		Reagent reagent = getReagent(stack);
		if (reagent != null) {
			World world = context.getLevel();
			boolean success = reagent.affectBlock(stack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA), context.getPlayer(), world, context.getClickedPos(), context.getClickedFace());
			if (success) {
				if (!world.isClientSide) {
					if (context.getPlayer() == null || !context.getPlayer().isCreative()) addReagentAmount(stack, (byte) -1);
					world.levelEvent(Constants.WorldEvents.BONEMEAL_PARTICLES, context.getClickedPos().above(), 0);
					playSFX(world, context.getPlayer(), ModSoundEvents.INJECT.get());
				}
				return ActionResultType.sidedSuccess(world.isClientSide);
			}

			if (world.isClientSide) playSFX(world, context.getPlayer(), SoundEvents.DISPENSER_FAIL);
			return ActionResultType.FAIL;
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
		Reagent reagent = getReagent(stack);
		if (reagent != null) {
			if (reagent.affectEntity(stack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA), player, target)) {
				if (!target.level.isClientSide) {
					if (reagent.isAttributeModifier()) reagent.applyAttributesModifiersToEntity(target);
					if (!player.isCreative()) addReagentAmount(stack, (byte) -1);

					target.level.levelEvent(Constants.WorldEvents.SPAWN_EXPLOSION_PARTICLE, target.blockPosition(), 0);
					playSFX(target.level, player, ModSoundEvents.INJECT.get());
				}
				return ActionResultType.sidedSuccess(target.level.isClientSide);
			}

			if (player.level.isClientSide) playSFX(player.level, player, SoundEvents.DISPENSER_FAIL);
			return ActionResultType.FAIL;
		}
		else { //the device is empty
			if (!target.level.isClientSide) {
				CompoundNBT reagentNbt = BloodSampleReagent.getBloodSampleFromEntity(player, target);
				if (reagentNbt != null && !reagentNbt.isEmpty()) {
					CompoundNBT nbt = stack.getOrCreateTag();
					Reagent.serialize(ModReagents.BLOOD_SAMPLE.get(), nbt);
					nbt.put(Reagent.NBT_KEY_DATA, reagentNbt);
					setReagentAmount(stack, getMaxReagentAmount());

					playSFX(target.level, player, ModSoundEvents.INJECT.get());
					target.hurt(DamageSource.sting(player), 0.5f);

					if (player.isCreative()) {
						player.setItemInHand(hand, stack); //fix for creative mode (normally the stack is not modified in creative)
					}
					return ActionResultType.SUCCESS;
				}
			}
		}

		return ActionResultType.PASS;
	}

	public boolean interactWithPlayerSelf(ItemStack stack, PlayerEntity player) {
		Reagent reagent = getReagent(stack);
		if (reagent != null) {
			boolean success = reagent.affectPlayerSelf(stack.getOrCreateTag().getCompound(Reagent.NBT_KEY_DATA), player);
			if (success && !player.level.isClientSide) {
				if (reagent.isAttributeModifier()) reagent.applyAttributesModifiersToEntity(player);
				if (!player.isCreative()) addReagentAmount(stack, (byte) -1);
			}
			return success;
		}
		else { //the device is empty
			if (!player.level.isClientSide) {
				CompoundNBT reagentNbt = BloodSampleReagent.getBloodSampleFromEntityUnchecked(player);
				if (reagentNbt != null && !reagentNbt.isEmpty()) {
					CompoundNBT nbt = stack.getOrCreateTag();
					Reagent.serialize(ModReagents.BLOOD_SAMPLE.get(), nbt);
					nbt.put(Reagent.NBT_KEY_DATA, reagentNbt);
					setReagentAmount(stack, getMaxReagentAmount());

					playSFX(player.level, player, ModSoundEvents.INJECT.get());
					player.hurt(DamageSource.sting(player), 0.5f);

					if (player.isCreative()) {
						player.setItemInHand(player.getUsedItemHand(), stack); //fix for creative mode (normally the stack is not modified in creative)
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
			playSFX(world, player, SoundEvents.DISPENSER_FAIL);
			return ActionResult.fail(flags); //don't send button press to server
		}
		return ActionResult.success(flags);
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerWorld world, ServerPlayerEntity player, byte flags) {
		if (interactWithPlayerSelf(stack, player)) {
			playSFX(world, player, ModSoundEvents.INJECT.get());
		}
		else {
			playSFX(world, player, SoundEvents.DISPENSER_FAIL);
		}
	}

	public void playSFX(World world, LivingEntity shooter, SoundEvent soundEvent) {
		SoundCategory soundcategory = shooter instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
		world.playSound(world.isClientSide && shooter instanceof PlayerEntity ? (PlayerEntity) shooter : null, shooter.getX(), shooter.getY(), shooter.getZ(), soundEvent, soundcategory, 0.8f, 1f / (random.nextFloat() * 0.5f + 1f) + 0.2f);
	}
}
