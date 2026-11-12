package co.edu.uniquindio.cinemauq.excepciones;

public class CinemaUQException extends RuntimeException {

    public CinemaUQException(String mensaje) {
        super(mensaje);
    }

    public CinemaUQException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
