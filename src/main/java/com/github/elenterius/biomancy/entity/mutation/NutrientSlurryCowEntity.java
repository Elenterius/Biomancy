package com.github.elenterius.biomancy.entity.mutation;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
import com.google.common.primitives.UnsignedBytes;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NutrientSlurryCowEntity extends CowEntity {

	public static final int BUCKET_FILLING_COST = 100;

	private static final DataParameter<Byte> SLURRY_AMOUNT = EntityDataManager.defineId(NutrientSlurryCowEntity.class, DataSerializers.BYTE);

	private int eatGrassTimer;
	private EatGrassGoal eatGrassGoal;

	public NutrientSlurryCowEntity(EntityType<? extends CowEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(SLURRY_AMOUNT, (byte) 0);
	}

	@Override
	protected void registerGoals() {
		eatGrassGoal = new EatGrassGoal(this);
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new PanicGoal(this, 2d));
		goalSelector.addGoal(2, new BreedGoal(this, 1d));
		goalSelector.addGoal(3, new TemptGoal(this, 1.25d, Ingredient.of(Items.WHEAT), false));
		goalSelector.addGoal(4, new FollowParentGoal(this, 1.25d));
		goalSelector.addGoal(5, eatGrassGoal);
		goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1d));
		goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6f));
		goalSelector.addGoal(8, new LookRandomlyGoal(this));
	}

	@Override
	protected void customServerAiStep() {
		eatGrassTimer = eatGrassGoal.getEatAnimationTick();
		super.customServerAiStep();
	}

	@Override
	public void aiStep() {
		if (level.isClientSide) eatGrassTimer = Math.max(0, eatGrassTimer - 1);
		super.aiStep();
	}

	@Override
	public NutrientSlurryCowEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
		return ModEntityTypes.NUTRIENT_SLURRY_COW.get().create(world);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("SlurryAmount", entityData.get(SLURRY_AMOUNT));
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		entityData.set(SLURRY_AMOUNT, compound.getByte("SlurryAmount"));
	}

	@Override
	public void ate() {
		if (isBaby()) ageUp(60);
		else growSlurry();
	}

	public void growSlurry() {
		int oldAmount = getSlurryAmount();
		if (oldAmount < 255) {
			setSlurryAmount(oldAmount + 20); // 5 * 20 = 1 Bucket of Nutrient Slurry
		}
	}

	public int getSlurryAmount() {
		return UnsignedBytes.toInt(entityData.get(SLURRY_AMOUNT));
	}

	public void setSlurryAmount(int amount) {
		entityData.set(SLURRY_AMOUNT, UnsignedBytes.saturatedCast(amount));
	}

	public void reduceSlurry() {
		setSlurryAmount(getSlurryAmount() - BUCKET_FILLING_COST);
	}

	@Override
	public ActionResultType mobInteract(PlayerEntity playerIn, Hand hand) {
		ItemStack stack = playerIn.getItemInHand(hand);
		if (stack.getItem() == Items.BUCKET) {
			if (!isBaby() && getSlurryAmount() >= BUCKET_FILLING_COST) {
				playerIn.playSound(SoundEvents.COW_MILK, 1f, 1f);
				ItemStack filledBucket = DrinkHelper.createFilledResult(stack, playerIn, ModItems.NUTRIENT_SLURRY_BUCKET.get().getDefaultInstance());
				playerIn.setItemInHand(hand, filledBucket);
				reduceSlurry();
				return ActionResultType.sidedSuccess(level.isClientSide);
			}
			else return ActionResultType.PASS;
		}

		return super.mobInteract(playerIn, hand);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleEntityEvent(byte id) {
		if (id == 0xA) eatGrassTimer = 40;
		else super.handleEntityEvent(id);
	}

	@OnlyIn(Dist.CLIENT)
	public float getHeadRotationPointY(float partialTick) {
		if (eatGrassTimer <= 0) return 0;
		else if (eatGrassTimer >= 4 && eatGrassTimer <= 36) return 1f;
		else return (eatGrassTimer < 4) ? (eatGrassTimer - partialTick) / 4f : -(eatGrassTimer - 40f - partialTick) / 4f;
	}

	@OnlyIn(Dist.CLIENT)
	public float getHeadRotationAngleX(float partialTick) {
		if (eatGrassTimer > 4 && eatGrassTimer <= 36) {
			float f = (eatGrassTimer - 4f - partialTick) / 32f;
			return ((float) Math.PI / 5f) + 0.21991149f * MathHelper.sin(f * 28.7f);
		}
		else return eatGrassTimer > 0 ? (float) Math.PI / 5f : xRot * ((float) Math.PI / 180f);
	}
}
