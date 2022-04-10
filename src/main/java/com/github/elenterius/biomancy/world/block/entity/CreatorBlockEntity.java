package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModTags;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import com.github.elenterius.biomancy.world.entity.fleshblob.TumorFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;

public class CreatorBlockEntity extends SimpleSyncedBlockEntity implements IAnimatable {

	private static final int MAX_ITEMS = 6;
	public static final int DURATION = 20 * 4; //in ticks

	private final AnimationFactory animationFactory = new AnimationFactory(this);
	private final ItemHandler inv = new ItemHandler(getMaxFillLevel());
	private byte fillLevel = 0;

	public CreatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CREATOR.get(), pos, state);
	}

	public boolean insertItem(ItemStack stack) {
		if (level == null || stack.isEmpty() || inv.countNonEmptySlots() == getMaxFillLevel()) return false;

		int count = stack.getCount();
		for (int i = 0; i < inv.getSlots(); i++) {
			if (inv.isItemValid(i, stack)) {
				boolean isRawMeat = stack.is(ModTags.Items.RAW_MEATS);
				stack = inv.insertItem(i, stack, false);
				if (stack.isEmpty() || stack.getCount() < count) {
					BlockPos pos = getBlockPos();
					BlockState state = getBlockState();
					level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
					int particleCount = level.random.nextInt(6, 9);
					((ServerLevel) level).sendParticles(isRawMeat ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.ANGRY_VILLAGER, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, particleCount, 0.5d, 0.25d, 0.5d, 0);
					return true;
				}
			}
		}

		return false;
	}

	public int getFillLevel() {
		return fillLevel;
	}

	public int getMaxFillLevel() {
		return MAX_ITEMS;
	}

	private long ticks;

	public static void serverTick(Level level, BlockPos pos, BlockState state, CreatorBlockEntity creatorEntity) {
		if (creatorEntity.getFillLevel() == creatorEntity.getMaxFillLevel()) {
			creatorEntity.ticks++;
			if (creatorEntity.ticks > DURATION) {
				creatorEntity.onSacrifice((ServerLevel) level);
				creatorEntity.ticks = 0;
			}
		}
	}

	public void onSacrifice(ServerLevel level) {
		int meatItems = inv.countMeatItems();
		int uniqueMeats = inv.countUniqueMeatItems();
		float penalty = 1f - (float) meatItems / MAX_ITEMS; //other items
		float chance = (uniqueMeats < 3 ? 0.85f : 1f) - penalty;

		//clear inventory
		inv.clearAllSlots();
		fillLevel = 0;
		setChanged();

		BlockPos pos = getBlockPos();
		syncToClient();

		if (level.random.nextFloat() < chance) {
			spawnMob(level, pos, penalty, 1f - (float) uniqueMeats / MAX_ITEMS);
			level.playSound(null, pos, SoundEvents.PLAYER_BURP, SoundSource.BLOCKS, 1f, level.random.nextFloat(0.25f, 0.75f));
			level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
		}
		else {
			attackAOE(level, pos);
			level.playSound(null, pos, SoundEvents.GOAT_SCREAMING_RAM_IMPACT, SoundSource.BLOCKS, 1f, 0.5f);
		}
	}

	public void spawnMob(ServerLevel level, BlockPos pos, float penalty, float tumorFactor) {
		FleshBlob fleshBlob = ModEntityTypes.FLESH_BLOB.get().create(level);
		if (fleshBlob != null) {
			fleshBlob.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, 0, 0);
			if (penalty >= 0.4f) {
				fleshBlob.setHangry();
			}
			if (tumorFactor > 0) {
				int flags = 0;
				for (TumorFlag flag : TumorFlag.values()) {
					if (level.random.nextFloat() < tumorFactor) flags = TumorFlag.setFlag(flags, flag);
				}
				fleshBlob.setTumorFlags((byte) flags);
			}
			level.addFreshEntity(fleshBlob);
		}
//			OculusObserverEntity entity = ModEntityTypes.OCULUS_OBSERVER.get().create(worldIn);
//			if (entity != null) {
//				entity.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, 0, 0);
//				worldIn.addFreshEntity(entity);
//			}
	}

	public void attackAOE(ServerLevel level, BlockPos pos) {
		List<Entity> victims = level.getEntities((Entity) null, new AABB(pos).inflate(1d), Entity::isAlive);
		victims.forEach(entity -> entity.hurt(ModDamageSources.CREATOR_SPIKES, 4f));
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putByte("FillLevel", (byte) inv.countNonEmptySlots());
		tag.put("Inventory", inv.serializeNBT());
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		tag.putByte("FillLevel", (byte) inv.countNonEmptySlots());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("FillLevel")) {
			fillLevel = tag.getByte("FillLevel");
		}
		if (tag.contains("Inventory")) {
			inv.deserializeNBT(tag.getCompound("Inventory"));
			fillLevel = (byte) inv.countNonEmptySlots();
		}
	}

	private <E extends BlockEntity & IAnimatable> PlayState handleIdleAnim(AnimationEvent<E> event) {
		event.getController().transitionLengthTicks = 0;
		event.getController().setAnimation(new AnimationBuilder().addAnimation("creator.anim.idle", true));
		return PlayState.CONTINUE;
	}

//	private <E extends BlockEntity & IAnimatable> PlayState handleAttackAnim(AnimationEvent<E> event) {
//		event.getController().transitionLengthTicks = 0;
//		event.getController().setAnimation(new AnimationBuilder().addAnimation("creator.anim.attack", false));
//		return PlayState.STOP;
//	}

//	private <A extends IAnimatable> void onCustomInstruction(CustomInstructionKeyframeEvent<A> event) {
//		if (level == null || level.isClientSide()) return; //TODO: verify this runs on the "server" side
//
//		for (String instruction : event.instructions) {
//			if (instruction.equals("spike_attack")) {
////				attackAOE((ServerLevel) level, getBlockPos());
//			}
//		}
//	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "idle_controller", 0, this::handleIdleAnim));
//		AnimationController<CreatorBlockEntity> attack_controller = new AnimationController<>(this, "attack_controller", 0, this::handleAttackAnim);
//		attack_controller.registerCustomInstructionListener(this::onCustomInstruction);
//		data.addAnimationController(attack_controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return animationFactory;
	}

	private class ItemHandler extends ItemStackHandler {

		public ItemHandler(int slots) {
			super(slots);
		}

		public void clearAllSlots() {
			stacks.clear();
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return getStackInSlot(slot).isEmpty();
		}

		@Override
		protected void onContentsChanged(int slot) {
			fillLevel = (byte) countNonEmptySlots();
			setChanged();
		}

		public int countNonEmptySlots() {
			int count = 0;
			for (ItemStack stack : stacks) if (!stack.isEmpty()) count++;
			return count;
		}

		public int countMeatItems() {
			int count = 0;
			for (ItemStack stack : stacks) if (!stack.isEmpty() && stack.is(ModTags.Items.RAW_MEATS)) count++;
			return count;
		}

		public int countUniqueMeatItems() {
			List<ItemStack> uniqueMeats = new ArrayList<>(stacks.size());
			for (ItemStack stack : stacks) {
				if (!stack.isEmpty() && stack.is(ModTags.Items.RAW_MEATS)) {
					boolean skip = false;
					for (ItemStack uniqueMeat : uniqueMeats) {
						if (ItemHandlerHelper.canItemStacksStack(stack, uniqueMeat)) {
							skip = true;
							break;
						}
					}
					if (skip) continue;
					uniqueMeats.add(stack);
				}
			}
			return uniqueMeats.size();
		}

	}

}
