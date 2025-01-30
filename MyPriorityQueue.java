import java.util.ArrayList;

// Priority Queue class to use in Dijkstra

public class MyPriorityQueue<T extends Comparable<T>> {
    private final ArrayList<T> heap;

    public MyPriorityQueue() {
        heap = new ArrayList<>();
    }



    public void add(T element) {
        heap.add(element);
        percolateUp(heap.size() - 1);
    }


    public T poll() {
        if (isEmpty()) return null;

        T root = heap.getFirst(); // The smallest element
        int lastIndex = heap.size() - 1;
        T last = heap.get(lastIndex);
        heap.remove(lastIndex);

        if (!isEmpty()) {
            heap.set(0, last);
            percolateDown(0);
        }

        return root;
    }


    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // return number of elements
    public int size() {
        return heap.size();
    }


    // moves the element at 'index' up the heap until the heap property is restored
    private void percolateUp(int index) {
        T element = heap.get(index);
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            T parent = heap.get(parentIndex);

            // if element is smaller than its parent, swap and continue up
            if (element.compareTo(parent) < 0) {
                heap.set(index, parent);
                index = parentIndex;
            } else {
                break;
            }
        }
        heap.set(index, element);
    }


    //moves the element at 'index' down the heap until the heap property is restored
    private void percolateDown(int index) {
        int size = heap.size();
        T element = heap.get(index);

        int half = size / 2; // no need to check beyond leaf nodes
        while (index < half) {
            int leftChild = (index * 2) + 1;
            int rightChild = leftChild + 1;

            // find the smaller of the two children
            int smallest = leftChild;
            if (rightChild < size && heap.get(rightChild).compareTo(heap.get(leftChild)) < 0) {
                smallest = rightChild;
            }

            // if the smallest child is not smaller than the element, heap is fixed
            if (heap.get(smallest).compareTo(element) >= 0) {
                break;
            }

            // otherwise, move the smaller child up
            heap.set(index, heap.get(smallest));
            index = smallest;
        }
        heap.set(index, element);
    }
}
