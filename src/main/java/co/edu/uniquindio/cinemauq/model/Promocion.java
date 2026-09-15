package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDate;

public class Promocion {

    private String idPromocion;
    private String nombre;
    private TipoPromocion tipo;
    private double porcentajeOValor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Promocion(String idPromocion, String nombre, TipoPromocion tipo,
                     double porcentajeOValor, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idPromocion = idPromocion;
        this.nombre = nombre;
        this.tipo = tipo;
        this.porcentajeOValor = porcentajeOValor;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getIdPromocion() {
        return idPromocion;
    }

    public void setIdPromocion(String idPromocion) {
        this.idPromocion = idPromocion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoPromocion getTipo() {
        return tipo;
    }

    public void setTipo(TipoPromocion tipo) {
        this.tipo = tipo;
    }

    public double getPorcentajeOValor() {
        return porcentajeOValor;
    }

    public void setPorcentajeOValor(double porcentajeOValor) {
        this.porcentajeOValor = porcentajeOValor;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return "Promocion{" +
                "idPromocion='" + idPromocion + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", porcentajeOValor=" + porcentajeOValor +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                '}';
    }
}