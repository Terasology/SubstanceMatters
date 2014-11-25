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

import org.terasology.asset.AssetFactory;
import org.terasology.asset.AssetResolver;
import org.terasology.asset.AssetType;
import org.terasology.asset.AssetUri;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabData;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.naming.Name;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;
import org.terasology.substanceMatters.components.SubstanceComponent;

public class MaterialItemAssetResolver implements AssetResolver<Prefab, PrefabData> {

    @Override
    public AssetUri resolve(Name partialUri) {
        String[] parts = partialUri.toLowerCase().split("\\.", 2);
        if (parts.length == 2) {
            AssetUri uri = Assets.resolveAssetUri(AssetType.PREFAB, parts[0]);
            if (uri != null) {
                return new AssetUri(AssetType.PREFAB, uri.getModuleName(), partialUri);
            }
        }
        return null;
    }

    @Override
    public Prefab resolve(AssetUri uri, AssetFactory<PrefabData, Prefab> factory) {
        String[] parts = uri.getAssetName().toLowerCase().split("\\.", 2);
        if (parts.length == 2) {
            Prefab prefab = Assets.getPrefab(parts[0]);
            if (prefab == null) {
                return null;
            }
            Prefab substancePrefab = Assets.getPrefab(parts[1]);
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

                return Assets.generateAsset(uri, outputPrefabdata, Prefab.class);
            }
        }
        return null;
    }
}