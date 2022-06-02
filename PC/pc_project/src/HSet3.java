import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class HSet3<E> implements IHSet<E> {
    private LinkedList<E>[] table;
    private ReentrantReadWriteLock[] lockArray;
    private Condition[] conditionArray;

    public HSet3(int ht_size) {
        table          = createTable(ht_size);
        lockArray      = createLockArray(ht_size);
        conditionArray = createConditionArray(ht_size);
    }

    private LinkedList<E> getEntry(E elem) {
        return table[Math.abs(elem.hashCode() % table.length)];
    }

    private ReentrantReadWriteLock getLock(E elem) {
        return lockArray[Math.abs(elem.hashCode() % lockArray.length)];
    }

    private Condition getCondition(E elem) {
        return conditionArray[Math.abs(elem.hashCode() % conditionArray.length)];
    }

    private LinkedList<E>[] createTable(int ht_size) {
        @SuppressWarnings("unchecked")
        LinkedList<E>[] t = (LinkedList<E>[]) new LinkedList[ht_size];
        for (int i = 0; i < t.length; i++) {
            t[i] = new LinkedList<>();
        }
        return t;
    }

    private ReentrantReadWriteLock[] createLockArray(int ht_size) {
        ReentrantReadWriteLock[] lA = new ReentrantReadWriteLock[ht_size];
        for (int i = 0; i < lA.length; i++) {
            lA[i] = new ReentrantReadWriteLock();
        }
        return lA;
    }

    private Condition[] createConditionArray(int ht_size) {
        Condition[] cA = new Condition[ht_size];
        for (int i = 0; i < cA.length; i++) {
            cA[i] = lockArray[i].writeLock().newCondition(); 
        }
        return cA;
    }

    @Override
    public int capacity() {
        return table.length;
    }

    @Override
    public int size() {
        int size = 0;
        for (ReentrantReadWriteLock rrwl : lockArray) {
            rrwl.readLock().lock();
        }
        try {
            for (LinkedList<E> list : table) {
                size += list.size();
            }
            return size;
        }
        finally {
            for (ReentrantReadWriteLock rrwl : lockArray) {
                rrwl.readLock().unlock();
            }
        }
    }

    @Override 
    public boolean add(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        getLock(elem).writeLock().lock();
        try {
            LinkedList<E> list = getEntry(elem);
            boolean r = ! list.contains(elem);
            if (r) {
                list.addFirst(elem);
                getCondition(elem).signalAll();
            }
            return r;
        }
        finally { getLock(elem).writeLock().unlock(); }
    }

    @Override 
    public boolean remove(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        getLock(elem).writeLock().lock();
        try {
            boolean r = getEntry(elem).remove(elem);
            return r;
        }
        finally { getLock(elem).writeLock().unlock(); }
    }

    @Override
    public boolean contains(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        getLock(elem).readLock().lock();
        try { return getEntry(elem).contains(elem); }
        finally { getLock(elem).readLock().unlock(); }
    }

    @Override
    public void waitFor(E elem) {
        if (elem == null) { throw new IllegalArgumentException(); }
        getLock(elem).writeLock().lock();
        try {
            while (! getEntry(elem).contains(elem)) {
                try { getCondition(elem).await(); }
                catch(InterruptedException e) { }
            }
        }
        finally { getLock(elem).writeLock().unlock(); }
    }

    @Override
    public void rehash() {
        for (ReentrantReadWriteLock rrwl : lockArray) {
            rrwl.writeLock().lock();
        }
        try {
            LinkedList<E>[] oldTable = table;
            table = createTable(2 * oldTable.length);
            for (LinkedList<E> list : oldTable) {
                for (E elem : list) {
                    getEntry(elem).add(elem);
                }
            }
        }
        finally {
            for (ReentrantReadWriteLock rrwl : lockArray) {
                rrwl.writeLock().unlock();
            }
        }
    }
}