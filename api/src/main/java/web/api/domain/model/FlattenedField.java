package web.api.domain.model;

import lombok.Data;
import web.api.domain.model.type.FieldType;

@Data
public class FlattenedField {

    private String originalPath;
    private String cleanPath;
    private String value;
    private FieldType type;
    private boolean isUnmapped;

    public FlattenedField() {
        this.isUnmapped = true;
    }

    public String getDisplayPath() {
        return cleanPath != null ? cleanPath : originalPath;
    }
}