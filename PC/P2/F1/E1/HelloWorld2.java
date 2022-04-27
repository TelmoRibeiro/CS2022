import java.util.Scanner;

public class HelloWorld2 {
  public static void main(String[] args) throws InterruptedException {
    int n = Integer.parseInt(args[0]);
    Thread[] t = new Thread[n];
    for (int i = 0; i < n; i++) {
      int id = i + 1;
      t[i] = new Thread(() -> {
        System.out.println("Hello from spawned thread " + id);
      });
      //t[i].setDaemon(true);
      t[i].start();
    }
    System.out.println("Hello from main thread");
  }
}