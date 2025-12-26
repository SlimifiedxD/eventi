package org.slimecraft.eventi;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a listener of an event. A handler will be executed when this listener receives
 * the event, that is, when it is fired. Filters allow for keeping handlers tidy, only executing
 * logic if it should be executed.
 * One is typically constructed by using the {@link EventListenerBuilder} which is typically
 * obtained through the {@link EventNode#addListener(Class)} method.
 * @param <T> The type to listen to.
 */
public final class EventListener<T> {
    private Class<T> clazz;
    private final List<Consumer<T>> handlers;
    private final List<Predicate<T>> filters;

    public EventListener(@NotNull Class<T> clazz) {
        this.clazz = clazz;
        handlers = new ArrayList<>();
        filters = new ArrayList<>();
    }

    public void addHandler(@NotNull Consumer<T> handler) {
        handlers.add(handler);
    }

    public @NotNull List<Consumer<T>> getHandlers() {
        return handlers;
    }

    public void addFilter(@NotNull Predicate<T> filter) {
        filters.add(filter);
    }

    public @NotNull List<Predicate<T>> getFilters() {
        return filters;
    }

    public @NotNull Class<T> getClazz() {
        return clazz;
    }
}
