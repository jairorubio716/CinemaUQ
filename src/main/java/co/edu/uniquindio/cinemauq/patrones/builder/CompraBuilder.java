package co.edu.uniquindio.cinemauq.patrones.builder;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Promocion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompraBuilder {

    private String idCompra;
    private LocalDateTime fecha = LocalDateTime.now();
    private Cliente cliente;
    private final List<Entrada> entradas = new ArrayList<>();
    private final List<IItemVendible> confiteria = new ArrayList<>();
    private final List<Promocion> promociones = new ArrayList<>();
    private double descuento;

    public CompraBuilder conId(String idCompra) {
        this.idCompra = idCompra;
        return this;
    }

    public CompraBuilder conFecha(LocalDateTime fecha) {
        if (fecha != null) {
            this.fecha = fecha;
        }
        return this;
    }

    public CompraBuilder paraCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public CompraBuilder conEntrada(Entrada entrada) {
        if (entrada != null) {
            entradas.add(entrada);
        }
        return this;
    }

    public CompraBuilder conEntradas(List<Entrada> nuevas) {
        if (nuevas != null) {
            entradas.addAll(nuevas);
        }
        return this;
    }

    public CompraBuilder conItem(IItemVendible item) {
        if (item != null) {
            confiteria.add(item);
        }
        return this;
    }

    public CompraBuilder conConfiteria(List<IItemVendible> items) {
        if (items != null) {
            confiteria.addAll(items);
        }
        return this;
    }

    public CompraBuilder conPromocion(Promocion promocion) {
        if (promocion != null) {
            promociones.add(promocion);
        }
        return this;
    }

    public CompraBuilder conDescuento(double descuento) {
        this.descuento = Math.max(0, descuento);
        return this;
    }

    public Compra construir() {
        if (cliente == null) {
            throw new CinemaUQException("La compra requiere un cliente asociado.");
        }
        if (entradas.isEmpty() && confiteria.isEmpty()) {
            throw new CinemaUQException("La compra debe tener al menos una entrada o un ítem de confitería.");
        }
        if (idCompra == null || idCompra.isBlank()) {
            idCompra = "CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        Compra compra = new Compra(idCompra, fecha, cliente);
        for (Entrada entrada : entradas) {
            compra.agregarEntrada(entrada);
        }
        for (IItemVendible item : confiteria) {
            compra.agregarItem(item);
        }
        for (Promocion promocion : promociones) {
            compra.agregarPromocion(promocion);
        }
        compra.setDescuento(descuento);
        compra.calcularTotal();
        return compra;
    }
}
