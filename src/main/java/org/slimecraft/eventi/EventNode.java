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
    private final List<EventNode> children;

    public EventNode() {
        listenerBuilders = new ArrayList<>();
        listeners = new ArrayList<>();
        listenerMethods = new HashMap<>();
        children = new ArrayList<>();
    }

    public @NotNull
    static EventNode global() {
        return Global.INSTANCE;
    }

    public void addListener(@NotNull Object listener) {
        if (listenerMethods.containsKey(listener))
            throw new IllegalStateException("Listener was already registered");
        listenerMethods.computeIfAbsent(listener, o -> new ArrayList<>()).addAll(Arrays.stream(listener.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Listener.class)).peek(m -> m.setAccessible(true)).toList());
        listenerMethods.get(listener).forEach(method -> {
            final Class<?>[] params = method.getParameterTypes();
            if (params.length != 1) {
                throw new IllegalArgumentException("The method must have exactly 1 parameter");
            }
            final EventListener<?> listenerToAdd = new EventListener<>(params[0]);
            listenerToAdd.addHandler(t -> {
                try {
                    method.invoke(listener, t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Method could not be invoked; please check the method signature", e);
                }
            });
            listeners.add(listenerToAdd);
        });
    }

    private static final class Global {
        public static final EventNode INSTANCE = new EventNode();
    }

    public <T> void fire(@NotNull T event) {
        List<EventListener<?>> allListeners = new ArrayList<>(listenerBuilders.stream().map(EventListenerBuilder::build).toList());
        allListeners.addAll(listeners);

        allListeners.forEach(l -> {
            if (l.getClazz() != event.getClass()) return;
            l.getHandlers().stream().map(h -> (Consumer<T>) h).forEach(h -> h.accept(event));
        });

        getChildren().forEach(c -> fire(event));
    }

    public @NotNull <T> EventListenerBuilder<T> addListener(@NotNull Class<T> clazz) {
        final EventListenerBuilder<T> builder = new EventListenerBuilder<>(clazz);
        listenerBuilders.add(builder);
        return builder;
    }

    public <T> void addListener(@NotNull EventListener<T> listener) {
        listeners.add(listener);
    }

    public void addChild(@NotNull EventNode child) {
        children.add(child);
    }

    public @NotNull List<EventNode> getChildren() {
        return children;
    }
}