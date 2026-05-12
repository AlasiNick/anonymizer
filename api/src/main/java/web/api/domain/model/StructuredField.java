package web.api.domain.model;

import lombok.Data;
import web.api.domain.model.type.FieldType;

@Data
public class StructuredField {

    private String attributeName;
    private String value;
    private FieldType fieldType;
}
