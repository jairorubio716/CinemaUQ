package co.edu.uniquindio.cinemauq.patrones.strategy;

public class SinReembolso implements IReembolso {

    @Override
    public double calcularReembolso(double valorPagado) {
        return 0;
    }

    @Override
    public String getDescripcion() {
        return "No aplica reembolso.";
    }
}
