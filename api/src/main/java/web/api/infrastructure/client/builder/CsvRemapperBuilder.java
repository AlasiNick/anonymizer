package web.api.infrastructure.client.builder;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import web.api.utility.CsvUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvRemapperBuilder {

    public static List<String[]> read(byte[] csvBytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(CsvUtils.stripBom(csvBytes));
            CSVReader reader = new CSVReader(new InputStreamReader(bais, StandardCharsets.UTF_8));

            List<String[]> rows = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(row);
            }
            return rows;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV", e);
        }
    }

    public static byte[] write(List<String[]> rows) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CsvUtils.writeBom(baos);
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            for (String[] row : rows) {
                writer.writeNext(row);
            }

            writer.flush();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to write CSV", e);
        }
    }

    public static String[] readHeader(byte[] csvBytes) {
        List<String[]> rows = read(csvBytes);
        return rows.isEmpty() ? new String[0] : rows.getFirst();
    }
}
