package rise_of_duebel.dyn4j;

public class PhysicsUtils {

    public static boolean is(ColliderBody body, String... types) {
        for (Object type : types) {
            if (body.getUserData().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEntity(ColliderBody body) {
        return ((String) body.getUserData()).toUpperCase().startsWith("ENTITY_");
    }

    public static boolean isGround(ColliderBody body) {
        return ((String) body.getUserData()).toUpperCase().startsWith("GROUND_");
    }
}
