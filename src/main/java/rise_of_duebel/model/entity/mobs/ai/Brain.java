package rise_of_duebel.model.entity.mobs.ai;

import rise_of_duebel.model.entity.mobs.EntityMob;

import java.util.ArrayList;
import java.util.List;

public class Brain<T extends EntityMob<?>> {

    private List<MemoryModule<?>> memories = new ArrayList<>();

    public Brain() {

    }

    public <T> void forget(MemoryModule<T> memory, T value) {

    }

    public <T> void forgetAll(MemoryModule<T> memory) {

    }

    public <T> void remember(MemoryModule<T> memory, T value) {

    }

    public List<MemoryModule<?>> getMemories() {
        return this.memories;
    }
}
