package duebel_level.graphics.level;

import duebel_level.model.transitions.Transition;

public record LevelSwitch(String id, LevelSwitchDirection dir, LevelMap last, LevelMap next, Runnable runnable, Transition<LevelMap> transition) {
    public enum LevelSwitchDirection {
        NEXT,
        PREVIOUS
    }
}
