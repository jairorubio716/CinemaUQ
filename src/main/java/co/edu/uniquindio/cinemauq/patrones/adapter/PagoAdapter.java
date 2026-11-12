package co.edu.uniquindio.cinemauq.patrones.adapter;

public class PagoAdapter implements IPasarelaPago {

    private final PasarelaExterna pasarelaExterna;

    public PagoAdapter(PasarelaExterna pasarelaExterna) {
        this.pasarelaExterna = pasarelaExterna;
    }

    @Override
    public boolean pagarExterno(String referencia, double monto) {
        if (pasarelaExterna == null) {
            return false;
        }
        String respuesta = pasarelaExterna.transferir(monto, referencia);
        return "OK".equalsIgnoreCase(respuesta);
    }
}
