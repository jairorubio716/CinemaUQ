package co.edu.uniquindio.cinemauq.app;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formato {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");
    private static final NumberFormat MONEDA = NumberFormat.getNumberInstance(COLOMBIA);
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("EEE d MMM · h:mm a", COLOMBIA);
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("d MMM yyyy", COLOMBIA);
    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("h:mm a", COLOMBIA);
    private static final DateTimeFormatter DIA =
            DateTimeFormatter.ofPattern("EEE d", COLOMBIA);

    private Formato() {
    }

    public static String moneda(double valor) {
        return "$ " + MONEDA.format(Math.round(valor));
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "—" : FECHA_HORA.format(fecha);
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "—" : FECHA.format(fecha);
    }

    public static String hora(LocalDateTime fecha) {
        return fecha == null ? "—" : HORA.format(fecha);
    }

    public static String dia(LocalDate fecha) {
        return fecha == null ? "—" : capitalizar(DIA.format(fecha));
    }

    public static String texto(Enum<?> valor) {
        if (valor == null) {
            return "—";
        }
        String texto = valor.name().replace('_', ' ').toLowerCase(COLOMBIA);
        return capitalizar(texto);
    }

    public static String clasificacion(Enum<?> valor) {
        if (valor == null) {
            return "—";
        }
        return switch (valor.name()) {
            case "PG_13" -> "PG-13";
            case "NC_17" -> "NC-17";
            default -> valor.name();
        };
    }

    public static String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase(COLOMBIA) + texto.substring(1);
    }

    public static String truncar(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 1) + "…";
    }
}
