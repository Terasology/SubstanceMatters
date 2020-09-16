// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.systems;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabData;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.assets.AssetDataProducer;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.module.annotations.RegisterAssetDataProducer;
import org.terasology.gestalt.naming.Name;
import org.terasology.itemRendering.tintOverlay.TintOverlayTextureProducer;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RegisterAssetDataProducer
public class MaterialItemAssetProducer implements AssetDataProducer<PrefabData> {
    private static final Name MODULENAME = new Name("SubstanceMatters");
    private static final Name RESOURCENAME = new Name("MaterialItem");
    private static final String DELIMITER = "\\|";
    private static final Logger logger = LoggerFactory.getLogger(TintOverlayTextureProducer.class);

    @Override
    public Set<ResourceUrn> getAvailableAssetUrns() {
        return Collections.emptySet();
    }

    @Override
    public Set<Name> getModulesProviding(Name resourceName) {
        if (RESOURCENAME.equals(resourceName)) {
            return ImmutableSet.of(MODULENAME);
        }
        return Collections.emptySet();
    }

    @Override
    public ResourceUrn redirect(ResourceUrn urn) {
        return urn;
    }

    @Override
    public Optional<PrefabData> getAssetData(ResourceUrn urn) throws IOException {
        if (MODULENAME.equals(urn.getModuleName()) && RESOURCENAME.equals(urn.getResourceName()) && !urn.getFragmentName().isEmpty()) {
            String[] splitFragment = urn.getFragmentName().toString().split(DELIMITER);
            if (splitFragment.length == 2) {
                String itemPrefabName = splitFragment[0];
                String materialPrefabName = splitFragment[1];

                Prefab prefab = Assets.getPrefab(itemPrefabName).get();
                Prefab substancePrefab = Assets.getPrefab(materialPrefabName).get();
                SubstanceComponent substanceComponent = substancePrefab.getComponent(SubstanceComponent.class);
                MaterialItemComponent materialItemComponent = prefab.getComponent(MaterialItemComponent.class);
                DisplayNameComponent displayNameComponent = prefab.getComponent(DisplayNameComponent.class);

                if (substanceComponent != null && materialItemComponent != null) {
                    PrefabData outputPrefabdata = PrefabData.createFromPrefab(prefab);

                    // update what this item is made of
                    MaterialItemComponent newMaterialItemComponent = new MaterialItemComponent();
                    newMaterialItemComponent.icon = materialItemComponent.icon;
                    outputPrefabdata.addComponent(newMaterialItemComponent);

                    MaterialCompositionComponent materialCompositionComponent = new MaterialCompositionComponent();
                    materialCompositionComponent.addSubstance(substancePrefab, 1f);
                    outputPrefabdata.addComponent(materialCompositionComponent);

                    DisplayNameComponent newDisplayNameComponent = new DisplayNameComponent();
                    newDisplayNameComponent.name = substanceComponent.name + " " + displayNameComponent.name;
                    outputPrefabdata.addComponent(newDisplayNameComponent);

                    return Optional.of(outputPrefabdata);
                }
            }
        }
        return Optional.empty();
    }
}
