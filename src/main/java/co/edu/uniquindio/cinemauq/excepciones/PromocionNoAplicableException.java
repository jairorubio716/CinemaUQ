package co.edu.uniquindio.cinemauq.excepciones;

public class PromocionNoAplicableException extends CinemaUQException {

    public PromocionNoAplicableException(String mensaje) {
        super(mensaje);
    }

    public PromocionNoAplicableException(String idPromocion, boolean acumulada) {
        super("La promoción '" + idPromocion + "' no es aplicable"
                + (acumulada ? " o ya está acumulada sobre el ítem." : "."));
    }
}
