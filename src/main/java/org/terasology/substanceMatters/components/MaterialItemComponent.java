// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.inventory.logic.ItemDifferentiating;

/**
 * Attach this to items that are made of a particular substance.  The icon will be tinted to the substance's
 * definition.
 */
public class MaterialItemComponent implements Component, ItemDifferentiating {
    public String icon;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MaterialItemComponent that = (MaterialItemComponent) o;

        return icon != null ? icon.equals(that.icon) : that.icon == null;
    }

    @Override
    public int hashCode() {
        return icon != null ? icon.hashCode() : 0;
    }
}
