package web.api.web.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FlattenedResponse {

    List<FieldResponse> fields;
}
