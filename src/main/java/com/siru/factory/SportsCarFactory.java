package com.siru.factory;

public class SportsCarFactory implements CarFactory {

    @Override
    public Car newCar(String color) {
        Car car = new SportsCar();
        car.setColor(color);
        return car;
    }

}
