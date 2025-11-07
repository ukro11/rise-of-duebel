package project_base.event.services;

public interface EventProcessCallback<T> {
    default void onSuccess(T data) {}
    default void onFailure(Throwable e) {}
}
