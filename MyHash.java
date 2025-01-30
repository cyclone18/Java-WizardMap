

public class MyHash<K, V> {
    private MyLinkedList<K, V>[] table;    // main hash table
    private MyLinkedList<K, V>[] oldTable; // old hash table during rehashing
    private int size;                // current number of elements
    private int capacity;            // current capacity of the hash table
    private int oldCapacity;         // capacity of the old table during rehashing
    private static final double LOAD_FACTOR = 0.75; // load factor threshold
    private static final int INITIAL_CAPACITY = 16; // initial capacity
    private int rehashIndex;         // index of the bucket being rehashed
    private boolean rehashing;       // indicates if rehashing is in progress

    @SuppressWarnings("unchecked")
    public MyHash() {
        this.capacity = INITIAL_CAPACITY;
        this.oldCapacity = 0; // no old table initially
        this.oldTable = null;

        // Generic array creation requires a cast
        this.table = (MyLinkedList<K, V>[]) new MyLinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new MyLinkedList<>();
        }
        this.size = 0;
        this.rehashing = false; // rehashing is not active initially
    }

    // compute hash for the main table
    private int hash(K key) {
        return Math.abs(key.hashCode() % capacity);
    }

    // compute hash for the old table during rehashing
    private int hashOld(K key) {
        return Math.abs(key.hashCode() % oldCapacity);
    }

    // add a key-value pair to the hash table
    public synchronized void put(K key, V value) {  // ensure thread-safe access
        if (rehashing) {
            processRehashing();
        }

        if (!rehashing && (double) size / capacity > LOAD_FACTOR) {
            startResizing(); // trigger resizing if load factor exceeds threshold
        }

        int hash = hash(key); // compute the hash
        if (table[hash] == null) {
            table[hash] = new MyLinkedList<>();
        }
        if (!table[hash].contains(key)) {
            size++;
        }
        table[hash].add(key, value); // add or update the key-value pair
    }

    // retrieve a value by key
    public V get(K key) {
        while (rehashing) { // ensure ongoing rehashing is handled
            processRehashing();
        }

        synchronized (this) {
            int hash = hash(key);
            V value = table[hash] != null ? table[hash].get(key) : null;
            // check the old table if the value is not found in the main table
            if (value == null && oldTable != null) {
                int oldHash = hashOld(key);
                value = oldTable[oldHash] != null ? oldTable[oldHash].get(key) : null;
            }

            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void startResizing() {
        rehashing = true; // set rehashing to active
        rehashIndex = 0;  // start the rehashing from index 0
        oldTable = table;
        oldCapacity = capacity;
        capacity = getPrimeCapacity(capacity * 2); // compute new capacity as next prime

        // Recreate the table with the new capacity
        table = (MyLinkedList<K, V>[]) new MyLinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new MyLinkedList<>();
        }
    }

    // find the next prime number greater or equal to num
    private int getPrimeCapacity(int num) {
        while (!isPrime(num)) {
            num++;
        }
        return num;
    }

    // check if a number is prime
    private boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // process ongoing rehashing
    private synchronized void processRehashing() {
        if (!rehashing || oldTable == null) return; // no ongoing rehashing

        int batchSize = 1; // rehash one bucket at a time
        while (rehashIndex < oldCapacity && batchSize > 0) {
            MyLinkedList<K, V> bucket = oldTable[rehashIndex]; // get current bucket from old table
            if (bucket != null) {
                MyLinkedList<K, V>.Node current = bucket.getHead();
                while (current != null) {
                    int newHash = hash(current.key);
                    if (table[newHash] == null) {
                        table[newHash] = new MyLinkedList<>();
                    }
                    // Transfer key-value pair to the new table
                    table[newHash].add(current.key, current.value);
                    current = current.next;
                }
            }
            rehashIndex++;
            batchSize--;
        }

        if (rehashIndex >= oldCapacity) {
            rehashing = false; // rehashing completed
            oldTable = null; // allow GC
        }
    }

}
