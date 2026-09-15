package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;

public class SolicitudRecarga {

    private String idSolicitud;
    private LocalDateTime fecha;
    private double monto;
    private EstadoSolicitudRecarga estado;

    public SolicitudRecarga(String idSolicitud, LocalDateTime fecha,
                            double monto, EstadoSolicitudRecarga estado) {
        this.idSolicitud = idSolicitud;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = estado;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public EstadoSolicitudRecarga getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudRecarga estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "SolicitudRecarga{" +
                "idSolicitud='" + idSolicitud + '\'' +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", estado=" + estado +
                '}';
    }
}