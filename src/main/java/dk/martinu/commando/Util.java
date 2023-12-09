package dk.martinu.commando;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class Util {

    /**
     * Returns {@code true} if the specified character {@code c} is a decimal
     * digit, otherwise {@code false} is returned.
     * <p>
     * A decimal digit is any character between {@code '0'} and {@code '9'}
     * (inclusive).
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is a decimal digit, otherwise
     * {@code false}
     */
    @Contract(pure = true)
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Returns {@code true} if the specified character {@code c} is a
     * hexadecimal digit, otherwise {@code false} is returned.
     * <p>
     * A hexadecimal digit is any character between {@code '0'} and
     * {@code '9'}, or {@code 'A'} and {@code 'F'}, or {@code 'a'} and
     * {@code 'f'} (inclusive).
     *
     * @param c the character to be tested
     * @return {@code true} if {@code c} is a hexadecimal digit, otherwise
     * {@code false}
     */
    @Contract(pure = true)
    public static boolean isHexDigit(char c) {
        if (isDigit(c)) {
            return true;
        }
        else if (c >= 'A' && c <= 'F') {
            return true;
        }
        else {
            return c >= 'a' && c <= 'f';
        }
    }

    @Contract(pure = true)
    public static boolean isNameInvalid(@NotNull String name) {
        Objects.requireNonNull(name, "name is null");
        if (name.isEmpty()) {
            return true;
        }
        for (int i = 0, len = name.length(); i < len; i++) {
            char c = name.charAt(i);
            if ((c < '0' || c > '9') && (c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && c != '_') {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an unescaped array of {@code chars}. If the unescaped array is
     * equal to {@code chars} then {@code chars}
     * is returned.
     *
     * @param chars the characters to unescape
     * @return an unescaped array
     */
    @Contract(value = "null -> fail", pure = true)
    public static char[] unescape(char[] chars) {
        return unescape(chars, 0, chars.length);
    }

    /**
     * Returns an unescaped array of {@code chars} in the specified range. If
     * the unescaped array is equal to {@code chars}, then {@code chars} is
     * returned.
     *
     * @param chars the characters to unescape
     * @param start the starting index, inclusive
     * @param end   the ending index, exclusive
     * @return an unescaped array of characters
     */
    @Contract(value = "null, _, _ -> fail", pure = true)
    public static char[] unescape(char[] chars, int start, int end) {
        CharBuffer buffer = new CharBuffer(end - start);
        for (int i = start; i < end; ) {

            // ordinary character
            if (chars[i] != '\\') {
                buffer.append(chars[i++]);
            }

            // start of escape sequence
            else {
                // remaining characters, including the backslash
                int rem = end - i;
                // no more chars
                if (rem == 1) {
                    // TODO output warning
                    break;
                }

                // six-character escape sequence, e.g. \\u0020
                else if (chars[i + 1] == 'u' || chars[i + 1] == 'U') {
                    if (rem >= 6 && isHexDigit(chars[i + 2]) && isHexDigit(chars[i + 3])
                            && isHexDigit(chars[i + 4]) && isHexDigit(chars[i + 5])) {
                        // get int value of 4-digit hex and cast it to char
                        buffer.append((char) Integer.valueOf(String.copyValueOf(
                                chars, i + 2, 4), 16).intValue());
                        i += 6;
                    }
                    // sequence is incomplete
                    else {
                        // TODO output warning
                        buffer.append(chars[i + 1]);
                        i += 2;
                    }
                }
                // two-character escape sequence, e.g. \\n
                else {
                    char c = chars[i + 1];
                    if (c == '0') {
                        buffer.append('\0');
                    }
                    else if (c == 'b') {
                        buffer.append('\b');
                    }
                    else if (c == 't') {
                        buffer.append('\t');
                    }
                    else if (c == 'n') {
                        buffer.append('\n');
                    }
                    else if (c == 'f') {
                        buffer.append('\f');
                    }
                    else if (c == 'r') {
                        buffer.append('\r');
                    }
                    else {
                        buffer.append(c);
                    }
                    i += 2;
                }
            }
        }

        if (buffer.cursor() != chars.length) {
            return buffer.toCharArray();
        }
        else {
            return chars;
        }
    }

    /**
     * A fixed-size, unsafe buffer of {@code char} values. Values can be
     * appended to the buffer, but not removed.
     */
    private static class CharBuffer {

        /**
         * The buffer array.
         */
        final char[] chars;
        /**
         * Current cursor location for appending values.
         */
        int cursor = 0;

        /**
         * Creates a new buffer with the specified size.
         *
         * @throws NegativeArraySizeException if {@code size} is negative
         */
        CharBuffer(int size) {
            chars = new char[size];
        }

        /**
         * Appends the specified character to this buffer.
         *
         * @throws ArrayIndexOutOfBoundsException if this buffer is full
         */
        void append(char c) {
            chars[cursor++] = c;
        }

        /**
         * Returns the current cursor location in this buffer.
         */
        int cursor() {
            return cursor;
        }

        /**
         * Returns an array of the character values in this buffer. If this
         * buffer is full, then its own internal array is returned. Otherwise,
         * a new array with a length equal to the number of values is allocated
         * and returned.
         */
        @Contract(pure = true)
        char[] toCharArray() {
            if (cursor != chars.length) {
                char[] rv = new char[cursor];
                System.arraycopy(chars, 0, rv, 0, cursor);
                return rv;
            }
            else { return chars; }
        }
    }
}
