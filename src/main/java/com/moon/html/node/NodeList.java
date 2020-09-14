package com.moon.html.node;

import com.moon.core.enums.Arrays2;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @author moonsky
 */
public class NodeList<N extends Node> extends ArrayList<N> {

    public final static NodeList EMPTY = new UnmodifiableNodeList();

    public NodeList(N... nodes) {
        if (nodes != null) {
            this.ensureCapacity(nodes.length);
            for (N node : nodes) {
                this.add(node);
            }
        }
    }

    private final static class UnmodifiableNodeList extends NodeList<Node> {

        @Override
        public void trimToSize() {
        }

        @Override
        public void ensureCapacity(int minCapacity) {
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public int indexOf(Object o) {
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            return -1;
        }

        @Override
        public Object clone() {
            return this;
        }


        @Override
        public Object[] toArray() {
            return Arrays2.OBJECTS.empty();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return Arrays.copyOf(a, 0);
        }

        @Override
        public Node get(int index) {
            return null;
        }

        @Override
        public Node set(int index, Node element) {
            return null;
        }

        @Override
        public boolean add(Node node) {
            return false;
        }

        @Override
        public void add(int index, Node element) {
        }

        @Override
        public Node remove(int index) {
            return null;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean addAll(Collection<? extends Node> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Node> c) {
            return false;
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public ListIterator<Node> listIterator(int index) {
            return ListItr.itr;
        }


        @Override
        public ListIterator<Node> listIterator() {
            return ListItr.itr;
        }

        @Override
        public Iterator<Node> iterator() {
            return ListItr.itr;
        }

        @Override
        public List<Node> subList(int fromIndex, int toIndex) {
            return this;
        }

        @Override
        public void forEach(Consumer<? super Node> action) {
        }

        @Override
        public Spliterator<Node> spliterator() {
            return EmptySpliterator.spliterator;
        }

        @Override
        public boolean removeIf(Predicate<? super Node> filter) {
            return false;
        }

        @Override
        public void replaceAll(UnaryOperator<Node> operator) {
        }

        @Override
        public void sort(Comparator<? super Node> c) {
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c == null || c.isEmpty();
        }

        @Override
        public String toString() {
            return getClass().toString();
        }

        @Override
        public Stream<Node> stream() {
            return Stream.of();
        }

        @Override
        public Stream<Node> parallelStream() {
            return Stream.of();
        }
    }

    private final static class EmptySpliterator implements Spliterator {

        private final static EmptySpliterator spliterator = new EmptySpliterator();

        @Override
        public boolean tryAdvance(Consumer action) {
            return false;
        }

        @Override
        public Spliterator trySplit() {
            return this;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }

    private final static class ListItr implements ListIterator {

        private final static ListItr itr = new ListItr();

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public Object previous() {
            return null;
        }

        @Override
        public int nextIndex() {
            return -1;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public void remove() {
        }

        @Override
        public void set(Object o) {
        }

        @Override
        public void add(Object o) {
        }
    }
}

