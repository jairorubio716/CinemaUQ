package co.edu.uniquindio.cinemauq.patrones.strategy;

public class ReembolsoParcial implements IReembolso {

    private final double porcentaje;

    public ReembolsoParcial(double porcentaje) {
        this.porcentaje = Math.max(0, Math.min(100, porcentaje));
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    @Override
    public double calcularReembolso(double valorPagado) {
        return Math.max(0, valorPagado) * (porcentaje / 100.0);
    }

    @Override
    public String getDescripcion() {
        return "Reembolso del " + porcentaje + "% del valor pagado.";
    }
}
