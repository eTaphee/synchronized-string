public class SynchronizedString {

  private int value = 0;

  public void incrementValue(long id) {
    synchronized ("lock" + id) {
      value++;
      System.out.println("value: " + value);
    }
  }

  public void incrementValueWithIntern(long id) {
    synchronized (("lock" + id).intern()) {
      value++;
      System.out.println("value: " + value);
    }
  }

  public int getValue() {
    return value;
  }
}
