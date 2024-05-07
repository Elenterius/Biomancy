package com.github.elenterius.biomancy.item.weapon;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.entity.mob.ControllableMob;
import com.github.elenterius.biomancy.entity.mob.FleshkinHumanoid;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.item.KeyPressListener;
import com.github.elenterius.biomancy.ownable.OwnableMob;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
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

public class ControlStaffItem extends Item implements KeyPressListener, ItemTooltipStyleProvider {

	public ControlStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
		ControllableMob.Command command = getCommand(stack).cycle();
		player.playSound(SoundEvents.GENERIC_HURT, 0.8f, 0.25f + level.random.nextFloat() * 0.25f);
		return InteractionResultHolder.success(command.serialize());
	}

	@Override
	public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
		ControllableMob.Command command = ControllableMob.Command.deserialize(flags);
		setCommand(stack, command);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player player = context.getPlayer();
		if (context.getLevel().isClientSide || player == null) return InteractionResult.FAIL;

		List<Mob> mobs = context.getLevel().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(16d),
				mob -> mob instanceof ControllableMob && mob instanceof OwnableMob ownable && ownable.isOwner(player));

		int size = mobs.size();
		if (size == 0) {
			MutableComponent component = ComponentUtil.literal("Couldn't find any controllable mobs nearby!").withStyle(ChatFormatting.RED);
			player.displayClientMessage(component, true);
			return InteractionResult.FAIL;
		}

		ControllableMob.Command command = getCommand(stack);
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
		for (Mob mob : mobs) {
			ControllableMob<Mob> controllable = ControllableMob.cast(mob);
			controllable.updateRestriction(command, pos);
			controllable.setActiveCommand(command);
		}

		FleshkinHumanoid.displayCommandSetMsg(player, ComponentUtil.literal(size + (size > 1 ? " mobs" : " mob")), command);
		return InteractionResult.SUCCESS;
	}

	public ControllableMob.Command getCommand(ItemStack stack) {
		return ControllableMob.Command.deserialize(stack.getOrCreateTag().getByte("Command"));
	}

	public void setCommand(ItemStack stack, ControllableMob.Command command) {
		stack.getOrCreateTag().putByte("Command", command.serialize());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.addAll(ClientTextUtil.getItemInfoTooltip(stack));
		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(TextComponentUtil.getTooltipText("command").append(": ").withStyle().withStyle(ChatFormatting.GRAY).append(ComponentUtil.literal(getCommand(stack).toString()).withStyle(ChatFormatting.AQUA)));
		tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action_cycle")).withStyle(ChatFormatting.DARK_GRAY));
	}

//	@Override
	//	public Component getHighlightTip(ItemStack stack, Component displayName) {
	//		return ComponentFacade.builder().append(displayName).append(" (").append(ComponentFacade.literal(getCommand(stack).toString()).withStyle(ChatFormatting.AQUA)).append(")");
	//	}

}
