package com.github.elenterius.biomancy.world.item.weapon;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.world.entity.ownable.Fleshkin;
import com.github.elenterius.biomancy.world.entity.ownable.IControllableMob;
import com.github.elenterius.biomancy.world.item.ICustomTooltip;
import com.github.elenterius.biomancy.world.item.IKeyListener;
import com.github.elenterius.biomancy.world.ownable.IOwnableMob;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ControlStaffItem extends Item implements IKeyListener, ICustomTooltip {

	public ControlStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		IControllableMob.Command command = getCommand(stack).cycle();
		player.playSound(SoundEvents.GENERIC_HURT, 0.8f, 0.25f + level.random.nextFloat() * 0.25f);
		return InteractionResultHolder.success(command.serialize());
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		IControllableMob.Command command = IControllableMob.Command.deserialize(flags);
		setCommand(stack, command);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player player = context.getPlayer();
		if (context.getLevel().isClientSide || player == null) return InteractionResult.FAIL;

		List<Mob> mobs = context.getLevel().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(16d),
				mob -> mob instanceof IControllableMob && mob instanceof IOwnableMob ownable && ownable.isOwner(player));

		int size = mobs.size();
		if (size == 0) {
			MutableComponent component = ComponentUtil.literal("Couldn't find any controllable mobs nearby!").withStyle(ChatFormatting.RED);
			player.displayClientMessage(component, true);
			return InteractionResult.FAIL;
		}

		IControllableMob.Command command = getCommand(stack);
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
		for (Mob mob : mobs) {
			IControllableMob<Mob> controllable = IControllableMob.cast(mob);
			controllable.updateRestriction(command, pos);
			controllable.setActiveCommand(command);
		}

		Fleshkin.displayCommandSetMsg(player, ComponentUtil.literal(size + (size > 1 ? " mobs" : " mob")), command);
		return InteractionResult.SUCCESS;
	}

	public IControllableMob.Command getCommand(ItemStack stack) {
		return IControllableMob.Command.deserialize(stack.getOrCreateTag().getByte("Command"));
	}

	public void setCommand(ItemStack stack, IControllableMob.Command command) {
		stack.getOrCreateTag().putByte("Command", command.serialize());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));
		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(TextComponentUtil.getTooltipText("command").append(": ").withStyle().withStyle(ChatFormatting.GRAY).append(ComponentUtil.literal(getCommand(stack).toString()).withStyle(ChatFormatting.AQUA)));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action_cycle")).withStyle(ChatFormatting.DARK_GRAY));
	}

//	@Override
	//	public Component getHighlightTip(ItemStack stack, Component displayName) {
	//		return ComponentUtil.mutable().append(displayName).append(" (").append(ComponentUtil.literal(getCommand(stack).toString()).withStyle(ChatFormatting.AQUA)).append(")");
	//	}

}
