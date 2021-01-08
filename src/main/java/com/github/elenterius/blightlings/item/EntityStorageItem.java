package com.github.elenterius.blightlings.item;

import com.github.elenterius.blightlings.BlightlingsMod;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EntityStorageItem extends BagItem {

	public EntityStorageItem(Properties properties) {
		super(properties);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		CompoundNBT parentNBT = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		if (parentNBT.contains("Entity")) {
			CompoundNBT childNBT = parentNBT.getCompound("Entity");
			tooltip.add(new StringTextComponent("Contains: ").append(new TranslationTextComponent(childNBT.getString("Name")).mergeStyle(TextFormatting.GRAY)));
		}
		else tooltip.add(new StringTextComponent("Contains: Nothing"));
	}

	@Override
	public ITextComponent getHighlightTip(ItemStack stack, ITextComponent displayName) {
		CompoundNBT nbt = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		if (nbt.contains("Entity")) {
			return new StringTextComponent("").append(displayName).appendString(" (").append(new TranslationTextComponent(nbt.getCompound("Entity").getString("Name")).appendString(")"));
		}
		return super.getHighlightTip(stack, displayName);
	}

	@Override
	public float getFullness(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		if (nbt.contains("Entity")) {
			return 1f;
		}
		return 0f;
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getWorld().isRemote()) return ActionResultType.PASS;

		CompoundNBT nbt = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
		PlayerEntity player = context.getPlayer();
		if (player != null) {
			onPlayerInteractWithItem(stack, player);
			if (nbt.contains("Entity")) {
				CompoundNBT wrapper = nbt.getCompound("Entity");
				if (context.getWorld().isBlockModifiable(player, context.getPos()) && player.canPlayerEdit(context.getPos(), context.getFace(), stack)) {
					Entity entity = EntityType.loadEntityAndExecute(wrapper.getCompound("Data"), context.getWorld(), (entity_) -> {

						float widthFactor = entity_.getWidth() * 0.6f; //prevent mobs from suffocating in walls as much as possible
						float yOffset = context.getFace().getYOffset();
						float heightModifier = yOffset < 0f ? -entity_.getHeight() : yOffset > 0f ? 0f : entity_.getHeight() * 0.5f;
						Vector3d pos = context.getHitVec().add(context.getFace().getXOffset() * widthFactor, heightModifier, context.getFace().getZOffset() * widthFactor);

						entity_.setLocationAndAngles(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(context.getWorld().rand.nextFloat() * 360f), 0f);
						if (entity_ instanceof MobEntity) {
							MobEntity mobentity = (MobEntity) entity_;
							mobentity.rotationYawHead = mobentity.rotationYaw;
							mobentity.renderYawOffset = mobentity.rotationYaw;
						}
						entity_.setMotion(0, 0, 0);
						entity_.fallDistance = 0;
						return entity_;
					});
					if (entity != null) {
						if (context.getWorld().addEntity(entity)) {
							context.getWorld().playSound(null, context.getPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.75F, 0.35f + context.getWorld().rand.nextFloat() * 0.25f);
							nbt.remove("Entity");
							return ActionResultType.SUCCESS;
						}
					}
					ITextComponent textComponent = new TranslationTextComponent("msg.blightlings.failed_to_spawn", new TranslationTextComponent(wrapper.getString("Name"))).mergeStyle(TextFormatting.RED);
					player.sendStatusMessage(textComponent, true);
					return ActionResultType.FAIL;
				}
				ITextComponent textComponent = new TranslationTextComponent("msg.blightlings.not_allowed").mergeStyle(TextFormatting.RED);
				player.sendStatusMessage(textComponent, true);
				return ActionResultType.FAIL;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		if (playerIn.world.isRemote()) return ActionResultType.PASS;
		onPlayerInteractWithItem(stack, playerIn);

		boolean isValidTarget = target.isAlive() && !(target instanceof PlayerEntity) && (target.isNonBoss() || playerIn.isCreative()); // blame creative player if something breaks due to storing boss mobs
		if (isValidTarget) {
			CompoundNBT nbt = stack.getOrCreateChildTag(BlightlingsMod.MOD_ID);
			if (!nbt.contains("Entity")) {
				CompoundNBT wrapper = new CompoundNBT();

				CompoundNBT data = new CompoundNBT();
				target.writeUnlessPassenger(data);
				data.remove("UUID");
				wrapper.put("Data", data);

				wrapper.putString("Name", target.getType().getTranslationKey());
				nbt.put("Entity", wrapper);
				target.remove();

				playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.9F, 0.3f + playerIn.world.rand.nextFloat() * 0.25f);

				if (playerIn.isCreative()) {
					playerIn.setHeldItem(hand, stack); //fix for creative mode (normally the stack is not modified in creative)
				}
				return ActionResultType.SUCCESS;
			}
			else {
				playerIn.sendStatusMessage(new TranslationTextComponent("msg.blightlings.already_full").mergeStyle(TextFormatting.RED), true);
			}
		}

		return ActionResultType.PASS;
	}
}
