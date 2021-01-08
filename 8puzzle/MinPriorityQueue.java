/* *****************************************************************************
 *  Name: Spyros Dellas
 *  Date: 13/05/2020
 *  Description: Min oriented Priority Queue implementation using a binary heap
 **************************************************************************** */

import java.math.BigInteger;

public class MinPriorityQueue<K extends Comparable<K>> {

    // Represents a heap of size N in an of length N + 1, with pq[0] unused
    // and the heap in pq[1] through pq[N]
    private K[] pq;

    private int size;

    // create a priority queue
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MinPriorityQueue() {
        pq = (K[]) new Comparable[2];
        size = 0;
    }

    // create a priority queue of initial capacity max
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MinPriorityQueue(int max) {
        pq = (K[]) new Comparable[max + 1];
        size = 0;
    }

    // create a priority queue from the keys in a[]
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MinPriorityQueue(K[] a) {
        size = a.length;
        pq = (K[]) new Comparable[size + 1];
        System.arraycopy(a, 0, pq, 1, size);
        for (int k = size / 2; k > 0; k--) {
            sink(k);
        }
    }

    // Bottom-up reheapify
    private void swim(int k) {
        while (k > 1 && more(k / 2, k)) {
            exch(k / 2, k);
            k /= 2;
        }
    }

    // Top-down reheapify
    private void sink(int k) {
        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && more(j, j + 1)) {
                j++;
            }
            if (more(k, j)) {
                exch(k, j);
                k = j;
            }
            else {
                break;
            }
        }
    }

    // insert a key into the priority queue
    public void insert(K v) {
        if (pq.length == size + 1) resize(2 * size);
        pq[++size] = v;
        swim(size);
    }

    // return the smallest key
    public K min() {
        return pq[1];
    }

    // return and remove the smallest key
    public K delMin() {
        K minKey = pq[1];
        pq[1] = pq[size];
        pq[size--] = null;
        sink(1);
        if (size >= 4 && size <= pq.length / 4) {
            resize(pq.length / 2);
        }
        return minKey;
    }

    // is the priority queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of keys in the priority queue
    public int size() {
        return size;
    }

    // Resize the priority queue into a new array of size newSize
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void resize(int newSize) {
        K[] copy = (K[]) new Comparable[newSize];
        System.arraycopy(pq, 1, copy, 1, size);
        pq = copy;
    }

    private boolean more(int i, int j) {
        return pq[i].compareTo(pq[j]) > 0;
    }

    private void exch(int i, int j) {
        K buffer = pq[i];
        pq[i] = pq[j];
        pq[j] = buffer;
    }

    // Certify that this is a min oriented priority queue
    private boolean isPQ() {
        for (int k = 1; k <= size / 2; k++) {
            int j = 2 * k;
            if (more(k, j) || (j < size && more(k, 2 * k + 1)))
                return false;
        }
        return true;
    }

    // test client
    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        BigInteger[] t = new BigInteger[n];
        BigInteger k = BigInteger.ONE;
        for (int i = 1; i <= n; i++) {
            t[n - i] = k.multiply(k).multiply(k);
            k = k.add(BigInteger.ONE);
        }

        System.out.println("Big Integer array created");
        MinPriorityQueue<BigInteger> pq = new MinPriorityQueue<>(t);
        for (int i = 1; i <= 7; i++) {
            System.out.print(pq.delMin() + " ");
        }
        System.out.println("\nCertified as a min oriented priority queue: " + pq.isPQ());
    }
}
