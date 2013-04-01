package de.jowisoftware.sshclient.application.settings.persistence.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PersistenceAnnotationTraverser {
    private PersistenceAnnotationTraverser() { /* this utility class cannot be initialized */ }

    public static void notifySafe(final Object object) {
        notifyByAnnotation(object, PersistPreSave.class);
    }

    public static void notifyLoad(final Object object) {
        notifyByAnnotation(object, PersistPostLoad.class);
    }

    private static void notifyByAnnotation(final Object object, final Class<? extends Annotation> annotationClass) {
        for (final Method method : object.getClass().getMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                try {
                    method.invoke(object);
                } catch (IllegalAccessException|InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void traverseObject(final Object object, final PersistCallback callback) {
        for (final Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Persist.class)) {
                final Persist annotation = field.getAnnotation(Persist.class);
                field.setAccessible(true);

                final String name;
                final String annotationName = annotation.value();
                if (annotationName.isEmpty()) {
                    name = field.getName();
                } else {
                    name = annotationName;
                }

                doCallback(object, callback, field, annotation, name);
            }
        }
    }

    private static void doCallback(final Object object, final PersistCallback callback, final Field field,
            final Persist annotation, final String name) {
        if (annotation.traversalType().equals(TraversalType.LIST)) {
            callback.foundList(field, object, annotation, name);
        } else if (annotation.traversalType().equals(TraversalType.MAP)) {
            callback.foundMap(field, object, annotation, name);
        } else if (annotation.traversalType().equals(TraversalType.RECURSIVE)) {
            callback.foundSubObject(field, object, annotation, name);
        } else {
            callback.foundField(field, object, annotation, name);
        }
    }
}

