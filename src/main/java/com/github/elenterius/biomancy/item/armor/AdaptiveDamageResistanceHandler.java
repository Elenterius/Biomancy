package com.github.elenterius.biomancy.item.armor;

import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class AdaptiveDamageResistanceHandler {

	private static final float[] HEALTH_GATES = {0.25f, 0.45f, 0.65f, 0.8f};
	private static final float[] ADAPTATION_EFFECTIVENESS = {0.9f, 0.8f, 0.75f, 0.7f};
	private static final float ADDITIONAL_RESISTANCE = 0.4f;
	private static final float[] DAMAGE_GATE_PERCENTAGES = {0.25f, 0.2f, 0.2f, 0.15f};

	private static final Set<String> VALID_NAMESPACES = Set.of("minecraft", "forge", "c");
	private static final String TAG_PREFIX = "is_";

	public static float absorbDamage(LivingEntity livingEntity, DamageSource damageSource, float damage, AcolyteArmorItem armor, ItemStack stack) {
		List<TagKey<DamageType>> rootDamageTypes = damageSource.typeHolder().getTagKeys()
				.filter(tagKey -> VALID_NAMESPACES.contains(tagKey.location().getNamespace()) && tagKey.location().getPath().startsWith(TAG_PREFIX))
				.toList();

		if (rootDamageTypes.isEmpty()) return damage;

		DamageTypeResistanceTracker resistanceTracker = armor.getDamageTypeResistanceTracker(stack);

		for (TagKey<DamageType> damageType : rootDamageTypes) {
			resistanceTracker.count(damageType);
		}

		float healthPct = 1f - armor.getDamage(stack) / (float) armor.getMaxDamage(stack); //item durability

		for (double healthGate : HEALTH_GATES) {
			if (healthPct <= healthGate) {
				adaptToDamageType(resistanceTracker);
				break;
			}
		}

		float reducedDamage = reduceDamage(livingEntity, damageSource, damage, resistanceTracker);

		armor.saveDamageTypeResistanceTracker(resistanceTracker, stack);

		return reducedDamage;
	}

	private static float reduceDamage(LivingEntity livingEntity, DamageSource damageSource, float damage, DamageTypeResistanceTracker resistanceTracker) {

		Map<TagKey<DamageType>, Float> resistances = resistanceTracker.resistances;

		for (Map.Entry<TagKey<DamageType>, Float> resistance : resistances.entrySet()) {
			if (damageSource.is(resistance.getKey())) {
				damage *= (1 - resistance.getValue());
			}
		}

		if (resistanceTracker.hasResistanceForLargestDamageTypeCount()) {
			damage *= 1f - ADDITIONAL_RESISTANCE;
		}

		for (float damageGatePct : DAMAGE_GATE_PERCENTAGES) {
			float damageGate = livingEntity.getMaxHealth() * damageGatePct;
			if (livingEntity.getHealth() - damage <= damageGate) {
				damage = Math.max(damage - (damageGate - livingEntity.getHealth()), 0);
				break;
			}
		}

		return Math.max(damage, 0);
	}

	private static void adaptToDamageType(DamageTypeResistanceTracker damageTypeTracker) {
		int maxCount = 0;
		TagKey<DamageType> adaptedType = null;

		for (Map.Entry<TagKey<DamageType>, Integer> entry : damageTypeTracker.counter.entrySet()) {
			Integer value = entry.getValue();
			TagKey<DamageType> key = entry.getKey();

			if (value > maxCount && !damageTypeTracker.resistances.containsKey(key)) {
				maxCount = value;
				adaptedType = key;
			}
		}

		if (adaptedType != null) {
			Map<TagKey<DamageType>, Float> resistances = damageTypeTracker.resistances;
			int i = Math.min(resistances.size(), ADAPTATION_EFFECTIVENESS.length - 1);
			resistances.put(adaptedType, ADAPTATION_EFFECTIVENESS[i]);
		}
	}

	public record DamageTypeResistanceTracker(Map<TagKey<DamageType>, Integer> counter, Map<TagKey<DamageType>, Float> resistances) {
		static DamageTypeResistanceTracker fromNBT(CompoundTag compoundTag) {
			Map<TagKey<DamageType>, Integer> counter = new HashMap<>();
			Map<TagKey<DamageType>, Float> resistances = new HashMap<>();

			ListTag list = compoundTag.getList("list", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag = list.getCompound(i);

				ResourceLocation key = new ResourceLocation(tag.getString("damage_type_tag"));
				TagKey<DamageType> damageType = TagKey.create(Registries.DAMAGE_TYPE, key);

				if (tag.contains("count")) {
					counter.put(damageType, tag.getInt("count"));
				}

				if (tag.contains("resistance")) {
					resistances.put(damageType, tag.getFloat("resistance"));
				}
			}

			return new DamageTypeResistanceTracker(counter, resistances);
		}

		static void appendTooltipText(CompoundTag compoundTag, List<Component> tooltip) {

			if (!compoundTag.contains("list")) return;

			tooltip.add(ComponentUtil.emptyLine());
			tooltip.add(ComponentUtil.mutable().withStyle(TextStyles.GRAY)
					.append(ComponentUtil.literal("Adaptations:"))
			);

			ListTag list = compoundTag.getList("list", Tag.TAG_COMPOUND);

			for (int i = 0; i < list.size(); i++) {
				CompoundTag tag = list.getCompound(i);

				tooltip.add(ComponentUtil.mutable().withStyle(TextStyles.GRAY)
						.append(ComponentUtil.literal(tag.getString("damage_type_tag")))
				);

				if (tag.contains("count")) {
					tooltip.add(ComponentUtil.mutable().withStyle(TextStyles.GRAY).append(ComponentUtil.space())
							.append(ComponentUtil.literal("Count: " + tag.getInt("count")))
					);
				}

				if (tag.contains("resistance")) {
					tooltip.add(ComponentUtil.mutable().withStyle(TextStyles.GRAY).append(ComponentUtil.space())
							.append(ComponentUtil.literal("Resistance: " + tag.getFloat("resistance")))
					);
				}
			}
		}

		public void count(TagKey<DamageType> damageType) {
			counter.merge(damageType, 1, Integer::sum);
		}

		public boolean hasResistanceForLargestDamageTypeCount() {
			return counter.entrySet().stream()
					.max(Map.Entry.comparingByValue())
					.map(Map.Entry::getKey).filter(resistances::containsKey)
					.isPresent();
		}

		public CompoundTag toNBT() {
			Set<TagKey<DamageType>> damageTypes = new HashSet<>();
			damageTypes.addAll(counter.keySet());
			damageTypes.addAll(resistances.keySet());

			ListTag list = new ListTag();

			for (TagKey<DamageType> damageType : damageTypes) {
				CompoundTag tag = new CompoundTag();
				tag.putString("damage_type_tag", damageType.location().toString());

				if (counter.containsKey(damageType)) {
					tag.putInt("count", counter.get(damageType));
				}

				if (resistances.containsKey(damageType)) {
					tag.putFloat("resistance", resistances.get(damageType));
				}

				list.add(tag);
			}

			CompoundTag compoundTag = new CompoundTag();
			compoundTag.put("list", list);

			return compoundTag;
		}
	}

}
