import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SynchronizedTests {

  @Test
  @DisplayName("문자열 테스트")
  void test_string_equals() {
    String s1 = "a";
    s1 += "bc";
    String s2 = "abc";
    String s3 = "a" + "bc";

    assertNotSame(s1, s2);
    assertNotSame(s1, s3);
    assertNotSame(s2, s3);
  }

  @Test
  @DisplayName("문자열 동기화 테스트")
  void test_synchronized_string() throws InterruptedException {
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
    assertEquals(count, synchronizedString.getValue());
  }

  @Test
  @DisplayName("문자열 동기화 테스트(intern)")
  void test_synchronized_intern_string() throws InterruptedException {
    // given
    SynchronizedString synchronizedString = new SynchronizedString();

    // when
    int count = 10000;
    ExecutorService executorService = Executors.newFixedThreadPool(20);
    CountDownLatch countDownLatch = new CountDownLatch(count);

    for (int i = 0; i < count; i++) {
      executorService.execute(() -> {
        synchronizedString.incrementValueWithIntern(1L);
        countDownLatch.countDown();
      });
    }

    countDownLatch.await();

    // then
    assertEquals(count, synchronizedString.getValue());
  }
}
