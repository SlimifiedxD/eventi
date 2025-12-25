package org.slimecraft.eventi;

import io.github.classgraph.*;
import org.slimecraft.eventi.annotation.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public final class EventNode {
    private final List<EventListenerBuilder<?>> listenerBuilders;
    private final List<EventListener<?>> listeners;
    private final Map<Object, List<Method>> listenerMethods;

    public EventNode() {
        listenerBuilders = new ArrayList<>();
        listeners = new ArrayList<>();
        listenerMethods = new HashMap<>();
    }

    public static EventNode global() {
        return Global.INSTANCE;
    }

    public void addListener(Object listener) {
        listenerMethods.computeIfAbsent(listener, o -> new ArrayList<>()).addAll(Arrays.stream(listener.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Listener.class)).peek(m -> m.setAccessible(true)).toList());
    }

    private static final class Global {
        public static final EventNode INSTANCE = new EventNode();
    }

    public <T> void fire(T event) {
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

    public <T> EventListenerBuilder<T> addListener(Class<T> clazz) {
        final EventListenerBuilder<T> builder = new EventListenerBuilder<>(clazz);
        listenerBuilders.add(builder);
        return builder;
    }

    public <T> void addListener(EventListener<T> listener) {
        listeners.add(listener);
    }
}