package com.github.elenterius.blightlings.item;

public interface IHighlightRayTraceResultItem
{
    float DEFAULT_MAX_DISTANCE = 20f;

    default double getMaxRayTraceDistance() {
        return DEFAULT_MAX_DISTANCE;
    }
}
