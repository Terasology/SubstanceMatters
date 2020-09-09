// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.processParts;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.workstation.process.inventory.InventoryOutputItemsComponent;
import org.terasology.workstation.processPart.inventory.ProcessEntityIsInvalidForInventoryItemEvent;

import java.util.Map;

@RegisterSystem
public class InjectSubstancesProcessPartCommonSystem extends BaseComponentSystem {

    @ReceiveEvent
    public void validateInputItemsHaveReplacementSubstance(ProcessEntityIsInvalidForInventoryItemEvent event,
                                                           EntityRef processEntity,
                                                           InjectSubstancesComponent injectSubstancesComponent) {
        if (injectSubstancesComponent.replace.size() > 0) {
            MaterialCompositionComponent materialCompositionComponent =
                    event.getItem().getComponent(MaterialCompositionComponent.class);
            if (materialCompositionComponent == null) {
                event.consume();
                return;
            }
            for (Map.Entry<String, String> replacement : injectSubstancesComponent.replace.entrySet()) {
                if (!materialCompositionComponent.contents.containsKey(replacement.getKey())) {
                    event.consume();
                    return;
                }
            }

        }
    }

    @ReceiveEvent
    public void injectSubstancesToOutputItems(OnAddedComponent event, EntityRef processEntity,
                                              InventoryOutputItemsComponent outputItemsComponent,
                                              InjectSubstancesComponent injectSubstancesComponent) {
        for (EntityRef item : outputItemsComponent.getOutputItems()) {
            MaterialCompositionComponent materialCompositionComponent =
                    item.getComponent(MaterialCompositionComponent.class);
            if (materialCompositionComponent == null) {
                materialCompositionComponent = new MaterialCompositionComponent();
            }

            // add new substances
            for (Map.Entry<String, Float> entry : injectSubstancesComponent.add.entrySet()) {
                materialCompositionComponent.addSubstance(entry.getKey(), entry.getValue());
            }

            // replace any substances
            for (Map.Entry<String, String> replacement : injectSubstancesComponent.replace.entrySet()) {
                materialCompositionComponent.replaceSubstance(replacement.getKey(), replacement.getValue());
            }

            item.addOrSaveComponent(materialCompositionComponent);
            TransferSubstancesProcessPartCommonSystem.setDisplayName(item, materialCompositionComponent);
        }
    }
}
