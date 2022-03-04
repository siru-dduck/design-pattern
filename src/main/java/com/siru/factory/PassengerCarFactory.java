package com.siru.factory;

public class PassengerCarFactory implements CarFactory {

    @Override
    public Car newCar(String color) {
        Car car = new PassengerCar();
        car.setColor(color);

        return car;
    }

}
