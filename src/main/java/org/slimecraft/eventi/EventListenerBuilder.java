package org.slimecraft.eventi;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Builds an {@link EventListener}.
 * @param <T> The type to use for the event listener.
 */
public final class EventListenerBuilder<T> {
    private final EventListener<T> listener;

    public EventListenerBuilder(EventListener<T> listener) {
        this.listener = listener;
    }

    public EventListenerBuilder(Class<T> clazz) {
        this(new EventListener<>(clazz));
    }

    public void handle(Consumer<T> handler) {
        listener.addHandler(handler);
    }

    public void filter(Predicate<T> filter) {
        listener.addFilter(filter);
    }

    public EventListener<T> build() {
        return listener;
    }
}
