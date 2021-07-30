package com.github.elenterius.biomancy.entity.mutation;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import com.github.elenterius.biomancy.init.ModItems;
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

	private static final DataParameter<Byte> SLURRY_AMOUNT = EntityDataManager.createKey(NutrientSlurryCowEntity.class, DataSerializers.BYTE);
	private int eatGrassTimer;
	private EatGrassGoal eatGrassGoal;

	public NutrientSlurryCowEntity(EntityType<? extends CowEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(SLURRY_AMOUNT, (byte) 0);
	}

	@Override
	protected void registerGoals() {
		eatGrassGoal = new EatGrassGoal(this);
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new PanicGoal(this, 2d));
		goalSelector.addGoal(2, new BreedGoal(this, 1d));
		goalSelector.addGoal(3, new TemptGoal(this, 1.25d, Ingredient.fromItems(Items.WHEAT), false));
		goalSelector.addGoal(4, new FollowParentGoal(this, 1.25d));
		goalSelector.addGoal(5, eatGrassGoal);
		goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1d));
		goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6f));
		goalSelector.addGoal(8, new LookRandomlyGoal(this));
	}

	@Override
	protected void updateAITasks() {
		eatGrassTimer = eatGrassGoal.getEatingGrassTimer();
		super.updateAITasks();
	}

	@Override
	public void livingTick() {
		if (world.isRemote) eatGrassTimer = Math.max(0, eatGrassTimer - 1);
		super.livingTick();
	}

	@Override
	public NutrientSlurryCowEntity createChild(ServerWorld world, AgeableEntity mate) {
		return ModEntityTypes.NUTRIENT_SLURRY_COW.get().create(world);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("SlurryAmount", dataManager.get(SLURRY_AMOUNT));
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		dataManager.set(SLURRY_AMOUNT, compound.getByte("SlurryAmount"));
	}

	@Override
	public void eatGrassBonus() {
		if (isChild()) addGrowth(60);
		else growSlurry();
	}

	public void growSlurry() {
		byte amount = dataManager.get(SLURRY_AMOUNT);
		if (amount <= 90) dataManager.set(SLURRY_AMOUNT, (byte) (amount + 10));
	}

	public int getSlurryAmount() {
		return dataManager.get(SLURRY_AMOUNT);
	}

	public void reduceSlurry() {
		byte amount = dataManager.get(SLURRY_AMOUNT);
		dataManager.set(SLURRY_AMOUNT, (byte) (Math.max(0, amount - 100)));
	}

	@Override
	public ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if (stack.getItem() == Items.BUCKET) {
			if (!isChild() && getSlurryAmount() >= 100) {
				playerIn.playSound(SoundEvents.ENTITY_COW_MILK, 1f, 1f);
				ItemStack filledBucket = DrinkHelper.fill(stack, playerIn, ModItems.NUTRIENT_SLURRY_BUCKET.get().getDefaultInstance());
				playerIn.setHeldItem(hand, filledBucket);
				reduceSlurry();
				return ActionResultType.func_233537_a_(world.isRemote);
			}
			else return ActionResultType.PASS;
		}

		return super.getEntityInteractionResult(playerIn, hand);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 0xA) eatGrassTimer = 40;
		else super.handleStatusUpdate(id);
	}

	@OnlyIn(Dist.CLIENT)
	public float getHeadRotationPointY(float partialTick) {
		if (eatGrassTimer <= 0) return 0;
		else if (eatGrassTimer >= 4 && eatGrassTimer <= 36) return 1f;
		else return eatGrassTimer < 4 ? ((float) eatGrassTimer - partialTick) / 4f : -((float) (eatGrassTimer - 40) - partialTick) / 4f;
	}

	@OnlyIn(Dist.CLIENT)
	public float getHeadRotationAngleX(float partialTick) {
		if (eatGrassTimer > 4 && eatGrassTimer <= 36) {
			float f = ((float) (eatGrassTimer - 4) - partialTick) / 32f;
			return ((float) Math.PI / 5f) + 0.21991149f * MathHelper.sin(f * 28.7f);
		}
		else return eatGrassTimer > 0 ? ((float) Math.PI / 5f) : rotationPitch * ((float) Math.PI / 180f);
	}
}
