package rise_of_duebel.dyn4j;

import org.dyn4j.dynamics.BodyFixture;
import rise_of_duebel.graphics.level.spawner.ObjectIdResolver;

import java.util.Arrays;

public class PhysicsUtils {

    public static boolean is(ColliderBody body, ColliderBody other) {
        return body.getUserData().equals(other.getUserData());
    }

    public static boolean is(BodyFixture body, BodyFixture other) {
        return body.getUserData().equals(other.getUserData());
    }

    public static boolean is(ColliderBody body, String... types) {
        for (Object type : types) {
            if (body.getUserData().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean is(BodyFixture body, BodyFixture... types) {
        for (BodyFixture type : types) {
            if (body.getUserData().equals(type.getUserData())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEntity(ColliderBody body) {
        return ((String) body.getUserData()).toUpperCase().startsWith("ENTITY_");
    }

    public static boolean isGround(ColliderBody body) {
        if (!(body instanceof WorldCollider)) return false;
        return ((WorldCollider) body).getResolver() != null && ((WorldCollider) body).getResolver().getTypeSpawner() == ObjectIdResolver.MapSpawner.PLATFORM;
    }

    public static boolean contains(String userdata, BodyFixture... other) {
        return Arrays.stream(other).anyMatch(o -> o.getUserData().equals(userdata));
    }
}
