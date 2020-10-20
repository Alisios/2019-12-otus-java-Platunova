package ru.otus.generics;

import java.util.*;

public class DIYarrayList<T> implements List<T> {

    private T[] genericData;
    private int size;
    private static final int capacity_initial = 10;

    @SuppressWarnings("unchecked")
    DIYarrayList() {
        this.size = 0;
        this.genericData = (T[]) new Object[capacity_initial];
    }

    @SuppressWarnings("unchecked")
    public DIYarrayList(Collection<? extends T> c) {
        genericData = (T[]) c.toArray();
        if ((size = genericData.length) != 0) {
            if (genericData.getClass() != Object[].class)
                genericData = Arrays.copyOf(genericData, this.size);
        } else {
            this.genericData = null;
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(genericData, this.size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < this.size) {
            return (T[]) Arrays.copyOf(genericData, this.size, a.getClass());
        } else {
            System.arraycopy(genericData, 0, a, 0, this.size);
            return a;
        }
    }

    @Override
    public boolean add(T e) {
        if (this.size >= genericData.length) {
            @SuppressWarnings("unchecked")
            T[] optional_ar = (T[]) new Object[genericData.length << 1];
            System.arraycopy(genericData, 0, optional_ar, 0, genericData.length);
            genericData = optional_ar;
        }
        genericData[this.size] = e;
        this.size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        if ((index < 0) || (index >= this.size)) {
            throw new IndexOutOfBoundsException("Index " + (index) + " is out of range of list with size " + size);
        }
        return genericData[index];
    }

    @Override
    public T set(int index, T element) {
        if ((index < 0) || (index >= this.size)) {
            throw new IndexOutOfBoundsException("Index " + (index) + " is out of range of list with size " + size);
        }
        T previous = get(index);
        genericData[index] = element;
        return previous;
    }

    @Override
    public void add(int index, T element) {
        if ((index < 0) || (index >= this.size))
            throw new IndexOutOfBoundsException("Index " + (index) + " is out of range of list with size " + size);
        add(element);
        for (int i = size - 1; i > index; i--)
            genericData[i] = genericData[i - 1];
        genericData[index] = element;
    }

    @Override
    public T remove(int index) {
        if ((index < 0) || (index >= this.size))
            throw new IndexOutOfBoundsException("Index " + (index) + " is out of range of list with size " + size);
        T previous = get(index);
        for (int i = index; index < this.size - 1; i++)
            genericData[i] = genericData[i + 1];
        this.size--;
        return previous;
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }


    @Override
    public ListIterator<T> listIterator(int index) {
        if ((index < 0) || (index >= this.size))
            throw new IndexOutOfBoundsException("Index " + (index) + " is out of range of list with size " + size);

        return new ListIterator<>() {
            int current_index = index;

            @Override
            public boolean hasNext() {
                return current_index < size;
            }

            @Override
            public T next() {
                current_index++;
                return get(current_index - 1);
            }

            @Override
            public boolean hasPrevious() {
                return current_index > 0; //current_index < size && genericData[current_index] != null;
            }

            @Override
            public T previous() {
                current_index--;
                return get(current_index);
            }

            @Override
            public int nextIndex() {
                return current_index;
            }

            @Override
            public int previousIndex() {
                return current_index--;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(T t) {
                if ((current_index < 0) || (current_index > size)) {
                    throw new IndexOutOfBoundsException("Index " + (current_index - 1) + " is out of range of list with size " + size);
                }
                genericData[current_index - 1] = t;
            }

            @Override
            public void add(T t) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public Iterator<T> iterator() {
        return this.listIterator(0);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}
