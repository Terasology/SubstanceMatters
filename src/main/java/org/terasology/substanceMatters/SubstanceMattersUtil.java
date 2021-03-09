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

    public static void setTintParametersFromSubstance(String substance, TintOverlayIconComponent.TintParameter tintParameter) {
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
