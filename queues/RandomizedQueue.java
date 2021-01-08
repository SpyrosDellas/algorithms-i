/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 26/04/2020
 *
 * Description:
 * A randomized queue is similar to a stack or queue, except that the item
 * removed is chosen uniformly at random among items in the data structure.
 *
 * Iterator.
 * Each iterator returns the items in uniformly random order. The order of
 * two or more iterators to the same randomized queue is mutually
 * independent; each iterator must maintain its own random order.
 *
 * Corner cases.
 * Throws the specified exception for the following corner cases:
 * - IllegalArgumentException if the client calls enqueue() with a null
 *   argument.
 * - java.util.NoSuchElementException if the client calls either sample() or
 *   dequeue() when the randomized queue is empty.
 * - java.util.NoSuchElementException if the client calls the next() method in
 *   the iterator when there are no more items to return.
 *  - UnsupportedOperationException if the client calls the remove() method in
 *   the iterator.
 *
 * Unit testing.
 * The main() method calls directly every public constructor and method to
 * verify that they work as prescribed (e.g., by printing results to standard
 * output).
 *
 * Performance requirements.
 * The randomized queue implementation supports each randomized queue operation
 * (besides creating an iterator) in constant amortized time. That is, any
 * intermixed sequence of m randomized queue operations (starting from an
 * empty queue) takes at most cm steps in the worst case, for some constant c.
 * A randomized queue containing n items uses at most 48n + 192 bytes of memory.
 * Additionally, the iterator implementation supports operations next() and
 * hasNext() in constant worst-case time; and construction in linear time; it
 * uses a linear amount of extra memory per iterator.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] rQueue;
    private int size;

    // construct an empty randomized queue
    public RandomizedQueue() {
        rQueue = (Item[]) new Object[2];
        size = 0;
    }

    // resize the containing array
    private void resize(int newSize) {
        Item[] copy = (Item[]) new Object[newSize];
        for (int i = 0; i < size; i++) {
            copy[i] = rQueue[i];
        }
        rQueue = copy;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Cannot add a null item");
        if (size == rQueue.length) {
            resize(2 * size);
        }
        rQueue[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Randomised queue is empty.");
        int randomIndex = (int) (Math.random() * size);
        Item item = rQueue[randomIndex];
        rQueue[randomIndex] = rQueue[--size];
        rQueue[size] = null;
        if (rQueue.length >= 4 && size == rQueue.length / 4) {
            resize(rQueue.length / 2);
        }
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Randomised queue is empty.");
        int randomIndex = (int) (Math.random() * size);
        return rQueue[randomIndex];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomisedQueueIterator();
    }

    private class RandomisedQueueIterator implements Iterator<Item> {

        private int[] record;
        private int cursor;

        public RandomisedQueueIterator() {
            record = new int[size];
            for (int i = 0; i < record.length; i++) {
                record[i] = i;
            }
            cursor = 0;
        }

        public boolean hasNext() {
            return cursor < record.length;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            int randomIndex = (int) (Math.random() * (size - cursor));
            int selection = record[cursor + randomIndex];
            Item item = rQueue[selection];
            record[cursor + randomIndex] = record[cursor];
            record[cursor++] = selection;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing
    public static void main(String[] args) {
        In test = new In("mediumTale.txt");
        RandomizedQueue<String> rQ = new RandomizedQueue<>();
        while (!test.isEmpty()) {
            String s = test.readString();
            rQ.enqueue(s);
        }
        System.out.println("Randomised queue size is: " + rQ.size());
        System.out.println("Is the randomised queue empty? " + rQ.isEmpty());
        System.out.println("Iterate through the randomised queue using a for-each loop:");
        for (String s : rQ) {
            System.out.print(s + " ");
        }
        System.out
                .println("\nCreate an iterator and use it to loop through the first 5 elements:");
        Iterator<String> iter = rQ.iterator();
        for (int i = 0; i < 5; i++) {
            System.out.print(iter.next() + " ");
        }
        System.out.print("\nRandom dequeue operation: " + rQ.dequeue());
        System.out.print("\nRandom sample operation: " + rQ.sample());

        System.out.print("\nTesting 2 iterators on same randomised queue:\n");
        RandomizedQueue<Integer> rInt = new RandomizedQueue<>();
        for (int i = 0; i < 5; i++) {
            rInt.enqueue(i);
        }
        for (int e1 : rInt) {
            for (int e2 : rInt) {
                System.out.print(e1 + "-" + e2 + " ");
            }
            System.out.println();
        }
        RandomizedQueue<Integer> rInt1 = new RandomizedQueue<>();
        rInt1.dequeue();
    }
}
