package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Compra {

    private String idCompra;
    private LocalDateTime fecha;
    private double total;
    private EstadoCompra estado;
    private Cliente cliente;
    private List<Entrada> entradas = new ArrayList<>();
    private List<IItemVendible> confiteria = new ArrayList<>();
    private List<Promocion> promociones = new ArrayList<>();
    private double descuento;

    public Compra(String idCompra, LocalDateTime fecha, Cliente cliente) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.cliente = cliente;
        this.estado = EstadoCompra.PENDIENTE;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<Entrada> entradas) {
        this.entradas = entradas;
    }

    public List<IItemVendible> getConfiteria() {
        return confiteria;
    }

    public void setConfiteria(List<IItemVendible> confiteria) {
        this.confiteria = confiteria;
    }

    public List<Promocion> getPromociones() {
        return promociones;
    }

    public void setPromociones(List<Promocion> promociones) {
        this.promociones = promociones;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public void agregarEntrada(Entrada entrada) {
        if (entrada != null) {
            entradas.add(entrada);
        }
    }

    public void agregarItem(IItemVendible item) {
        if (item != null) {
            confiteria.add(item);
        }
    }

    public void agregarPromocion(Promocion promocion) {
        if (promocion != null) {
            promociones.add(promocion);
        }
    }

    public double calcularTotal() {
        double suma = 0;
        for (Entrada entrada : entradas) {
            suma += entrada.getPrecio();
        }
        for (IItemVendible item : confiteria) {
            suma += item.obtenerPrecio();
        }
        double resultado = suma - descuento;
        this.total = Math.max(0, resultado);
        return total;
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
