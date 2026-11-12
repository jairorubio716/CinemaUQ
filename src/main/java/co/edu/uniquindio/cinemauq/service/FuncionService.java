package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.Funcion;

import java.util.ArrayList;
import java.util.List;

/**
 * Cancelación de funciones por parte del cine (RF-017, RN-06).
 */
public class FuncionService {

    private final CatalogoService catalogo;
    private final ReembolsoService reembolsoService;

    public FuncionService(CatalogoService catalogo, ReembolsoService reembolsoService) {
        this.catalogo = catalogo;
        this.reembolsoService = reembolsoService;
    }

    /**
     * Cancela una función y reembolsa el 100% a cada compra pagada afectada (RN-06).
     * Devuelve el total reembolsado.
     */
    public double cancelarFuncion(Administrador admin, Funcion funcion) {
        if (admin == null) {
            throw new CinemaUQException("Solo un administrador puede cancelar funciones (RN-16).");
        }
        if (funcion == null) {
            throw new CinemaUQException("Se requiere una función para cancelar.");
        }
        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            throw new CinemaUQException("La función ya está cancelada.");
        }
        funcion.setEstado(EstadoFuncion.CANCELADA);

        List<Compra> afectadas = new ArrayList<>();
        for (Compra compra : catalogo.getCompras()) {
            if (compra.getEstado() == EstadoCompra.PAGADA && tieneEntradaEnFuncion(compra, funcion)) {
                afectadas.add(compra);
            }
        }
        double totalReembolsado = 0;
        for (Compra compra : afectadas) {
            totalReembolsado += reembolsoService.cancelarCompra(compra);
        }
        catalogo.guardar();
        return totalReembolsado;
    }

    public void activarVenta(Administrador admin, Funcion funcion) {
        if (admin == null) {
            throw new CinemaUQException("Solo un administrador puede habilitar la venta (RN-16).");
        }
        if (funcion == null) {
            throw new CinemaUQException("Se requiere una función.");
        }
        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            throw new CinemaUQException("No se puede habilitar la venta de una función cancelada.");
        }
        funcion.setEstado(EstadoFuncion.EN_VENTA);
        catalogo.guardar();
    }

    private boolean tieneEntradaEnFuncion(Compra compra, Funcion funcion) {
        for (Entrada entrada : compra.getEntradas()) {
            if (entrada.getFuncion() != null
                    && entrada.getFuncion().getIdFuncion().equals(funcion.getIdFuncion())) {
                return true;
            }
        }
        return false;
    }
}
