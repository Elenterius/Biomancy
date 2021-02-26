package com.github.elenterius.biomancy.tileentity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.FleshChestBlock;
import com.github.elenterius.biomancy.init.ModTileEntityTypes;
import com.github.elenterius.biomancy.inventory.FleshChestContainer;
import com.github.elenterius.biomancy.inventory.SimpleInvContents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(value = Dist.CLIENT, _interface = IChestLid.class)
public class FleshChestTileEntity extends OwnableTileEntity implements INamedContainerProvider, IChestLid, ITickableTileEntity {

	public static final int INV_SLOTS_COUNT = 6 * 9;

	private final SimpleInvContents invContents;

	protected float lidAngle;
	protected float prevLidAngle;
	protected int numPlayersUsing;
	private int ticks = 0;

	public FleshChestTileEntity() {
		super(ModTileEntityTypes.FLESH_CHEST.get());
		invContents = SimpleInvContents.createServerContents(INV_SLOTS_COUNT, SimpleInvContents.ISHandlerType.NON_NESTING, this::canPlayerOpenInv, this::markDirty);
		invContents.setOpenInventoryConsumer(this::onOpenInventory);
		invContents.setCloseInventoryConsumer(this::onCloseInventory);
	}

	public static void playSound(World worldIn, SoundEvent soundIn, BlockPos posIn) {
		worldIn.playSound(null, posIn.getX() + 0.5d, posIn.getY() + 0.5d, posIn.getZ() + 0.5d, soundIn, SoundCategory.BLOCKS, 0.5f, worldIn.rand.nextFloat() * 0.1f + 0.9f);
	}

	public static int calculatePlayersUsing(World worldIn, float x, float y, float z) {
		return calculatePlayersUsing(worldIn, x, y, z, 5f);
	}

	public static int calculatePlayersUsing(World worldIn, float x, float y, float z, float range) {
		int i = 0;
		for (PlayerEntity player : worldIn.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(x - range, y - range, z - range, x + range + 1f, y + range + 1f, z + range + 1f))) {
			if (player.openContainer instanceof ChestContainer) {
				i++;
			}
		}
		return i;
	}

	@Nullable
	@Override
	public Container createMenu(int screenId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return FleshChestContainer.createServerContainer(screenId, playerInventory, invContents);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public float getLidAngle(float partialTicks) {
		return MathHelper.lerp(partialTicks, prevLidAngle, lidAngle);
	}

	public void onOpenInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (numPlayersUsing < 0) {
				numPlayersUsing = 0;
			}

			++numPlayersUsing;
			sendToClient();
		}
	}

	public void onCloseInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			--numPlayersUsing;
			sendToClient();
		}
	}

	@Override
	public boolean receiveClientEvent(int id, int data) {
		if (id == 1) {
			numPlayersUsing = data;
			return true;
		}
		else {
			return super.receiveClientEvent(id, data);
		}
	}

	protected void sendToClient() {
		if (world != null && !world.isRemote) {
			Block block = getBlockState().getBlock();
			if (block instanceof FleshChestBlock) {
				world.addBlockEvent(pos, block, 1, numPlayersUsing);
			}
		}
	}

	@Override
	public void tick() {
		if (world == null) return;

		if (!world.isRemote) {
			ticks++;
			if (ticks % 200 == 0) {
				numPlayersUsing = calculatePlayersUsing(world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		prevLidAngle = lidAngle;
		if (numPlayersUsing > 0 && lidAngle == 0f) {
			playSound(world, SoundEvents.BLOCK_CHEST_OPEN, pos);
		}

		if (numPlayersUsing == 0 && lidAngle > 0f || numPlayersUsing > 0 && lidAngle < 1f) {
			float prevLidAngle = lidAngle;
			lidAngle += numPlayersUsing > 0 ? 0.1f : -0.1f;
			lidAngle = MathHelper.clamp(lidAngle, 0f, 1f);
			if (lidAngle < 0.5f && prevLidAngle >= 0.5f) {
				playSound(world, SoundEvents.BLOCK_CHEST_CLOSE, pos);
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if (!invContents.isEmpty()) nbt.put("Inventory", invContents.serializeNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		invContents.deserializeNBT(nbt.getCompound("Inventory"));
		if (invContents.getSizeInventory() != INV_SLOTS_COUNT) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected count.");
		}
	}

	@Override
	public CompoundNBT writeToItemBlockEntityTag(CompoundNBT nbt) {
		super.writeToItemBlockEntityTag(nbt);
		if (!invContents.isEmpty()) nbt.put("Inventory", invContents.serializeNBT());
		return nbt;
	}

	@Override
	public void invalidateCaps() {
		invContents.getOptionalItemStackHandler().invalidate();
		super.invalidateCaps();
	}

	@Nullable
	public IInventory getInventory() {
		return !removed ? invContents : null;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return invContents.getOptionalItemStackHandler().cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return BiomancyMod.getTranslationText("container", "bioflesh_chest");
	}

}