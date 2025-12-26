package org.slimecraft.eventi;

import org.jetbrains.annotations.NotNull;
import org.slimecraft.eventi.annotation.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

/**
 * An event node is where events are fired: {@link #fire(Object)}
 * and subscribed to: {@link #addListener(Class)}. The {@link #global()}
 * method is provided for a convenient quick-and-dirty solution.
 * Soon, however, event nodes will allow for children, meaning that
 * events will be able to be scoped to certain nodes, allowing for cleaner code.
 */
public final class EventNode {
    private final List<EventListenerBuilder<?>> listenerBuilders;
    private final List<EventListener<?>> listeners;
    private final Map<Object, List<Method>> listenerMethods;

    public EventNode() {
        listenerBuilders = new ArrayList<>();
        listeners = new ArrayList<>();
        listenerMethods = new HashMap<>();
    }

    public @NotNull static EventNode global() {
        return Global.INSTANCE;
    }

    public void addListener(@NotNull Object listener) {
        listenerMethods.computeIfAbsent(listener, o -> new ArrayList<>()).addAll(Arrays.stream(listener.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Listener.class)).peek(m -> m.setAccessible(true)).toList());
    }

    private static final class Global {
        public static final EventNode INSTANCE = new EventNode();
    }

    public <T> void fire(@NotNull T event) {
        listenerMethods.forEach((o, methods) -> {
            methods.forEach(method -> {
                try {
                    method.invoke(o, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        });

        List<EventListener<?>> allListeners = new ArrayList<>(listenerBuilders.stream().map(EventListenerBuilder::build).toList());
        allListeners.addAll(listeners);

        allListeners.forEach(l -> {
            if (l.getClazz() != event.getClass()) return;
            l.getHandlers().stream().map(h -> (Consumer<T>) h).forEach(h -> h.accept(event));
        });
    }

    public @NotNull <T> EventListenerBuilder<T> addListener(@NotNull Class<T> clazz) {
        final EventListenerBuilder<T> builder = new EventListenerBuilder<>(clazz);
        listenerBuilders.add(builder);
        return builder;
    }

    public <T> void addListener(EventListener<T> listener) {
        listeners.add(listener);
    }
}