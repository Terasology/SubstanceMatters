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
    public void validateInputItemsHaveReplacementSubstance(ProcessEntityIsInvalidForInventoryItemEvent event, EntityRef processEntity,
                                                           InjectSubstancesComponent injectSubstancesComponent) {
        if (injectSubstancesComponent.replace.size() > 0) {
            MaterialCompositionComponent materialCompositionComponent = event.getItem().getComponent(MaterialCompositionComponent.class);
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
            MaterialCompositionComponent materialCompositionComponent = item.getComponent(MaterialCompositionComponent.class);
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
