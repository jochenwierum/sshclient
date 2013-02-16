package de.jowisoftware.sshclient.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Persist {
    String value() default "";
    TraversalType traversalType() default TraversalType.SIMPLE;
    Class<?> targetClass() default Object.class;
    Class<?> targetClass2() default Object.class;
    boolean traverseListAndMapChildrenRecursively() default false;
}
