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
import org.terasology.engine.entitySystem.Component;

import java.util.Map;

public class InjectSubstancesComponent implements Component {
    /**
     * A map of substance prefab and how much is added
     */
    public Map<String, Float> add = Maps.newHashMap();

    /**
     * Straight out change one substance to another
     */
    public Map<String, String> replace = Maps.newHashMap();

}
