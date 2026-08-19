package com.aihealthcare.service;

public record PdfBrandingConfig(
        String documentTitle,
        String documentSubtitle,
        String referenceLabel,
        String referenceValue,
        String documentId
) {
}
