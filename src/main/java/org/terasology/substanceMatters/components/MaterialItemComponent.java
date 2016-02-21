/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.substanceMatters.components;

import org.terasology.entitySystem.Component;
import org.terasology.logic.inventory.ItemDifferentiating;

/**
 * Attach this to items that are made of a particular substance.  The icon will be tinted to the substance's definition.
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

        if (icon != null ? !icon.equals(that.icon) : that.icon != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return icon != null ? icon.hashCode() : 0;
    }
}
