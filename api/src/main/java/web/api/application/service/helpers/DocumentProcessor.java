package web.api.application.service.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import web.api.application.service.helpers.dto.ProcessingResult;
import web.api.domain.model.FlattenedField;
import web.api.infrastructure.mapper.XmlFlattener;
import web.api.infrastructure.parser.SecureXmlParser;
import web.api.infrastructure.parser.XmlValidationService;
import web.api.web.dto.request.UploadXmlRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentProcessor {

    private final XmlValidationService validationService;
    private final XmlFlattener xmlFlattener;

    public ProcessingResult process(UploadXmlRequest request) throws Exception {
        validationService.validateExtension(request.getFileName());
        byte[] xmlBytes = validationService.validateAndDecodeBase64(request.getBase64Content());
        validationService.validateXml(xmlBytes);

        Document document = SecureXmlParser.createDocument(xmlBytes);
        List<FlattenedField> fields = xmlFlattener.flatten(document);

        return new ProcessingResult(fields, xmlBytes);
    }
}
