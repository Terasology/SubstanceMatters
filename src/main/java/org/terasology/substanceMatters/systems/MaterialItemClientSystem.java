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

import org.terasology.utilities.Assets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.substanceMatters.SubstanceMattersUtil;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.tintOverlay.TintOverlayIconComponent;

import java.util.Optional;

/**
 * Creates an icon on the fly for items with the MaterialItemComponent using the TintOverlay system
 */
@RegisterSystem
public class MaterialItemClientSystem extends BaseComponentSystem {
    @ReceiveEvent
    public void onMaterialItemActivated(OnActivatedComponent event, EntityRef entityRef,
                                        MaterialItemComponent materialItem) {
        setIcon(entityRef, materialItem, entityRef.getComponent(MaterialCompositionComponent.class));
    }

    @ReceiveEvent
    public void onMaterialCompositionActivated(OnActivatedComponent event, EntityRef entityRef,
                                               MaterialItemComponent materialItem,
                                               MaterialCompositionComponent materialComposition) {
        setIcon(entityRef, materialItem, materialComposition);
    }

    @ReceiveEvent
    public void onMaterialCompositionChanged(OnChangedComponent event, EntityRef entityRef,
                                             MaterialItemComponent materialItem,
                                             MaterialCompositionComponent materialComposition) {
        setIcon(entityRef, materialItem, materialComposition);
    }


    private void setIcon(EntityRef entityRef, MaterialItemComponent materialItem, MaterialCompositionComponent materialComposition) {
        Optional<Prefab> substancePrefab;
        if (materialComposition == null || materialComposition.contents.size() == 0) {
            substancePrefab = Assets.getPrefab(SubstanceMattersUtil.UNKNOWNSUBSTANCE);
        } else {
            substancePrefab = Assets.getPrefab(materialComposition.getPrimarySubstance());
        }

        if (!substancePrefab.isPresent()) {
            return;
        }

        SubstanceComponent substance = substancePrefab.get().getComponent(SubstanceComponent.class);
        TintOverlayIconComponent tintOverlayIconComponent = new TintOverlayIconComponent();
        tintOverlayIconComponent.texture.put(materialItem.icon,
                new TintOverlayIconComponent.TintParameter(
                        substance.hue, substance.brightnessScale, substance.saturationScale, 0, 0));
        if (entityRef.hasComponent(TintOverlayIconComponent.class)) {
            entityRef.saveComponent(tintOverlayIconComponent);
        } else {
            entityRef.addComponent(tintOverlayIconComponent);
        }
    }
}
