package deidentification.arx.domain;

import deidentification.arx.domain.type.FieldType;
import lombok.Data;

@Data
public class FlattenedField {

    private String originalPath;
    private String cleanPath;
    private String value;
    private FieldType type;
    private boolean isUnmapped;
}
