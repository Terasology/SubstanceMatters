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

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.registry.CoreRegistry;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.workstation.process.inventory.InventoryOutputComponent;

import java.util.HashSet;
import java.util.Map;
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
        MaterialItemComponent materialItem = entityRef.getComponent(MaterialItemComponent.class);
        MaterialCompositionComponent materialComposition = processEntity.getComponent(MaterialCompositionComponent.class);
        if (materialItem != null && materialComposition != null) {
            MaterialCompositionComponent newMaterialComposition = new MaterialCompositionComponent();
            for (Map.Entry<Prefab, Float> substance : materialComposition.contents.entrySet()) {
                newMaterialComposition.contents.put(substance.getKey(), substance.getValue() / amount);
            }
            entityRef.addComponent(materialComposition);
        }
        result.add(entityRef);

        return result;
    }
}
