public class LinkedQueue<E> implements Queue<E> {

    private static class Elem<T> {

        private T value;
        private Elem<T> next;

        private Elem( T value, Elem<T> next ) {
            this.value = value;
            this.next = next;
        }
    }
    private EmptyQueueException e;
    private Elem<E> front;
    private Elem<E> rear;

    public E peek() {
        if(front.value==null){
            throw e=new EmptyQueueException("Empty Queue");
        }
        else return front.value;
    }

    public void enqueue( E o ) {
        Elem<E> newElem;
        if(o==null){
            throw e=new EmptyQueueException("Empty Queue");
        }
        else {
            newElem = new Elem<E>(o, null);

            if (rear == null) {
                front = rear = newElem;
            } else {
                rear.next = newElem;
                rear = newElem;
            }
        }
    }

    public E dequeue() {
        E result = front.value;
        if ( front != null & front.next == null ) {
            front = rear = null;
        } else {
            front = front.next;
        }
        return result;
    }

    public boolean isEmpty() {
        return front == null;
    }

}
