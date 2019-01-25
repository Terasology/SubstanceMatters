/*
 * Copyright 2015 MovingBlocks
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
package org.terasology.substanceMatters.systems;

import com.google.common.collect.Iterables;
import org.terasology.utilities.Assets;
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.system.FluidRegistry;
import org.terasology.fluid.system.TextureFluidRenderer;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.assets.texture.TextureUtil;
import org.terasology.rendering.nui.Color;
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
                ResourceUrn fluidTextureUri = TextureUtil.getTextureUriForColor(new Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), awtColor.getAlpha()));
                Texture texture = Assets.get(fluidTextureUri, Texture.class).get();
                fluidRegistry.registerFluid(prefab.getName(), new TextureFluidRenderer(texture, substanceComponent.name));
            }
        }
    }
}
