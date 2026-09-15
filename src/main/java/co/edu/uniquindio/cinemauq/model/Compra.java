package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;

public class Compra {

    private String idCompra;
    private LocalDateTime fecha;
    private double total;
    private EstadoCompra estado;

    public Compra(String idCompra, LocalDateTime fecha, double total, EstadoCompra estado) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public String getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(String idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public void setEstado(EstadoCompra estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra='" + idCompra + '\'' +
                ", fecha=" + fecha +
                ", total=" + total +
                ", estado=" + estado +
                '}';
    }
}