package commando.test;

import dk.martinu.commando.OrderedSet;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderedSet.of(T[])")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderedSetStaticTest {

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

    /**
     * Provides an {@code Integer[]} argument which does not contain any
     * {@code null} elements, but is guaranteed to have at least one duplicate
     * element.
     */
    static Stream<Arguments> duplicateIntsProvider() {
        return Stream.of(
                Arguments.of((Object) new Integer[] {1, 1}),
                Arguments.of((Object) new Integer[] {1, 1, 3}),
                Arguments.of((Object) new Integer[] {1, 2, 1}),
                Arguments.of((Object) new Integer[] {1, 2, 2, 1}),
                Arguments.of((Object) new Integer[] {1, 2, 3, 1}),
                Arguments.of((Object) new Integer[] {1, 2, 3, 3}),
                Arguments.of((Object) new Integer[] {1, 2, 3, 3, 4}),
                Arguments.of((Object) new Integer[] {1, 1, 1, 1, 1})
        );
    }

    /**
     * Provides an {@code Integer[]} argument which does not contain any
     * duplicate elements (except {@code null}, but is guaranteed to have at
     * least one {@code null} element.
     */
    static Stream<Arguments> nullIntsProvider() {
        return Stream.of(
                Arguments.of((Object) new Integer[] {null}),
                Arguments.of((Object) new Integer[] {null, null}),
                Arguments.of((Object) new Integer[] {null, null, null}),
                Arguments.of((Object) new Integer[] {1, null}),
                Arguments.of((Object) new Integer[] {1, 2, null}),
                Arguments.of((Object) new Integer[] {1, null, 3}),
                Arguments.of((Object) new Integer[] {null, 2, 3})
        );
    }

    /**
     *
     */
    @DisplayName("can instantiate ordered sets with distinct elements")
    @ParameterizedTest
    @MethodSource("distinctIntsProvider")
    public void canInstantiate(Integer[] source) {
        assertDoesNotThrow(() -> {
            OrderedSet.of(source);
        });
        assertNotNull(OrderedSet.of(source));
    }

    @DisplayName("method fails with null parameter")
    @Test
    public void failsWithNullParameter() {
        assertThrows(NullPointerException.class, () -> OrderedSet.of(null));
    }

    @DisplayName("instantiation fails with duplicate elements")
    @ParameterizedTest
    @MethodSource("duplicateIntsProvider")
    public void instantiateFailsWithDuplicate(Integer[] source) {
        assertThrows(IllegalArgumentException.class, () -> OrderedSet.of(source));
    }

    @DisplayName("instantiation fails with null elements")
    @ParameterizedTest
    @MethodSource("nullIntsProvider")
    public void instantiateFailsWithNull(Integer[] source) {
        assertThrows(NullPointerException.class, () -> OrderedSet.of(source));
    }
}
