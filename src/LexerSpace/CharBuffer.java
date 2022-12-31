package LexerSpace;

import java.io.BufferedReader;
import java.io.IOException;

class CharBuffer {
    public final StringBuilder buffer = new StringBuilder();
    public final BufferedReader reader;

    public CharBuffer(BufferedReader reader) {
        this.reader = reader;
    }

    public short peek() throws IOException {
        if (buffer.isEmpty()) {
            buffer.append((char) reader.read());
        }
        return (short) buffer.charAt(0);
    }

    public short read() throws IOException {
        short c = peek();
        buffer.deleteCharAt(0);
        return c;
    }

    public void putBack(String str) {
        if (str != null && !str.isEmpty()) {
            buffer.insert(0, str);
        }
    }
}
