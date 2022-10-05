package com.github.elenterius.biomancy.world.item;

import com.github.elenterius.biomancy.styles.ClientTextUtil;
import com.github.elenterius.biomancy.styles.HrTooltipComponent;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EssenceItem extends Item implements IBiomancyItem {

	public static final String NBT_KEY_ENTITY_TYPE = "EntityType";
	public static final String KEY_DATA = "EssenceData";
	public static final String NBT_KEY_COLORS = "Colors";
	public static final String KEY_ENTITY_NAME = "Name";
	public static final String KEY_IS_PLAYER = "IsPlayer";
	public static final String KEY_ENTITY_UUID = "EntityUUID";

	public EssenceItem(Properties properties) {
		super(properties);
	}

	public boolean isValidSamplingTarget(LivingEntity livingEntity) {
		return true;
	}

	@Nullable
	public <T extends Entity> EntityType<T> getEntityType(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag().getCompound(KEY_DATA);
		EntityType<?> value = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(tag.getString(NBT_KEY_ENTITY_TYPE)));
		//noinspection unchecked
		return value != null ? (EntityType<T>) value : null;
	}

	public boolean setEntityType(ItemStack stack, LivingEntity livingEntity) {
		CompoundTag data = getDataFromEntity(livingEntity);
		if (data != null) {
			CompoundTag tag = stack.getOrCreateTag();

			if (livingEntity instanceof Player player) {
				UUID uuid = player.getUUID();
				int background = (int) (uuid.getMostSignificantBits() & 0xffffff);
				int highlight = (int) (uuid.getLeastSignificantBits() & 0xffffff);
				tag.putIntArray(NBT_KEY_COLORS, new int[]{background, highlight});
			}
			else {
				SpawnEggItem spawnEggItem = ForgeSpawnEggItem.fromEntityType(livingEntity.getType());
				if (spawnEggItem != null) {
					int background = spawnEggItem.getColor(0);
					int highlight = spawnEggItem.getColor(1);
					tag.putIntArray(NBT_KEY_COLORS, new int[]{background, highlight});
				}
				else {
					//handle mobs that don't have spawn eggs
					ResourceLocation key = EntityType.getKey(livingEntity.getType());
					int background = key.hashCode() & 0xffffff;
					int highlight = key.toString().hashCode() & 0xffffff;
					tag.putIntArray(NBT_KEY_COLORS, new int[]{background, highlight});
				}
			}

			tag.put(KEY_DATA, data);
			return true;
		}
		return false;
	}

	public int getColor(ItemStack stack, int tintIndex) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(NBT_KEY_COLORS)) {
			int[] colors = tag.getIntArray(NBT_KEY_COLORS);
			return tintIndex == 0 ? colors[0] : colors[1];
		}
		return -1;
	}

	@Nullable
	private CompoundTag getDataFromEntity(LivingEntity target) {
		boolean isValidEntity = target.isAlive() && isValidSamplingTarget(target);
		return isValidEntity ? getDataFromEntityUnchecked(target) : null;
	}

	@Nullable
	private CompoundTag getDataFromEntityUnchecked(LivingEntity target) {
		CompoundTag tag = new CompoundTag();
		String typeId = target instanceof Player player ? getPlayerTypeId(player) : target.getEncodeId();
		if (typeId != null) {
			tag.putString(NBT_KEY_ENTITY_TYPE, typeId);
			tag.putString(KEY_ENTITY_NAME, target.getType().getDescriptionId());
			tag.putBoolean(KEY_IS_PLAYER, target instanceof Player);
			tag.putUUID(KEY_ENTITY_UUID, target.getUUID());
			return tag;
		}
		return null;
	}

	private String getPlayerTypeId(Player player) {
		return EntityType.getKey(player.getType()).toString();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ClientTextUtil.getItemInfoTooltip(stack.getItem()));

		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(KEY_DATA)) {
			CompoundTag subTag = tag.getCompound(KEY_DATA);
			if (subTag.getBoolean(KEY_IS_PLAYER)) {
				String name = ClientTextUtil.tryToGetPlayerNameOnClientSide(subTag.getUUID(KEY_ENTITY_UUID));
				tooltip.add(new TextComponent(name).withStyle(ChatFormatting.GRAY));
			}
			tooltip.add(TextComponentUtil.getTooltipText("contains_unique_dna").withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new HrTooltipComponent());
	}

	@Override
	public Component getName(ItemStack stack) {
		MutableComponent entityName = getEntityName(stack);
		Component itemName = super.getName(stack);
		if (entityName != null) {
			return entityName.append(" ").append(itemName);
		}
		return itemName;
	}

	@Nullable
	private MutableComponent getEntityName(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(KEY_DATA)) {
			CompoundTag subTag = tag.getCompound(KEY_DATA);
			return new TranslatableComponent(subTag.getString(KEY_ENTITY_NAME));
		}
		return null;
	}

}
