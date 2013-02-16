package de.jowisoftware.sshclient.persistence.annotations;

import java.lang.reflect.Field;

public interface PersistCallback {
    void foundField(Field field, Object object, Persist annotation, String name);
    void foundList(Field field, Object object, Persist annotation, String name);
    void foundSubObject(Field field, Object object, Persist annotation, String name);
    void foundMap(Field field, Object object, Persist annotation, String name);
}
