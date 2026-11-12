package co.edu.uniquindio.cinemauq.patrones.strategy;

public interface IReembolso {

    double calcularReembolso(double valorPagado);

    String getDescripcion();
}
