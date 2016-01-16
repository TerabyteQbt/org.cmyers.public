package org.cmyers.nappysak.util;

/**
 * This class just holds a reference to some return value
 *
 * @author cmyers
 *
 * @param <T>
 */
public class ReturnValue<T> {

    private T obj;

    public ReturnValue() {
        this.obj = null;
    }

    public ReturnValue(T value) {
        this.obj = value;
    }

    public T getValue() {
        return obj;
    }

    public void setValue(T value) {
        obj = value;
    }

    // convenience ctors
    public static <T> ReturnValue<T> create() {
        return new ReturnValue<T>();
    }

    public static <T> ReturnValue<T> create(T value) {
        return new ReturnValue<T>(value);
    }
}
