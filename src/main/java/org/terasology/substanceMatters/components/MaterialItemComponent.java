// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.components;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.module.inventory.components.ItemDifferentiating;

/**
 * Attach this to items that are made of a particular substance.  The icon will be tinted to the substance's definition.
 */
public class MaterialItemComponent implements Component<MaterialItemComponent>, ItemDifferentiating {
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

        if (icon != null ? !icon.equals(that.icon) : that.icon != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return icon != null ? icon.hashCode() : 0;
    }

    @Override
    public void copy(MaterialItemComponent other) {
        this.icon = other.icon;
    }
}
