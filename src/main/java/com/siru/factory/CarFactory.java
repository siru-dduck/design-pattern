package com.siru.factory;

public interface CarFactory {

    default Car createCar(String kind, String color) {
        validate(kind, color);
        return newCar(color);
    }

    Car newCar(String color);

    private void validate(String kind, String color) {
        // validate
        if(kind == null || kind.isBlank()) {
            throw new IllegalArgumentException("만들려는 차 종류를 입력해주세요.");
        }

        if(color == null || color.isBlank()) {
            throw new IllegalArgumentException("만들려는 차 색을 입력해주세요.");
        }
    }
}
