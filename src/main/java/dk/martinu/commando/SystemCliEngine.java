package dk.martinu.commando;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class SystemCliEngine extends AbstractCliEngine {

    public final PrintWriter out = new PrintWriter(new OutWrapper());

    public SystemCliEngine(boolean daemon) {
        super(new Scanner(new InWrapper()), daemon);
    }

    @Override
    protected void freeResources() {
        out.close();
    }

    @Override
    @NotNull
    protected PrintWriter out() {
        return out;
    }


    private static final class InWrapper extends InputStream {

        @Override
        public int available() throws IOException {
            return System.in.available();
        }

        @Override
        public void close() {
            // DO NOTHING
        }

        @Override
        public void mark(int readlimit) {
            super.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return System.in.markSupported();
        }

        @Override
        public int read() throws IOException {
            return System.in.read();
        }

        @Override
        public int read(@SuppressWarnings("NullableProblems") byte[] b) throws IOException {
            return System.in.read(b);
        }

        @Override
        public int read(@SuppressWarnings("NullableProblems") byte[] b, int off, int len) throws IOException {
            return System.in.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return System.in.readAllBytes();
        }

        @Override
        public byte[] readNBytes(int len) throws IOException {
            return System.in.readNBytes(len);
        }

        @Override
        public int readNBytes(byte[] b, int off, int len) throws IOException {
            return System.in.readNBytes(b, off, len);
        }

        @Override
        public void reset() throws IOException {
            super.reset();
        }

        @Override
        public long skip(long n) throws IOException {
            return System.in.skip(n);
        }

        @Override
        public void skipNBytes(long n) throws IOException {
            super.skipNBytes(n);
        }

        @Override
        public long transferTo(OutputStream out) throws IOException {
            return System.in.transferTo(out);
        }
    }

    private static final class OutWrapper extends Writer {

        @Override
        public void close() {
            // DO NOTHING
        }

        @Override
        public void flush() {
            System.out.flush();
        }

        @Override
        public void write(@SuppressWarnings("NullableProblems") char[] chars, int offset, int length) {
            if (offset == 0 && chars.length == length) {
                System.out.print(chars);
            }
            else {
                // assertion to ensure copyOfRange does not return array padded with null characters
                assert (offset + length) <= chars.length : "to=" + (offset + length);
                System.out.print(Arrays.copyOfRange(chars, offset, offset + length));
            }
        }
    }
}
