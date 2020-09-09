// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.processParts;

import org.terasology.engine.entitySystem.MutableComponentContainer;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.utilities.Assets;
import org.terasology.inventory.logic.InventoryUtils;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.workstation.process.inventory.InventoryInputItemsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputItemsComponent;

@RegisterSystem
public class TransferSubstancesProcessPartCommonSystem extends BaseComponentSystem {
    public static void setDisplayName(MutableComponentContainer entity,
                                      MaterialCompositionComponent materialComposition) {
        // set the display name if this is a materialItem
        MaterialItemComponent materialItem = entity.getComponent(MaterialItemComponent.class);
        DisplayNameComponent displayNameComponent = entity.getComponent(DisplayNameComponent.class);
        if (materialItem != null && displayNameComponent != null && materialComposition.hasSubstance()) {
            Prefab substancePrefab = Assets.getPrefab(materialComposition.getPrimarySubstance()).get();
            SubstanceComponent substanceComponent = substancePrefab.getComponent(SubstanceComponent.class);
            if (substanceComponent != null) {
                displayNameComponent.name = substanceComponent.name + " " + displayNameComponent.name;
                entity.saveComponent(displayNameComponent);
            }
        }
    }

    @ReceiveEvent
    public void extractSubstanceFromSelectedItems(OnAddedComponent event, EntityRef processEntity,
                                                  InventoryInputItemsComponent inputItemsComponent,
                                                  TransferSubstancesComponent transferSubstancesComponent) {
        if (transferSubstancesComponent.extract) {
            MaterialCompositionComponent materialCompositionComponent = new MaterialCompositionComponent();
            for (EntityRef item : inputItemsComponent.items) {
                materialCompositionComponent.addMaterialFromItem(item, InventoryUtils.getStackCount(item));
            }
            processEntity.addComponent(materialCompositionComponent);
        }
    }

    @ReceiveEvent
    public void injectSubstancesToOutputItems(OnAddedComponent event, EntityRef processEntity,
                                              InventoryOutputItemsComponent outputItemsComponent,
                                              TransferSubstancesComponent transferSubstancesComponent,
                                              MaterialCompositionComponent materialComposition) {
        if (transferSubstancesComponent.inject) {
            int totalStackCount = 0;
            for (EntityRef item : outputItemsComponent.getOutputItems()) {
                totalStackCount += InventoryUtils.getStackCount(item);
            }

            for (EntityRef item : outputItemsComponent.getOutputItems()) {
                MaterialCompositionComponent materialCompositionToSave =
                        item.getComponent(MaterialCompositionComponent.class);
                if (materialCompositionToSave == null) {
                    materialCompositionToSave = new MaterialCompositionComponent();
                }

                MaterialCompositionComponent newMaterialComposition = materialComposition.copy();
                if (newMaterialComposition.hasSubstance()) {
                    newMaterialComposition.divide(InventoryUtils.getStackCount(item) / totalStackCount);
                    materialCompositionToSave.addSubstance(newMaterialComposition);
                    item.addOrSaveComponent(materialCompositionToSave);
                    setDisplayName(item, materialComposition);
                }
            }
        }
    }
}
