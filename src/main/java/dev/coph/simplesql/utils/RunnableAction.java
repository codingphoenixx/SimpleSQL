package dev.coph.simplesql.utils;

import lombok.SneakyThrows;

public interface RunnableAction<T> {

    void run(T t) throws Exception;

}
