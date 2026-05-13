package web.api.infrastructure.parser;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;
import web.api.domain.model.FlattenedField;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StructuredCsvBuilder {

    public byte[] build(List<FlattenedField> fields) {

        try (
                ByteArrayOutputStream baos =
                        new ByteArrayOutputStream();

                OutputStreamWriter osw =
                        new OutputStreamWriter(
                                baos,
                                StandardCharsets.UTF_8
                        );

                CSVWriter writer = new CSVWriter(osw)
        ) {

            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);

            String[] headers = fields.stream()
                    .map(FlattenedField::getDisplayPath)
                    .distinct()
                    .toArray(String[]::new);

            writer.writeNext(headers);

            String[] values = fields.stream()
                    .map(FlattenedField::getValue)
                    .toArray(String[]::new);

            writer.writeNext(values);

            writer.flush();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to build ARX CSV",
                    e
            );
        }
    }
}