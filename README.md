## Synchronized

Java는 멀티 스레드 환경에서 동시성 제어를 하기 위해 여러 방법을 지원한다.

- Lock
- Synchronized
- Concurrent Collections
- 등등

`Synchronized`는 동시성 제어를 위한 가장 기초적인 방법으로, 특정 객체를 잠금(lock)으로써 임계 영역을 설정한다.

```java
synchronized(obj) {
  // 임계 영역(critical section)
}
```

메서드 대상으로 `Synchronized`를 사용할 수 있지만 이 또한 클래스 객체를 잠금(lock) 하여 임계 영역을 설정하는 것이다.

```java
class foo {
  synchronized void someAction() {
    // code
  }
}

class foo {
  void someAction() {
    synchronized(this) {
      // code
    }
  }
}
```

## 주의 사항

하지만 `Synchronized`에 잠금 객체를 문자열(String)을 사용하는 경우 주의가 필요하다. 

**만약 다음과 같이 문자열을 “+” 연산자로 합치고 잠금 객체로 사용할 경우 문제가 발생한다.**

```java
public class SynchronizedString {

  private int value = 0;

  public void incrementValue(long id) {
    synchronized ("lock" + id) {
      value++;
      System.out.println("value: " + value);
    }
  }

  public int getValue() {
    return value;
  }
}

@Test
@DisplayName("리터럴 문자열 동기화 테스트")
void test_synchronized_literal_string() throws InterruptedException {
  // given
  SynchronizedString synchronizedString = new SynchronizedString();

  // when
  int count = 10000;
  ExecutorService executorService = Executors.newFixedThreadPool(20);
  CountDownLatch countDownLatch = new CountDownLatch(count);

  for (int i = 0; i < count; i++) {
    executorService.execute(() -> {
      synchronizedString.incrementValue(1L);
      countDownLatch.countDown();
    });
  }

  countDownLatch.await();

  // then
	// 테스트가 실패한다.
  assertEquals(count, synchronizedString.getValue());
}
```

이유는 `(“lock” + id)` 부분 때문이다. Java에서 문자열은 `불변성(immutable)`을 가지는데, 이 특성으로 문자열은 한번 생성되면 수정이 불가능하고 “+” 연산자를 사용하게 된다면 새로운 문자열 객체를 생성하게 된다. 그래서 **힙 메모리 상 주소가 다르기 때문에 동일 객체로 인식하지 않아** 정상적으로 동기화가 되지 않는다.

<img src="https://etaphee.s3.ap-northeast-2.amazonaws.com/github/synchronized-string-nointern.drawio.svg" width=500 />

## 해결 방법

해결 방법은 새로 생성한 문자열에 대해 lock을 거는 게 아닌, `String Pool`에 있는 문자열에 대해 lock을 거는 것이다. Spring Pool에 접근하기 위해서는 `intern()` 메서드를 사용한다. intern() 메서드를 호출하면 String Pool에 존재하는 문자열이라면 풀의 문자열을 반환하고, 없다면 풀에 등록 후 반환한다.

```java
public void incrementValueWithIntern(long id) {
  synchronized (("lock" + id).intern()) {
    value++;
    System.out.println("value: " + value);
  }
}
```

<img src="https://etaphee.s3.ap-northeast-2.amazonaws.com/github/synchronized-string-intern.drawio.svg" width=500 />
