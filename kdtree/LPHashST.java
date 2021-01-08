/* *****************************************************************************
 * Name: Spyridon Dellas
 * Date: 19/05/2020
 * Description: Linear probing hash symbol table implementation.
 *
 * Implementation details:
 * - Keys must not be null. Use of a null key results in a runtime exception
 *
 * - Values must not be null. This convention is directly tied to our
 *   specification in the API that get() should return null for keys
 *   not in the table, effectively associating the value null with every key not
 *   in the table.
 *
 * - Optimisation: The array alternates holding keys and values. This has
 *   better locality for large tables than using separate arrays.
 *
 * - Optimisation 2: Array sizes are prime numbers. This optimizes keys
 *   distribution
 *
 * k dk  prime = (2^k − dk)
 * 5 1   31
 * 6 3   61
 * 7 1   127
 * 8 5   251
 * 9 3   509
 *
 * 10 3  1021
 * 11 9  2039
 * 12 3  4093
 * 13 1  8191
 * 14 3  16381
 * 15 19 32749
 * 16 15 65521
 * 17 1  131071
 * 18 5  262139
 * 19 1  524287
 *
 * 20 3  1048573
 * 21 9  2097143
 * 22 3  4194301
 * 23 15 8388593
 * 24 3  16777213
 * 25 39 33554393
 * 26 5  67108859
 * 27 39 134217689
 * 28 57 268435399
 * 29 3  536870909
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class LPHashST<K, V> {

    private static final int MIN_SIZE = 31; // min size of the key-value pairs array
    private static final byte[] DK = {
            0, 0, 0, 0, 0, 1, 3, 1, 5, 3,
            3, 9, 3, 1, 3, 19, 15, 1, 5, 1,
            3, 9, 3, 15, 3, 39, 5, 39, 57, 3
    };

    private Object[] table;            // the array holding the key-value pairs
    private int size;                  // number of key-value pairs in the table
    private int m;                     // size of linear-probing array
    private int lgm;                   // first power of 2 larger than m
    private K lastKey;                 // cache of the last accessed key
    private V lastValue;               // cache of the last accessed value
    private int lastIndex;             // cache of the last accessed key's index

    private long putCounter = 0;
    private long collisionCounter = 0;

    // public constructor
    public LPHashST() {
        size = 0;
        m = MIN_SIZE;
        table = new Object[m];
        lgm = 5;
    }


    // private constructor to simplify resizing (allows reuse of put)
    private LPHashST(int m, int lgm) {
        size = 0;
        this.m = m;
        table = new Object[m];
        this.lgm = lgm;
    }


    // put key-value pair into the table (and remove key from table if value
    // is null)
    public void put(K key, V value) {

        if (key == null)
            throw new IllegalArgumentException("calls put() with null key");

        if (value == null) {
            delete(key);
            return;
        }

        if (lastKey != null && lastKey.equals(key)) {
            table[lastIndex] = value;
            lastValue = value;
            return;
        }

        putCounter++;

        // find the key's bin
        int bin = hash(key);

        // scan through the array and either insert a new key-value pair or
        // update an existing key's value
        while (table[bin] != null) {
            if (key.equals(table[bin])) {
                table[bin + 1] = value;
                return;
            }
            bin = (bin + 2) % (m - 1);
            collisionCounter++;
        }

        // key not found; insert a new key-value pair and update size
        table[bin] = key;
        table[bin + 1] = value;
        size++;

        // double the array if half-full
        if (size >= m / 4) {
            resize(true);
        }
    }

    // value paired with key (null if key is absent)
    @SuppressWarnings("unchecked")
    public V get(K key) {

        if (key == null)
            throw new IllegalArgumentException("calls get() with null key");

        if (lastKey != null && lastKey.equals(key))
            return lastValue;

        // find the key's bin
        int bin = hash(key);

        // scan through the array and either return the key's value or null
        while (table[bin] != null) {
            if (key.equals(table[bin])) {
                lastKey = key;
                lastValue = (V) table[bin + 1];
                lastIndex = bin + 1;
                return lastValue;
            }
            bin = (bin + 2) % (m - 1);
        }
        return null;
    }


    // remove key (and its value) from table
    @SuppressWarnings("unchecked")
    public void delete(K key) {

        if (key == null)
            throw new IllegalArgumentException("calls delete() with null key");

        // scan through the array to locate the key (if it exists)
        int bin = hash(key);
        while (table[bin] != null) {
            if (key.equals(table[bin])) {

                // key found; delete it
                table[bin] = null;
                table[bin + 1] = null;
                size--;

                // check subsequent keys and rearrange if necessary
                bin = (bin + 2) % (m - 1);
                while (table[bin] != null) {
                    K next = (K) table[bin];
                    if (hash(next) != bin) {
                        put(next, (V) table[bin + 1]);
                        table[bin] = null;
                        table[bin + 1] = null;
                        size--;
                    }
                    bin = (bin + 2) % (m - 1);
                }

                // halve the array if empty less than 1/8
                if (m > 60 && size < m / 16) {
                    resize(false);
                }

                // job done; return
                return;
            }
            bin = (bin + 2) % (m - 1);
        }
    }


    // is the table empty?
    public boolean isEmpty() {
        return size == 0;
    }


    // number of key-value pairs
    public int size() {
        return size;
    }


    // is there a value paired with key?
    public boolean contains(K key) {
        if (key == null)
            throw new IllegalArgumentException("calls contains() with null key");

        return get(key) != null;
    }


    @SuppressWarnings("unchecked")
    public Iterable<K> keys() {
        Queue<K> keys = new Queue<>();
        for (int i = 0; i < m - 1; i += 2) {
            if (table[i] != null) {
                keys.enqueue((K) table[i]);
            }
        }

        return keys;
    }


    private double averageCollisions() {
        return collisionCounter / (double) putCounter;
    }


    private int hash(K key) {

        // convert positive or negative integer hashcode to positive and obtain
        // hash position in the table
        // Note: We cannot use Java's Math.abs() since it overflows if
        // hashCode equals -2^31
        int bin = (key.hashCode() & 0x7fffffff) % m;

        // The keys are stored in even indices less than m-1
        return (2 * bin) % (m - 1);
    }


    @SuppressWarnings("unchecked")
    private void resize(boolean doubleSize) {

        int newSize;
        if (doubleSize) {
            if (lgm < 29) {
                newSize = 2 * (m + DK[lgm]) - DK[lgm + 1];
            }
            else {
                newSize = 2 * m;
            }
            LPHashST<K, V> copy = new LPHashST<>(newSize, lgm + 1);

            for (int i = 0; i < m - 1; i += 2) {
                if (table[i] != null)
                    copy.put((K) table[i], (V) table[i + 1]);
            }

            this.table = copy.table;
            m = newSize;
            lgm++;
        }

        else {
            if (lgm < 30) {
                newSize = (m + DK[lgm]) / 2 - DK[lgm - 1];
            }
            else {
                newSize = m / 2;
            }
            LPHashST<K, V> copy = new LPHashST<>(newSize, lgm - 1);

            for (int i = 0; i < m - 1; i += 2) {
                if (table[i] != null)
                    copy.put((K) table[i], (V) table[i + 1]);
            }

            this.table = copy.table;
            m = newSize;
            lgm--;
        }
    }


    // unit testing
    /*
     * Reads in a command-line integer and sequence of words from
     * standard input and prints out a word (whose length exceeds
     * the threshold) that occurs most frequently to standard output.
     * It also prints out the number of words whose length exceeds
     * the threshold and the number of distinct such words.
     */
    public static void main(String[] args) {

        LPHashST<String, Integer> st = new LPHashST<>();

        int distinct = 0;
        int words = 0;
        int minlen = Integer.parseInt(args[0]);

        // compute frequency counts
        In file = new In("leipzig1M.txt");
        double start = System.currentTimeMillis();
        while (!file.isEmpty()) {
            String key = file.readString();
            if (key.length() < minlen) continue;
            words++;
            if (st.contains(key)) {
                st.put(key, st.get(key) + 1);
            }
            else {
                st.put(key, 1);
                distinct++;
            }
        }
        double end = System.currentTimeMillis();
        StdOut.println("Time to build the table: " + (end - start) / 1000 + " secs");

        System.out.println("Average collisions / put: " + st.averageCollisions());

        // find a key with the highest frequency count
        String max = "";
        st.put(max, 0);

        for (String word : st.keys()) {
            if (st.get(word) > st.get(max))
                max = word;
        }

        System.out.println("Symbol table size = " + st.size());
        StdOut.println(max + " " + st.get(max));
        StdOut.println("distinct = " + distinct);
        StdOut.println("words    = " + words);

        for (String word : st.keys()) {
            st.delete(word);
            st.delete("");
            st.put(word, null);
        }
        System.out.println("Symbol table size after deleting all keys = " + st.size());
    }
}
