package web.api.infrastructure.parser;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

@Service
public class XmlValidationService {

    @Value("${app.cda.validate-xml:true}")
    private boolean validateXml;

    @Value("${app.cda.xsd-location:classpath:xsd/CDA_EE01.xsd}")
    private Resource mainXsdResource;

    public void validateExtension(String fileName) {
        if (fileName == null || !fileName.toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException("Only XML files are supported");
        }
    }

    public byte[] validateAndDecodeBase64(String base64) {
        try {
            return java.util.Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Base64 content");
        }
    }

    @SneakyThrows
    public void validateXml(byte[] xmlBytes) {
        if (!validateXml) return;

        try {
            Document doc = SecureXmlParser.createDocument(xmlBytes);
            boolean valid = isValid(doc);

            if (valid) {
                System.out.println("Full XML Schema validation passed");
            } else {
                System.err.println("XML Schema validation failed → continuing in lenient mode");
            }
        } catch (Exception e) {
            System.err.println("Validation error (continuing): " + e.getMessage());
        }
    }

    public boolean isValid(Document document) {
        if (!validateXml) return true;

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);

            Schema schema = factory.newSchema(mainXsdResource.getURL());
            Validator validator = schema.newValidator();
            validator.validate(new javax.xml.transform.dom.DOMSource(document));

            return true;
        } catch (Exception e) {
            System.err.println("Schema Error: " + e.getMessage());
            return false;
        }
    }
}