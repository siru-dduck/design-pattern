# 싱글톤 패턴

### 목차
1. 개요
2. new를 이용한 객체생성의 문제점과 해결방안
3. 멀티스레드 환경에서도 안전한 객체생성하기
4. 싱글톤 패턴을 깨트리는 방법
   1. 리플랙션으로 싱글톤 깨트리기
   2. 직렬화와 역직렬화로 싱글톤 깨트리기
5. 안전하게 싱글톤을 구현하기
6. java, spring에서의 싱글톤 사례

## 개요
상글톤은 하나의 프로세스, 어플리케이션에서 반드시 객체의 유일성을 보장해주는 패턴이다. 쉽게 말하면 어떤 특정 클래스에 대해서 해당 클래스의 객체가 반드시
하나만 존재하도록 보장해주는 패턴이다. 그렇다면 싱글톤 패턴은 왜 필요한가? 객체가 반드시 하나만 존재하도록 해야되는 이유 혹은 케이스가 있는가?
가령 우리가 게임을 플레이하면 환경설정에서 게임의 볼륨, 수직동기화, 키 매핑등을 설정한다. 만약 설정을 담당하는 객체가 여러개라면 설정이 꼬일 염려가 있다. 
사용자는 분명 음량을 높였는데 음량조절이 안된다던지 하는 부작용이 있을 가능성이 있다. 이러한 문제를 방지하기위해 설정을 담당하는 객체를 반드시 하나만
보장할 수 있다.
---
## new를 이용한 객체생성의 문제점과 해결방안
```java
Settings settings1 = new Settings();
Settings settings2 = new Settings();
System.out.println(settings1 == settings2); // false -> Setting 객체는 유일하지 않다.
```
`new` 키워드를 이용해 객체를 위의 예제와 같이 생성할 경우 개발자가 설령 유일성을 보장하려는 의도를 가져도 실수에 의해 객체를 여러개를 생성할 염려가 있다.

```java
public class Settings {

    private static Settings instance;

    private Settings() {}

    public static Settings getInstance() {
        if(instance == null) {
            instance = new Settings();
        }
        return instance;
    }
}
```
`private` 키워드를 통해 명시적인 객체생성을 방지하고 `static` 메소드를 이용해 인스턴스가 생성된 적이 있는지 조건문을 걸고 생성된 객체가 없는 경우에만
객체를 `Settings`클래스 내부에서만 생성하고 관리하도록 함으로써 객체의 유일성을 보장할 수 있다. 하지만.. 이 코드의 문제점은 멀티스레드 환경에서는 인스턴스의
유일성을 보장할 수 없다. A, B 2개의 스레드가 있다고 가정해보자 A스레드가 최초로 `getInstance`메소드를 호출하고 조건문 안으로 진입한 순간 인터럽트가 
일어났다고 가정하자 그 다음으로 B스레드가 조건문 내부를 통과해 `Settings`객체를 생성하고 반환하고 다시 A 스레드가 cpu타임을 획득하고 객체를 만들었다고 보지
이 상황에서는 서로 다른 두 객체가 생성되었기에 객체의 유일성을 보장 못했다.

---

## 멀티스레드 환경에서도 안전한 객체생성하기
### synchronized 키워드를 이용한 동기화
```java
public class Settings {

    private static Settings instance;

    private Settings() {}

    // getInstance 메소드는 반드시 하나의 스레드만 접근가능
    public static synchronized Settings getInstance() {
        if(instance == null) {
            instance = new Settings();
        }
        return instance;
    }
}
```
위 예제는 멀티스레드에도 안전하다 이전의 `getInstance` 메소드에 `synchronized` 키워드를 붙여 하나의 스레드만 접근가능하도록 했다. 이 방법의 문제점은
여러 스레드가 접근할때 동기화로 인해 락 경합이 발생해 성능상의 불이익이 있다.

