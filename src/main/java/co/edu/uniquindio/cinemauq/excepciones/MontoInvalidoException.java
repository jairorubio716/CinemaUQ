package co.edu.uniquindio.cinemauq.excepciones;

public class MontoInvalidoException extends CinemaUQException {

    public MontoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public MontoInvalidoException(double monto) {
        super("El monto ingresado ($" + monto + ") no es válido; debe ser mayor que cero.");
    }
}
