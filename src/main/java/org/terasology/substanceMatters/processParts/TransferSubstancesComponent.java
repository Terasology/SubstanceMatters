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

import org.terasology.entitySystem.Component;

/**
 * Creates an material item containing the materials that it is composed of based on the original input items.  The item will appear like the largest amount of substance.
 */
public class TransferSubstancesComponent implements Component {
    boolean extract = true;
    boolean inject = true;
}
