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
package org.terasology.substanceMatters.systems;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.asset.Assets;
import org.terasology.assets.AssetDataProducer;
import org.terasology.assets.ResourceUrn;
import org.terasology.assets.module.annotations.RegisterAssetDataProducer;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabData;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.naming.Name;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;
import org.terasology.tintOverlay.TintOverlayTextureProducer;

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
