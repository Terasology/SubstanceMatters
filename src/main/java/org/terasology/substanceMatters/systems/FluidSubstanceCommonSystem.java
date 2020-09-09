// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.systems;

import com.google.common.collect.Iterables;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.Assets;
import org.terasology.fluid.system.FluidRegistry;
import org.terasology.substanceMatters.components.FluidSubstanceComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;

/**
 * Registers fluid from a prefab based on the presence of FluidSubstanceComponent
 */
@RegisterSystem
public class FluidSubstanceCommonSystem extends BaseComponentSystem {
    @In
    FluidRegistry fluidRegistry;

    @Override
    public void initialise() {
        super.initialise();
        for (Prefab prefab : Iterables.transform(Assets.list(Prefab.class), x -> Assets.get(x, Prefab.class).get())) {
            if (prefab.hasComponent(FluidSubstanceComponent.class)) {
                SubstanceComponent substanceComponent = prefab.getComponent(SubstanceComponent.class);
                java.awt.Color awtColor = java.awt.Color.getHSBColor(substanceComponent.hue / 360f,
                        0.5f * substanceComponent.saturationScale,
                        0.5f * substanceComponent.brightnessScale);

                fluidRegistry.registerFluid(prefab.getName(), substanceComponent.name, awtColor);
            }
        }
    }
}
