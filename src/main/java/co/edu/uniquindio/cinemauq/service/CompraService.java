package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.FuncionNoVendibleException;
import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.TipoMovimiento;
import co.edu.uniquindio.cinemauq.patrones.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.patrones.decorator.ProductoConPromocion;
import co.edu.uniquindio.cinemauq.patrones.factory.IFabricaEntrada;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompraService {

    private final CatalogoService catalogo;
    private final IFabricaEntrada fabricaEntrada;
    private final PuntoService puntoService;
    private final NotificadorObservable notificador;

    public CompraService(CatalogoService catalogo, IFabricaEntrada fabricaEntrada,
                         PuntoService puntoService, NotificadorObservable notificador) {
        this.catalogo = catalogo;
        this.fabricaEntrada = fabricaEntrada;
        this.puntoService = puntoService;
        this.notificador = notificador;
    }

    public Compra comprar(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                          List<IItemVendible> confiteria, List<Promocion> promociones) {
        if (cliente == null) {
            throw new CinemaUQException("Se requiere un cliente para realizar la compra.");
        }
        if (funcion == null) {
            throw new CinemaUQException("Se requiere una función para la compra.");
        }
        List<Asiento> asientosSeleccionados = asientos != null ? asientos : new ArrayList<>();
        List<IItemVendible> items = confiteria != null ? confiteria : new ArrayList<>();
        List<Promocion> promos = promociones != null ? promociones : new ArrayList<>();

        if (!funcion.estaVendible()) {
            throw new FuncionNoVendibleException(funcion.getIdFuncion(), String.valueOf(funcion.getEstado()));
        }
        validarAforo(funcion, asientosSeleccionados);
        for (Asiento asiento : asientosSeleccionados) {
            if (!funcion.estaDisponible(asiento)) {
                throw new AsientoNoDisponibleException(asiento.getIdAsiento());
            }
        }

        CompraBuilder builder = new CompraBuilder()
                .conId("CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .conFecha(LocalDateTime.now())
                .paraCliente(cliente);

        for (Asiento asiento : asientosSeleccionados) {
            String idEntrada = "ENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Entrada entrada = fabricaEntrada.crearEntrada(
                    idEntrada, funcion, asiento, funcion.getPrecioEntrada());
            builder.conEntrada(entrada);
        }
        for (IItemVendible item : items) {
            Promocion aplicable = buscarPromocionAplicable(item, promos);
            if (aplicable != null) {
                builder.conItem(new ProductoConPromocion(item, aplicable));
            } else {
                builder.conItem(item);
            }
        }
        for (Promocion promocion : promos) {
            builder.conPromocion(promocion);
        }

        Compra compra = builder.construir();
        double total = compra.calcularTotal();
        cobrar(cliente, total);
        compra.cambiarEstado(EstadoCompra.PAGADA);

        for (Entrada entrada : compra.getEntradas()) {
            funcion.agregarEntrada(entrada);
        }
        cliente.agregarCompra(compra);
        catalogo.agregarCompra(compra);
        int puntosGanados = puntoService.acumular(cliente, total);

        notificador.notificar("Compra " + compra.getIdCompra() + " pagada por $"
                + total + ". Puntos ganados: " + puntosGanados + ".");
        catalogo.guardar();
        return compra;
    }

    private void validarAforo(Funcion funcion, List<Asiento> asientos) {
        if (funcion.getSala() == null) {
            return;
        }
        int capacidad = funcion.getSala().getCapacidad();
        int ocupados = funcion.getAsientosVendidos() + asientos.size();
        if (capacidad > 0 && ocupados > capacidad) {
            throw new CinemaUQException("La función supera el aforo de la sala ("
                    + capacidad + " asientos).");
        }
    }

    private void cobrar(Cliente cliente, double total) {
        TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
        if (tarjeta == null) {
            throw new CinemaUQException("El cliente no tiene una tarjeta virtual asociada.");
        }
        if (!tarjeta.estaActiva()) {
            throw new CinemaUQException("La tarjeta virtual no está activa.");
        }
        if (!tarjeta.tieneSaldo(total)) {
            throw new SaldoInsuficienteException(tarjeta.getSaldo(), total);
        }
        tarjeta.setSaldo(tarjeta.getSaldo() - total);
        tarjeta.registrarMovimiento(new Movimiento(
                catalogo.siguienteIdMovimiento(), LocalDateTime.now(),
                TipoMovimiento.COMPRA, total, tarjeta.getSaldo()));
    }

    private Promocion buscarPromocionAplicable(IItemVendible item, List<Promocion> promociones) {
        if (item == null) {
            return null;
        }
        for (Promocion promocion : promociones) {
            if (promocion != null && promocion.estaVigente()
                    && promocion.calcularDescuento(item.obtenerPrecio()) > 0) {
                return promocion;
            }
        }
        return null;
    }
}
