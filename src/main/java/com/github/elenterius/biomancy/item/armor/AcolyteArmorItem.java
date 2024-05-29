package com.github.elenterius.biomancy.item.armor;

import com.github.elenterius.biomancy.client.render.item.armor.AcolyteArmorRenderer;
import com.github.elenterius.biomancy.item.ItemTooltipStyleProvider;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public final class AcolyteArmorItem extends LivingArmorGeoItem implements ItemTooltipStyleProvider {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public AcolyteArmorItem(ArmorMaterial material, Type type, int maxNutrients, Properties properties) {
		super(material, type, maxNutrients, properties);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@Override
			public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> original) {
				if (renderer == null) {
					renderer = new AcolyteArmorRenderer();
				}

				renderer.prepForRender(livingEntity, itemStack, slot, original);

				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		//TODO: add idle animations?
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	public AdaptiveDamageResistanceHandler.DamageTypeResistanceTracker getDamageTypeResistanceTracker(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag().getCompound("damage_resistance_tracker");
		return AdaptiveDamageResistanceHandler.DamageTypeResistanceTracker.fromNBT(tag);
	}

	public void saveDamageTypeResistanceTracker(AdaptiveDamageResistanceHandler.DamageTypeResistanceTracker resistanceTracker, ItemStack stack) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		compoundTag.put("damage_resistance_tracker", resistanceTracker.toNBT());
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(ComponentUtil.emptyLine());

		tooltip.add(TextComponentUtil.getAbilityText("fleshkin_affinity").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("fleshkin_affinity.desc")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(ComponentUtil.emptyLine());
		tooltip.add(TextComponentUtil.getAbilityText("bio_alchemical_epidermis").withStyle(ChatFormatting.GRAY));
		tooltip.add(ComponentUtil.literal(" ").append(TextComponentUtil.getAbilityText("bio_alchemical_epidermis.desc")).withStyle(ChatFormatting.DARK_GRAY));
		tooltip.add(ComponentUtil.emptyLine());

		CompoundTag compoundTag = stack.getOrCreateTag().getCompound("damage_resistance_tracker");
		AdaptiveDamageResistanceHandler.DamageTypeResistanceTracker.appendTooltipText(compoundTag, tooltip);

		appendLivingToolTooltip(stack, tooltip);

		if (stack.isEnchanted()) {
			tooltip.add(ComponentUtil.emptyLine());
		}
	}

}