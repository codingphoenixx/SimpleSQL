package dev.coph.simplesql.object;

import java.util.concurrent.CompletableFuture;

public class QueueObject<T> {

    private QueueObject<?> appended;

    private final Action<T> action;

    public QueueObject(Action<T> action) {
        this.action = action;
    }

    public void append(QueueObject<?> queueObject) {
        this.appended = queueObject;
    }

    public CompletableFuture<Void> queue() {
        return CompletableFuture.runAsync(action::run)
                .thenCompose(v -> {
                    if (appended != null) {
                        return appended.queue();
                    }
                    return CompletableFuture.completedFuture(null);
                });
    }

    public T complete() {
        T result = action.run();
        if (appended != null) {
            appended.complete();
        }
        return result;
    }

    public interface Action<T> {
        T run();
    }
}
