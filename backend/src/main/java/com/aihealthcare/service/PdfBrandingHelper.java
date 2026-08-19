package com.aihealthcare.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PdfBrandingHelper {

    private static final DeviceRgb PRIMARY = new DeviceRgb(37, 99, 235);
    private static final DeviceRgb PRIMARY_DARK = new DeviceRgb(30, 64, 175);
    private static final DeviceRgb PRIMARY_LIGHT = new DeviceRgb(239, 246, 255);
    private static final DeviceRgb TEXT = new DeviceRgb(30, 41, 59);
    private static final DeviceRgb MUTED = new DeviceRgb(100, 116, 139);
    private static final DeviceRgb BORDER = new DeviceRgb(219, 234, 254);
    private static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb SUCCESS = new DeviceRgb(22, 163, 74);
    private static final DeviceRgb WARNING = new DeviceRgb(217, 119, 6);
    private static final DeviceRgb DANGER = new DeviceRgb(220, 38, 38);

    private static final DateTimeFormatter FOOTER_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private PdfBrandingHelper() {
    }

    public static Document openDocument(ByteArrayOutputStream baos) {
        Document document = new Document(new PdfDocument(new PdfWriter(baos)));
        document.setMargins(36, 40, 48, 40);
        return document;
    }

    /**
     * Opens a document with repeating header/footer on every page via page events.
     * Body content must not call {@link #addBrandedHeader} or {@link #addFooter}.
     */
    public static Document openDocumentWithPageBranding(ByteArrayOutputStream baos, PdfBrandingConfig config) {
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE,
                new PdfPageBrandingHandler(config, PdfPageBrandingHandler.Part.HEADER));
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE,
                new PdfPageBrandingHandler(config, PdfPageBrandingHandler.Part.FOOTER));

        Document document = new Document(pdfDoc);
        document.setMargins(
                PdfPageBrandingHandler.getTopMargin(),
                40,
                PdfPageBrandingHandler.getBottomMargin(),
                40);
        return document;
    }

    public static void addBrandedHeader(Document document, String documentTitle, String documentSubtitle,
                                        String referenceLabel, String referenceValue) {
        Table header = new Table(UnitValue.createPercentArray(new float[]{62, 38}))
                .useAllAvailableWidth()
                .setMarginBottom(4);

        Cell brandCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Table brandRow = new Table(UnitValue.createPercentArray(new float[]{14, 86}))
                .useAllAvailableWidth();

        Cell logoCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(0);
        Image logo = createLogoImage();
        if (logo != null) {
            logoCell.add(logo);
        } else {
            logoCell.add(new Paragraph("H")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(PRIMARY_DARK)
                    .setTextAlignment(TextAlignment.CENTER));
        }
        brandRow.addCell(logoCell);

        Div brandText = new Div();
        brandText.add(new Paragraph("Healix")
                .setFontSize(22)
                .setBold()
                .setFontColor(PRIMARY_DARK)
                .setMarginBottom(0));
        brandText.add(new Paragraph("Intelligent Healthcare Management Platform")
                .setFontSize(9)
                .setFontColor(MUTED)
                .setMarginTop(2));

        brandRow.addCell(new Cell()
                .add(brandText)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPaddingLeft(8));

        brandCell.add(brandRow);
        header.addCell(brandCell);

        Div meta = new Div().setTextAlignment(TextAlignment.RIGHT);
        meta.add(new Paragraph(documentTitle.toUpperCase())
                .setFontSize(11)
                .setBold()
                .setFontColor(WHITE)
                .setBackgroundColor(PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(6)
                .setMarginBottom(6));
        if (documentSubtitle != null && !documentSubtitle.isBlank()) {
            meta.add(new Paragraph(documentSubtitle)
                    .setFontSize(10)
                    .setFontColor(TEXT)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(2));
        }
        if (referenceLabel != null && referenceValue != null) {
            meta.add(new Paragraph(referenceLabel + ": " + referenceValue)
                    .setFontSize(9)
                    .setFontColor(MUTED)
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        header.addCell(new Cell()
                .add(meta)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

        document.add(header);
        document.add(createAccentLine());
    }

    public static void addSectionTitle(Document document, String title) {
        document.add(new Paragraph(title)
                .setFontSize(11)
                .setBold()
                .setFontColor(PRIMARY_DARK)
                .setMarginTop(8)
                .setMarginBottom(4));
    }

    public static Table createInfoTable() {
        return new Table(UnitValue.createPercentArray(new float[]{34, 66}))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(BORDER, 1));
    }

    public static void addInfoRow(Table table, String label, String value) {
        table.addCell(labelCell(label));
        table.addCell(valueCell(value));
    }

    public static Div createContentBlock(String heading, String body) {
        Div block = new Div()
                .setBackgroundColor(PRIMARY_LIGHT)
                .setBorder(new SolidBorder(BORDER, 1))
                .setPadding(8)
                .setMarginTop(4);

        if (heading != null && !heading.isBlank()) {
            block.add(new Paragraph(heading)
                    .setBold()
                    .setFontSize(10)
                    .setFontColor(PRIMARY_DARK)
                    .setMarginBottom(4));
        }
        block.add(new Paragraph(body != null ? body : "N/A")
                .setFontSize(9)
                .setFontColor(TEXT));
        return block;
    }

    public static void addContentBlock(Document document, String heading, String body) {
        document.add(createContentBlock(heading, body));
    }

    public static void addNoteList(Document document, String... notes) {
        Div notesBox = new Div()
                .setBorder(new SolidBorder(BORDER, 1))
                .setBackgroundColor(WHITE)
                .setPadding(12)
                .setMarginTop(10);

        notesBox.add(new Paragraph("Important Notes")
                .setBold()
                .setFontSize(10)
                .setFontColor(PRIMARY_DARK)
                .setMarginBottom(6));

        for (String note : notes) {
            notesBox.add(new Paragraph("• " + note)
                    .setFontSize(9)
                    .setFontColor(MUTED)
                    .setMarginBottom(3));
        }
        document.add(notesBox);
    }

    public static void addSignatureBlock(Document document, String signerLabel, String signerName) {
        document.add(buildSignatureBlock(signerLabel, signerName));
    }

    /** Compact signature block — kept together so it never splits across pages. */
    public static Div buildSignatureBlock(String signerLabel, String signerName) {
        Div signatureWrapper = new Div();
        signatureWrapper.setKeepTogether(true);
        signatureWrapper.setMarginTop(8);

        Table signature = new Table(UnitValue.createPercentArray(new float[]{55, 45}))
                .useAllAvailableWidth();
        signature.setKeepTogether(true);

        Div left = new Div();
        left.add(new Paragraph("Authorized Signature")
                .setFontSize(8)
                .setFontColor(MUTED)
                .setMarginBottom(2));
        left.add(new Paragraph("___________________________")
                .setFontSize(9)
                .setMarginTop(6));
        if (signerName != null) {
            left.add(new Paragraph(signerName)
                    .setFontSize(9)
                    .setBold()
                    .setFontColor(TEXT)
                    .setMarginTop(2));
        }

        Div right = new Div().setTextAlignment(TextAlignment.RIGHT);
        right.add(new Paragraph("Date")
                .setFontSize(8)
                .setFontColor(MUTED)
                .setMarginBottom(2));
        right.add(new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")))
                .setFontSize(9)
                .setFontColor(TEXT)
                .setMarginTop(12));

        signature.addCell(new Cell().add(left).setBorder(Border.NO_BORDER).setPadding(0));
        signature.addCell(new Cell().add(right).setBorder(Border.NO_BORDER).setPadding(0));
        signatureWrapper.add(signature);

        if (signerLabel != null) {
            signatureWrapper.add(new Paragraph(signerLabel)
                    .setFontSize(7)
                    .setFontColor(MUTED)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(4));
        }
        return signatureWrapper;
    }

    /** Groups trailing content + signature on the same page when possible. */
    public static void addClosingWithSignature(Document document, Div trailingContent,
                                               String signerLabel, String signerName) {
        Div closing = new Div();
        closing.setKeepTogether(true);
        closing.setMarginTop(4);
        if (trailingContent != null) {
            closing.add(trailingContent);
        }
        closing.add(buildSignatureBlock(signerLabel, signerName));
        document.add(closing);
    }

    public static void addFooter(Document document, String documentId) {
        document.add(new Paragraph("\n"));
        document.add(createAccentLine().setMarginBottom(8));

        Paragraph footer = new Paragraph()
                .setFontSize(8)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER);
        footer.add("Healix Healthcare Platform  •  www.healix.in  •  support@healix.in\n");
        footer.add("Generated on " + LocalDateTime.now().format(FOOTER_DATE));
        if (documentId != null) {
            footer.add("  •  Document ID: " + documentId);
        }
        document.add(footer);
    }

    public static DeviceRgb riskColor(String riskLevel) {
        if (riskLevel == null) return WARNING;
        return switch (riskLevel.toUpperCase()) {
            case "LOW" -> SUCCESS;
            case "HIGH", "CRITICAL" -> DANGER;
            default -> WARNING;
        };
    }

    private static Image createLogoImage() {
        try (InputStream stream = PdfBrandingHelper.class.getResourceAsStream("/assets/healix-logo.png")) {
            if (stream != null) {
                return new Image(ImageDataFactory.create(stream.readAllBytes()))
                        .setWidth(42)
                        .setHeight(42)
                        .setHorizontalAlignment(HorizontalAlignment.LEFT);
            }
        } catch (Exception ignored) {
            // Fall back to text-only branding if logo is unavailable.
        }
        return null;
    }

    private static Div createAccentLine() {
        Div line = new Div()
                .setHeight(3)
                .setBackgroundColor(PRIMARY)
                .setMarginTop(8)
                .setMarginBottom(12);
        return line;
    }

    private static Cell labelCell(String label) {
        return new Cell()
                .add(new Paragraph(label).setFontSize(8).setBold().setFontColor(MUTED))
                .setBackgroundColor(PRIMARY_LIGHT)
                .setBorder(new SolidBorder(BORDER, 0.5f))
                .setPadding(6);
    }

    private static Cell valueCell(String value) {
        return new Cell()
                .add(new Paragraph(value != null ? value : "N/A").setFontSize(9).setFontColor(TEXT))
                .setBackgroundColor(WHITE)
                .setBorder(new SolidBorder(BORDER, 0.5f))
                .setPadding(6);
    }
}
