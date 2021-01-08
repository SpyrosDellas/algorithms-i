/* *****************************************************************************
 *  Name: Spyridon Dellas
 *  Date: 20/05/2020
 *  Description: Left-leaning red-black tree implementation
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

public class RedBlackTree<K extends Comparable<K>, V> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;
    private Node root;           // root of the BST
    private Node lastAccessed;   // cache of the last accessed node

    private class Node {
        private K key;
        private V val;
        private Node left;
        private Node right;
        private int size;        // number of nodes in subtree rooted here
        private boolean color;   // color of link from parent to this node

        public Node(K key, V val, int size, boolean color) {
            this.key = key;
            this.val = val;
            this.size = size;
            this.color = color;
        }
    }

    // create a red-black tree
    public RedBlackTree() {
        root = null;
        lastAccessed = null;
    }

    /* *************************************************************************
     *                          Node helper methods
     **************************************************************************/

    // is node x red? false if x is null
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    // number of nodes in subtree rooted at x; 0 if x is null
    private int size(Node x) {
        if (x == null) return 0;
        return x.size;
    }

    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        return size(root);
    }

    // Is this symbol table empty?
    public boolean isEmpty() {
        return root == null;
    }

    /* *************************************************************************
     *                      Standard BST search
     **************************************************************************/

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

    /* *************************************************************************
     *                     Red-black tree insertion
     **************************************************************************/

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
        root.color = BLACK;
        assert check();
    }

    // Change key’s value to val if key in subtree rooted at x. Otherwise, add
    // new node to subtree associating key with val.
    // The code for the recursive put() for red-black BSTs is identical to put()
    // in elementary BSTs except for the three if statements after the recursive
    // calls, which provide near-perfect balance in the tree by maintaining a
    // 1-1 correspondence with 2-3 trees, on the way up the search path.
    // The first rotates left any right-leaning 3-node (or a right-leaning red
    // link at the bottom of a temporary 4-node);
    // The second rotates right the top link in a temporary 4-node with two
    // left-leaning red links;
    // The third flips colors to pass a red link up the tree
    private Node put(Node x, K key, V val) {
        if (x == null) {
            return new Node(key, val, 1, RED);
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
        if (!isRed(x.left) && isRed(x.right))
            x = rotateLeft(x);
        if (isRed(x.left) && isRed(x.left.left))
            x = rotateRight(x);
        if (isRed(x.left) && isRed(x.right))
            flipColors(x);
        return x;
    }

    /* *************************************************************************
     *                     Red-black tree deletion
     **************************************************************************/

    // Removes the smallest key and associated value from the symbol table
    public void deleteMin() {
        if (isEmpty())
            throw new NoSuchElementException("BST is empty");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMin(root);
        if (!isEmpty()) root.color = BLACK;
        assert check();
    }

    // delete the key-value pair with the minimum key rooted at h
    private Node deleteMin(Node h) {
        if (h.left == null)
            return null;

        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);

        h.left = deleteMin(h.left);
        return balance(h);
    }

    // Removes the smallest key and associated value from the symbol table
    public void deleteMax() {
        if (isEmpty())
            throw new NoSuchElementException("BST is empty");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMax(root);
        if (!isEmpty()) root.color = BLACK;
        assert check();
    }

    // delete the key-value pair with the maximum key rooted at h
    private Node deleteMax(Node h) {
        if (isRed(h.left))
            h = rotateRight(h);

        if (h.right == null)
            return null;

        if (!isRed(h.right) && !isRed(h.right.left))
            h = moveRedRight(h);

        h.right = deleteMax(h.right);
        return balance(h);
    }

    // Removes the specified key and its associated value from this symbol table
    public void delete(K key) {
        if (key == null)
            throw new IllegalArgumentException("argument to delete() is null");
        if (!contains(key)) return;

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, key);
        if (!isEmpty()) root.color = BLACK;
        assert check();
    }

    // delete the key-value pair with the given key rooted at h
    private Node delete(Node h, K key) {
        if (key.compareTo(h.key) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, key);
        }
        else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (key.compareTo(h.key) == 0 && (h.right == null))
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (key.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.val = x.val;
                // h.val = get(h.right, min(h.right).key);
                // h.key = min(h.right).key;
                h.right = deleteMin(h.right);
            }
            else h.right = delete(h.right, key);
        }
        return balance(h);
    }


    /* *************************************************************************
     *                Red-black tree helper functions
     **************************************************************************/

    // Make a right-leaning link lean to the left
    // We organize the computation as a method that takes a link to a red-black
    // BST as argument and, assuming that link to be to a Node h whose right
    // link is red, makes the necessary adjustments and returns a link to a
    // node that is the root of red-black BST for the same set of keys whose
    // left link is red.
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    // Make a left-leaning link lean to the right
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        x.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
        h.color = !h.color;
    }

    // restore red-black tree invariant
    private Node balance(Node h) {
        if (isRed(h.right))
            h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left))
            h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right))
            flipColors(h);
        h.size = size(h.left) + size(h.right) + 1;
        return h;
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    /* *************************************************************************
     *                      Utility functions
     **************************************************************************/

    // Returns the height of the BST (for debugging)
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null)
            return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /* *************************************************************************
     *                  Ordered symbol table methods
     **************************************************************************/
    // smallest key
    public K min() {
        if (isEmpty())
            throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).key;
    }

    private Node min(Node x) {
        while (x.left != null)
            x = x.left;
        return x;
    }

    // largest key
    public K max() {
        if (isEmpty())
            throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).key;
    }

    private Node max(Node x) {
        while (x.right != null)
            x = x.right;
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

    /* *************************************************************************
     *                 Range count and range search
     **************************************************************************/

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

    /* *************************************************************************
     *       Check integrity of red-black tree data structure
     **************************************************************************/

    private boolean check() {
        if (!isBST())
            StdOut.println("Not in symmetric order");
        if (!isSizeConsistent())
            StdOut.println("Subtree counts not consistent");
        if (!isRankConsistent())
            StdOut.println("Ranks not consistent");
        if (!is23())
            StdOut.println("Not a 2-3 tree");
        if (!isBalanced())
            StdOut.println("Not balanced");
        return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    private boolean isBST(Node x, K min, K max) {
        if (x == null)
            return true;
        if (min != null && x.key.compareTo(min) <= 0)
            return false;
        if (max != null && x.key.compareTo(max) >= 0)
            return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }

    // are the size fields correct?
    private boolean isSizeConsistent() {
        return isSizeConsistent(root);
    }

    private boolean isSizeConsistent(Node x) {
        if (x == null)
            return true;
        if (x.size != size(x.left) + size(x.right) + 1)
            return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    }

    // check that ranks are consistent
    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i)))
                return false;
        for (K key : keys())
            if (key.compareTo(select(rank(key))) != 0)
                return false;
        return true;
    }

    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean is23() {
        if (root == null)
            return true;
        if (root.color != BLACK)
            return false;
        return is23(root);
    }

    private boolean is23(Node x) {
        if (x == null)
            return true;
        if (isRed(x.right))
            return false;
        if (isRed(x) && isRed(x.left))
            return false;
        return is23(x.left) && is23(x.right);
    }

    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() {
        int black = 0;     // number of black links on path from root to max
        Node x = root;
        while (x != null) {
            black++;
            x = x.right;
        }
        return isBalanced(root, black);
    }

    // does every path from the root to a leaf have the given number of black links?
    private boolean isBalanced(Node x, int black) {
        if (x == null)
            return black == 0;
        if (!isRed(x))
            black--;
        return isBalanced(x.left, black) && isBalanced(x.right, black);
    }

    /* ************************************************************************
     *                          Test client
     **************************************************************************/

    public static void main(String[] args) {
        RedBlackTree<Integer, String> st = new RedBlackTree<>();

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
        System.out.println("Iterating through the keys using keysIterator():");
        while (keys.hasNext()) {
            System.out.println(keys.next());
        }

    }
}

