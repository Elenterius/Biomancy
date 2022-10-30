package com.github.elenterius.biomancy.world.block.entity;

import com.github.elenterius.biomancy.init.ModBlockEntities;
import com.github.elenterius.biomancy.init.ModDamageSources;
import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.network.ISyncableAnimation;
import com.github.elenterius.biomancy.network.ModNetworkHandler;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.SacrificeHandler;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.world.block.CreatorBlock;
import com.github.elenterius.biomancy.world.entity.fleshblob.FleshBlob;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;

public class CreatorBlockEntity extends SimpleSyncedBlockEntity implements IAnimatable, ISyncableAnimation {

	public static final int DURATION = 20 * 4; //in ticks

	public static final String SACRIFICE_SYNC_KEY = "SyncSacrificeHandler";
	public static final String SACRIFICE_KEY = "SacrificeHandler";

	private long ticks;
	private final SacrificeHandler sacrificeHandler = new SacrificeHandler();

	private final AnimationFactory animationFactory = new AnimationFactory(this);
	private boolean playAttackAnimation = false;

	public CreatorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CREATOR.get(), pos, state);
	}

	public boolean insertItem(ItemStack stack) {
		if (level == null || level.isClientSide() || stack.isEmpty()) return false;
		if (sacrificeHandler.isFull()) return false;

		ItemStack prevStack = stack.copy();
		if (sacrificeHandler.addItem(stack)) {
			setChanged();
			syncToClient();
			visualizeIngredientValidity((ServerLevel) level, prevStack);
			return true;
		}
		return false;
	}

	private void visualizeIngredientValidity(ServerLevel level, ItemStack stack) {
		if (sacrificeHandler.isValidIngredient(stack)) {
			BlockPos pos = getBlockPos();
			if (sacrificeHandler.getSuccessModifier(stack) <= 0) {
				int particleCount = level.random.nextInt(1, 3);
				sendParticlesToClient(level, pos, ParticleTypes.ANGRY_VILLAGER, particleCount);
			}
			int particleCount = level.random.nextInt(6, 9);
			SimpleParticleType particleType = sacrificeHandler.isLifeEnergySource(stack) ? ParticleTypes.GLOW : ParticleTypes.HAPPY_VILLAGER;
			sendParticlesToClient(level, pos, particleType, particleCount);
		} else {
			int particleCount = level.random.nextInt(2, 4);
			sendParticlesToClient(level, getBlockPos(), ParticleTypes.ANGRY_VILLAGER, particleCount);
		}
	}

	private void sendParticlesToClient(ServerLevel level, BlockPos pos, ParticleOptions particleOptions, int particleCount) {
		level.sendParticles(particleOptions, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, particleCount, 0.5d, 0.25d, 0.5d, 0);
	}

	public boolean isFull() {
		return sacrificeHandler.isFull();
	}

	public float getBiomassPct() {
		return sacrificeHandler.getBiomassPct();
	}

	public float getLifeEnergyPct() {
		return sacrificeHandler.getLifeEnergyPct();
	}

	public boolean hasModifiers() {
		return sacrificeHandler.hasModifiers();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CreatorBlockEntity creator) {
		if (creator.isFull()) {
			creator.ticks++;
			if (creator.ticks > DURATION) {
				creator.onSacrifice((ServerLevel) level);
				creator.ticks = 0;
			}
		}
	}

	public void onSacrifice(ServerLevel level) {
		BlockPos pos = getBlockPos();
		if (level.random.nextFloat() < sacrificeHandler.getSuccessChance()) {
			spawnMob(level, pos, sacrificeHandler);
			SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CREATOR_SPAWN_MOB);
			level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, 1, 0, 0, 0, 0);
		} else {
			if (sacrificeHandler.getSuccessChance() > -9999) {
				attackAOE(level, pos);
				SoundUtil.broadcastBlockSound(level, pos, ModSoundEvents.CREATOR_SPIKE_ATTACK);
			} else {
				if (level.canSeeSky(pos.above())) {
					LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
					if (lightningBolt != null) {
						lightningBolt.moveTo(Vec3.atBottomCenterOf(pos));
						level.addFreshEntity(lightningBolt);
					}
				}

				SoundUtil.broadcastBlockSound(level, pos, SoundEvents.TRIDENT_THUNDER, 5f, 0.9f);
				level.players().forEach(player -> player.sendMessage(new TextComponent("How dare you do this... I am watching you!").withStyle(TextStyles.MAYKR_RUNES_RED), Util.NIL_UUID));
			}
		}

		resetState();
	}

	private void resetState() {
		sacrificeHandler.reset();
		setChanged();
		syncToClient();
	}

	public void spawnMob(ServerLevel level, BlockPos pos, SacrificeHandler sacrificeHandler) {
		FleshBlob fleshBlob = ModEntityTypes.FLESH_BLOB.get().create(level);
		if (fleshBlob != null) {
			float yaw = CreatorBlock.getYRotation(getBlockState());
			fleshBlob.moveTo(pos.getX() + 0.5f, pos.getY() + 4f / 16f, pos.getZ() + 0.5f, yaw, 0);
			fleshBlob.yHeadRot = fleshBlob.getYRot();
			fleshBlob.setHostile(level.random.nextFloat() < sacrificeHandler.getHostileChance());
			fleshBlob.setTumors(sacrificeHandler.getTumorFactor());
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
		tag.put(SACRIFICE_KEY, sacrificeHandler.serializeNBT());
	}

	@Override
	protected void saveForSyncToClient(CompoundTag tag) {
		tag.put(SACRIFICE_SYNC_KEY, sacrificeHandler.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains(SACRIFICE_KEY)) {
			sacrificeHandler.deserializeNBT(tag.getCompound(SACRIFICE_KEY));
		} else if (tag.contains(SACRIFICE_SYNC_KEY)) {
			sacrificeHandler.deserializeNBT(tag.getCompound(SACRIFICE_SYNC_KEY));
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

}
