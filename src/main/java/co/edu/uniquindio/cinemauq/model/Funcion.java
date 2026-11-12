package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Funcion {

    private String idFuncion;
    private LocalDateTime fechaHora;
    private double precioEntrada;
    private EstadoFuncion estado;
    private Pelicula pelicula;
    private Sala sala;
    private List<Entrada> entradasVendidas = new ArrayList<>();

    public Funcion(String idFuncion, LocalDateTime fechaHora, double precioEntrada,
                   EstadoFuncion estado, Pelicula pelicula, Sala sala) {
        this.idFuncion = idFuncion;
        this.fechaHora = fechaHora;
        this.precioEntrada = precioEntrada;
        this.estado = estado;
        this.pelicula = pelicula;
        this.sala = sala;
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

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public List<Entrada> getEntradasVendidas() {
        return entradasVendidas;
    }

    public void setEntradasVendidas(List<Entrada> entradasVendidas) {
        this.entradasVendidas = entradasVendidas;
    }

    public boolean estaVendible() {
        return estado == EstadoFuncion.EN_VENTA;
    }

    public boolean estaDisponible(Asiento asiento) {
        if (asiento == null) {
            return false;
        }
        for (Entrada entrada : entradasVendidas) {
            Asiento ocupado = entrada.getAsiento();
            if (ocupado != null && ocupado.getIdAsiento() == asiento.getIdAsiento()) {
                return false;
            }
        }
        return true;
    }

    public void agregarEntrada(Entrada entrada) {
        if (entrada != null) {
            entradasVendidas.add(entrada);
        }
    }

    public List<Asiento> getAsientosDisponibles() {
        List<Asiento> disponibles = new ArrayList<>();
        if (sala == null) {
            return disponibles;
        }
        for (Asiento asiento : sala.getAsientos()) {
            if (estaDisponible(asiento)) {
                disponibles.add(asiento);
            }
        }
        return disponibles;
    }

    public int getAsientosVendidos() {
        return entradasVendidas.size();
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
