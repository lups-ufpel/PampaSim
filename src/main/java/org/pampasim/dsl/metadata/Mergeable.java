package org.pampasim.dsl.metadata;

public interface Mergeable<T> {
    T merge(T other);
}
