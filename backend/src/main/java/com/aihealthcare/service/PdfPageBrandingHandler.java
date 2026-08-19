package com.aihealthcare.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Renders repeating header and footer on every PDF page using iText page events.
 */
public class PdfPageBrandingHandler implements IEventHandler {

    private static final DeviceRgb PRIMARY = new DeviceRgb(37, 99, 235);
    private static final DeviceRgb PRIMARY_DARK = new DeviceRgb(30, 64, 175);
    private static final DeviceRgb MUTED = new DeviceRgb(100, 116, 139);

    private static final float MARGIN_LEFT = 40;
    private static final float MARGIN_RIGHT = 40;
    private static final float HEADER_TOP_PADDING = 14;
    private static final float HEADER_HEIGHT = 62;
    private static final float FOOTER_BOTTOM_PADDING = 12;
    private static final float FOOTER_HEIGHT = 32;

    private static final DateTimeFormatter FOOTER_DATE =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public enum Part {
        HEADER, FOOTER
    }

    private final PdfBrandingConfig config;
    private final Part part;

    public PdfPageBrandingHandler(PdfBrandingConfig config, Part part) {
        this.config = config;
        this.part = part;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        PdfCanvas pdfCanvas = new PdfCanvas(
                page.newContentStreamBefore(),
                page.getResources(),
                pdfDoc);

        if (part == Part.HEADER) {
            drawHeader(pdfCanvas, pageSize);
        } else {
            drawFooter(pdfCanvas, pageSize);
        }
        pdfCanvas.release();
    }

    private void drawHeader(PdfCanvas pdfCanvas, Rectangle pageSize) {
        float width = pageSize.getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
        float topY = pageSize.getTop() - HEADER_TOP_PADDING;
        Rectangle headerArea = new Rectangle(MARGIN_LEFT, topY - HEADER_HEIGHT, width, HEADER_HEIGHT);

        Canvas canvas = new Canvas(pdfCanvas, headerArea);

        Table header = new Table(UnitValue.createPercentArray(new float[]{58, 42}))
                .useAllAvailableWidth();

        Cell brandCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);

        Table brandRow = new Table(UnitValue.createPercentArray(new float[]{18, 82}))
                .useAllAvailableWidth();

        Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        Image logo = loadLogo(34, 34);
        if (logo != null) {
            logoCell.add(logo);
        }
        brandRow.addCell(logoCell);

        Div brandText = new Div();
        brandText.add(new Paragraph("Healix")
                .setFontSize(16)
                .setBold()
                .setFontColor(PRIMARY_DARK)
                .setMarginBottom(0));
        brandText.add(new Paragraph("www.healix.in")
                .setFontSize(8)
                .setFontColor(PRIMARY)
                .setMarginTop(1));
        brandText.add(new Paragraph("Intelligent Healthcare Platform")
                .setFontSize(7)
                .setFontColor(MUTED)
                .setMarginTop(1));

        brandRow.addCell(new Cell().add(brandText).setBorder(Border.NO_BORDER).setPaddingLeft(4));
        brandCell.add(brandRow);
        header.addCell(brandCell);

        Div meta = new Div().setTextAlignment(TextAlignment.RIGHT);
        meta.add(new Paragraph(config.documentTitle().toUpperCase())
                .setFontSize(10)
                .setBold()
                .setFontColor(DeviceRgb.WHITE)
                .setBackgroundColor(PRIMARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(4)
                .setMarginBottom(3));
        if (config.documentSubtitle() != null && !config.documentSubtitle().isBlank()) {
            meta.add(new Paragraph(config.documentSubtitle())
                    .setFontSize(8)
                    .setFontColor(PRIMARY_DARK)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(1));
        }
        if (config.referenceLabel() != null && config.referenceValue() != null) {
            meta.add(new Paragraph(config.referenceLabel() + ": " + config.referenceValue())
                    .setFontSize(8)
                    .setFontColor(MUTED)
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        header.addCell(new Cell().add(meta).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
        canvas.add(header);

        Div accent = new Div().setHeight(2).setBackgroundColor(PRIMARY).setMarginTop(4);
        canvas.add(accent);
        canvas.close();
    }

    private void drawFooter(PdfCanvas pdfCanvas, Rectangle pageSize) {
        float width = pageSize.getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
        Rectangle footerArea = new Rectangle(
                MARGIN_LEFT,
                pageSize.getBottom() + FOOTER_BOTTOM_PADDING,
                width,
                FOOTER_HEIGHT);

        Canvas canvas = new Canvas(pdfCanvas, footerArea);

        Paragraph footer = new Paragraph()
                .setFontSize(7)
                .setFontColor(MUTED)
                .setTextAlignment(TextAlignment.CENTER);

        footer.add("Healix Healthcare Platform  •  www.healix.in  •  support@healix.in\n");
        footer.add("Generated on " + LocalDateTime.now().format(FOOTER_DATE));
        if (config.documentId() != null) {
            footer.add("  •  Document ID: " + config.documentId());
        }

        canvas.add(footer);
        canvas.close();
    }

    static Image loadLogo(float width, float height) {
        try (InputStream stream = PdfPageBrandingHandler.class.getResourceAsStream("/assets/healix-logo.png")) {
            if (stream != null) {
                return new Image(ImageDataFactory.create(stream.readAllBytes()))
                        .setWidth(width)
                        .setHeight(height);
            }
        } catch (Exception ignored) {
            // Logo optional
        }
        return null;
    }

    public static float getTopMargin() {
        return HEADER_TOP_PADDING + HEADER_HEIGHT + 8;
    }

    public static float getBottomMargin() {
        return FOOTER_BOTTOM_PADDING + FOOTER_HEIGHT + 8;
    }
}
