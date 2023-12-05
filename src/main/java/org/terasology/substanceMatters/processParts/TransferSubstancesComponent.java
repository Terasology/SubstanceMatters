// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.processParts;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Creates an material item containing the materials that it is composed of based on the original input items.  The item will appear like the largest amount of substance.
 */
public class TransferSubstancesComponent implements Component<TransferSubstancesComponent> {
    public boolean extract = true;
    public boolean inject = true;

    @Override
    public void copyFrom(TransferSubstancesComponent other) {
        this.extract = other.extract;
        this.inject = other.inject;
    }
}
