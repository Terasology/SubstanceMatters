// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.processParts;

import org.terasology.engine.entitySystem.Component;

/**
 * Creates an material item containing the materials that it is composed of based on the original input items.  The item
 * will appear like the largest amount of substance.
 */
public class TransferSubstancesComponent implements Component {
    boolean extract = true;
    boolean inject = true;
}
