package rise_of_duebel.model.entity;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.model.entity.impl.EntityPlayer;
import rise_of_duebel.model.scene.impl.GameScene;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private final World<ColliderBody> world;
    private final List<Entity<?>> entities;
    private double accumulator;

    public EntityManager() {
        this.world = new World<ColliderBody>();
        this.world.setGravity(new Vector2(0, 900));
        this.entities = new ArrayList<>();
    }

    public EntityPlayer spawnPlayer(double x, double y) {
        EntityPlayer player = new EntityPlayer(this.world, x, y, 192, 128);
        this.entities.add(player);
        return player;
    }

    public void destroy(Entity entity) {
        this.world.removeBody(entity.getBody());
        GameScene.getInstance().getRenderer().unregister(entity);
    }

    public void updateWorld(double dt) {
        double fixedDt = 1.0 / 400.0;
        this.accumulator += dt;

        while (accumulator >= fixedDt) {
            try {
                this.world.step(1, fixedDt);
                accumulator -= fixedDt;
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}
