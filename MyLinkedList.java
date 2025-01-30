
public class MyLinkedList<K, V> { // I used this class explicitly for my custom hashmap implementation
    public class Node {
        K key;
        V value;
        Node next;

        Node(K key, V value) { // constructor
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }
    private Node head;
    public Node getHead() {
        return head;
    }
    public MyLinkedList() { // constructor for linked list
        this.head = null;
    }

    // add a key-value pair to the linked list
    public void add(K key, V value) {
        Node newNode = new Node(key, value);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                // if the key already exists, update the value
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            // check the last node
            if (current.key.equals(key)) {
                current.value = value;
            } else {
                current.next = newNode;
            }
        }
    }

    // get a value by key
    public V get(K key) {
        Node current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null; // key not found
    }


    // check if a key exists
    public boolean contains(K key) {
        Node current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
}
