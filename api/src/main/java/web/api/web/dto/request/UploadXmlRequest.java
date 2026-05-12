package web.api.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UploadXmlRequest {

        @NotBlank
        String fileName;

        @NotBlank
        String base64Content;
}
