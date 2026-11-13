package co.edu.uniquindio.cinemauq.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public final class ExportadorReporte {

    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ExportadorReporte() {
    }

    public static void exportarTxt(Reporte reporte, Path destino) throws IOException {
        if (reporte == null || destino == null) {
            throw new IllegalArgumentException("No hay un reporte para exportar.");
        }
        crearDirectorio(destino);
        Files.writeString(destino, reporte.textoPlano(), StandardCharsets.UTF_8);
    }

    public static void exportarPdf(Reporte reporte, Path destino) throws IOException {
        if (reporte == null || destino == null) {
            throw new IllegalArgumentException("No hay un reporte para exportar.");
        }
        crearDirectorio(destino);
        Document documento = new Document(PageSize.A4, 36, 36, 42, 36);
        try (OutputStream salida = Files.newOutputStream(destino)) {
            try {
                PdfWriter.getInstance(documento, salida);
                documento.open();

                BaseFont base = BaseFont.createFont(BaseFont.HELVETICA,
                        BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                BaseFont negrita = BaseFont.createFont(BaseFont.HELVETICA_BOLD,
                        BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                Font titulo = new Font(negrita, 18, Font.BOLD, new Color(229, 9, 44));
                Font subtitulo = new Font(negrita, 13, Font.BOLD, new Color(35, 35, 45));
                Font seccion = new Font(negrita, 11, Font.BOLD, new Color(35, 35, 45));
                Font normal = new Font(base, 9, Font.NORMAL, new Color(55, 55, 65));
                Font tenue = new Font(base, 8, Font.NORMAL, new Color(110, 110, 125));
                Font encabezado = new Font(negrita, 8, Font.BOLD, Color.WHITE);
                Font celda = new Font(base, 8, Font.NORMAL, new Color(45, 45, 55));

                Paragraph marca = new Paragraph("CINEMA UQ", titulo);
                marca.setSpacingAfter(2);
                documento.add(marca);

                Paragraph nombre = new Paragraph(reporte.tipo().getCodigo() + " - "
                        + reporte.tipo().getTitulo(), subtitulo);
                nombre.setSpacingAfter(4);
                documento.add(nombre);

                documento.add(new Paragraph("Generado: "
                        + FECHA_HORA.format(reporte.generado()), tenue));
                documento.add(new Paragraph("Periodo consultado: " + reporte.periodo(), tenue));
                documento.add(new Paragraph("Registros encontrados: "
                        + reporte.filas().size(), tenue));

                Paragraph resumen = new Paragraph("Resumen", seccion);
                resumen.setSpacingBefore(14);
                resumen.setSpacingAfter(4);
                documento.add(resumen);
                for (Reporte.Metrica metrica : reporte.metricas()) {
                    documento.add(new Paragraph(
                            metrica.etiqueta() + ": " + metrica.valor(), normal));
                }

                Paragraph detalle = new Paragraph("Detalle", seccion);
                detalle.setSpacingBefore(14);
                detalle.setSpacingAfter(4);
                documento.add(detalle);

                if (reporte.columnas().isEmpty()) {
                    documento.add(new Paragraph("Sin columnas para mostrar.", normal));
                } else {
                    documento.add(tabla(reporte.columnas(), reporte.filas(),
                            encabezado, celda));
                }

                documento.add(new Paragraph(
                        "Reporte generado por el modulo administrativo de CinemaUQ.", tenue));
            } finally {
                if (documento.isOpen()) {
                    documento.close();
                }
            }
        } catch (DocumentException e) {
            throw new IOException("No fue posible generar el archivo PDF.", e);
        }
    }

    private static PdfPTable tabla(List<String> columnas, List<List<String>> filas,
                                   Font encabezado, Font celda) throws DocumentException {
        PdfPTable tabla = new PdfPTable(columnas.size());
        tabla.setWidthPercentage(100);
        tabla.setHeaderRows(1);
        tabla.setSpacingBefore(4);
        tabla.setSpacingAfter(8);
        float[] anchos = new float[columnas.size()];
        Arrays.fill(anchos, 1f);
        tabla.setWidths(anchos);

        for (String columna : columnas) {
            PdfPCell celdaEncabezado = new PdfPCell(new Phrase(columna, encabezado));
            celdaEncabezado.setBackgroundColor(new Color(28, 28, 40));
            celdaEncabezado.setBorderColor(new Color(45, 45, 60));
            celdaEncabezado.setPadding(5);
            celdaEncabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            tabla.addCell(celdaEncabezado);
        }

        for (List<String> fila : filas) {
            for (int i = 0; i < columnas.size(); i++) {
                String valor = i < fila.size() ? fila.get(i) : "";
                PdfPCell celdaDato = new PdfPCell(new Phrase(valor, celda));
                celdaDato.setBorderColor(new Color(210, 210, 220));
                celdaDato.setPadding(4);
                tabla.addCell(celdaDato);
            }
        }
        return tabla;
    }

    private static void crearDirectorio(Path destino) throws IOException {
        Path padre = destino.toAbsolutePath().getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
    }
}
