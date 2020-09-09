// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.substanceMatters.systems;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.prefab.PrefabManager;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.engine.logic.console.commandSystem.annotations.Sender;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.logic.inventory.events.GiveItemEvent;
import org.terasology.engine.logic.permission.PermissionManager;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.substanceMatters.components.MaterialCompositionComponent;
import org.terasology.substanceMatters.components.MaterialItemComponent;

@RegisterSystem
public class SubstanceMattersCommands extends BaseComponentSystem {
    @In
    PrefabManager prefabManager;
    @In
    EntityManager entityManager;

    @Command(shortDescription = "Adds a material item to your inventory", runOnServer = true,
            requiredPermission = PermissionManager.CHEAT_PERMISSION)
    public String giveMaterialItem(
            @Sender EntityRef client,
            @CommandParam("prefab") String itemPrefabName,
            @CommandParam("material") String material,
            @CommandParam(value = "materialAmount", required = false) Float materialAmount) {
        Prefab prefab = prefabManager.getPrefab(itemPrefabName);
        if (prefab != null && prefab.getComponent(ItemComponent.class) != null) {
            EntityRef item = entityManager.create(prefab);

            if (!item.hasComponent(MaterialItemComponent.class)) {
                return "Item is not a material item";
            }

            MaterialCompositionComponent materialCompositionComponent = new MaterialCompositionComponent();
            materialCompositionComponent.addSubstance(material, materialAmount == null ? 1f : materialAmount);
            item.addComponent(materialCompositionComponent);

            EntityRef playerEntity = client.getComponent(ClientComponent.class).character;
            GiveItemEvent giveItemEvent = new GiveItemEvent(playerEntity);
            item.send(giveItemEvent);
            if (!giveItemEvent.isHandled()) {
                item.destroy();
            }
            return "You received a material item of " + prefab.getName();
        } else {
            return "Item not found";
        }
    }
}
