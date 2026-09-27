// simple resizable array, similar idea to ArrayList but done manually
public class DynamicArray {
    private int[] data;
    private int size;

    public DynamicArray() {
        data = new int[4];
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

    // add to end
    public void add(int x) {
        if (size == data.length) grow();
        data[size] = x;
        size++;
    }

    // add at given index, shifts everything after it to the right
    public void add(int index, int x) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("bad index " + index);
        if (size == data.length) grow();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = x;
        size++;
    }

    // remove element at index, shifts everything after it to the left
    public int remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("bad index " + index);
        int removed = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
        return removed;
    }

    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("bad index " + index);
        return data[index];
    }

    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            if (data[i] == x) return true;
        }
        return false;
    }

    // like contains but also returns how many comparisons it took (used for benchmarking)
    public int containsCount(int x) {
        int comparisons = 0;
        for (int i = 0; i < size; i++) {
            comparisons++;
            if (data[i] == x) break;
        }
        return comparisons;
    }
}
