/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.substanceMatters.processParts;

import org.terasology.engine.entitySystem.MutableComponentContainer;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.systems.InventoryUtils;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.workstation.process.inventory.InventoryInputItemsComponent;
import org.terasology.workstation.process.inventory.InventoryOutputItemsComponent;

@RegisterSystem
public class TransferSubstancesProcessPartCommonSystem extends BaseComponentSystem {
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
                MaterialCompositionComponent materialCompositionToSave = item.getComponent(MaterialCompositionComponent.class);
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

    public static void setDisplayName(MutableComponentContainer entity, MaterialCompositionComponent materialComposition) {
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
}
