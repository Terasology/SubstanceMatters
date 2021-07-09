// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * A definition for a substance.  Color values are for generated icons.
 */
public class SubstanceComponent implements Component<SubstanceComponent> {
    public String name;
    public String description = "";
    public int hue;
    public float saturationScale = 1f;
    public float brightnessScale = 1f;

    @Override
    public void copy(SubstanceComponent other) {
        this.name = other.name;
        this.description = other.description;
        this.hue = other.hue;
        this.saturationScale = other.saturationScale;
        this.brightnessScale = other.brightnessScale;
    }
}
