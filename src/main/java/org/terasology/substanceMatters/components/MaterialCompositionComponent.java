// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.utilities.Assets;
import org.terasology.inventory.logic.InventoryUtils;
import org.terasology.inventory.logic.ItemDifferentiating;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A list of the contents of an item.  Not intended to be added directly to a prefab with json.
 */
public class MaterialCompositionComponent implements Component, ItemDifferentiating {
    /**
     * A map of the substance prefab and how much is contained
     */
    @Replicate
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
                addSubstance(entry.getKey(), entry.getValue() * Math.min(itemCount,
                        InventoryUtils.getStackCount(item)));
            }
        }
    }

    public void addSubstance(MaterialCompositionComponent otherMaterialComposition) {
        for (Map.Entry<String, Float> entry : otherMaterialComposition.contents.entrySet()) {
            addSubstance(entry.getKey(), entry.getValue());
        }
    }

    public void addSubstance(String substance, Float amount) {
        Prefab substancePrefab = Assets.getPrefab(substance).get();
        addSubstance(substancePrefab, amount);
    }

    public void addSubstance(Prefab substance, Float amount) {
        if (amount != null) {
            String substanceUri = substance.getName();
            float previousAmount = 0f;
            if (contents.containsKey(substanceUri)) {
                previousAmount = contents.get(substanceUri);
            }
            contents.put(substanceUri, previousAmount + amount);
        }
    }

    public Float removeSubstance(String substance) {
        Prefab substancePrefab = Assets.getPrefab(substance).get();
        return removeSubstance(substancePrefab);
    }

    public Float removeSubstance(Prefab substance) {
        String substanceUri = substance.getName();
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
        if (contents.size() == 0) {
            return null;
        } else {
            return getSortedByAmountDesc().get(0).getKey();
        }
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

        // TODO: this needs to compare each of the content substances and amounts
        String thisPrimarySubstance = getPrimarySubstance();
        return thisPrimarySubstance == null || thisPrimarySubstance.equals(that.getPrimarySubstance());
    }

    @Override
    public int hashCode() {
        String substance = getPrimarySubstance();
        if (substance != null) {
            return substance.hashCode();
        } else {
            return super.hashCode();
        }
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

    public String toDisplayString() {
        PrefabManager prefabManager = CoreRegistry.get(PrefabManager.class);
        String display = "";
        for (Map.Entry<String, Float> substanceAmount : getSortedByAmountDesc()) {
            String substanceName = substanceAmount.getKey();
            Prefab substancePrefab = prefabManager.getPrefab(substanceAmount.getKey());
            if (substancePrefab != null) {
                SubstanceComponent substanceComponent = substancePrefab.getComponent(SubstanceComponent.class);
                if (substanceComponent != null) {
                    substanceName = substanceComponent.name;
                }
            }
            display += substanceAmount.getValue() + " " + substanceName + "\n";
        }

        display = display.trim();
        return display;
    }
}
