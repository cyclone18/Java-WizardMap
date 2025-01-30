public class MyList<T> {
    private T[] elements;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public MyList() {
        //  we use Object[] then cast
        this.elements = (T[]) new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    public void add(T element) {
        ensureCapacity();
        elements[size++] = element;
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index);
        return elements[index];
    }

    public int size() {
        return size;
    }


    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length * 2;
            T[] newArray = (T[]) new Object[newCapacity];
            System.arraycopy(elements, 0, newArray, 0, size);
            elements = newArray;
        }
    }
}

