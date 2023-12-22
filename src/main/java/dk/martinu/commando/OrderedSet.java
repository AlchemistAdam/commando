package dk.martinu.commando;

import org.jetbrains.annotations.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;

import static java.util.Spliterator.*;

@Unmodifiable
public abstract class OrderedSet<E> implements Set<E> {

    @SuppressWarnings("rawtypes")
    private static final OrderedSet EMPTY_SET = new SetEmpty();

    @Contract(value = "null -> fail", pure = true)
    @Unmodifiable
    @NotNull
    public static <T> OrderedSet<T> of(T[] source) {
        Objects.requireNonNull(source, "source array is null");
        switch (source.length) {
            case 0 -> {
                //noinspection unchecked
                return EMPTY_SET;
            }
            case 1 -> {
                return new Set12<>(source[0], null);
            }
            case 2 -> {
                return new Set12<>(source[0], Objects.requireNonNull(source[1], "null element"));
            }
            default -> {
                return new SetN<>(source);
            }
        }
    }

    @Contract(value = "_ -> fail")
    @Override
    public final boolean add(E e) {
        throw new UnsupportedOperationException("add");
    }

    @Contract(value = "_ -> fail")
    @Override
    public final boolean addAll(@NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException("addAll");
    }

    @Contract(value = "-> fail")
    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    @Override
    public boolean contains(Object o) {
        Objects.requireNonNull(o, "element is null");
        return containsImpl(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        Objects.requireNonNull(collection, "collection is null");
        for (Object o : collection) {
            Objects.requireNonNull(o, "collection contains null elements");
            if (!containsImpl(o)) {
                return false;
            }
        }
        return true;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        else if (obj instanceof Set<?> set && size() == set.size()) {
            Iterator<E> i1 = iterator();
            Iterator<?> i2 = set.iterator();
            while (i1.hasNext() && i2.hasNext()) {
                if (!Objects.equals(i1.next(), i2.next())) {
                    return false;
                }
            }
            return !i1.hasNext() && !i2.hasNext();
        }
        else {
            return false;
        }
    }

    @Contract(value = "_ -> fail")
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Contract(value = "_ -> fail")
    @Override
    public final boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Contract(value = "_ -> fail")
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException("removeIf");
    }

    @Contract(value = "_ -> fail")
    @Override
    public final boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public final Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, DISTINCT | ORDERED | IMMUTABLE | NONNULL);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        Objects.requireNonNull(generator, "generator is null");
        return toArray(generator.apply(size()));
    }

    abstract boolean containsImpl(@NotNull Object o);

    private static final class Set12<E> extends OrderedSet<E> {

        @NotNull
        private final E e1;
        @Nullable
        private final E e2;

        private Set12(@NotNull E e1, @Nullable E e2) {
            this.e1 = Objects.requireNonNull(e1, "null element");
            this.e2 = e2;
            if (Objects.equals(e1, e2)) {
                throw new IllegalArgumentException("duplicate element {" + e1 + "}");
            }
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action, "action is null");
            action.accept(e1);
            if (e2 != null) {
                action.accept(e2);
            }
        }

        @Override
        public boolean isEmpty() { return false; }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new Set12Iterator();
        }

        @Override
        public int size() {
            return e2 == null ? 1 : 2;
        }

        @SuppressWarnings({"NullableProblems", "unchecked"})
        @Override
        public <T> T[] toArray(T[] array) {
            Objects.requireNonNull(array, "array is null");
            int size = size();
            if (array.length < size) {
                array = (T[]) Array.newInstance(array.getClass().componentType(), size);
            }
            array[0] = (T) e1;
            if (e2 != null) {
                array[1] = (T) e2;
            }
            if (array.length > size) {
                array[size] = null;
            }
            return array;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public Object[] toArray() {
            if (e2 == null) {
                return new Object[] {e1};
            }
            else {
                return new Object[] {e1, e2};
            }
        }

        @Override
        boolean containsImpl(@NotNull Object o) {
            return Objects.equals(o, e1) || (e2 != null && Objects.equals(o, e2));
        }

        private final class Set12Iterator implements Iterator<E> {

            private int cursor = 0;

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                Objects.requireNonNull(action, "action is null");
                while (hasNext()) {
                    action.accept(next());
                }
            }

            @Override
            public boolean hasNext() {
                return cursor == 0 || (cursor == 1 && e2 != null);
            }

            @Override
            public E next() {
                if (cursor == 0) {
                    cursor++;
                    return e1;
                }
                else if (cursor == 1 && e2 != null) {
                    cursor++;
                    return e2;
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        }
    }

    private static final class SetEmpty<E> extends OrderedSet<E> {

        @SuppressWarnings("rawtypes")
        private static final SetEmptyIterator EMPTY_ITERATOR = new SetEmptyIterator();

        @Override
        public void forEach(@NotNull Consumer<? super E> action) {
            Objects.requireNonNull(action, "action is null");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            //noinspection unchecked
            return EMPTY_ITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public <T> T[] toArray(T[] array) {
            Objects.requireNonNull(array, "array is null");
            if (array.length > 0) {
                array[0] = null;
            }
            return array;
        }

        @Override
        boolean containsImpl(@NotNull Object o) {
            return false;
        }

        private static final class SetEmptyIterator<E> implements Iterator<E> {

            @Override
            public void forEachRemaining(@NotNull Consumer<? super E> action) {
                Objects.requireNonNull(action, "action is null");
            }

            @Override
            public boolean hasNext() { return false; }

            @Override
            public E next() { throw new NoSuchElementException(); }
        }
    }

    private static final class SetN<E> extends OrderedSet<E> {

        @NotNull
        private final E[] elements;

        private SetN(E[] source) {
            //noinspection unchecked
            elements = (E[]) Array.newInstance(source.getClass().componentType(), source.length);
            for (int i = 0; i < elements.length; i++) {
                E element = Objects.requireNonNull(source[i], "null element");
                for (int k = 0; k < i; k++) {
                    if (Objects.equals(element, elements[k])) {
                        throw new IllegalArgumentException("duplicate element {" + element + "}");
                    }
                }
                elements[i] = element;
            }
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action, "action is null");
            for (E element : elements) {
                action.accept(element);
            }
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new SetNIterator();
        }

        @Override
        public int size() {
            return elements.length;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public Object[] toArray() {
            Object[] array = new Object[elements.length];
            System.arraycopy(elements, 0, array, 0, elements.length);
            return array;
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public <T> T[] toArray(T[] array) {
            Objects.requireNonNull(array, "array is null");
            if (array.length < elements.length) {
                //noinspection unchecked
                array = (T[]) Array.newInstance(array.getClass().componentType(), elements.length);
            }
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(elements, 0, array, 0, elements.length);
            if (array.length > elements.length) {
                array[elements.length] = null;
            }
            return array;
        }

        @Override
        boolean containsImpl(@NotNull Object o) {
            for (E element : elements) {
                if (Objects.equals(o, element)) {
                    return true;
                }
            }
            return false;
        }

        private final class SetNIterator implements Iterator<E> {

            private int cursor = 0;

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                Objects.requireNonNull(action, "action is null");
                while (cursor < elements.length) {
                    action.accept(elements[cursor++]);
                }
            }

            @Override
            public boolean hasNext() {
                return cursor < elements.length;
            }

            @Override
            public E next() {
                if (cursor < elements.length) {
                    return elements[cursor++];
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}
