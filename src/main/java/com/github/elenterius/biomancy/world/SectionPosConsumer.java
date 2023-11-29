package com.github.elenterius.biomancy.world;

@FunctionalInterface
public interface SectionPosConsumer {
	void accept(int sectionX, int sectionY, int sectionZ);
}
