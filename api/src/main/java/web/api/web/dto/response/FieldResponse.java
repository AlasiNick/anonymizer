package web.api.web.dto.response;

import lombok.Data;
import web.api.domain.model.type.FieldType;

@Data
public class FieldResponse {

    String path;
    String value;
    FieldType type;
    private String originalPath;
    private String cleanPath;
}
