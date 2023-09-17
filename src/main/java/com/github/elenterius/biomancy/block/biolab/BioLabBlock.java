package com.github.elenterius.biomancy.block.biolab;

import com.github.elenterius.biomancy.block.HorizontalFacingMachineBlock;
import com.github.elenterius.biomancy.block.entity.MachineBlockEntity;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.VoxelShapeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Stream;

public class BioLabBlock extends HorizontalFacingMachineBlock {

	public static final VoxelShape SHAPE_NORTH = createShape(Direction.NORTH);
	public static final VoxelShape SHAPE_SOUTH = createShape(Direction.SOUTH);
	public static final VoxelShape SHAPE_WEST = createShape(Direction.WEST);
	public static final VoxelShape SHAPE_EAST = createShape(Direction.EAST);

	public BioLabBlock(Properties properties) {
		super(properties);
	}

	private static VoxelShape createShape(Direction direction) {
		return Stream.of(
				VoxelShapeUtil.createYRotatedTowards(direction, 2, 0, 2, 14, 7, 14),
				VoxelShapeUtil.createYRotatedTowards(direction, 4, 10, 4, 12, 16, 12),
				VoxelShapeUtil.createYRotatedTowards(direction, 6, 1, 12, 10, 10, 15),
				VoxelShapeUtil.createYRotatedTowards(direction, 1, 1, 4, 15, 10, 12)
		).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.block());
	}


	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntities.BIO_LAB.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.BIO_LAB.get(), MachineBlockEntity::serverTick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof BioLabBlockEntity bioLab && bioLab.canPlayerOpenInv(player)) {
			if (!level.isClientSide) {
				NetworkHooks.openScreen((ServerPlayer) player, bioLab, buffer -> buffer.writeBlockPos(pos));
				SoundUtil.broadcastBlockSound((ServerLevel) level, pos, ModSoundEvents.UI_BIO_LAB_OPEN);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.CONSUME;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case NORTH -> SHAPE_NORTH;
			case SOUTH -> SHAPE_SOUTH;
			case WEST -> SHAPE_WEST;
			case EAST -> SHAPE_EAST;
			default -> Shapes.block();
		};
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(5) == 0 && Boolean.TRUE.equals(state.getValue(CRAFTING))) {
			int particleAmount = random.nextInt(1, 5);
			int color = 0x9acd32; //yellowgreen
			double r = (color >> 16 & 255) / 255d;
			double g = (color >> 8 & 255) / 255d;
			double b = (color & 255) / 255d;
			for (int i = 0; i < particleAmount; i++) {
				level.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.25F), pos.getY() + 0.65d, pos.getZ() + 0.5d + ((random.nextFloat() - random.nextFloat()) * 0.25F), r, g, b);
			}
			if (random.nextInt(3) == 0) {
				SoundUtil.clientPlayBlockSound(level, pos, ModSoundEvents.BIO_LAB_CRAFTING_RANDOM, 0.65f);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		int fuelAmount = getFuelAmount(stack);
		if (fuelAmount > 0) {
			tooltip.add(ComponentUtil.emptyLine());
			DecimalFormat df = ClientTextUtil.getDecimalFormatter("#,###,###");
			tooltip.add(ComponentUtil.translatable("tooltip.biomancy.nutrients_fuel").withStyle(ChatFormatting.GRAY));
			tooltip.add(ComponentUtil.literal("%s/%s u".formatted(df.format(fuelAmount), df.format(BioLabBlockEntity.MAX_FUEL))).withStyle(TextStyles.NUTRIENTS));
		}
	}

	public static int getFuelAmount(ItemStack stack) {
		CompoundTag tag = BlockItem.getBlockEntityData(stack);
		return tag != null && tag.contains("Fuel") ? tag.getCompound("Fuel").getInt("Amount") : 0;
	}
}