### 이른 초기화 (eager initialization)사용하기
```java
public class Settings {

    private static final Settings INSTANCE = new Settings();

    private Settings() {}

    public static synchronized Settings getInstance() {
        return INSTANCE;
    }
}
```
클래스 로딩 시점에 객체를 초기화 해주기 때문에 멀티스레드 환경에서 안전하다. 하지만 미리 객체를 생성하는 방식이 단점이 될 수 있다. 객체의 실제 사용시점 전에는
불필요한 객체를 생성하는 꼴이 된다.

### double checked locking
```java
public class Settings {
    
    private static volatile Settings instance;

    private Settings() {}

    public static Settings getInstance() {
        if(instance == null) {
            synchronized (Settings.class) {
                if(instance == null) {
                    instance = new Settings();
                }
            }
        }
        return instance;
    }
}
```
이전에 `synchronized` 키워드를 쓴 방식과 유사한데 동기화 블럭을 두번 걸었다. 이로 인한 장점은 객체가 이미 생성된 경우 많은 스레드가 `getInstance`를 
호출해도 락 경합이 일어나지 않고 객체를 필요할때 생성 할 수 있다는 장점이 있다. 아무래도 코드가 복잡하기도 하고 `volatile`키워드가 자바 1.5부터 지원하는
문제가 있다.

### static inner 클래스 사용하기
```java
public class Settings {

    private Settings() {}

    private static class SettingsHolder {
        private static final Settings INSTANCE = new Settings();
    }

    public static Settings getInstance() {
        return SettingsHolder.INSTANCE;
    }
}
```
실제로 인스턴스를 사용하려할때 클래스가 로딩되면서 객체를 세팅하고 멀티스레드에 안전한 권장된 싱글톤 예제이다.

---
## 싱글톤 패턴을 깨트리는 방법
### 1. 리플랙션으로 싱글톤 깨트리기
```java
Settings settings1 = Settings.getInstance();

Constructor<Settings> constructor = Settings.class.getDeclaredConstructor();
constructor.setAccessible(true);
Settings settings2 = constructor.newInstance(); // 인스턴스 새로 생성

System.out.println(settings1 == settings2); // false -> 서로 다른 객체를 의미
```
자바의 리플랙션을 이용해 생성자 정보를 뽑아내 인스턴스를 새로 만들어 인스턴스의 유일성을 깨트릴 수 있다.

### 2. 직렬화와 역직렬화로 싱글톤 깨트리기
```java
Settings settings1 = Settings.getInstance();
Settings settings2 = null;
try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream("settings.obj"))){
    out.writeObject(settings1);
}

try (ObjectInput in = new ObjectInputStream(new FileInputStream("settings.obj"))) {
    settings2 = (Settings) in.readObject();
}

System.out.println(settings1 == settings2); // false -> 서로 다른 객체를 의미
```
객체를 파일로 직렬화해서 저장하고 다시 읽어드릴때 인스턴스가 새로 생성되어 객체가 두개 이상 존재하게 된다. 하지만 이를 막을 수 있는 방법이 있다.
```java
public class Settings implements Serializable {

    private Settings() {}

    private static class SettingsHolder {
        private static final Settings INSTANCE = new Settings();
    }

    public static Settings getInstance() {
        return SettingsHolder.INSTANCE;
    }

    // 역직렬화시 호출하는 함수 
    protected Object readResolve() {
        return getInstance(); // 내부적을 싱글톤을 보장하는 getInstance 메소드를 호출한다.
    }
}
```
`readResolve`메소드를 구현해서 내부적으로 getInstance를 호출해 반환한다. 이러면 역직렬화시 `readResolve`를 호출하기 때문에 역직렬화시에도 싱글톤을
보증할 수 있다.

--- 
## 안전하게 싱글톤을 구현하기
```java
public enum Settings {
    INSTANCE;
}
```
리플랙션, 역직렬화에 안전하지만 lazy loading이 안된다는 단점이 있다.

---
## java, spring에서의 싱글톤 사례
- java `Runtime` 객체
- spring의 `ApplicationContext`를 통해 관리되는 빈(Bean)