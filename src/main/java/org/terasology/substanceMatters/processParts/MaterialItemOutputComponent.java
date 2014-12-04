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
package org.terasology.substanceMatters.processParts;

import org.terasology.asset.Assets;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.registry.CoreRegistry;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.workstation.process.inventory.InventoryInputComponent;
import org.terasology.workstation.process.inventory.InventoryOutputComponent;

import java.util.HashSet;
import java.util.Set;

/**
 * Creates an material item containing the materials that it is composed of based on the original input items.  The item will appear like the largest amount of substance.
 */
public class MaterialItemOutputComponent extends InventoryOutputComponent {
    public String item;
    public int amount = 1;

    @Override
    protected Set<EntityRef> createOutputItems(EntityRef processEntity) {
        EntityManager entityManager = CoreRegistry.get(EntityManager.class);

        Set<EntityRef> result = new HashSet<>();
        EntityRef entityRef = entityManager.create(item);

        // grab the material composition from the process entity and the input items
        MaterialCompositionComponent materialComposition = processEntity.getComponent(MaterialCompositionComponent.class);
        if (materialComposition == null) {
            materialComposition = new MaterialCompositionComponent();
        }
        InventoryInputComponent.InventoryInputProcessPartItemsComponent inputItemsContainer = processEntity.getComponent(InventoryInputComponent.InventoryInputProcessPartItemsComponent.class);
        if (inputItemsContainer != null) {
            materialComposition.addMaterialFromItems(inputItemsContainer.items);
        }

        if (materialComposition.hasSubstance()) {
            materialComposition.divide(amount);
            entityRef.addComponent(materialComposition);
        }

        // set the stack size
        ItemComponent itemComponent = entityRef.getComponent(ItemComponent.class);
        itemComponent.stackCount = (byte) amount;
        entityRef.saveComponent(itemComponent);

        // set the display name if this is a materialItem
        MaterialItemComponent materialItem = entityRef.getComponent(MaterialItemComponent.class);
        DisplayNameComponent displayNameComponent = entityRef.getComponent(DisplayNameComponent.class);
        if (materialItem != null && displayNameComponent != null && materialComposition.hasSubstance()) {
            Prefab substancePrefab = Assets.getPrefab(materialComposition.getPrimarySubstance());
            SubstanceComponent substanceComponent = substancePrefab.getComponent(SubstanceComponent.class);
            if (substanceComponent != null) {
                displayNameComponent.name = substanceComponent.name + " " + displayNameComponent.name;
                entityRef.saveComponent(displayNameComponent);
            }
        }

        result.add(entityRef);

        return result;
    }
}
