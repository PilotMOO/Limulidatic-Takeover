package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.data;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;

@SuppressWarnings("unchecked")
public class ReversibleArray<N> extends AbstractList<N> implements Iterable<N> {
    public ReversibleArray(int startingCount){ this(startingCount, false); }
    public ReversibleArray(int startingCount, boolean flip){ this((N[])new Object[startingCount], flip); }
    public ReversibleArray(N[] objs, boolean flip){
        this.objects = objs;
        this.capacity = objs.length;
        this.count = lastIndexOf(null);
        this.flip = flip;
    }
    private N[] objects;
    private int count, capacity;
    private boolean flip;
    public void flip(){ flip = !flip; }
    public void startFirst(){ flip = true; }

    @Override
    public boolean add(N n) {
        add(n, 1);
        return true;
    }
    public void add(N obj, int grow){
        System.out.println("Adding... count: " + count + ", capacity: " + capacity + ", grow: " + grow);
        if (capacity <= count + 1){
            int newSize = capacity + grow;
            Object[] array = new Object[newSize];
            System.arraycopy(objects, 0, array, 0, count);
            objects = (N[])array;
            capacity = newSize;
        }
        objects[++count] = obj;
    }

    public N get(int index) {
        return objects[flip ? capacity - index : index];
    }
    public N getContextless(int index) {
        return objects[index];
    }

    public N remove(int index) {
        if (flip) index = capacity - index;
        N obj = objects[index];
        objects[index] = null;
        return obj;
    }
    public N removeContextless(int index){
        N obj = objects[index];
        objects[index] = null;
        return obj;
    }

    @Override
    public N set(int index, N element) {
        if (index >= capacity){
            Object[] array = new Object[index];
            System.arraycopy(objects, 0, array, 0, count);
            objects = (N[])array;
        }
        return objects[index] = element;
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean contains(Object o) {
        Iterator<N> iter = iterator();
        do { if (iter.next().equals(o)) return true; }
        while (iterator().hasNext());
        return false;
    }

    public void clear(boolean keepSize) {
        objects = (N[]) new Object[keepSize ? capacity : (capacity = 0)];
        count = 0;
    }

    public void cap(){
        if (count == 0){
            objects = (N[])new Object[0];
            return;
        }
        int index = capacity;
        while (objects[--index] == null);
        cap(index);
    }
    public void cap(int count){
        Object[] values = new Object[count];
        System.arraycopy(objects, 0, values, 0, count);
        this.count = capacity = count;
        objects = (N[])values;
    }

    @Override public @NotNull Iterator<N> iterator() { return new InvertibleIterator(); }
    @Override public @NotNull ListIterator<N> listIterator() { return new InvertibleListIterator();}
    @Override public @NotNull ListIterator<N> listIterator(int index) {
        InvertibleListIterator lIter = new InvertibleListIterator();
        lIter.index = flip ? capacity - index : index;
        return lIter;
    }

    private class InvertibleIterator implements Iterator<N>{
        protected InvertibleIterator(){
            array = objects;
            size = array.length;
            if (flip){ index = size; inc = -1; } else { index = -1; inc = 1; }
        }
        protected final int size;
        protected final N[] array;
        protected int index;
        protected final int inc;

        @Override
        public boolean hasNext() {
            int mod = index + inc;
            return mod >= 0 && mod < size;
        }
        @Override public N next() { return array[index += inc]; }
    }
    private class InvertibleListIterator extends InvertibleIterator implements ListIterator<N>{
        @Override
        public boolean hasPrevious() {
            return flip ? index < size : index > 0;
        }

        @Override
        public N previous() {
            return array[index -= inc];
        }

        @Override public int nextIndex() { return index + 1; }
        @Override public int previousIndex() { return index - 1; }

        @Override
        public void remove() {
            array[index] = null;
            ReversibleArray.this.remove(index);
        }

        @Override
        public void set(N n) {
            array[index] = n;
            ReversibleArray.this.set(index, n);
        }

        @Override
        public void add(N n) {
            throw new UnsupportedOperationException("FUCK YOU");
        }
    }
}
