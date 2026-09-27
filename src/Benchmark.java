import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Benchmark {

    static final int[] SIZES = {100, 1000, 10000, 100000};
    static final int REPEATS = 5;
    static final long SEED = 42;

    public static void main(String[] args) throws IOException {
        workload1RandomAccess();
        workload2Search();
        workload3InsertRemove();
        workload4Heap();
        System.out.println("done, csv files written to results/tables");
    }

    // fills a structure with n random ints, same seed every time so runs are comparable
    static int[] makeRandomData(int n, Random rnd) {
        int[] out = new int[n];
        for (int i = 0; i < n; i++) out[i] = rnd.nextInt(1000000000);
        return out;
    }

    static DynamicArray buildArray(int[] src) {
        DynamicArray a = new DynamicArray();
        for (int v : src) a.add(v);
        return a;
    }

    static LinkedList buildList(int[] src) {
        LinkedList l = new LinkedList();
        for (int v : src) l.add(v);
        return l;
    }

    // ---------------- Workload 1: random access ----------------
    static void workload1RandomAccess() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter("results/tables/workload1_random_access.csv"));
        out.println("structure,n,avg_time_ns,accesses");

        for (int n : SIZES) {
            Random seedRnd = new Random(SEED);
            int[] data = makeRandomData(n, seedRnd);
            int[] indices = new int[10000];
            Random idxRnd = new Random(SEED);
            for (int i = 0; i < indices.length; i++) indices[i] = idxRnd.nextInt(n);

            long totalArr = 0;
            long totalList = 0;
            for (int rep = 0; rep < REPEATS; rep++) {
                DynamicArray arr = buildArray(data);
                long t0 = System.nanoTime();
                long sink = 0;
                for (int idx : indices) sink += arr.get(idx);
                long t1 = System.nanoTime();
                totalArr += (t1 - t0);
                if (sink == Long.MIN_VALUE) System.out.println(""); // stop JIT from removing the loop

                LinkedList list = buildList(data);
                long t2 = System.nanoTime();
                sink = 0;
                for (int idx : indices) sink += list.get(idx);
                long t3 = System.nanoTime();
                totalList += (t3 - t2);
                if (sink == Long.MIN_VALUE) System.out.println("");
            }
            out.println("DynamicArray," + n + "," + (totalArr / REPEATS) + "," + indices.length);
            out.println("LinkedList," + n + "," + (totalList / REPEATS) + "," + indices.length);
        }
        out.close();
    }

    // ---------------- Workload 2: search ----------------
    static void workload2Search() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter("results/tables/workload2_search.csv"));
        out.println("structure,n,avg_time_ns,total_comparisons");

        for (int n : SIZES) {
            Random seedRnd = new Random(SEED);
            int[] data = makeRandomData(n, seedRnd);
            // mix of values that exist and values that probably don't, same list used for both structures
            int[] queries = new int[1000];
            Random qRnd = new Random(SEED);
            for (int i = 0; i < queries.length; i++) {
                if (qRnd.nextBoolean() && n > 0) {
                    queries[i] = data[qRnd.nextInt(n)];
                } else {
                    queries[i] = qRnd.nextInt(1000000000);
                }
            }

            long totalArrTime = 0, totalListTime = 0;
            long totalArrComp = 0, totalListComp = 0;
            for (int rep = 0; rep < REPEATS; rep++) {
                DynamicArray arr = buildArray(data);
                long t0 = System.nanoTime();
                for (int q : queries) arr.contains(q);
                long t1 = System.nanoTime();
                totalArrTime += (t1 - t0);

                LinkedList list = buildList(data);
                long t2 = System.nanoTime();
                for (int q : queries) list.contains(q);
                long t3 = System.nanoTime();
                totalListTime += (t3 - t2);

                // comparisons counted separately since contains() doesn't return them
                if (rep == 0) {
                    for (int q : queries) totalArrComp += arr.containsCount(q);
                    for (int q : queries) totalListComp += list.containsCount(q);
                }
            }
            out.println("DynamicArray," + n + "," + (totalArrTime / REPEATS) + "," + totalArrComp);
            out.println("LinkedList," + n + "," + (totalListTime / REPEATS) + "," + totalListComp);
        }
        out.close();
    }

    // ---------------- Workload 3: insertion / removal ----------------
    static void workload3InsertRemove() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter("results/tables/workload3_insert_remove.csv"));
        out.println("structure,n,position,operation,avg_time_ns,element_moves");

        for (int n : SIZES) {
            Random seedRnd = new Random(SEED);
            int[] data = makeRandomData(n, seedRnd);

            runInsertRemove("DynamicArray", n, data, 0, out, true);
            runInsertRemove("LinkedList", n, data, 0, out, false);
            runInsertRemove("DynamicArray", n, data, n / 2, out, true);
            runInsertRemove("LinkedList", n, data, n / 2, out, false);
        }
        out.close();
    }

    static void runInsertRemove(String name, int n, int[] data, int pos, PrintWriter out, boolean isArray) {
        long insTime = 0, remTime = 0;
        long insMoves = 0, remMoves = 0;

        for (int rep = 0; rep < REPEATS; rep++) {
            if (isArray) {
                DynamicArray arr = buildArray(data);
                long t0 = System.nanoTime();
                long moves = 0;
                for (int i = 0; i < 1000; i++) {
                    arr.add(pos, i);
                    moves += (arr.size() - pos); // rough count of shifted elements
                }
                long t1 = System.nanoTime();
                insTime += (t1 - t0);
                insMoves = moves;

                long t2 = System.nanoTime();
                moves = 0;
                for (int i = 0; i < 1000; i++) {
                    arr.remove(pos);
                    moves += (arr.size() - pos);
                }
                long t3 = System.nanoTime();
                remTime += (t3 - t2);
                remMoves = moves;
            } else {
                LinkedList list = buildList(data);
                long t0 = System.nanoTime();
                for (int i = 0; i < 1000; i++) {
                    list.add(pos, i);
                }
                long t1 = System.nanoTime();
                insTime += (t1 - t0);
                insMoves = (long) pos * 1000; // node hops needed to reach pos each time

                long t2 = System.nanoTime();
                for (int i = 0; i < 1000; i++) {
                    list.remove(pos);
                }
                long t3 = System.nanoTime();
                remTime += (t3 - t2);
                remMoves = (long) pos * 1000;
            }
        }
        String posLabel = (pos == 0) ? "start" : "middle";
        out.println(name + "," + n + "," + posLabel + ",insert," + (insTime / REPEATS) + "," + insMoves);
        out.println(name + "," + n + "," + posLabel + ",remove," + (remTime / REPEATS) + "," + remMoves);
    }

    // ---------------- Workload 4: heap priority processing ----------------
    static void workload4Heap() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter("results/tables/workload4_heap.csv"));
        out.println("n,avg_insert_time_ns,avg_extract_time_ns,total_comparisons,sorted_output_ok");

        for (int n : SIZES) {
            Random seedRnd = new Random(SEED);
            int[] data = makeRandomData(n, seedRnd);

            long insTotal = 0, extTotal = 0;
            long comparisons = 0;
            boolean sortedOk = true;

            for (int rep = 0; rep < REPEATS; rep++) {
                MinHeap h = new MinHeap();
                long t0 = System.nanoTime();
                for (int v : data) h.insert(v);
                long t1 = System.nanoTime();
                insTotal += (t1 - t0);

                long t2 = System.nanoTime();
                int prev = Integer.MIN_VALUE;
                boolean ok = true;
                for (int i = 0; i < n; i++) {
                    int m = h.extractMin();
                    if (m < prev) ok = false;
                    prev = m;
                }
                long t3 = System.nanoTime();
                extTotal += (t3 - t2);
                if (!ok) sortedOk = false;

                if (rep == REPEATS - 1) comparisons = h.comparisons;
            }
            out.println(n + "," + (insTotal / REPEATS) + "," + (extTotal / REPEATS) + "," + comparisons + "," + sortedOk);
        }
        out.close();
    }
}
