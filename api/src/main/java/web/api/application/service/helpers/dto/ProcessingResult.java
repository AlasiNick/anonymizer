package web.api.application.service.helpers.dto;

import web.api.domain.model.FlattenedField;

import java.util.List;

public record ProcessingResult(
        List<FlattenedField> fields,
        byte[] originalXmlBytes
) {}
