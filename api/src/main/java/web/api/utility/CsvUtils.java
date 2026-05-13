package web.api.utility;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

@UtilityClass
public final class CsvUtils {

    public static void writeBom(OutputStream os) {
        try {
            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write UTF‑8 BOM", e);
        }
    }

    public static byte[] stripBom(byte[] bytes) {
        if (bytes.length >= 3 &&
                bytes[0] == (byte) 0xEF &&
                bytes[1] == (byte) 0xBB &&
                bytes[2] == (byte) 0xBF) {

            byte[] cleaned = new byte[bytes.length - 3];
            System.arraycopy(bytes, 3, cleaned, 0, cleaned.length);
            return cleaned;
        }
        return bytes;
    }
}
