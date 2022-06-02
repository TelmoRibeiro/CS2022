import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HSet1<E> implements IHSet<E>{

    private LinkedList<E>[] table;
    private int size;
    private ReentrantLock rl;
    private Condition notContained;

    public HSet1(int ht_size) {
        table = createTable(ht_size);
        size = 0;
        rl = new ReentrantLock();
        notContained = rl.newCondition();
    }

    private LinkedList<E> getEntry(E elem) {
        return table[Math.abs(elem.hashCode() % table.length)];
    }

    private LinkedList<E>[] createTable(int ht_size) {
        @SuppressWarnings("unchecked")
        LinkedList<E>[] t = (LinkedList<E>[]) new LinkedList[ht_size];
        for (int i = 0; i < t.length; i++) {
            t[i] = new LinkedList<>();
        }
        return t;
    }

    @Override
    public int capacity() {
        return table.length;
    }

    @Override
    public int size() {
        rl.lock();
        try { return size; }
        finally { rl.unlock(); }
    }

    @Override
    public boolean add(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rl.lock();
        try {
            LinkedList<E> list = getEntry(elem);
            boolean r = ! list.contains(elem);
            if (r) {
                list.addFirst(elem);
                notContained.signalAll();
                size++;
            }
            return r;
        }
        finally { rl.unlock(); }
    }

    @Override
    public boolean remove(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rl.lock();
        try { 
            boolean r = getEntry(elem).remove(elem);
            if (r) { size--; }
            return r;
        }
        finally { rl.unlock(); }
    }

    @Override
    public boolean contains(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rl.lock();
        try { return getEntry(elem).contains(elem); }
        finally { rl.unlock(); }
    }

    @Override
    public void waitFor(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rl.lock();
        try {
            while (! getEntry(elem).contains(elem)) {
                try { notContained.await(); }
                catch(InterruptedException e) { }
            }
        }
        finally { rl.unlock(); }
    }

    @Override
    public void rehash() {
        rl.lock();
        try {
            LinkedList<E>[] oldTable = table;
            table = createTable(2 * oldTable.length);
            for (LinkedList<E> list : oldTable) {
                for (E elem : list) {
                    getEntry(elem).add(elem);
                }
            }
        }
        finally { rl.unlock(); } 
    }
}