package org.slimecraft.eventi;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Builds an {@link EventListener}.
 * @param <T> The type to use for the event listener.
 */
public final class EventListenerBuilder<T> {
    private final EventListener<T> listener;

    public EventListenerBuilder(@NotNull EventListener<T> listener) {
        this.listener = listener;
    }

    public EventListenerBuilder(@NotNull Class<T> clazz) {
        this(new EventListener<>(clazz));
    }

    public void handle(@NotNull Consumer<T> handler) {
        listener.addHandler(handler);
    }

    public void filter(@NotNull Predicate<T> filter) {
        listener.addFilter(filter);
    }

    public @NotNull EventListener<T> build() {
        return listener;
    }
}
