package com.github.elenterius.biomancy.block;

import com.github.elenterius.biomancy.entity.aberration.FleshBlobEntity;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

public class MeatsoupCauldronBlock extends Block {

	public static final IntegerProperty FLAGS = IntegerProperty.create("flags", 0, Flags.getMaxNumber());
	public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_8;
	public static final int MAX_LEVEL = 8;
	private static final VoxelShape INSIDE = makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.or(
			makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D),
			makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D),
			makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D),
			INSIDE), IBooleanFunction.ONLY_FIRST);

	public MeatsoupCauldronBlock(Properties properties) {
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(LEVEL, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(LEVEL, FLAGS);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (tryToUseHopper((World) worldIn, currentPos, stateIn, stateIn.get(LEVEL))) {
			return Blocks.CAULDRON.getDefaultState();
		}

		return stateIn;
	}

	public void setSoupLevel(World worldIn, BlockPos pos, BlockState state, int flagValue, int level, int modifier) {
		worldIn.setBlockState(pos, state.with(LEVEL, MathHelper.clamp(level + modifier, 0, MAX_LEVEL - 1)), Constants.BlockFlags.BLOCK_UPDATE);
		worldIn.updateComparatorOutputLevel(pos, this);
		if (level + modifier >= MAX_LEVEL - 1 && Flags.isFlagSet(flagValue, Flags.BONE_MEAL)) {
			worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), (modifier > 1 ? 45 : 55) + 1 + worldIn.rand.nextInt(modifier > 1 ? 15 : 25));
		}
	}

	public boolean tryToUseHopper(World worldIn, BlockPos pos, BlockState state, int level) {
		if (level == MAX_LEVEL) {
			Block neighbourBlock = worldIn.getBlockState(pos.down()).getBlock();
			if (neighbourBlock == Blocks.HOPPER) {
				ItemStack resultStack = new ItemStack(ModItems.NECROTIC_FLESH.get(), 9);
				worldIn.setBlockState(pos, state.with(LEVEL, 0));
				spawnAsEntity(worldIn, pos.add(0.5d, 0.5d, 0.5d), resultStack);
				worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
				worldIn.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
				return true;
			}
		}
		return false;
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		int level = state.get(LEVEL);
		if (level >= MAX_LEVEL - 1) {
			int flagValue = state.get(FLAGS);
			float p = Flags.isFlagSet(flagValue, Flags.ROTTEN_FLESH) ? 0.6f : 0.4f;
			if (worldIn.rand.nextFloat() <= p) {
				worldIn.setBlockState(pos, Blocks.CAULDRON.getDefaultState());

				FleshBlobEntity blobEntity = ModEntityTypes.FLESH_BLOB.get().create(worldIn);
				if (blobEntity != null) {
					blobEntity.setLocationAndAngles(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, 0, 0);
					if (p > 0.4f && worldIn.rand.nextFloat() < 0.55f) {
						blobEntity.setCustomEntityData((byte) 1);
					}
					worldIn.addEntity(blobEntity);
				}
			}
			else {
				if (level < MAX_LEVEL) {
					level = MAX_LEVEL;
					worldIn.setBlockState(pos, state.with(LEVEL, MAX_LEVEL), Constants.BlockFlags.BLOCK_UPDATE);
					worldIn.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, 0.5F);
				}
			}
		}
		tryToUseHopper(worldIn, pos, state, level);
	}

	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!worldIn.isRemote && entityIn instanceof ItemEntity) {
			ItemStack stack = ((ItemEntity) entityIn).getItem();
			if (!stack.isEmpty()) {
				int level = state.get(LEVEL);
				int flagValue = state.get(FLAGS);
				Item item = stack.getItem();
				if (level < MAX_LEVEL - 3 && item.isIn(ModTags.Items.RAW_MEATS)) {
					stack.grow(-1);
					setSoupLevel(worldIn, pos, state, flagValue, level, 1);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
				}
				else if (!Flags.isFlagSet(flagValue, Flags.ROTTEN_FLESH) && (item == Items.ROTTEN_FLESH || item == ModItems.MUTAGENIC_BILE.get())) {
					stack.grow(-1);
					worldIn.setBlockState(pos, state.with(FLAGS, Flags.setFlag(flagValue, Flags.ROTTEN_FLESH)));
					worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
				}
				else if (!Flags.isFlagSet(flagValue, Flags.BONE_MEAL) && item instanceof BoneMealItem) {
					stack.grow(-1);
					worldIn.setBlockState(pos, state.with(FLAGS, Flags.setFlag(flagValue, Flags.BONE_MEAL)));
					worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
					if (level >= MAX_LEVEL - 1) {
						worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 55 + 1 + worldIn.rand.nextInt(20));
					}
				}
				else if (level >= MAX_LEVEL - 3 && level < MAX_LEVEL - 1) {
					if (item instanceof PotionItem || item == ModItems.REJUVENATING_MUCUS.get()) {
						Potion potion = PotionUtils.getPotionFromItem(stack);
						if (potion == Potions.HEALING || potion == Potions.REGENERATION || item == ModItems.REJUVENATING_MUCUS.get()) {
							stack.grow(-1);
							if (item != ModItems.REJUVENATING_MUCUS.get()) {
								entityIn.entityDropItem(stack.hasContainerItem() ? stack.getContainerItem() : new ItemStack(Items.GLASS_BOTTLE));
							}
							setSoupLevel(worldIn, pos, state, flagValue, level, 1);
							worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
						}
						else if (potion == Potions.STRONG_HEALING || potion == Potions.STRONG_REGENERATION || potion == Potions.LONG_REGENERATION) {
							stack.grow(-1);
							entityIn.entityDropItem(stack.hasContainerItem() ? stack.getContainerItem() : new ItemStack(Items.GLASS_BOTTLE));
							setSoupLevel(worldIn, pos, state, flagValue, level, 2);
							worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
						}
					}
				}
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		int level = state.get(LEVEL);

		if (level >= MAX_LEVEL) {
			if (!worldIn.isRemote) {
				worldIn.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
				ItemStack resultStack = new ItemStack(ModItems.NECROTIC_FLESH.get(), 9);
				if (!player.addItemStackToInventory(resultStack)) {
					spawnAsEntity(worldIn, pos.add(0d, 0.5d, 0d), resultStack);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}
			}
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}

		ItemStack stack = player.getHeldItem(handIn);
		if (!stack.isEmpty()) {
			int flagValue = state.get(FLAGS);
			Item item = stack.getItem();
			if (!Flags.isFlagSet(flagValue, Flags.ROTTEN_FLESH) && (item == Items.ROTTEN_FLESH || item == ModItems.MUTAGENIC_BILE.get())) {
				if (!worldIn.isRemote) {
					if (!player.abilities.isCreativeMode) {
						stack.grow(-1);
					}

					player.addStat(Stats.USE_CAULDRON);
					worldIn.setBlockState(pos, state.with(FLAGS, Flags.setFlag(flagValue, Flags.ROTTEN_FLESH)));
					worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
				}

				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
			else if (!Flags.isFlagSet(flagValue, Flags.BONE_MEAL) && item instanceof BoneMealItem) {
				if (!worldIn.isRemote) {
					if (!player.abilities.isCreativeMode) {
						stack.grow(-1);
					}

					player.addStat(Stats.USE_CAULDRON);
					worldIn.setBlockState(pos, state.with(FLAGS, Flags.setFlag(flagValue, Flags.BONE_MEAL)));
					worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
					if (level >= MAX_LEVEL - 1) {
						worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 55 + 1 + worldIn.rand.nextInt(20));
					}
				}

				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
			else if (level < MAX_LEVEL - 3 && item.isIn(ModTags.Items.RAW_MEATS)) {
				if (!worldIn.isRemote) {
					if (!player.abilities.isCreativeMode) {
						stack.grow(-1);
					}

					player.addStat(Stats.USE_CAULDRON);
					setSoupLevel(worldIn, pos, state, flagValue, level, 1);
					worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH_SMALL, SoundCategory.BLOCKS, 1.0F, 0.5F);
				}

				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
			else if (level >= MAX_LEVEL - 3 && level < MAX_LEVEL - 1) {
				if (item instanceof PotionItem || item == ModItems.REJUVENATING_MUCUS.get()) {
					Potion potion = PotionUtils.getPotionFromItem(stack);
					if (potion == Potions.HEALING || potion == Potions.REGENERATION || item == ModItems.REJUVENATING_MUCUS.get()) {
						if (!worldIn.isRemote) {
							if (!player.abilities.isCreativeMode) {
								stack.grow(-1);
								if (stack.isEmpty() && item != ModItems.REJUVENATING_MUCUS.get()) {
									player.setHeldItem(handIn, stack.hasContainerItem() ? stack.getContainerItem() : new ItemStack(Items.GLASS_BOTTLE));
								}
							}

							player.addStat(Stats.USE_CAULDRON);
							setSoupLevel(worldIn, pos, state, flagValue, level, 1);
							worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
						}

						return ActionResultType.func_233537_a_(worldIn.isRemote);
					}
					else if (potion == Potions.STRONG_HEALING || potion == Potions.STRONG_REGENERATION || potion == Potions.LONG_REGENERATION) {
						if (!worldIn.isRemote) {
							if (!player.abilities.isCreativeMode) {
								stack.grow(-1);
								if (stack.isEmpty()) {
									player.setHeldItem(handIn, stack.hasContainerItem() ? stack.getContainerItem() : new ItemStack(Items.GLASS_BOTTLE));
								}
							}

							player.addStat(Stats.USE_CAULDRON);
							setSoupLevel(worldIn, pos, state, flagValue, level, 2);
							worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
						}

						return ActionResultType.func_233537_a_(worldIn.isRemote);
					}
				}
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(4) == 0) {
			int level = stateIn.get(LEVEL);
			if (level > MAX_LEVEL - 3) {
				BlockState blockstate = worldIn.getBlockState(pos);
				double yOffset = blockstate.getShape(worldIn, pos).max(Direction.Axis.Y, 0.5d, 0.5d) + 0.03125d;
				for (int i = 0; i < rand.nextInt(level); i++) {
					worldIn.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.13125f + 0.7375f * rand.nextFloat(), pos.getY() + yOffset + (1 - yOffset), pos.getZ() + 0.13125f + 0.7375f * rand.nextFloat(), 1.8f, 1.4f, 1.4f);
				}
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return INSIDE;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return blockState.get(LEVEL);
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	public enum Flags {
		BONE_MEAL,
		ROTTEN_FLESH;

		private final int bitPosition = 1 << ordinal();

		public static int getMaxNumber() {
			return (int) Math.pow(2, Flags.values().length) - 1;
		}

		public static boolean isFlagSet(int value, Flags flag) {
			return (value & flag.bitPosition) != 0;
		}

		public static int setFlag(int value, Flags flag) {
			return value | flag.bitPosition;
		}

		public static int unsetFlag(int value, Flags flag) {
			return value & ~flag.bitPosition;
		}

		public int getBitPosition() {
			return bitPosition;
		}
	}

}
