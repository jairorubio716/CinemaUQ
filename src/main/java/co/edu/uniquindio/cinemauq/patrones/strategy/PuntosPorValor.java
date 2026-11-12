package co.edu.uniquindio.cinemauq.patrones.strategy;

public class PuntosPorValor implements IStrategyPuntos {

    private final double valorPorPunto;

    public PuntosPorValor(double valorPorPunto) {
        this.valorPorPunto = valorPorPunto > 0 ? valorPorPunto : 1000;
    }

    @Override
    public int calcularPuntos(double monto) {
        if (monto <= 0) {
            return 0;
        }
        return (int) (monto / valorPorPunto);
    }

    @Override
    public String getDescripcion() {
        return "1 punto por cada $" + valorPorPunto + " COP gastados.";
    }
}
