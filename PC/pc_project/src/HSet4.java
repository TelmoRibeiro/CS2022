import scala.concurrent.stm.Ref;
import scala.concurrent.stm.TArray;
import scala.concurrent.stm.japi.STM;

public class HSet4<E> implements IHSet<E>{

  private static class Node<T> {
    T value;
    Ref.View<Node<T>> prev = STM.newRef(null);
    Ref.View<Node<T>> next = STM.newRef(null);
  }

  private final Ref.View<TArray.View<Node<E>>> table;
  private final Ref.View<Integer> size;

  public HSet4(int h_size) {
    table = STM.newRef(STM.newTArray(h_size));
    size = STM.newRef(0); 
  }

  @Override
  public int capacity() {
    return table.get().length();
  }

  @Override
  public int size() {
    return size.get();
  }

  private void shiftTable(int index) {
    STM.atomic (() -> {
      if (index >= capacity() - 1) { return; }
      Node<E> currentNode = table.get().apply(index);
      for (int i = index + 1; i < size(); i++) {
        if (i >= capacity() - 1) { return; }
        currentNode = currentNode.next.get();
        table.get().update(i, currentNode);
      }
    });
  }

  @Override
  public boolean add(E elem) {
    if (elem == null) { throw new IllegalArgumentException(); }
    return STM.atomic(() -> {
      //if (size() == capacity()) { STM.retry(); }
      Boolean r = !contains(elem);
      if (r) {
        Node<E> currentNode = table.get().apply(0);
        Node<E> toAddNode   = new Node<>();
        toAddNode.value     = elem;
        if (currentNode != null) {
          toAddNode.next.set(currentNode);
          currentNode.prev.set(toAddNode); 
        } 
        STM.increment(size, 1);
        table.get().update(0, toAddNode);
        shiftTable(0);
      }
      return r;
    });
  }

  @Override
  public boolean remove(E elem) {
    if (elem == null) { throw new IllegalArgumentException(); }
    return STM.atomic(() -> {
      if (size() == 0) { STM.retry(); }
      Boolean r = contains(elem);
      if (r) {
        Node<E> currentNode = table.get().apply(0);
        int     cNIndex     = 0; 
        while (!currentNode.value.equals(elem)) {
          currentNode = currentNode.next.get();
          cNIndex++;
        }
        Node<E> nextNode = currentNode.next.get();
        Node<E> prevNode = currentNode.prev.get();
        if (prevNode != null) prevNode.next.set(nextNode);
        if (nextNode != null) nextNode.prev.set(prevNode);
        STM.increment(size, -1);
        if (cNIndex < capacity()) { 
          table.get().update(cNIndex, nextNode);
          if (cNIndex > 0) { table.get().update(cNIndex - 1, prevNode); }
        }
        shiftTable(cNIndex); 
      }
      return r;
    });
  }

  @Override
  public boolean contains(E elem) {
    if (elem == null) { throw new IllegalArgumentException(); }
    return STM.atomic(() -> {
      Node<E> currentNode = table.get().apply(0);
      while (currentNode != null && !currentNode.value.equals(elem)) {
        currentNode = currentNode.next.get();
      }
      return currentNode != null && currentNode.value.equals(elem);
    });
  }

  @Override
  public void waitFor(E elem) {
    if (elem == null) { throw new IllegalArgumentException(); }
    STM.atomic(() -> {
      while (!contains(elem)) { 
        STM.retry(); 
      }
    });
  }

  @Override
  public void rehash() {
    // TODO
    throw new Error("not implemented");
  }
}
