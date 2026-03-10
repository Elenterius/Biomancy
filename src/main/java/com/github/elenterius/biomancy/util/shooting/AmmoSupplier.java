package com.github.elenterius.biomancy.util.shooting;

import com.github.elenterius.biomancy.api.nutrients.NutrientsContainerItem;
import com.github.elenterius.biomancy.item.weapon.gun.GunItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface AmmoSupplier {

	int getAmount();

	void consume(int amount);

	static AmmoSupplier fromInventory(LivingEntity shooter, GunItem<?> gunItem, ItemStack gunStack) {
		return AmmoFromInventory.findAmmoInInventory(shooter, gunItem, gunStack);
	}

	static AmmoSupplier fromNutrientsContainer(NutrientsContainerItem containerItem, ItemStack containerStack) {
		return new AmmoFromNutrientsContainer(containerItem, containerStack);
	}

	record AmmoFromInventory(ItemStack ammoStack) implements AmmoSupplier {

		public static AmmoFromInventory findAmmoInInventory(LivingEntity shooter, GunItem<?> gunItem, ItemStack gunStack) {
			ItemStack ammo = shooter.getProjectile(gunStack); //vanilla mobs only look for held ammo (i.e. in off-hand)
			if (ammo.getItem() == Items.ARROW) { //if mobs/creative players can't find any ammo they will return arrows
				if (shooter instanceof Player player && player.getAbilities().instabuild) {
					ammo = ammo.copy();
					ammo.setCount(gunItem.getReloadCost(gunStack));
					return new AmmoFromInventory(ammo);
				}

				if (gunItem.getSupportedHeldProjectiles().test(ammo) || gunItem.getAllSupportedProjectiles().test(ammo)) return new AmmoFromInventory(ammo);
				return new AmmoFromInventory(ItemStack.EMPTY);
			}
			return new AmmoFromInventory(ammo);
		}

		@Override
		public int getAmount() {
			return ammoStack.getCount();
		}

		@Override
		public void consume(int amount) {
			ammoStack.shrink(amount);
		}
	}

	record AmmoFromNutrientsContainer(NutrientsContainerItem containerItem, ItemStack containerStack) implements AmmoSupplier {
		@Override
		public int getAmount() {
			return containerItem.getNutrients(containerStack);
		}

		@Override
		public void consume(int amount) {
			containerItem.consumeNutrients(containerStack, amount);
		}
	}

}
