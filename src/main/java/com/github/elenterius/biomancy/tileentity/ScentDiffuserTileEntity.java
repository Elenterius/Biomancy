package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.block.ScentDiffuserBlock;
import com.github.elenterius.biomancy.init.ModEffects;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.init.ModReagents;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.HandlerBehaviors;
import com.github.elenterius.biomancy.inventory.SimpleInventory;
import com.github.elenterius.biomancy.item.ReagentItem;
import com.github.elenterius.biomancy.reagent.BloodSampleReagent;
import com.github.elenterius.biomancy.reagent.Reagent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class ScentDiffuserTileEntity extends SimpleSyncedTileEntity implements ITickableTileEntity {

	public static final Predicate<ItemStack> VALID_BILE_ITEM = stack -> stack.getItem() == ModItems.HORMONE_BILE.get();
	public static final Predicate<ItemStack> VALID_REAGENT_ITEM = stack -> stack.getItem() instanceof ReagentItem;
	public static final int BILE_COST = 2;
	public static final int DISTANCE = 24;

	private final SimpleInventory<?> reagentInv;
	private final SimpleInventory<?> bileInv;
	private final LazyOptional<CombinedInvWrapper> combinedInventory;
	private int timerTicks = -1;

	public ScentDiffuserTileEntity() {
		super(ModTileEntityTypes.SCENT_DIFFUSER_TILE.get());
		reagentInv = SimpleInventory.createServerContents(1, ish -> HandlerBehaviors.filterInput(ish, VALID_REAGENT_ITEM), player -> false, this::setChanged);
		bileInv = SimpleInventory.createServerContents(1, ish -> HandlerBehaviors.filterInput(ish, VALID_BILE_ITEM), player -> false, this::setChanged);
		combinedInventory = LazyOptional.of(() -> new CombinedInvWrapper(bileInv.getItemHandler(), reagentInv.getItemHandler()));
	}

	private static void baitEntityToPos(CreatureEntity entity, double x, double y, double z, int distance) {
		entity.getNavigation().moveTo(x, y, z, 1d);
		entity.addEffect(new EffectInstance(ModEffects.ATTRACTED.get(), 300, distance));
	}

	private static void repelEntityFromPos(CreatureEntity entity, double x, double y, double z, int distance) {
		entity.getNavigation().moveTo(x, y, z, 1d);
		entity.addEffect(new EffectInstance(ModEffects.REPULSED.get(), 300, distance));
	}

	@Override
	public void tick() {
		if (level != null && !level.isClientSide) {
			timerTicks--;
			if (timerTicks < 0) {
				BlockPos pos = getBlockPos();
				BlockState state = level.getBlockState(pos);
				boolean hasSignal = Boolean.TRUE.equals(state.getValue(ScentDiffuserBlock.POWERED));
				if (hasSignal) {
					diffuseScent(state, level, pos);
					timerTicks = 20 * 4;
				}
			}
		}
	}

	private void diffuseScent(BlockState state, World level, BlockPos pos) {
		if (level.isEmptyBlock(pos.above())) {
			Scent scent = state.getValue(ScentDiffuserBlock.SCENT);
			if (diffuseScent(level, scent, pos, DISTANCE)) {
				level.playSound(null, pos, SoundEvents.HORSE_BREATHE, SoundCategory.BLOCKS, 3f, 0.25f);
				level.blockEvent(pos, state.getBlock(), scent.id, 0);
			}
		}
	}

	private boolean diffuseScent(World level, Scent scent, BlockPos pos, int distance) {
		ItemStack bileStack = bileInv.getItem(0);
		if (bileStack.getCount() >= BILE_COST && VALID_BILE_ITEM.test(bileStack)) {
			ItemStack reagentStack = reagentInv.getItem(0);
			if (reagentStack.getCount() > 0 && VALID_REAGENT_ITEM.test(reagentStack)) {
				Reagent reagent = Reagent.deserialize(reagentStack.getOrCreateTag());
				if (reagent == ModReagents.BLOOD_SAMPLE.get()) {
					EntityType<Entity> entityType = BloodSampleReagent.getEntityType(reagentStack);
					if (entityType != null) {
						double x = pos.getX() + 0.5d;
						double y = pos.getY() + 0.5d;
						double z = pos.getZ() + 0.5d;
						List<CreatureEntity> entities = getCreatureEntities(level, entityType, x, y, z, distance);
						for (CreatureEntity entity : entities) scent.affectCreature(entity, x, y, z, distance);

						reagentStack.shrink(1);
						bileStack.shrink(BILE_COST);
						return true;
					}
				}
			}
		}
		return false;
	}

	private <T extends CreatureEntity> List<T> getCreatureEntities(World level, EntityType<?> entityType, double x, double y, double z, int distance) {
		AxisAlignedBB bb = new AxisAlignedBB(x - distance, y - distance, z - distance, x + distance, y + distance, z + distance);
		//noinspection unchecked
		return (List<T>) level.getEntities(entityType, bb, e -> e.isAlive() && e instanceof CreatureEntity);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.put("ReagentInv", reagentInv.serializeNBT());
		nbt.put("BileInv", bileInv.serializeNBT());
		nbt.putInt("Timer", timerTicks);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		reagentInv.deserializeNBT(nbt.getCompound("ReagentInv"));
		bileInv.deserializeNBT(nbt.getCompound("BileInv"));
		timerTicks = nbt.getInt("Timer");
	}

	public void dropAllInvContents(World world, BlockPos pos) {
		InventoryHelper.dropContents(world, pos, reagentInv);
		InventoryHelper.dropContents(world, pos, bileInv);
	}

	@Override
	public void invalidateCaps() {
		reagentInv.getOptionalItemStackHandler().invalidate();
		bileInv.getOptionalItemStackHandler().invalidate();
		combinedInventory.invalidate();
		super.invalidateCaps();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return combinedInventory.cast();
		}
		return super.getCapability(cap, side);
	}

	public enum Scent implements IStringSerializable {
		BAIT(0, "bait", ScentDiffuserTileEntity::baitEntityToPos),
		REPEL(1, "repel", ScentDiffuserTileEntity::repelEntityFromPos);

		public final byte id;
		private final AffectedCreatureConsumer consumer;
		private final String name;

		Scent(int id, String name, AffectedCreatureConsumer consumer) {
			this.id = (byte) id;
			this.consumer = consumer;
			this.name = name;
		}

		public Scent cycle() {
			return this == BAIT ? REPEL : BAIT;
		}

		public void affectCreature(CreatureEntity entity, double x, double y, double z, int distance) {
			consumer.accept(entity, x, y, z, distance);
		}

		@Override
		public String getSerializedName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}

		@FunctionalInterface
		public interface AffectedCreatureConsumer {
			void accept(CreatureEntity entity, double x, double y, double z, int distance);
		}
	}

}
