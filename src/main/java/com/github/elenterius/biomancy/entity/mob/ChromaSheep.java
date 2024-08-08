package com.github.elenterius.biomancy.entity.mob;

import com.github.elenterius.biomancy.init.ModEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ChromaSheep extends Sheep {

	public ChromaSheep(EntityType<? extends Sheep> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public DyeColor getColor() {
		return DyeColor.byId(random.nextInt(16));
	}

	@Override
	public boolean canMate(Animal otherAnimal) {
		if (otherAnimal == this) return false;

		return otherAnimal instanceof Sheep && isInLove() && otherAnimal.isInLove();
	}

	@Nullable
	@Override
	public Sheep getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		if (otherParent.getClass() != getClass() && random.nextFloat() < 0.8f) {
			return (Sheep) otherParent.getBreedOffspring(level, this);
		}

		return ModEntityTypes.CHROMA_SHEEP.get().create(level);
	}

}
