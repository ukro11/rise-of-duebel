package rise_of_duebel.model.entity;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.graphics.spawner.ObjectSpawner;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.GameScene;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class EntityManager {

    private final World<ColliderBody> world;
    private final Map<String, Entity<?>> entities;

    public EntityManager() {
        this.world = new World<ColliderBody>();
        this.world.setGravity(0, 30);
        this.entities = new HashMap<>();
    }

    public EntityPlayer spawnPlayer(double x, double y) {
        EntityPlayer player = new EntityPlayer(this.world, x, y, 192, 128);
        ObjectSpawner.objects.forEach(obj -> obj.onRegisterPlayer(player));
        this.entities.put(player.id, player);
        return player;
    }

    public void destroy(Entity entity) {
        this.world.removeBody(entity.getBody());
        GameScene.getInstance().getRenderer().unregister(entity);
    }

    public void updateWorld(double dt) {
        this.world.update(dt);
    }

    public Map<String, Entity<?>> getEntities() {
        return this.entities;
    }

    public World<ColliderBody> getWorld() {
        return this.world;
    }
}
