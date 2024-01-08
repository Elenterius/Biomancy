package com.github.elenterius.biomancy.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Predicate;

public final class ModBlockMaterials {

	public static final Predicate<Block> FLESH_PREDICATE = block -> {
		Material material = block.defaultBlockState().getMaterial();
		return material == ModBlockMaterials.FLESH_MATERIAL || material == ModBlockMaterials.FLESH_VEINS_MATERIAL;
	};

	public static final Material FLESH_MATERIAL = Builder.create().build();
	public static final Material FLESH_VEINS_MATERIAL = Builder.create().noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();

	private ModBlockMaterials() {}

	public static class Builder {
		private PushReaction pushReaction = PushReaction.NORMAL;
		private boolean blocksMotion = true;
		private boolean flammable;
		private boolean liquid;
		private boolean replaceable;
		private boolean solid = true;
		private MaterialColor color = MaterialColor.COLOR_PINK;
		private boolean solidBlocking = true;

		private Builder() {}

		public static Builder create() {
			return new Builder();
		}

		public Builder color(MaterialColor color) {
			this.color = color;
			return this;
		}

		public Builder liquid() {
			liquid = true;
			return this;
		}

		public Builder nonSolid() {
			solid = false;
			return this;
		}

		public Builder noCollider() {
			blocksMotion = false;
			return this;
		}

		public Builder notSolidBlocking() {
			solidBlocking = false;
			return this;
		}

		public Builder flammable() {
			flammable = true;
			return this;
		}

		public Builder replaceable() {
			replaceable = true;
			return this;
		}

		public Builder destroyOnPush() {
			pushReaction = PushReaction.DESTROY;
			return this;
		}

		public Builder notPushable() {
			pushReaction = PushReaction.BLOCK;
			return this;
		}

		public Material build() {
			return new Material(color, liquid, solid, blocksMotion, solidBlocking, flammable, replaceable, pushReaction);
		}
	}
}
