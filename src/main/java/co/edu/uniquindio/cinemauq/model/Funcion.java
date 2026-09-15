package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;

public class Funcion {

    private String idFuncion;
    private LocalDateTime fechaHora;
    private double precioEntrada;
    private EstadoFuncion estado;

    public Funcion(String idFuncion, LocalDateTime fechaHora, double precioEntrada, EstadoFuncion estado) {
        this.idFuncion = idFuncion;
        this.fechaHora = fechaHora;
        this.precioEntrada = precioEntrada;
        this.estado = estado;
    }

    public String getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(String idFuncion) {
        this.idFuncion = idFuncion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(double precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    public void setEstado(EstadoFuncion estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Funcion{" +
                "idFuncion='" + idFuncion + '\'' +
                ", fechaHora=" + fechaHora +
                ", precioEntrada=" + precioEntrada +
                ", estado=" + estado +
                '}';
    }
}