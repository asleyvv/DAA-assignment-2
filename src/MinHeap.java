// array based binary min-heap
public class MinHeap {
    private int[] data;
    private int size;
    public long comparisons; // running total, used for benchmarking

    public MinHeap() {
        data = new int[16];
        size = 0;
    }

    public int size() {
        return size;
    }

    private void grow() {
        int[] tmp = new int[data.length * 2];
        for (int i = 0; i < size; i++) tmp[i] = data[i];
        data = tmp;
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }

    private void swap(int a, int b) {
        int t = data[a];
        data[a] = data[b];
        data[b] = t;
    }

    public void insert(int x) {
        if (size == data.length) grow();
        data[size] = x;
        int i = size;
        size++;
        // bubble up while parent is bigger than us
        while (i > 0) {
            comparisons++;
            if (data[parent(i)] > data[i]) {
                swap(i, parent(i));
                i = parent(i);
            } else {
                break;
            }
        }
    }

    public int peekMin() {
        if (size == 0) throw new RuntimeException("heap is empty");
        return data[0];
    }

    public int extractMin() {
        if (size == 0) throw new RuntimeException("heap is empty");
        int min = data[0];
        size--;
        data[0] = data[size];
        siftDown(0);
        return min;
    }

    private void siftDown(int i) {
        while (true) {
            int l = left(i);
            int r = right(i);
            int smallest = i;
            if (l < size) {
                comparisons++;
                if (data[l] < data[smallest]) smallest = l;
            }
            if (r < size) {
                comparisons++;
                if (data[r] < data[smallest]) smallest = r;
            }
            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    // sanity check used in tests, walks the array and checks parent <= child everywhere
    public boolean isValidHeap() {
        for (int i = 1; i < size; i++) {
            if (data[parent(i)] > data[i]) return false;
        }
        return true;
    }
}
