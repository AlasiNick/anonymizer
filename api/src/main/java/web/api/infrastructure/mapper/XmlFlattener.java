package web.api.infrastructure.mapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import web.api.domain.model.FlattenedField;
import web.api.domain.model.type.FieldType;

import java.util.*;

@Component
public class XmlFlattener {

    @Value("${app.cda.clean-paths:true}")
    private boolean cleanPathsEnabled;

    private static final Set<String> NOISE_SEGMENTS = Set.of(
            "ClinicalDocument", "component", "structuredBody", "section", "entry",
            "observation", "procedure", "encounter", "encompassingEncounter",
            "subject", "recordTarget", "patientRole", "patient", "assignedAuthor",
            "author", "custodian", "representedOrganization", "assignedCustodian",
            "assignedPerson", "patientPerson", "as", "of", "entryRelationship"
    );

    private static final Map<String, String> SEGMENT_ALIASES = Map.ofEntries(
            Map.entry("recordTarget", "patient"),
            Map.entry("patientRole", "patient"),
            Map.entry("patientPerson", "patient"),
            Map.entry("assignedPerson", "person"),
            Map.entry("assignedAuthor", "author"),
            Map.entry("representedOrganization", "organization"),
            Map.entry("representedCustodianOrganization", "organization"),
            Map.entry("addr", "address"),
            Map.entry("name", "name"),
            Map.entry("given", "given"),
            Map.entry("family", "family"),
            Map.entry("text", "text"),
            Map.entry("title", "title"),
            Map.entry("value", "value"),
            Map.entry("code", "code"),
            Map.entry("effectiveTime", "time"),
            Map.entry("table", "table"),
            Map.entry("tbody", "body"),
            Map.entry("thead", "header"),
            Map.entry("tr", "row"),
            Map.entry("td", "cell"),
            Map.entry("th", "headerCell")
    );

    public List<FlattenedField> flatten(Document document) {
        List<FlattenedField> fields = new ArrayList<>();
        Element root = document.getDocumentElement();
        flattenNode(root, "", "", fields);
        System.out.println("Total fields flattened: " + fields.size());
        return fields;
    }

    private void flattenNode(Node node, String originalPath, String cleanPath, List<FlattenedField> fields) {
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        String nodeName = sanitizeNodeName(node.getNodeName());
        String newOriginal = buildPath(originalPath, nodeName);
        String newClean = cleanPathsEnabled ? buildCleanPath(cleanPath, nodeName) : newOriginal;

        List<Node> children = collectElementChildren(node);

        if (children.isEmpty()) {
            String value = node.getTextContent().trim();
            if (!value.isBlank()) {
                FlattenedField field = new FlattenedField();
                field.setOriginalPath(newOriginal);
                field.setCleanPath(newClean);
                field.setValue(value);
                field.setType(classify(newClean, value));
                fields.add(field);
            }
            return;
        }

        Map<String, Integer> counter = new HashMap<>();
        for (Node child : children) {
            String childName = sanitizeNodeName(child.getNodeName());
            int idx = counter.merge(childName, 1, Integer::sum);

            String childOriginal = buildPath(newOriginal, childName);
            String childClean = cleanPathsEnabled ? buildCleanPath(newClean, childName) : childOriginal;

            if (idx > 1) {
                childClean += "_" + idx;
            }

            flattenNode(child, childOriginal, childClean, fields);
        }
    }

    private String buildCleanPath(String current, String nodeName) {
        if (NOISE_SEGMENTS.contains(nodeName)) {
            return current;
        }

        String segment = SEGMENT_ALIASES.getOrDefault(nodeName, nodeName);

        if (current.isBlank()) return segment;

        String last = getLastSegment(current);
        if (last.equals(segment) || last.equals(nodeName)) {
            return current;   // prevent duplication
        }

        return current + "_" + segment;
    }

    private String getLastSegment(String path) {
        if (path.isBlank()) return "";
        int idx = path.lastIndexOf('_');
        return idx == -1 ? path : path.substring(idx + 1);
    }

    private String buildPath(String current, String segment) {
        return current.isBlank() ? segment : current + "_" + segment;
    }

    private List<Node> collectElementChildren(Node node) {
        List<Node> list = new ArrayList<>();
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) list.add(nl.item(i));
        }
        return list;
    }

    private String sanitizeNodeName(String nodeName) {
        if (nodeName == null) return "";
        int colon = nodeName.indexOf(':');
        return colon >= 0 ? nodeName.substring(colon + 1) : nodeName;
    }

    private FieldType classify(String path, String value) {
        String lower = path.toLowerCase();
        if (lower.contains("id") || lower.contains("isikukood") || lower.contains("family") || lower.contains("given")) {
            return FieldType.DIRECT_IDENTIFIER;
        }
        if (lower.contains("birth") || lower.contains("gender") || lower.contains("address") ||
                lower.contains("county") || lower.contains("city") || lower.contains("postal")) {
            return FieldType.QUASI_IDENTIFIER;
        }
        if (lower.contains("diagnosis") || lower.contains("analysis") || lower.contains("medication") || lower.contains("code")) {
            return FieldType.SENSITIVE;
        }
        if (lower.contains("text") || lower.contains("content") || lower.contains("paragraph") ||
                lower.contains("table") || lower.contains("title")) {
            return FieldType.NARRATIVE;
        }
        return FieldType.UNKNOWN;
    }
}