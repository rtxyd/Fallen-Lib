package net.rtxyd.fallen.lib.util.call;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class BiThreadCallBox {

    @SuppressWarnings("rawtypes")
    private final Map[] storage = new Map[] {new HashMap<>(), new HashMap<>()};
    protected final Thread[] threads = new Thread[] {null, null};

    protected abstract void initThread(int index, Thread thread);

    @SuppressWarnings("rawtypes")
    private Map getCallBox() {
        Thread t = Thread.currentThread();
        if (threads[0] == t) return storage[0];
        if (threads[1] == t) return storage[1];
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> Callable<T> submit(ContextKey<T> key, Callable<T> value) {
        Map<ContextKey<T>, Callable<T>> b = getCallBox();
        if (b == null) return null;
        return b.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> Callable<T> get(ContextKey<T> key) {
        Map<ContextKey<T>, Callable<T>> b = getCallBox();
        if (b == null) return null;
        return b.get(key);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getAndCallIfPresent(ContextKey<T> key, Consumer<Exception> handleEx) {
        Map<ContextKey<T>, Callable<T>> b = getCallBox();
        if (b == null) return null;
        Callable<T> call = b.get(key);
        if (call != null) {
            try {
                return call.call();
            } catch (Exception e) {
                handleEx.accept(e);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T takeAndCallIfPresent(ContextKey<T> key, Consumer<Exception> handleEx) {
        Map<ContextKey<T>, Callable<T>> b = getCallBox();
        if (b == null) return null;
        Callable<T> call = b.remove(key);
        if (call != null) {
            try {
                return call.call();
            } catch (Exception e) {
                handleEx.accept(e);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> Callable<T> take(ContextKey<Callable<T>> key) {
        Map<ContextKey<T>, Callable<T>> b = getCallBox();
        if (b == null) return null;
        return b.remove(key);
    }

    @SuppressWarnings("rawtypes")
    public void remove(ContextKey<?> key) {
        Map b = getCallBox();
        if (b != null) b.remove(key);
    }

    @SuppressWarnings("rawtypes")
    public void clear() {
        Thread t = Thread.currentThread();
        if (threads[0] == t) {
            storage[0].clear();
        } else if (threads[1] == t) {
            storage[1].clear();
        }
    }

    @SuppressWarnings("rawtypes")
    public boolean isEmpty() {
        Map b = getCallBox();
        return b == null || b.isEmpty();
    }
}
