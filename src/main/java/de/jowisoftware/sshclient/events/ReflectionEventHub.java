package de.jowisoftware.sshclient.events;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

public class ReflectionEventHub<T> implements EventHub<T> {
    private final List<T> listeners;
    private final T proxy;

    private ReflectionEventHub(final Class<T> clazz) {
        listeners = new LinkedList<>();

        final InvocationHandler handler = createInvocationHandler();
        try {
            proxy = setupProxy(clazz, handler);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> EventHub<T> forEventClass(final Class<T> clazz) {
        return new ReflectionEventHub<>(clazz);
    }

    @SuppressWarnings("unchecked")
    private T setupProxy(final Class<T> clazz, final InvocationHandler handler)
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        final Class<?> proxyClass = Proxy.getProxyClass(
                getClass().getClassLoader(), clazz);
        return (T) proxyClass.
                getConstructor(new Class[] { InvocationHandler.class }).
                newInstance(handler);
    }

    private InvocationHandler createInvocationHandler() {
        return new InvocationHandler() {
            @Override
            public Object invoke(final Object proxyObject,
                    final Method method, final Object[] args) throws Throwable {
                for (final T listener : listeners) {
                    method.invoke(listener, args);
                }
                return null;
            }
        };
    }

    @Override
    public void register(final T listener) {
        listeners.add(listener);
    }

    @Override
    public T fire() {
        return proxy;
    }
}
