// 
// Counter exercise.
//
// ProgramaÃ§Ã£o Concorrente (CC3037), DCC/FCUP
// Eduardo R. B. Marques

import java.util.concurrent.atomic.AtomicInteger;

public class CounterProgram {
  public static void main(String[] args) throws InterruptedException {
    int n = args.length >= 1 ? Integer.parseInt(args[0]) : 2;
    int k = args.length >= 2 ? Integer.parseInt(args[1]) : 100;
    test(new BuggyCounter(), n, k);
  }
  static void test(Counter c, int n, int k) throws InterruptedException {
    System.out.println("=> Testing " + c.getClass().getName());
    Thread[] t = new Thread[n];
    for (int i = 0; i < n; i++) {
      t[i] = new Thread(() -> {
        for (int j = 0; j < k; j++) c.increment();
      });
      t[i].start();
    }
    for (int i = 0 ; i < n; i++) {
      t[i].join();
    }
    System.out.printf("expected counter value: %d%n", n*k);
    System.out.printf("actual counter value  : %d%n", c.getValue());
  }
}

interface Counter {
  void increment();
  int getValue();
}

class BuggyCounter implements Counter{
  private int value = 0;

  @Override
  public void increment() { value ++; }

  @Override
  public int getValue()   { return value; }
}

class CorrectCounter { // implements Counter {
  // TODO
}
class CorrectCounter2 { // implements Counter {
  private AtomicInteger value = new AtomicInteger(0);
  // TODO
}
