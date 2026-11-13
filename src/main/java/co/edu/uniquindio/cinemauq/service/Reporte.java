package co.edu.uniquindio.cinemauq.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Reporte {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MONEDA = NumberFormat.getNumberInstance(COLOMBIA);

    private final TipoReporte tipo;
    private final LocalDate inicio;
    private final LocalDate fin;
    private final LocalDateTime generado;
    private final List<Metrica> metricas;
    private final List<String> columnas;
    private final List<List<String>> filas;
    private final Map<String, Double> datosBarras;
    private final Map<String, Double> datosPastel;
    private final List<PuntoLinea> datosLinea;

    public Reporte(TipoReporte tipo, LocalDate inicio, LocalDate fin, LocalDateTime generado,
                   List<Metrica> metricas, List<String> columnas, List<List<String>> filas,
                   Map<String, Double> datosBarras, Map<String, Double> datosPastel,
                   List<PuntoLinea> datosLinea) {
        this.tipo = tipo;
        this.inicio = inicio;
        this.fin = fin;
        this.generado = generado;
        this.metricas = List.copyOf(metricas);
        this.columnas = List.copyOf(columnas);
        this.filas = filas.stream().map(List::copyOf).toList();
        this.datosBarras = Collections.unmodifiableMap(new LinkedHashMap<>(datosBarras));
        this.datosPastel = Collections.unmodifiableMap(new LinkedHashMap<>(datosPastel));
        this.datosLinea = List.copyOf(datosLinea);
    }

    public TipoReporte tipo() {
        return tipo;
    }

    public LocalDate inicio() {
        return inicio;
    }

    public LocalDate fin() {
        return fin;
    }

    public LocalDateTime generado() {
        return generado;
    }

    public List<Metrica> metricas() {
        return metricas;
    }

    public List<String> columnas() {
        return columnas;
    }

    public List<List<String>> filas() {
        return filas;
    }

    public Map<String, Double> datosBarras() {
        return datosBarras;
    }

    public Map<String, Double> datosPastel() {
        return datosPastel;
    }

    public List<PuntoLinea> datosLinea() {
        return datosLinea;
    }

    public String periodo() {
        return FECHA.format(inicio) + " - " + FECHA.format(fin);
    }

    public String nombreArchivo() {
        return tipo.getCodigo() + "-" + tipo.getSlug() + "-" + inicio + "-" + fin;
    }

    public String textoPlano() {
        String salto = System.lineSeparator();
        StringBuilder texto = new StringBuilder();
        texto.append("CINEMA UQ").append(salto);
        texto.append(tipo.getCodigo()).append(" - ").append(tipo.getTitulo()).append(salto);
        texto.append("Generado: ").append(FECHA_HORA.format(generado)).append(salto);
        texto.append("Periodo consultado: ").append(periodo()).append(salto);
        texto.append("Registros encontrados: ").append(filas.size()).append(salto);
        texto.append(salto).append("RESUMEN").append(salto);
        for (Metrica metrica : metricas) {
            texto.append("- ").append(metrica.etiqueta()).append(": ")
                    .append(metrica.valor()).append(salto);
        }
        texto.append(salto).append("DETALLE").append(salto);
        if (columnas.isEmpty()) {
            texto.append("Sin columnas para mostrar.").append(salto);
            return texto.toString();
        }
        texto.append(String.join(" | ", columnas)).append(salto);
        texto.append("-".repeat(90)).append(salto);
        if (filas.isEmpty()) {
            texto.append("Sin registros para el periodo consultado.").append(salto);
        } else {
            for (List<String> fila : filas) {
                texto.append(String.join(" | ", fila)).append(salto);
            }
        }
        return texto.toString();
    }

    public static String moneda(double valor) {
        return "$ " + MONEDA.format(Math.round(valor));
    }

    public record Metrica(String etiqueta, String valor) {
    }

    public record PuntoLinea(String etiqueta, double valor) {
    }
}
