import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class HSet2<E> implements IHSet<E>{

    private LinkedList<E>[] table;
    private int size;
    private ReentrantReadWriteLock rrwl;
    private Condition notContained;

    public HSet2(int ht_size) {
        table = createTable(ht_size);
        size = 0;
        rrwl = new ReentrantReadWriteLock();
        notContained = rrwl.writeLock().newCondition();
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
        rrwl.readLock().lock();
        try { return size; }
        finally { rrwl.readLock().unlock(); } 
    }

    @Override
    public boolean add(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rrwl.writeLock().lock();
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
        finally { rrwl.writeLock().unlock(); }
    }

    @Override
    public boolean remove(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rrwl.writeLock().lock();
        try {
            boolean r = getEntry(elem).remove(elem);
            if (r) { size--; }
            return r;
        }
        finally { rrwl.writeLock().unlock(); }
    }

    @Override
    public boolean contains(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rrwl.readLock().lock();
        try { return getEntry(elem).contains(elem); }
        finally { rrwl.readLock().unlock(); } 
    }

    @Override
    public void waitFor(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        rrwl.writeLock().lock();
        try {
            while (! getEntry(elem).contains(elem)) {
                try { notContained.await(); }
                catch(InterruptedException e) { }
            }
        }
        finally { rrwl.writeLock().unlock(); }
    }

    @Override
    public void rehash() {
        rrwl.writeLock().lock();
        try {
            LinkedList<E>[] oldTable = table;
            table = createTable(2 * oldTable.length);
            for (LinkedList<E> list : oldTable) {
                for (E elem : list) {
                    getEntry(elem).add(elem);
                }
            }
        }
        finally { rrwl.writeLock().unlock(); }
    }
}