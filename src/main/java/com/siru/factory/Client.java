package com.siru.factory;

public class Client {

    public static void main(String[] args) {
        Car passengerCar = new PassengerCarFactory().createCar("passenger car", "red");
        Car sportsCar = new SportsCarFactory().createCar("sports car", "green");

        System.out.println(passengerCar);
        System.out.println(sportsCar);
    }
}
