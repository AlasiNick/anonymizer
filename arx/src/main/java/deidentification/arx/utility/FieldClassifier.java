package deidentification.arx.utility;

import deidentification.arx.domain.type.FieldType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FieldClassifier {

    public FieldType classify(String path) {
        String lower = path.toLowerCase().trim();

        if (lower.contains("id") || lower.contains("isikukood") ||
                lower.contains("family") || lower.contains("given")) {
            return FieldType.DIRECT_IDENTIFIER;
        }

        if (lower.contains("address") || lower.contains("addr") ||
                lower.contains("street") || lower.contains("streetaddressline") ||
                lower.contains("housenumber") || lower.contains("postal") ||
                lower.contains("city") || lower.contains("county") ||
                lower.contains("precinct")) {
            return FieldType.QUASI_IDENTIFIER;
        }

        if (lower.contains("gender") || lower.contains("birthdate") || lower.contains("birth")) {
            return FieldType.QUASI_IDENTIFIER;
        }

        if (lower.contains("code") || lower.contains("codesystem")) {
            if (lower.contains("codesystem") || lower.contains("codesystemname") ||
                    lower.contains("displayname") || lower.endsWith("_code")) {

                if (lower.contains("value_code") || lower.contains("diagnosis") ||
                        lower.contains("medication") || lower.contains("procedure")) {
                    return FieldType.SENSITIVE;
                }
                return FieldType.INSENSITIVE_ATTRIBUTE;
            }
        }

        if (lower.contains("diagnosis") || lower.contains("analysis") ||
                lower.contains("medication")) {
            return FieldType.SENSITIVE;
        }

        if (lower.startsWith("author_") || lower.startsWith("organization_") ||
                lower.startsWith("custodian_")) {
            return FieldType.SENSITIVE;
        }

        if (lower.contains("text") || lower.contains("content") ||
                lower.contains("paragraph") || lower.contains("table") ||
                lower.contains("title")) {
            return FieldType.NARRATIVE;
        }

        return FieldType.UNKNOWN;
    }
}
