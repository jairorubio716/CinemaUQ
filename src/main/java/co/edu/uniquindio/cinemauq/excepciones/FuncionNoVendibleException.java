package co.edu.uniquindio.cinemauq.excepciones;

public class FuncionNoVendibleException extends CinemaUQException {

    public FuncionNoVendibleException(String mensaje) {
        super(mensaje);
    }

    public FuncionNoVendibleException(String idFuncion, String estado) {
        super("La función " + idFuncion + " no está disponible para venta (estado: " + estado + ").");
    }
}
