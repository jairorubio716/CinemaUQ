package co.edu.uniquindio.cinemauq.excepciones;

public class SaldoInsuficienteException extends CinemaUQException {

    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public SaldoInsuficienteException(double saldoDisponible, double montoRequerido) {
        super("Saldo insuficiente: disponible $" + saldoDisponible
                + ", requerido $" + montoRequerido + ".");
    }
}
