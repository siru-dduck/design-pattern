# 팩토리 패턴

## 목차
1. 개요
2. 팩토리 패턴 적용 
---

## 1.개요
| Car.java
```java
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
```
| Client.java
```java
public class Client {

    public static void main(String[] args) {
        Car passengerCar = CarFactory.createCar("passenger car", "red");
        Car sportsCar = CarFactory.createCar("sports car", "green");

        System.out.println(passengerCar);
        System.out.println(sportsCar);
    }
}
```
| CarFactory.java
```java
public class CarFactory {

    public static Car createCar(String kind, String color) {
        // validate
        if(kind == null || kind.isBlank()) {
            throw new IllegalArgumentException("만들려는 차 종류를 입력해주세요.");
        }

        if(color == null || color.isBlank()) {
            throw new IllegalArgumentException("만들려는 차 색을 입력해주세요.");
        }

        Car car = new Car();

        if("passenger car".equals(kind)) {
            car.setKind("passenger car");
        } else if("sports car".equals(kind)) {
            car.setKind("sports car");
        }

        car.setColor(color);


        return car;
    }

}
```
위 예제는 단순한 예제다 `Client`에서 `CarFactory`로 부터 'passenger car', 'sports' 자동차를 만들어 단순하게 출력한 예제이다. `CarFactory`는
현제 두 종류의 자동차를 생산할 수 있다. 하지만 자동차의 종류가 많아지고 요구사항이 증가하게 되면 너저분한 조건문과 코드를 계속해서 수정해야하는 문제가 생긴다.
확장에는 열려있고 변경에 닫혀있는 개방-폐쇄 원칙(OCP: open-closed principle)을 위배한다. 이를 팩토리 패턴으로 해결해보자.

---

## 2. 팩토리 패턴 적용
```java
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
```
기존의 `CarFactory`를 인터페이스로 나누었다. 클라이언트는 `createCar`메소드를 통해 `Car`객체를 얻는건 동일하다.
하지만 이전과 달리 유효성을 검증하는 부분, 실제 `Car`객체를 만들고 세팅하는 부분이 메소드단위로 잘 추상화가 되어있고
유효성 검증처럼 공통로직은 인터페이스 내부에 따로 공통으로 사용하는 메소드를 두고 변경이 잦은 `Car`생성부분은
추상 메소드로 따로 분리를 해두었다. 추상메소드는 각각 다른 종류의 `Car`객체를 생성하는 `CarFactory`인터페이스를 구현하는
클래스를 만들어 구현하면 된다. 아래 코드를 보자.

```java
public class PassengerCar extends Car {

    public PassengerCar() {
        setKind("passenger car");
    }
}

public class PassengerCarFactory implements CarFactory {

    @Override
    public Car newCar(String color) {
        Car car = new PassengerCar();
        car.setColor(color);

        return car;
    }

}

public class SportsCar extends Car {

    public SportsCar() {
        setKind("sports car");
    }
}


public class SportsCarFactory implements CarFactory {

    @Override
    public Car newCar(String color) {
        Car car = new SportsCar();
        car.setColor(color);
        return car;
    }

}
```
`Car`를 상속하는 `SportCar`와 `PassengerCar`를 만들고 각각의 `Car`객체를 생성하는
팩토리를 만들면 된다.

```java
public class Client {

    public static void main(String[] args) {
        Car passengerCar = new PassengerCarFactory().createCar("passenger car", "red");
        Car sportsCar = new SportsCarFactory().createCar("sports car", "green");

        System.out.println(passengerCar);
        System.out.println(sportsCar);
    }
}
```
다만 아쉬운점은 변경에 닫힌 코드를 위해 팩토리 패턴을 구현했지만 정작 클라이언트 코드에는 특정 클래스 타입을 의존해 변경이
발생했다. 이는 의존성 주입을 이용하면 쉽게 해결할 수 있다. 그러기 위해서는 먼저 인터페이스를 적용할 필요가 있다.