package de.jowisoftware.sshclient.application.settings.persistence;

import de.jowisoftware.sshclient.application.settings.persistence.annotations.Persist;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.PersistCallback;
import de.jowisoftware.sshclient.application.settings.persistence.annotations.PersistenceAnnotationTraverser;
import de.jowisoftware.sshclient.application.settings.persistence.xml.DocumentWriter;
import de.jowisoftware.sshclient.application.settings.persistence.xml.XMLDocumentWriter;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class WriteCallback implements PersistCallback {
    private final DocumentWriter writer;

    public WriteCallback(final DocumentWriter writer) {
        this.writer = writer;
    }

    @Override
    public void foundField(final Field field, final Object object, final Persist annotation, final String name) {
        try {
            final Object valueObject = field.get(object);
            if (valueObject != null) {
                writer.write(name, createString(valueObject));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void foundList(final Field field, final Object object, final Persist annotation, final String name) {
        final XMLDocumentWriter.ListWriter subWriter = writer.writeList(name, "item");
        try {
            for (final Object listItem : (List<?>) field.get(object)) {
                if (listItem != null) {
                    if (annotation.traverseListAndMapChildrenRecursively()) {
                        PersistenceAnnotationTraverser.notifySafe(listItem);
                        PersistenceAnnotationTraverser.traverseObject(listItem, new WriteCallback(subWriter.add()));
                    } else {
                        subWriter.add().write("", createString(listItem));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void foundSubObject(final Field field, final Object object, final Persist annotation, final String name) {
        try {
            final Object valueObject = field.get(object);
            final DocumentWriter subWriter = writer.writeSubNode(name);
            if (valueObject != null) {
                PersistenceAnnotationTraverser.notifySafe(valueObject);
                PersistenceAnnotationTraverser.traverseObject(valueObject, new WriteCallback(subWriter));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void foundMap(final Field field, final Object object, final Persist annotation, final String name) {
        final XMLDocumentWriter.ListWriter listWriter = writer.writeList(name, "item");
        try {
            @SuppressWarnings("unchecked")
            final Map<Object, Object> objectMap = (Map<Object, Object>) field.get(object);
            for (final Map.Entry<Object, Object> mapItem : objectMap.entrySet()) {
                if (mapItem.getValue() != null) {
                    final DocumentWriter subWriter = listWriter.add();
                    subWriter.write("@id", createString(mapItem.getKey()));
                    if (annotation.traverseListAndMapChildrenRecursively()) {
                        PersistenceAnnotationTraverser.notifySafe(object);
                        PersistenceAnnotationTraverser.traverseObject(mapItem.getValue(), new WriteCallback(subWriter));
                    } else {
                        subWriter.write("", createString(mapItem.getValue()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String createString(final Object object) {
        if (object instanceof Color) {
            return Integer.toString(((Color) object).getRGB() & 0xffffff, 16);
        } else {
            return object.toString();
        }
    }
}
