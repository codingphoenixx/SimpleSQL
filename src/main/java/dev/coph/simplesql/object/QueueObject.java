package dev.coph.simplesql.object;

import java.util.concurrent.CompletableFuture;

public class QueueObject<T> {

    private QueueObject appended;

    private final Action<T> action;

    public QueueObject(Action<T> action) {
        this.action = action;
    }

    private  void append(QueueObject<?> queueObject) {
        this.appended = queueObject;
    }


    public CompletableFuture<T> queue() {
        return CompletableFuture.supplyAsync(action::run);
    }

    public T complete() {
        return action.run();
    }

    public interface Action<T>{
        T run();
    }
}
