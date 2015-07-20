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
package org.terasology.substanceMatters.systems;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.fluid.component.FluidContainerItemComponent;
import org.terasology.substanceMatters.SubstanceMattersUtil;
import org.terasology.substanceMatters.components.MaterialFluidItemContainerComponent;
import org.terasology.tintOverlay.TintOverlayIconComponent;

/**
 * Creates an icon on the fly for items with the MaterialItemComponent using the TintOverlay system
 */
@RegisterSystem(RegisterMode.CLIENT)
public class MaterialFluidContainerClientSystem extends BaseComponentSystem {

    @ReceiveEvent
    public void onFluidContentsAdded(OnActivatedComponent event, EntityRef container,
                                     FluidContainerItemComponent fluidContainerItem,
                                     MaterialFluidItemContainerComponent materialFluidItemContainerComponent) {
        setFluidContainerIcon(container, fluidContainerItem, materialFluidItemContainerComponent.fluidIcon);
    }

    @ReceiveEvent
    public void onFluidContentsChanged(OnChangedComponent event, EntityRef container,
                                       FluidContainerItemComponent fluidContainerItem,
                                       MaterialFluidItemContainerComponent materialFluidItemContainerComponent) {
        setFluidContainerIcon(container, fluidContainerItem, materialFluidItemContainerComponent.fluidIcon);
    }

    private void setFluidContainerIcon(EntityRef container, FluidContainerItemComponent fluidContainerItem, String iconUri) {
        TintOverlayIconComponent tintOverlayIconComponent = container.getComponent(TintOverlayIconComponent.class);
        if (iconUri != null && tintOverlayIconComponent != null) {
            TintOverlayIconComponent.TintParameter tintParameter = tintOverlayIconComponent.getTintParameterForIcon(iconUri);
            if (tintParameter != null) {
                // set the tint
                tintParameter.invisible = fluidContainerItem.fluidType == null;
                SubstanceMattersUtil.setTintParametersFromSubstance(fluidContainerItem.fluidType, tintParameter);
            }

            container.saveComponent(tintOverlayIconComponent);
        }
    }
}
