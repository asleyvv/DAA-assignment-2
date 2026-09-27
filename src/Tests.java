import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

// basic manual tests, no framework, just prints PASS/FAIL and throws if something is really wrong
public class Tests {

    static int passed = 0;
    static int failed = 0;

    static void check(String name, boolean cond) {
        if (cond) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    public static void main(String[] args) {
        testDynamicArray();
        testLinkedList();
        testMinHeap();
        crossCheckWithJavaCollections();

        System.out.println("\n" + passed + " passed, " + failed + " failed");
    }

    static void testDynamicArray() {
        DynamicArray a = new DynamicArray();
        // empty structure
        check("empty size 0", a.size() == 0);
        check("empty contains false", !a.contains(5));

        // one element
        a.add(10);
        check("size 1 after add", a.size() == 1);
        check("get(0) after single add", a.get(0) == 10);

        // multiple elements
        a.add(20);
        a.add(30);
        check("size 3", a.size() == 3);
        check("get(1)", a.get(1) == 20);

        // duplicates
        a.add(10);
        check("contains duplicate", a.contains(10));

        // add at index
        a.add(0, 999);
        check("add at front", a.get(0) == 999);
        check("shifted element", a.get(1) == 10);

        // boundary index (end)
        a.add(a.size(), 111);
        check("add at end via index", a.get(a.size() - 1) == 111);

        // remove
        int removed = a.remove(0);
        check("removed correct val", removed == 999);
        check("size after remove", a.size() == 5);

        // invalid indices
        boolean threw = false;
        try { a.get(-1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get negative throws", threw);

        threw = false;
        try { a.get(a.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get past end throws", threw);

        threw = false;
        try { a.remove(100); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("remove bad index throws", threw);

        // large input, just make sure it doesn't crash and grows properly
        DynamicArray big = new DynamicArray();
        for (int i = 0; i < 50000; i++) big.add(i);
        check("large size", big.size() == 50000);
        check("large get", big.get(49999) == 49999);
    }

    static void testLinkedList() {
        LinkedList l = new LinkedList();
        check("empty size 0", l.size() == 0);
        check("empty contains false", !l.contains(1));

        l.add(1);
        check("size 1", l.size() == 1);
        check("get(0)", l.get(0) == 1);

        l.add(2);
        l.add(3);
        check("size 3", l.size() == 3);
        check("get(2)", l.get(2) == 3);

        l.add(1, 99);
        check("add middle", l.get(1) == 99);

        l.add(0, 77);
        check("add front", l.get(0) == 77);

        l.add(l.size(), 55);
        check("add end via index", l.get(l.size() - 1) == 55);

        l.add(5); // duplicate value check
        l.add(5);
        check("contains duplicate", l.contains(5));

        int removed = l.remove(0);
        check("remove front value", removed == 77);

        boolean threw = false;
        try { l.get(-1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get negative throws", threw);

        threw = false;
        try { l.get(l.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get past end throws", threw);

        // large input
        LinkedList big = new LinkedList();
        for (int i = 0; i < 20000; i++) big.add(i);
        check("large size", big.size() == 20000);
        check("large get", big.get(19999) == 19999);

        // remove down to empty, tail pointer should stay consistent
        LinkedList small = new LinkedList();
        small.add(1);
        small.remove(0);
        check("empty after removing only element", small.size() == 0);
        small.add(2); // should still work after list became empty
        check("add works after emptied", small.get(0) == 2);
    }

    static void testMinHeap() {
        MinHeap h = new MinHeap();

        boolean threw = false;
        try { h.peekMin(); } catch (RuntimeException e) { threw = true; }
        check("peekMin on empty throws", threw);

        h.insert(5);
        check("size 1", h.size() == 1);
        check("peekMin single", h.peekMin() == 5);

        h.insert(2);
        h.insert(8);
        h.insert(1);
        check("min after inserts", h.peekMin() == 1);
        check("heap property holds", h.isValidHeap());

        h.insert(1); // duplicate
        check("heap property holds with duplicate", h.isValidHeap());

        // extract everything, should come out sorted
        int prev = Integer.MIN_VALUE;
        boolean sorted = true;
        int count = h.size();
        for (int i = 0; i < count; i++) {
            int m = h.extractMin();
            if (m < prev) sorted = false;
            prev = m;
        }
        check("extractMin gives non-decreasing order", sorted);
        check("heap empty after draining", h.size() == 0);

        // large random test
        Random rnd = new Random(42);
        MinHeap big = new MinHeap();
        for (int i = 0; i < 10000; i++) {
            big.insert(rnd.nextInt(1000000));
        }
        check("large heap valid", big.isValidHeap());
        int last = Integer.MIN_VALUE;
        boolean bigSorted = true;
        while (big.size() > 0) {
            int m = big.extractMin();
            if (m < last) bigSorted = false;
            last = m;
        }
        check("large heap extracts sorted", bigSorted);
    }

    // compare our structures against java.util for a bunch of random ops to catch subtle bugs
    static void crossCheckWithJavaCollections() {
        Random rnd = new Random(7);
        DynamicArray da = new DynamicArray();
        ArrayList<Integer> ref = new ArrayList<>();

        for (int op = 0; op < 5000; op++) {
            int choice = rnd.nextInt(4);
            if (choice == 0 || ref.isEmpty()) {
                int v = rnd.nextInt(10000);
                da.add(v);
                ref.add(v);
            } else if (choice == 1) {
                int idx = rnd.nextInt(ref.size());
                int v = rnd.nextInt(10000);
                da.add(idx, v);
                ref.add(idx, v);
            } else if (choice == 2 && !ref.isEmpty()) {
                int idx = rnd.nextInt(ref.size());
                int a = da.remove(idx);
                int b = ref.remove(idx);
                if (a != b) { check("cross check remove mismatch", false); }
            } else if (!ref.isEmpty()) {
                int idx = rnd.nextInt(ref.size());
                if (da.get(idx) != ref.get(idx)) { check("cross check get mismatch", false); }
            }
        }
        boolean same = da.size() == ref.size();
        for (int i = 0; i < ref.size() && same; i++) {
            if (da.get(i) != ref.get(i)) same = false;
        }
        check("dynamic array matches ArrayList after random ops", same);

        // heap cross check against sorting
        Random rnd2 = new Random(9);
        MinHeap heap = new MinHeap();
        ArrayList<Integer> vals = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            int v = rnd2.nextInt(100000);
            heap.insert(v);
            vals.add(v);
        }
        Collections.sort(vals);
        boolean matchAll = true;
        for (int i = 0; i < vals.size(); i++) {
            if (heap.extractMin() != vals.get(i)) matchAll = false;
        }
        check("heap extraction matches sorted reference", matchAll);
    }
}
