package co.edu.uniquindio.cinemauq.patrones.strategy;

public class ReembolsoCompleto implements IReembolso {

    @Override
    public double calcularReembolso(double valorPagado) {
        return Math.max(0, valorPagado);
    }

    @Override
    public String getDescripcion() {
        return "Reembolso del 100% del valor pagado.";
    }
}
