package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.network.ISyncableAnimation;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.world.block.CreatorBlock;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;

public class CreatorBlockEntity extends SimpleSyncedBlockEntity implements IAnimatable, ISyncableAnimation {

	public static final int MAX_SLOTS = 6;
	public static final int DURATION = 20 * 4; //in ticks
	public static final String FILL_LEVEL_KEY = "FillLevel";
	public static final String INVENTORY_KEY = "Inventory";

	private long ticks;
	private final ItemHandler inv = new ItemHandler(MAX_SLOTS);
	private byte fillLevel = 0;

	private final AnimationFactory animationFactory = new AnimationFactory(this);
	private boolean playAttackAnimation = false;

	public CreatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CREATOR.get(), pos, state);
	}

	public boolean insertItem(ItemStack stack) {
		if (level == null || level.isClientSide() || stack.isEmpty() || !inv.hasEmptySlots()) return false;

		ItemStack prevStack = stack.copy();

		for (int i = 0; i < inv.getSlots(); i++) {
			if (inv.isItemValid(i, stack)) {
				stack = inv.insertItem(i, stack, false);
				if (stack.isEmpty() || stack.getCount() < prevStack.getCount()) {
					syncToClient();
					visualizeIngredientValidity((ServerLevel) level, prevStack);
					return true;
				}
			}
		}

		return false;
	}

	private void visualizeIngredientValidity(ServerLevel level, ItemStack stack) {
		if (SacrificeHelper.isValidIngredient(stack)) {
			BlockPos pos = getBlockPos();
			if (SacrificeHelper.getSuccessModifier(stack) <= 0) {
				int particleCount = level.random.nextInt(1, 3);
				sendParticlesToClient(level, pos, ParticleTypes.ANGRY_VILLAGER, particleCount);
			}
			int particleCount = level.random.nextInt(6, 9);
			sendParticlesToClient(level, pos, ParticleTypes.HAPPY_VILLAGER, particleCount);
		}
		else {
			int particleCount = level.random.nextInt(2, 4);
			sendParticlesToClient(level, getBlockPos(), ParticleTypes.ANGRY_VILLAGER, particleCount);
		}
	}

	private void sendParticlesToClient(ServerLevel level, BlockPos pos, ParticleOptions particleOptions, int particleCount) {
		level.sendParticles(particleOptions, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, particleCount, 0.5d, 0.25d, 0.5d, 0);
	}

	public boolean isFull() {
		return getFillLevel() == getMaxFillLevel();
	}

	public int getFillLevel() {
		return fillLevel;
	}

	public int getMaxFillLevel() {
		return MAX_SLOTS;
	}

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
		SacrificeHelper sacrificeHelper = new SacrificeHelper(inv.getItems(), level.random);

		//clear inventory
		inv.clearAllSlots();
		fillLevel = 0;
		setChanged();
		syncToClient();

		BlockPos pos = getBlockPos();
		if (sacrificeHelper.isSacrificeSuccessful()) {
			spawnMob(level, pos, sacrificeHelper);
			level.playSound(null, pos, SoundEvents.PLAYER_BURP, SoundSource.BLOCKS, 1f, level.random.nextFloat(0.25f, 0.75f));
			level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
		}
		else {
			attackAOE(level, pos);
			level.playSound(null, pos, SoundEvents.GOAT_SCREAMING_RAM_IMPACT, SoundSource.BLOCKS, 1f, 0.5f);
		}
	}

	public void spawnMob(ServerLevel level, BlockPos pos, SacrificeHelper sacrificeHelper) {
		FleshBlob fleshBlob = ModEntityTypes.FLESH_BLOB.get().create(level);
		if (fleshBlob != null) {
			float yaw = CreatorBlock.getYRotation(getBlockState());
			fleshBlob.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, yaw, 0);
			fleshBlob.yHeadRot = fleshBlob.getYRot();
			fleshBlob.setHostile(sacrificeHelper.isFleshBlobHostile());
			fleshBlob.setTumors(sacrificeHelper.getTumorFactor());
			level.addFreshEntity(fleshBlob);
		}
//			OculusObserverEntity entity = ModEntityTypes.OCULUS_OBSERVER.get().create(worldIn);
//			if (entity != null) {
//				entity.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, 0, 0);
//				worldIn.addFreshEntity(entity);
//			}
	}

	public void attackAOE() {
		if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
			attackAOE(serverLevel, worldPosition);
		}
	}

	protected void attackAOE(ServerLevel level, BlockPos pos) {
		ModNetworkHandler.sendAnimationToClients(this, 0, 0);

		float maxAttackDistance = 1.5f;
		float maxAttackDistanceSqr = maxAttackDistance * maxAttackDistance;
		Vec3 origin = Vec3.atCenterOf(pos);
		AABB aabb = AABB.ofSize(origin, maxAttackDistance * 2, maxAttackDistance * 2, maxAttackDistance * 2);

		List<Entity> victims = level.getEntities((Entity) null, aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
		for (Entity entity : victims) {
			float distSqr = (float) entity.distanceToSqr(origin);
			float pct = distSqr / maxAttackDistanceSqr;
			float damage = Mth.clamp(8f * (1 - pct), 0.5f, 8f); //linear damage falloff
			entity.hurt(ModDamageSources.CREATOR_SPIKES, damage);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putByte(FILL_LEVEL_KEY, (byte) inv.countUsedSlots());
		tag.put(INVENTORY_KEY, inv.serializeNBT());
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		tag.putByte(FILL_LEVEL_KEY, (byte) inv.countUsedSlots());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(FILL_LEVEL_KEY)) {
			fillLevel = tag.getByte(FILL_LEVEL_KEY);
		}
		if (tag.contains(INVENTORY_KEY)) {
			inv.deserializeNBT(tag.getCompound(INVENTORY_KEY));
			fillLevel = (byte) inv.countUsedSlots();
		}
	}

	@Override
	public void onAnimationSync(int id, int data) {
		startAttackAnimation();
	}

	public void startAttackAnimation() {
		playAttackAnimation = true;
	}

	public void stopAttackAnimation() {
		playAttackAnimation = false;
	}

	private PlayState handleAnim(AnimationEvent<CreatorBlockEntity> event) {
//		if (fillLevel >= getMaxFillLevel()) {
//			event.getController().setAnimation(new AnimationBuilder().addAnimation("creator.anim.work"));
//		}

		if (playAttackAnimation) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("creator.anim.spike"));
			if (event.getController().getAnimationState() != AnimationState.Stopped) return PlayState.CONTINUE;
			stopAttackAnimation();
		}

		if (event.getController().getAnimationState() == AnimationState.Stopped) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("creator.anim.idle"));
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::handleAnim));
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
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return getStackInSlot(slot).isEmpty();
		}

		@Override
		protected void onContentsChanged(int slot) {
			fillLevel = (byte) countUsedSlots();
			setChanged();
		}

		public boolean hasEmptySlots() {
			for (ItemStack stack : stacks) if (stack.isEmpty()) return true;
			return false;
		}

		public int countUsedSlots() {
			int count = 0;
			for (ItemStack stack : stacks) if (!stack.isEmpty()) count++;
			return count;
		}

		NonNullList<ItemStack> getItems() {
			return stacks;
		}

	}

}
