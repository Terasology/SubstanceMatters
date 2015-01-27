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
package org.terasology.substanceMatters.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.logic.inventory.ItemDifferentiating;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A list of the contents of an item.  Not intended to be added directly to a prefab with json.
 */
@ItemDifferentiating
public class MaterialCompositionComponent implements Component {
    /**
     * A map of the substance prefab and how much is contained
     */
    public Map<String, Float> contents = Maps.newHashMap();

    public MaterialCompositionComponent() {
    }

    public MaterialCompositionComponent copy() {
        MaterialCompositionComponent newMaterialComposition = new MaterialCompositionComponent();
        newMaterialComposition.contents = Maps.newHashMap(contents);
        return newMaterialComposition;
    }


    public void addMaterialFromItem(EntityRef item, int itemCount) {
        // extract all substance amounts from this item
        MaterialCompositionComponent itemMaterialComposition = item.getComponent(MaterialCompositionComponent.class);
        if (itemMaterialComposition != null) {
            for (Map.Entry<String, Float> entry : itemMaterialComposition.contents.entrySet()) {
                addSubstance(entry.getKey(), entry.getValue().floatValue() * Math.min(itemCount, InventoryUtils.getStackCount(item)));
            }
        }
    }

    public void addSubstance(String substance, Float amount) {
        Prefab substancePrefab = Assets.getPrefab(substance);
        addSubstance(substancePrefab, amount);
    }

    public void addSubstance(Prefab substance, Float amount) {
        if (amount != null) {
            String substanceUri = substance.getURI().toSimpleString();
            float previousAmount = 0f;
            if (contents.containsKey(substanceUri)) {
                previousAmount = contents.get(substanceUri);
            }
            contents.put(substanceUri, previousAmount + amount);
        }
    }

    public Float removeSubstance(String substance) {
        Prefab substancePrefab = Assets.getPrefab(substance);
        return removeSubstance(substancePrefab);
    }

    public Float removeSubstance(Prefab substance) {
        String substanceUri = substance.getURI().toSimpleString();
        return contents.remove(substanceUri);
    }

    public List<Map.Entry<String, Float>> getSortedByAmountDesc() {
        List<Map.Entry<String, Float>> sortedMaterials = Lists.newLinkedList(contents.entrySet());
        // sort desc
        Collections.sort(sortedMaterials, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        return sortedMaterials;
    }

    public String getPrimarySubstance() {
        return getSortedByAmountDesc().get(0).getKey();
    }

    public boolean hasSubstance() {
        return contents.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MaterialCompositionComponent that = (MaterialCompositionComponent) o;

        if (!getPrimarySubstance().equals(that.getPrimarySubstance())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getPrimarySubstance().hashCode();
    }

    public void divide(float divisor) {
        for (Map.Entry<String, Float> entry : contents.entrySet()) {
            entry.setValue(entry.getValue() / divisor);
        }
    }

    public void replaceSubstance(String existingSubstance, String replacementSubstance) {
        Float amount = removeSubstance(existingSubstance);
        addSubstance(replacementSubstance, amount);
    }
}
