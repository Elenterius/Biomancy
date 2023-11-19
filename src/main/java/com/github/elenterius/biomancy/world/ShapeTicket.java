package com.github.elenterius.biomancy.world;

import com.github.elenterius.biomancy.util.shape.Shape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ticket.SimpleTicket;

public class ShapeTicket extends SimpleTicket<Vec3> {
	private final Shape shape;

	public ShapeTicket(Shape shape) {
		this.shape = shape;
	}

	@Override
	public boolean matches(Vec3 toMatch) {
		return shape.contains(toMatch.x, toMatch.y, toMatch.z);
	}

	public Shape getShape() {
		return shape;
	}

}
