import java.util.Scanner;

// exprimentar Thread.sleep() ou Thread.yield() antes de t.start()!
public class HelloWorld {
  public static void main(String[] args) throws InterruptedException {
    hello1();
    hello2();
    hello3();
  }
  // Example creation 1 (extension of class java.lang.Thread)
  static class HelloThread extends Thread {
    @Override
    public void run() {
      System.out.println("Hello from spawned thread");
    }
  }
  static void hello1() throws InterruptedException {
    System.out.println("=> Hello 1");
    HelloThread t = new HelloThread();
    t.start();
    System.out.println("Hello form main thread");
    t.join();
  }
  // Example creation 2 (use of Runnable instance)
  static class HelloRunnable implements Runnable {
    @Override
    public void run() {
      System.out.println("Hello from spawned thread");
    }
  }
  static void hello2() throws InterruptedException {
    System.out.println("=> Hello 2");
    Thread t = new Thread(new HelloRunnable());
    t.start();
    System.out.println("Hello from main thread");
    t.join(); 
  }
  // Example 3 (use of a Runnable instance defined by a lambda expression)
  static void hello3() throws InterruptedException {
    System.out.println("=> Hello 3");
    Thread t = new Thread(() -> {
      System.out.println("Hello from spawned thread");
    });
    t.start();
    System.out.println("Hello from main thread");
    t.join();
  }
}