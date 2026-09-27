// singly linked list, keeps a tail pointer so add-to-end is O(1)
public class LinkedList {

    private static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    private Node head;
    private Node tail;
    private int size;

    public int size() {
        return size;
    }

    public void add(int x) {
        Node n = new Node(x);
        if (head == null) {
            head = n;
            tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
        size++;
    }

    public void add(int index, int x) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("bad index " + index);
        if (index == size) {
            add(x);
            return;
        }
        Node n = new Node(x);
        if (index == 0) {
            n.next = head;
            head = n;
            if (size == 0) tail = n;
            size++;
            return;
        }
        Node prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        n.next = prev.next;
        prev.next = n;
        size++;
    }

    public int remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("bad index " + index);
        int removed;
        if (index == 0) {
            removed = head.val;
            head = head.next;
            if (head == null) tail = null;
        } else {
            Node prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }
            removed = prev.next.val;
            prev.next = prev.next.next;
            if (prev.next == null) tail = prev;
        }
        size--;
        return removed;
    }

    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("bad index " + index);
        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.val;
    }

    public boolean contains(int x) {
        Node cur = head;
        while (cur != null) {
            if (cur.val == x) return true;
            cur = cur.next;
        }
        return false;
    }

    public int containsCount(int x) {
        int comparisons = 0;
        Node cur = head;
        while (cur != null) {
            comparisons++;
            if (cur.val == x) break;
            cur = cur.next;
        }
        return comparisons;
    }
}
