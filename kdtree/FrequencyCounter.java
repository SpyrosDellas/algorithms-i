/* *****************************************************************************
 * Data files:    https://algs4.cs.princeton.edu/31elementary/tinyTale.txt
 *                https://algs4.cs.princeton.edu/31elementary/tale.txt
 *                https://algs4.cs.princeton.edu/31elementary/leipzig100K.txt
 *                https://algs4.cs.princeton.edu/31elementary/leipzig300K.txt
 *                https://algs4.cs.princeton.edu/31elementary/leipzig1M.txt
 *
 *  Read in a list of words from standard input and print out the most frequently
 * occurring word that has length greater than a given threshold. It is useful
 * as a test client for various symbol table implementations.
 *
 *  % java FrequencyCounter 1 < tinyTale.txt
 *  it 10
 *
 *  % java FrequencyCounter 8 < tale.txt
 *  business 122
 *
 *  % java FrequencyCounter 10 < leipzig1M.txt
 *  government 24763
 *
 *
 ***************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;


public class FrequencyCounter {

    // Do not instantiate.
    private FrequencyCounter() {
    }

    /**
     * Reads in a command-line integer and sequence of words from
     * standard input and prints out a word (whose length exceeds
     * the threshold) that occurs most frequently to standard output.
     * It also prints out the number of words whose length exceeds
     * the threshold and the number of distinct such words.
     */
    public static void main(String[] args) {
        int distinct = 0;
        int words = 0;
        int minlen = Integer.parseInt(args[0]);
        RedBlackTree<String, Integer> st = new RedBlackTree<>();

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

        // find a key with the highest frequency count
        String max = "";
        st.put(max, 0);
        for (String word : st.keys()) {
            if (st.get(word) > st.get(max))
                max = word;
        }

        StdOut.println(max + " " + st.get(max));
        StdOut.println("distinct = " + distinct);
        StdOut.println("tree size = " + st.size());
        StdOut.println("words    = " + words);
        StdOut.println("Time to build the tree: " + (end - start) / 1000 + " secs");
    }
}
