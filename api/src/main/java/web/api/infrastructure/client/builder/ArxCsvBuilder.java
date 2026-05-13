package web.api.infrastructure.client.builder;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;
import web.api.domain.model.FlattenedField;
import web.api.utility.CsvUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArxCsvBuilder {

    public byte[] generateArxReadyCsv(List<FlattenedField> structuredFields) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            CsvUtils.writeBom(baos);
            List<String> columns = structuredFields.stream()
                    .map(FlattenedField::getCleanPath)
                    .distinct()
                    .sorted()
                    .toList();

            writer.writeNext(columns.toArray(new String[0]));
            Map<String, String> valueMap = structuredFields.stream()
                    .collect(Collectors.toMap(
                            FlattenedField::getCleanPath,
                            FlattenedField::getValue,
                            (v1, v2) -> v1,
                            LinkedHashMap::new));

            String[] row = columns.stream()
                    .map(col -> valueMap.getOrDefault(col, ""))
                    .toArray(String[]::new);

            writer.writeNext(row);
            writer.flush();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate ARX CSV", e);
        }
    }
}