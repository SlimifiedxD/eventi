package org.slimecraft.eventi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method will handle events.
 * It should take one parameter, which is the type of the event.
 * It will be called when an event of that type is fired.
 * For example:
 * {@snippet :
 * void personChangeName(PersonChangeNameEvent e) {
 *     // use the event
 * }
 *}
 * Methods annotated with this annotation should only ever have one parameter, which is the type
 * of the event.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
}
