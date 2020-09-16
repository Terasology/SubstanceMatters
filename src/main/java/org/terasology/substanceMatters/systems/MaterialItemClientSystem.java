// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.systems;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.utilities.Assets;
import org.terasology.itemRendering.tintOverlay.TintOverlayIconComponent;
import org.terasology.substanceMatters.SubstanceMattersUtil;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;

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


    private void setIcon(EntityRef entityRef, MaterialItemComponent materialItem,
                         MaterialCompositionComponent materialComposition) {
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
