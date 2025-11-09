package project_base.model.entity;

import project_base.graphics.spawner.ObjectSpawner;
import project_base.model.entity.impl.player.EntityPlayer;
import project_base.physics.BodyType;
import project_base.physics.colliders.ColliderPolygon;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {

    private final Map<String, Entity> entities = new HashMap<>();

    public EntityPlayer spawnPlayer(String id, double x, double y) {
        ColliderPolygon collider = ColliderPolygon.createCIPolygon(id, BodyType.DYNAMIC, x, y, 20, 14, 4.5);
        collider.setColliderClass("entity_player");
        EntityPlayer player = new EntityPlayer(collider, -16, -52, 32, 64);
        collider.setEntity(player);
        ObjectSpawner.objects.forEach(obj -> obj.onRegisterPlayer(player));
        this.registerEntity(player);
        return player;
    }

    public void registerEntity(Entity entity) {
        if (entity != null) {
            this.entities.put(entity.getId(), entity);
        }
    }

    public void unregister(Entity entity) {
        if (entity != null) {
            this.entities.remove(entity.getId());
        }
    }


    public Map<String, Entity> getEntities() {
        return this.entities;
    }
}
