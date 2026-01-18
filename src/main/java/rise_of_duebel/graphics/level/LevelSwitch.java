package rise_of_duebel.graphics.level;

import rise_of_duebel.model.transitions.Transition;

public record LevelSwitch(String id, LevelSwitchDirection dir, LevelMap last, LevelMap next, Runnable runnable, Transition<LevelMap> transition) {
    public enum LevelSwitchDirection {
        NEXT,
        PREVIOUS
    }
}
