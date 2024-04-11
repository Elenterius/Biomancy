package com.github.elenterius.biomancy.item;

import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModItems;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EssenceItem extends Item implements ItemTooltipStyleProvider {

	public static final String ENTITY_TYPE_KEY = "entity_type";
	public static final String ESSENCE_DATA_KEY = "essence_data";
	public static final String COLORS_KEY = "colors";
	public static final String ENTITY_NAME_KEY = "name";
	public static final String ENTITY_UUID_KEY = "entity_uuid";
	protected static final String ESSENCE_TIER_KEY = "essence_tier";

	public EssenceItem(Properties properties) {
		super(properties);
	}

	public static ItemStack fromEntity(LivingEntity livingEntity, int surgicalPrecisionLevel, int lootingLevel) {
		int count = 1 + livingEntity.getRandom().nextInt(0, 1 + lootingLevel);
		int essenceTier = 1 + livingEntity.getRandom().nextInt(0, 1 + surgicalPrecisionLevel);
		ItemStack stack = new ItemStack(ModItems.ESSENCE.get(), count);

		if (ModItems.ESSENCE.get().setEssenceData(stack, Mth.clamp(essenceTier, 1, 3), livingEntity)) {
			return stack;
		}

		return ItemStack.EMPTY;
	}

	public static ItemStack fromEntityType(EntityType<?> entityType, int essenceTier) {
		ItemStack stack = new ItemStack(ModItems.ESSENCE.get(), 1);

		int[] colors = getEssenceColors(entityType);

		if (ModItems.ESSENCE.get().setEssenceData(stack, Mth.clamp(essenceTier, 1, 3), entityType, null, colors)) {
			return stack;
		}

		return ItemStack.EMPTY;
	}

	public static int[] getEssenceColors(LivingEntity livingEntity, int tier) {
		if (livingEntity instanceof Player player) {
			if (tier < 3) return getEssenceColors(player.getType());

			UUID uuid = player.getUUID();
			int background = (int) (uuid.getMostSignificantBits() & 0xffffff);
			int highlight = (int) (uuid.getLeastSignificantBits() & 0xffffff);
			return new int[]{background, highlight};
		}
		else {
			return getEssenceColors(livingEntity.getType());
		}
	}

	public static int[] getEssenceColors(EntityType<?> entityType) {
		if (entityType == EntityType.PLAYER) {
			return new int[]{0x00AFAF, 0x463AA5}; //steve colors
		}

		SpawnEggItem spawnEggItem = ForgeSpawnEggItem.fromEntityType(entityType);
		if (spawnEggItem != null) {
			int background = spawnEggItem.getColor(0);
			int highlight = spawnEggItem.getColor(1);
			return new int[]{background, highlight};
		}
		else {
			//handle mobs that don't have spawn eggs
			ResourceLocation key = EntityType.getKey(entityType);
			int background = key.hashCode() & 0xffffff;
			int highlight = key.toString().hashCode() & 0xffffff;
			return new int[]{background, highlight};
		}
	}

	public boolean isValidSamplingTarget(LivingEntity livingEntity) {
		return true;
	}

	public boolean setEssenceData(ItemStack stack, int tier, LivingEntity livingEntity) {
		boolean isValidEntity = livingEntity.isAlive() && isValidSamplingTarget(livingEntity);
		if (!isValidEntity) return false;

		UUID entityUUID = tier >= 3 ? livingEntity.getUUID() : null;
		int[] colors = getEssenceColors(livingEntity, tier);

		return setEssenceData(stack, tier, livingEntity.getType(), entityUUID, colors);
	}

	public boolean setEssenceData(ItemStack stack, int tier, EntityType<?> entityType, @Nullable UUID entityUUID, int[] colors) {
		if (entityType != EntityType.PLAYER && !entityType.canSerialize()) return false;

		ResourceLocation entityTypeId = EntityType.getKey(entityType);

		CompoundTag essenceTag = new CompoundTag();
		essenceTag.putString(ENTITY_TYPE_KEY, entityTypeId.toString());
		essenceTag.putString(ENTITY_NAME_KEY, entityType.getDescriptionId());

		if (entityUUID != null) {
			essenceTag.putUUID(ENTITY_UUID_KEY, entityUUID);
		}

		CompoundTag tag = stack.getOrCreateTag();
		tag.put(ESSENCE_DATA_KEY, essenceTag);
		tag.putIntArray(COLORS_KEY, colors);
		tag.putInt(ESSENCE_TIER_KEY, tier);
		return true;

	}

	public boolean isValid(ItemStack stack) {
		return getEntityType(stack).isPresent();
	}

	public Optional<EntityType<?>> getEntityType(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag().getCompound(ESSENCE_DATA_KEY);
		return EntityType.byString(tag.getString(ENTITY_TYPE_KEY));
	}

	public Optional<UUID> getEntityUUID(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag().getCompound(ESSENCE_DATA_KEY);
		if (tag.hasUUID(ENTITY_UUID_KEY)) {
			return Optional.of(tag.getUUID(ENTITY_UUID_KEY));
		}
		return Optional.empty();
	}

	public int getColor(ItemStack stack, int tintIndex) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(COLORS_KEY, Tag.TAG_INT_ARRAY)) {
			int[] colors = tag.getIntArray(COLORS_KEY);
			return tintIndex == 0 ? colors[0] : colors[1];
		}
		return -1;
	}

	public int[] getColors(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(COLORS_KEY, Tag.TAG_INT_ARRAY)) {
			return tag.getIntArray(COLORS_KEY);
		}

		return new int[]{0xffff_ffff, 0xffff_ffff};
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack));

		tooltip.add(ComponentUtil.emptyLine());

		CompoundTag compoundTag = stack.getOrCreateTag();

		if (compoundTag.contains(ESSENCE_DATA_KEY)) {
			CompoundTag tag = compoundTag.getCompound(ESSENCE_DATA_KEY);

			if (tag.hasUUID(ENTITY_UUID_KEY)) {
				UUID entityUUID = tag.getUUID(ENTITY_UUID_KEY);

				if (tag.getString(ENTITY_NAME_KEY).equals(EntityType.PLAYER.getDescriptionId())) {
					String name = ClientTextUtil.tryToGetPlayerNameOnClientSide(entityUUID);
					tooltip.add(ComponentUtil.literal("Player: " + name).withStyle(ChatFormatting.GRAY));
				}
				else {
					tooltip.add(ComponentUtil.literal("UUID: " + entityUUID).withStyle(ChatFormatting.GRAY));
				}

				tooltip.add(ComponentUtil.emptyLine());
			}
		}

		int tier = compoundTag.getInt(ESSENCE_TIER_KEY);
		tooltip.add(ComponentUtil.literal("Tier: " + tier));
	}

	@Override
	public Component getName(ItemStack stack) {
		CompoundTag compoundTag = stack.getOrCreateTag();

		if (!compoundTag.contains(ESSENCE_DATA_KEY)) {
			return Component.translatable(getDescriptionId(stack));
		}

		CompoundTag tag = compoundTag.getCompound(ESSENCE_DATA_KEY);
		MutableComponent entityName = ComponentUtil.translatable(tag.getString(ENTITY_NAME_KEY));

		if (tag.hasUUID(ENTITY_UUID_KEY)) {
			return Component.translatable(getDescriptionId(stack) + ".unique_mob", entityName);
		}

		return Component.translatable(getDescriptionId(stack) + ".mob", entityName);
	}

}
