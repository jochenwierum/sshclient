package de.jowisoftware.sshclient.persistence.annotations;

import java.lang.reflect.Field;

public class PersistenceAnnotationTraverser {
    public static void traverseObject(final Object object, PersistCallback callback) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Persist.class)) {
                Persist annotation = field.getAnnotation(Persist.class);
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

