package web.api.domain.model;

import java.util.List;

public record RoutedFields(
        List<FlattenedField> structuredFields,
        List<FlattenedField> narrativeFields
) {}