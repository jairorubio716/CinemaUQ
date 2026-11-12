package co.edu.uniquindio.cinemauq.excepciones;

public class AsientoNoDisponibleException extends CinemaUQException {

    public AsientoNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public AsientoNoDisponibleException(int idAsiento) {
        super("El asiento " + idAsiento + " ya fue vendido para esta función.");
    }
}
