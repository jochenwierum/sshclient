package de.jowisoftware.sshclient.persistence;

import de.jowisoftware.sshclient.persistence.annotations.Persist;
import de.jowisoftware.sshclient.persistence.annotations.PersistCallback;
import de.jowisoftware.sshclient.persistence.annotations.PersistenceAnnotationTraverser;
import de.jowisoftware.sshclient.persistence.xml.DocumentReader;
import de.jowisoftware.sshclient.persistence.xml.XMLDocumentReader;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class ReadCallback implements PersistCallback {
    private final DocumentReader reader;

    public ReadCallback(final DocumentReader reader) {
        this.reader = reader;
    }

    @Override
    public void foundField(final Field field, final Object object, final Persist annotation, final String name) {
        try {
            final String value = reader.read(name);
            if (value != null) {
                Object valueObject = createValue(field.getType(), value);
                field.set(object, valueObject);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void foundList(final Field field, final Object object, final Persist annotation, final String name) {
        try {
            @SuppressWarnings("unchecked")
            final List<Object> list = (List<Object>) field.get(object);
            XMLDocumentReader.ListReader listReader = reader.readList(name, "item");

            DocumentReader itemReader = listReader.nextNode();
            while (itemReader != null) {
                Object listItem = restoreObject(itemReader, annotation);
                list.add(listItem);
                
                itemReader = listReader.nextNode();
            }
            
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object restoreObject(final DocumentReader itemReader, final Persist annotation) {
        if (annotation.traverseListAndMapChildrenRecursively()) {
            final Object targetObject;
            try {
                targetObject = annotation.targetClass().newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            PersistenceAnnotationTraverser.traverseObject(targetObject, new ReadCallback(itemReader));
            return targetObject;
        } else {
            return createValue(annotation.targetClass(), itemReader.read(""));
        }
    }

    @Override
    public void foundSubObject(final Field field, final Object object, final Persist annotation, final String name) {
        try {
            final Object subObject = field.get(object);
            PersistenceAnnotationTraverser.traverseObject(subObject, new ReadCallback(reader.readSubNode(name)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void foundMap(final Field field, final Object object, final Persist annotation, final String name) {
        try {
            @SuppressWarnings("unchecked")
            final Map<Object, Object> map = (Map<Object, Object>) field.get(object);
            XMLDocumentReader.ListReader listReader = reader.readList(name, "item");

            DocumentReader itemReader = listReader.nextNode();
            while (itemReader != null) {
                Object id = createValue(annotation.targetClass2(), itemReader.read("@id"));
                Object value = restoreObject(itemReader, annotation);
                map.put(id, value);

                itemReader = listReader.nextNode();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object createValue(final Class<?> type, final String value) {
        if (type.equals(String.class)) {
            return value;
        } else if (type.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if(type.equals(Integer.TYPE)) {
            return Integer.parseInt(value);
        } else if (type.equals(Boolean.TYPE)) {
            return Boolean.valueOf(value);
        } else if (type.equals(Color.class)) {
            return new Color(Integer.parseInt(value, 16));
        } else if (Enum.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            final Class<Enum> enumType = (Class<Enum>) type;
            return Enum.valueOf(enumType, value);
        } else {
            throw new RuntimeException("Could not set type " + type.getSimpleName());
        }
    }
}
