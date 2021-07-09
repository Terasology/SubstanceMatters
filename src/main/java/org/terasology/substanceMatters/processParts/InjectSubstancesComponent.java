// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.processParts;

import com.google.common.collect.Maps;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.Map;

public class InjectSubstancesComponent implements Component<InjectSubstancesComponent> {
    /**
     * A map of substance prefab and how much is added
     */
    public Map<String, Float> add = Maps.newHashMap();

    /**
     * Straight out change one substance to another
     */
    public Map<String, String> replace = Maps.newHashMap();

    @Override
    public void copy(InjectSubstancesComponent other) {
        this.add = Maps.newHashMap(other.add);
        this.replace = Maps.newHashMap(other.replace);
    }
}
