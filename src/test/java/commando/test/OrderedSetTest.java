package commando.test;

import dk.martinu.commando.OrderedSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderedSet")
public class OrderedSetTest {

    /**
     * Provides an {@code Integer[]} argument which does not contain any
     * {@code null} or duplicate elements.
     */
    static Stream<Arguments> distinctIntsProvider() {
        return Stream.of(
                Arguments.of((Object) new Integer[] {}),
                Arguments.of((Object) new Integer[] {1}),
                Arguments.of((Object) new Integer[] {1, 2}),
                Arguments.of((Object) new Integer[] {1, 2, 3}),
                Arguments.of((Object) new Integer[] {1, 2, 3, 4})
        );
    }

    @DisplayName("set contains all elements in source array")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void contains(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);
        for (int i = 0; i < source.length; i++) {
            assertTrue(set.contains(source[i]), "index " + i + ", value " + source[i]);
        }
    }

    @DisplayName("set contains all elements in collection of source array")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void containsAll(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);
        assertTrue(set.containsAll(List.of(source)));
    }

    @DisplayName("set has same order as source array")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void order(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);
        Iterator<Integer> iterator = set.iterator();

        for (Integer val : source) {
            assertTrue(iterator.hasNext());
            assertEquals(val, iterator.next());
        }

        assertFalse(iterator.hasNext());
    }

    @DisplayName("set has same size/length as source array")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void size(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);
        assertEquals(source.length, set.size());
    }

    @DisplayName("mutator methods throw UnsupportedOperationException")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void throwsUOE(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);
        assertThrows(UnsupportedOperationException.class, () -> set.add(-1));
        assertThrows(UnsupportedOperationException.class, () -> set.addAll(List.of(source)));
        assertThrows(UnsupportedOperationException.class, set::clear);
        assertThrows(UnsupportedOperationException.class, () -> set.remove(1));
        //noinspection SlowAbstractSetRemoveAll
        assertThrows(UnsupportedOperationException.class, () -> set.removeAll(List.of(source)));
        //noinspection DataFlowIssue
        assertThrows(UnsupportedOperationException.class, () -> set.removeIf(val -> val == 1));
        assertThrows(UnsupportedOperationException.class, () -> set.retainAll(List.of(source)));
        assertThrows(UnsupportedOperationException.class, () -> set.iterator().remove());
    }

    @DisplayName("can convert to array")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void toArray(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);
        Object[] copy = set.toArray();
        assertArrayEquals(source, copy);
    }

    @DisplayName("can convert to array with array parameter")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void toArrayWithArray(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);

        Object[] copy = set.toArray(new Integer[source.length]);
        assertArrayEquals(source, copy);

        copy = set.toArray(new Integer[0]);
        assertArrayEquals(source, copy);
    }

    @DisplayName("can convert to array with generator parameter")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void toArrayWithGenerator(Integer[] source) {
        OrderedSet<Integer> set = OrderedSet.of(source);
        Object[] copy = set.toArray(Integer[]::new);
        assertArrayEquals(source, copy);
    }
}
