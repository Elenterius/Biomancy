package com.github.elenterius.biomancy.world.section;

@FunctionalInterface
public interface SectionPosConsumer {
	void accept(int sectionX, int sectionY, int sectionZ);
}
