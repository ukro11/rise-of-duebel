package rise_of_duebel.model.entity;

import org.dyn4j.world.World;
import rise_of_duebel.dyn4j.ColliderBody;
import rise_of_duebel.graphics.spawner.ObjectSpawner;
import rise_of_duebel.model.entity.player.EntityPlayer;

public class EntityManager {

    private final World<ColliderBody> world;

    public EntityManager() {
        this.world = new World<ColliderBody>();
    }

    public EntityPlayer spawnPlayer(double x, double y) {
        EntityPlayer player = new EntityPlayer(this.world, x, y, 192, 128);
        ObjectSpawner.objects.forEach(obj -> obj.onRegisterPlayer(player));
        return player;
    }
}
