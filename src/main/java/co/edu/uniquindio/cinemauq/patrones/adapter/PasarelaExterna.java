package co.edu.uniquindio.cinemauq.patrones.adapter;

/**
 * Pasarela externa (p. ej. Nequi/Bancolombia) con una API incompatible
 * con la del sistema. No se puede modificar; se adapta con {@link PagoAdapter}.
 */
public class PasarelaExterna {

    public String transferir(double valor, String referencia) {
        if (valor <= 0 || referencia == null || referencia.isBlank()) {
            return "FONDOS_INSUFICIENTES";
        }
        return "OK";
    }
}
