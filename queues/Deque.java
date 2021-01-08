/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 26/04/2020
 *
 * Description:
 * A double-ended queue or deque (pronounced “deck”) is a
 * generalization of a stack and a queue that supports adding and removing
 * items from either the front or the back of the data structure.
 *
 * Corner cases.
 * Throws the specified exception for the following corner cases:
 * - IllegalArgumentException if the client calls either addFirst() or addLast()
 *   with a null argument.
 * - java.util.NoSuchElementException if the client calls either removeFirst()
 *   or removeLast when the deque is empty.
 * - java.util.NoSuchElementException if the client calls the next() method in
 *   the iterator when there are no more items to return.
 * - UnsupportedOperationException if the client calls the remove() method in
 *   the iterator.
 *
 * Unit testing.
 * The main() method calls directly every public constructor and method to help
 * verify that they work as prescribed (e.g., by printing results to standard
 * output).
 *
 * Performance requirements.
 * This linked list deque implementation supports each deque operation
 * (including construction) in constant worst-case time.
 * A deque containing n items uses at most 48n + 192 bytes of memory.
 * Additionally, the iterator implementation supports each operation (including
 * construction) in constant worst-case time.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node<Item> first;
    private Node<Item> last;
    private int size;

    private static class Node<Item> {
        private Item item;
        private Node<Item> previous;
        private Node<Item> next;
    }

    // construct an empty deque
    public Deque() {
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Cannot add a null item");
        Node<Item> oldFirst = first;
        first = new Node<>();
        first.item = item;
        first.next = oldFirst;
        if (first.next == null) {
            last = first;
        }
        else {
            oldFirst.previous = first;
        }
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Cannot add a null item");
        Node<Item> oldLast = last;
        last = new Node<>();
        last.item = item;
        if (isEmpty()) {
            first = last;
        }
        else {
            oldLast.next = last;
            last.previous = oldLast;
        }
        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        Item item = first.item;
        Node<Item> second = first.next;
        first = second;
        if (first == null) {
            last = null;
        }
        else {
            second.previous = null;
        }
        size--;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        Item item = last.item;
        Node<Item> beforeLast = last.previous;
        if (beforeLast == null) {
            first = null;
            last = null;
        }
        else {
            last = beforeLast;
            last.next = null;
        }
        size--;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node<Item> current = first;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing
    public static void main(String[] args) {
        In test = new In("mediumTale.txt");
        Deque<String> deque = new Deque<>();
        Deque<String> reversedDeque = new Deque<>();
        while (!test.isEmpty()) {
            String s = test.readString();
            reversedDeque.addLast(s);
            deque.addFirst(s);
        }

        System.out.println("Deque size is: " + reversedDeque.size());
        for (String s : reversedDeque) {
            System.out.print(s + " ");
        }
        System.out.println();
        System.out.println("Deque size is: " + deque.size());
        for (String s : deque) {
            System.out.print(s + " ");
        }
        System.out.print("\nRemoving last elements: \n");
        while (!deque.isEmpty()) {
            System.out.print(deque.removeLast() + " ");
        }
        System.out.println("\nDeque size is: " + deque.size());
        System.out.print("Removing first elements: \n");
        while (!reversedDeque.isEmpty()) {
            System.out.print(reversedDeque.removeFirst() + " ");
        }
        System.out.println("\nDeque size is: " + reversedDeque.size());
    }
}
