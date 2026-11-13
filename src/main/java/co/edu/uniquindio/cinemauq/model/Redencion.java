package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;

/**
 * Comprobante de canje de puntos CinemaUQ por un beneficio.
 */
public class Redencion {

    private String idRedencion;
    private String codigo;
    private String beneficio;
    private int puntos;
    private LocalDateTime fecha;
    private String idCliente;

    public Redencion(String idRedencion, String codigo, String beneficio, int puntos,
                     LocalDateTime fecha, String idCliente) {
        this.idRedencion = idRedencion;
        this.codigo = codigo;
        this.beneficio = beneficio;
        this.puntos = puntos;
        this.fecha = fecha;
        this.idCliente = idCliente;
    }

    public String getIdRedencion() {
        return idRedencion;
    }

    public void setIdRedencion(String idRedencion) {
        this.idRedencion = idRedencion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String beneficio) {
        this.beneficio = beneficio;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public String toString() {
        return "Redencion{" +
                "codigo='" + codigo + '\'' +
                ", beneficio='" + beneficio + '\'' +
                ", puntos=" + puntos +
                ", fecha=" + fecha +
                '}';
    }
}
