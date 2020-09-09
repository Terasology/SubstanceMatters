// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.components;

import org.terasology.engine.entitySystem.Component;

/**
 * A definition for a substance.  Color values are for generated icons.
 */
public class SubstanceComponent implements Component {
    public String name;
    public String description = "";
    public int hue;
    public float saturationScale = 1f;
    public float brightnessScale = 1f;
}
