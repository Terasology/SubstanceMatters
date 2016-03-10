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

import com.google.common.collect.Maps;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.workstation.process.ProcessPart;

import java.util.Map;

public class InjectSubstanceComponent implements Component, ProcessPart {
    /**
     * A map of substance prefab and how much is added
     */
    public Map<String, Float> add = Maps.newHashMap();

    public Map<String, String> replace = Maps.newHashMap();

    @Override
    public boolean validateBeforeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        MaterialCompositionComponent materialCompositionComponent = processEntity.getComponent(MaterialCompositionComponent.class);
        if (materialCompositionComponent == null) {
            materialCompositionComponent = new MaterialCompositionComponent();
        }

        // add new substances
        for (Map.Entry<String, Float> entry : add.entrySet()) {
            materialCompositionComponent.addSubstance(entry.getKey(), entry.getValue());
        }

        // replace any substances
        for (Map.Entry<String, String> replacement : replace.entrySet()) {
            if (materialCompositionComponent.contents.containsKey(replacement.getKey())) {
                materialCompositionComponent.replaceSubstance(replacement.getKey(), replacement.getValue());
            } else {
                return false;
            }
        }

        if (processEntity.hasComponent(MaterialCompositionComponent.class)) {
            processEntity.saveComponent(materialCompositionComponent);
        } else {
            processEntity.addComponent(materialCompositionComponent);
        }

        return true;
    }

    @Override
    public long getDuration(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
        return 0;
    }

    @Override
    public void executeStart(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
    }

    @Override
    public void executeEnd(EntityRef instigator, EntityRef workstation, EntityRef processEntity) {
    }
}
