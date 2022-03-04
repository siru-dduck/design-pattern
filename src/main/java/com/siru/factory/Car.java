package com.siru.factory;

public class Car {

    private String kind;
    private String color;

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Car{" +
                "kind='" + kind + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
