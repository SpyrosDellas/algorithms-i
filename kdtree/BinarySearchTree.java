/* *****************************************************************************
 *  Name: Spyridon Dellas
 *  Date: 19/05/2020
 *  Description: Classic unbalanced Binary Search Tree implementation
 *
 * Convention 1 - No duplicate keys
 * Only one value is associated with each key (no duplicate keys in a table).
 * When a client puts a key-value pair into a table already containing that key
 * (and an associated value), the new value replaces the old one.
 *
 * Convention 2 - No null keys
 * Keys must not be null. Use of a null key results in an exception at runtime.
 *
 * Convention 3 - No null values
 * No key can be associated with the value null. This convention is directly
 * tied to our specification in the API that get() should return null for keys
 * not in the table, effectively associating the value null with every key not
 * in the table.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinarySearchTree<K extends Comparable<K>, V> {

    private Node root;
    private Node lastAccessed;

    private class Node {
        private K key;
        private V val;
        private Node left;
        private Node right;
        private int size;   // number of nodes in subtree rooted here

        public Node(K key, V val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    // create a binary Search Tree
    public BinarySearchTree() {
        root = null;
        lastAccessed = null;
    }

    // is the table empty?
    public boolean isEmpty() {
        return size(root) == 0;
    }

    // number of key-value pairs
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null)
            return 0;
        else
            return x.size;
    }

    // is there a value paired with key?
    public boolean contains(K key) {
        if (key == null)
            throw new IllegalArgumentException("calls contains() with null key");
        return get(key) != null;
    }

    // value paired with key (null if key is absent)
    public V get(K key) {
        if (key == null)
            throw new IllegalArgumentException("calls get() with null key");
        if (lastAccessed != null && key.compareTo(lastAccessed.key) == 0)
            return lastAccessed.val;
        return get(root, key);
    }

    // Return value associated with key in the subtree rooted at x;
    // return null if key not present in subtree rooted at x
    private V get(Node x, K key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp == 0) {
                lastAccessed = x;
                return x.val;
            }
            else if (cmp < 0)
                x = x.left;
            else
                x = x.right;
        }
        return null;
    }

    // put key-value pair into the table (and remove key from table if value
    // is null)
    public void put(K key, V val) {
        if (key == null)
            throw new IllegalArgumentException("calls put() with null key");
        if (val == null) {
            delete(key);
            return;
        }
        if (lastAccessed != null && key.compareTo(lastAccessed.key) == 0) {
            lastAccessed.val = val;
            return;
        }
        root = put(root, key, val);
        assert check();
    }

    // Change key’s value to val if key in subtree rooted at x.
    // Otherwise, add new node to subtree associating key with val.
    private Node put(Node x, K key, V val) {
        if (x == null) {
            return new Node(key, val, 1);
        }
        int cmp = key.compareTo(x.key);
        if (cmp == 0) {
            x.val = val;
        }
        else if (cmp < 0) {
            x.left = put(x.left, key, val);
        }
        else {
            x.right = put(x.right, key, val);
        }
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    // delete smallest key
    public void deleteMin() {
        if (isEmpty())
            throw new NoSuchElementException("Symbol table is empty");
        root = deleteMin(root);
        assert check();
    }

    private Node deleteMin(Node x) {
        if (x.left == null)
            return x.right;
        x.left = deleteMin(x.left);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    // delete largest key
    public void deleteMax() {
        if (isEmpty())
            throw new NoSuchElementException("Symbol table is empty");
        root = deleteMax(root);
        assert check();
    }

    private Node deleteMax(Node x) {
        if (x.right == null)
            return x.left;
        x.right = deleteMax(x.right);
        x.size = x.left.size + x.right.size + 1;
        return x;
    }

    // remove key (and its value) from table
    public void delete(K key) {
        if (key == null)
            throw new IllegalArgumentException("calls delete() with null key");
        root = delete(root, key);
        assert check();
    }

    // implements Hibbart deletion, i.e. a deleted node x that has two children
    // is replaced by its successor
    private Node delete(Node x, K key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            x.left = delete(x.left, key);
        else if (cmp > 0)
            x.right = delete(x.right, key);
        else {
            if (x.left == null)
                return x.right;
            if (x.right == null)
                return x.left;
            Node copy = x;
            x = min(x.right);
            x.left = copy.left;
            x.right = deleteMin(copy.right);
        }
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    // smallest key
    public K min() {
        if (isEmpty())
            throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).key;
    }

    private Node min(Node x) {
        while (x.left != null) x = x.left;
        return x;
    }

    // largest key
    public K max() {
        if (isEmpty())
            throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).key;
    }

    private Node max(Node x) {
        while (x.right != null) x = x.right;
        return x;
    }

    // largest key less than or equal to key
    public K floor(K key) {
        if (key == null)
            throw new IllegalArgumentException("argument to floor() is null");
        if (isEmpty())
            throw new NoSuchElementException("calls floor() with empty symbol table");
        Node floor = floor(root, key);
        if (floor == null)
            throw new NoSuchElementException("argument to floor() is too small");
        return floor.key;
    }

    private Node floor(Node x, K key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0)
            return x;
        else if (cmp < 0)
            return floor(x.left, key);
        Node floor = floor(x.right, key);
        if (floor == null)
            return x;
        else
            return floor;
    }

    // smallest key greater than or equal to key
    public K ceiling(K key) {
        if (key == null)
            throw new IllegalArgumentException("argument to ceiling() is null");
        if (isEmpty())
            throw new NoSuchElementException("calls ceiling() with empty symbol table");
        Node ceiling = ceiling(root, key);
        if (ceiling == null)
            throw new NoSuchElementException("argument to ceiling() is too large");
        return ceiling.key;
    }

    private Node ceiling(Node x, K key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0)
            return x;
        else if (cmp > 0)
            return ceiling(x.right, key);
        Node ceiling = ceiling(x.left, key);
        if (ceiling == null)
            return x;
        else
            return ceiling;
    }

    // key of rank k
    public K select(int k) {
        if (isEmpty())
            throw new NoSuchElementException("calls select() with empty symbol table");
        if (k < 0)
            throw new IllegalArgumentException("argument to select() is less than zero");
        if (k >= size())
            throw new IllegalArgumentException("argument to select() is too large");
        return select(root, k);
    }

    private K select(Node x, int rank) {
        if (x == null) return null;
        int leftSize = size(x.left);
        if (leftSize == rank)
            return x.key;
        else if (leftSize > rank)
            return select(x.left, rank);
        else
            return select(x.right, rank - leftSize - 1);
    }

    // number of keys less than key
    public int rank(K key) {
        if (key == null)
            throw new IllegalArgumentException("argument to rank() is null");
        if (isEmpty())
            throw new NoSuchElementException("calls rank() with empty symbol table");
        return rank(root, key);
    }

    private int rank(Node x, K key) {
        if (x == null) return 0;
        int cmp = key.compareTo(x.key);
        if (cmp == 0)
            return size(x.left);
        else if (cmp < 0)
            return rank(x.left, key);
        else
            return size(x.left) + 1 + rank(x.right, key);
    }

    // number of keys in[lo ..hi]
    public int size(K lo, K hi) {
        if (lo == null)
            throw new IllegalArgumentException("first argument to size() is null");
        if (hi == null)
            throw new IllegalArgumentException("second argument to size() is null");
        if (hi.compareTo(lo) <= 0)
            return 0;
        else if (contains(hi))
            return rank(hi) - rank(lo) + 1;
        else
            return rank(hi) - rank(lo);
    }

    // all keys in the table, in sorted order
    public Iterable<K> keys() {
        if (isEmpty())
            return new Queue<K>();
        return keys(min(), max());
    }

    // keys in [lo ..hi], in sorted order
    public Iterable<K> keys(K lo, K hi) {
        if (lo == null)
            throw new IllegalArgumentException("first argument to keys() is null");
        if (hi == null)
            throw new IllegalArgumentException("second argument to keys() is null");
        Queue<K> keys = new Queue<>();
        addKeys(root, lo, hi, keys);
        return keys;
    }

    private void addKeys(Node x, K lo, K hi, Queue<K> keys) {
        if (x == null)
            return;
        int cmpLo = x.key.compareTo(lo);
        int cmpHi = x.key.compareTo(hi);
        if (cmpLo < 0)
            addKeys(x.right, lo, hi, keys);
        else if (cmpHi > 0)
            addKeys(x.left, lo, hi, keys);
        else {
            addKeys(x.left, lo, hi, keys);
            keys.enqueue(x.key);
            addKeys(x.right, lo, hi, keys);
        }
    }

    // Returns the height of the BST (a 1-node tree has height 0)
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    // Returns the keys in the BST in level order traversal
    public Iterable<K> levelOrder() {
        if (isEmpty())
            throw new NoSuchElementException("calls levelOrder() with empty symbol table");
        Queue<K> keys = new Queue<>();
        Queue<Node> nodes = new Queue<>();
        nodes.enqueue(root);
        while (!nodes.isEmpty()) {
            Node x = nodes.dequeue();
            if (x == null) continue;
            keys.enqueue(x.key);
            nodes.enqueue(x.left);
            nodes.enqueue(x.right);
        }
        return keys;
    }

    public Iterator<K> keysIterator() {
        return new KeysIter();
    }

    private class KeysIter implements Iterator<K> {

        private Stack<Node> subTree;

        public KeysIter() {
            subTree = new Stack<>();
            Node x = root;
            if (x == null)
                return;
            subTree.push(x);
            while (x.left != null) {
                subTree.push(x.left);
                x = x.left;
            }
        }

        public boolean hasNext() {
            return !subTree.isEmpty();
        }

        public K next() {
            if (!hasNext())
                throw new NoSuchElementException();
            Node result = subTree.pop();
            Node next = result;
            if (next.right != null) {
                subTree.push(next.right);
                next = next.right;
                while (next.left != null) {
                    subTree.push(next.left);
                    next = next.left;
                }
            }
            return result.key;
        }
    }

    /* ************************************************************************
     *               Check integrity of BST data structure
     **************************************************************************/

    private boolean check() {
        if (!isBST())
            StdOut.println("Not in symmetric order");
        if (!isSizeConsistent())
            StdOut.println("Subtree counts not consistent");
        if (!isRankConsistent())
            StdOut.println("Ranks not consistent");
        return isSizeConsistent() && isBST() && isRankConsistent();
    }

    private boolean isSizeConsistent() {
        return isSizeConsistent(root);
    }

    // are the size fields correct?
    private boolean isSizeConsistent(Node x) {
        if (x == null)
            return true;
        if (size(x) != size(x.left) + size(x.right) + 1)
            return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since
    // order is strict (no equals)
    private boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    private boolean isBST(Node x, K min, K max) {
        if (x == null)
            return true;
        if (min != null && x.key.compareTo(min) < 0)
            return false;
        if (max != null && x.key.compareTo(max) > 0)
            return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }

    // check that ranks are consistent
    private boolean isRankConsistent() {
        for (int rank = 0; rank < size(); rank++) {
            if (rank(select(rank)) != rank)
                return false;
        }
        for (K key : keys()) {
            if (select(rank(key)).compareTo(key) != 0)
                return false;
        }
        return true;
    }

    /* ************************************************************************
     *                          Test client
     **************************************************************************/

    public static void main(String[] args) {
        BinarySearchTree<Integer, String> st = new BinarySearchTree<>();

        In file = new In("tinyTale.txt");
        for (int i = 0; !file.isEmpty(); i++) {
            String word = file.readString();
            st.put(-i, word);
        }

        // for (String s : st.levelOrder())
        //    StdOut.println(s + " " + st.get(s));
        //

        StdOut.println("Minimum key = " + st.min());
        StdOut.println("Maximum key = " + st.max());
        StdOut.println("Binary tree size = " + st.size());
        for (int s : st.keys())
            StdOut.println(s + " " + st.get(s));

        for (int s : st.levelOrder())
            StdOut.println(s + " " + st.get(s));

        Iterator<Integer> keys = st.keysIterator();
        System.out.println("Has next? " + keys.hasNext());
        while (keys.hasNext()) {
            System.out.println(keys.next());
        }

    }
}
