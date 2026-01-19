package duebel_level.event.services;

public interface EventProcessCallback<T> {
    default void onSuccess(T data) {}
    default void onFailure(Throwable e) {}
}
