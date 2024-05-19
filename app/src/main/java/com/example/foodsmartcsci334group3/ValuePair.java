package com.example.foodsmartcsci334group3;

public class ValuePair<T, V> {
    private T label;
    private V value;
    public ValuePair(T inLabel, V inValue) {
        label = inLabel;
        value = inValue;
    }

    public T getLabel() {
        return label;
    }

    public V getValue() {
        return value;
    }

    public void setLabel(T inLabel) {
        label = inLabel;
    }

    public void setValue(V inValue) {
        value = inValue;
    }
}

