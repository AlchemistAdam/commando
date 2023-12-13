package commando.test;

import dk.martinu.commando.OrderedSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
