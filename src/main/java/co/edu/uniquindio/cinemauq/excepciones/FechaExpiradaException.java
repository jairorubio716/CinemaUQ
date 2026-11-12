package co.edu.uniquindio.cinemauq.excepciones;

public class FechaExpiradaException extends CinemaUQException {

    public FechaExpiradaException(String mensaje) {
        super(mensaje);
    }

    public FechaExpiradaException(String concepto, String fecha) {
        super("El elemento '" + concepto + "' se encuentra expirado (fecha: " + fecha + ").");
    }
}
