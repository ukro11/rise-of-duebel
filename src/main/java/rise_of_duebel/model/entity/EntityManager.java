package rise_of_duebel.model.entity;

import org.dyn4j.world.PhysicsWorld;
import org.dyn4j.world.World;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.graphics.level.spawner.ObjectSpawner;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.impl.GameScene;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private final World<ColliderBody> world;
    private final List<Entity<?>> entities;

    public EntityManager() {
        this.world = new World<ColliderBody>();
        this.world.setGravity(PhysicsWorld.EARTH_GRAVITY.getNegative());
        this.entities = new ArrayList<>();
    }

    public EntityPlayer spawnPlayer(double x, double y) {
        EntityPlayer player = new EntityPlayer(this.world, x, y, 192, 128);
        ObjectSpawner.objects.forEach(obj -> obj.onRegisterPlayer(player));
        this.entities.add(player);
        return player;
    }

    public void destroy(Entity entity) {
        this.world.removeBody(entity.getBody());
        GameScene.getInstance().getRenderer().unregister(entity);
    }

    public void updateWorld(double dt) {
        //this.world.update(dt);
        try {
            this.world.step(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Entity<?>> getEntities() {
        return this.entities;
    }

    public List<EntityPlayer> getPlayerEntities() {
        return this.entities.stream().filter(e -> e instanceof EntityPlayer).map(e -> (EntityPlayer) e).toList();
    }

    public World<ColliderBody> getWorld() {
        return this.world;
    }

    public boolean playerDown() {
        return false;
    }
}
