package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDate;

public class PuntoAcumulado {

    private int idPunto;
    private int cantidad;
    private LocalDate fechaObtencion;
    private LocalDate fechaVencimiento;

    public PuntoAcumulado(int idPunto, int cantidad,
                          LocalDate fechaObtencion, LocalDate fechaVencimiento) {
        this.idPunto = idPunto;
        this.cantidad = cantidad;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public void setFechaObtencion(LocalDate fechaObtencion) {
        this.fechaObtencion = fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public String toString() {
        return "PuntoAcumulado{" +
                "idPunto=" + idPunto +
                ", cantidad=" + cantidad +
                ", fechaObtencion=" + fechaObtencion +
                ", fechaVencimiento=" + fechaVencimiento +
                '}';
    }
}