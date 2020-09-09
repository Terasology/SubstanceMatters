// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters;

import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.utilities.Assets;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.tintOverlay.TintOverlayIconComponent;

import java.util.Optional;

public final class SubstanceMattersUtil {
    public static final String UNKNOWNSUBSTANCE = "SubstanceMatters:UnknownSubstance";

    private SubstanceMattersUtil() {
    }

    public static void setTintParametersFromSubstance(String substance,
                                                      TintOverlayIconComponent.TintParameter tintParameter) {
        if (tintParameter != null) {
            Optional<Prefab> substancePrefab;
            if (substance == null) {
                substancePrefab = Assets.getPrefab(UNKNOWNSUBSTANCE);
            } else {
                substancePrefab = Assets.getPrefab(substance);
            }

            SubstanceComponent substanceComponent = substancePrefab.get().getComponent(SubstanceComponent.class);
            tintParameter.hue = substanceComponent.hue;
            tintParameter.brightnessScale = substanceComponent.brightnessScale;
            tintParameter.saturationScale = substanceComponent.saturationScale;
        }
    }
}
