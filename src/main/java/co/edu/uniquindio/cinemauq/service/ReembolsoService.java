package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.TipoMovimiento;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;
import co.edu.uniquindio.cinemauq.patrones.strategy.IReembolso;
import co.edu.uniquindio.cinemauq.patrones.strategy.ReembolsoCompleto;
import co.edu.uniquindio.cinemauq.patrones.strategy.ReembolsoParcial;
import co.edu.uniquindio.cinemauq.patrones.strategy.SinReembolso;

import java.time.Duration;
import java.time.LocalDateTime;

public class ReembolsoService {

    private final CatalogoService catalogo;
    private final NotificadorObservable notificador;

    public ReembolsoService(CatalogoService catalogo, NotificadorObservable notificador) {
        this.catalogo = catalogo;
        this.notificador = notificador;
    }

    /**
     * Selecciona la estrategia de reembolso según las horas restantes (RN-05/RN-06).
     */
    public IReembolso seleccionarEstrategia(long horasRestantes, boolean canceladaPorCine) {
        if (canceladaPorCine || horasRestantes > 24) {
            return new ReembolsoCompleto();
        }
        if (horasRestantes >= 2) {
            return new ReembolsoParcial(80);
        }
        return new SinReembolso();
    }

    public double cancelarCompra(Compra compra) {
        if (compra == null) {
            throw new CinemaUQException("Se requiere una compra para cancelar.");
        }
        if (!compra.puedeReembolsarse()) {
            throw new CinemaUQException("La compra no admite reembolso en su estado actual ("
                    + compra.getEstado() + ").");
        }
        long horas = horasHastaFuncion(compra);
        boolean canceladaPorCine = funcionCancelada(compra);
        IReembolso estrategia = seleccionarEstrategia(horas, canceladaPorCine);
        double valorReembolso = estrategia.calcularReembolso(compra.getTotal());
        if (valorReembolso <= 0) {
            throw new CinemaUQException("No se permite la cancelación: faltan menos de 2 horas "
                    + "para la función.");
        }

        TarjetaVirtual tarjeta = compra.getCliente() != null
                ? compra.getCliente().getTarjetaVirtual() : null;
        if (tarjeta == null) {
            throw new CinemaUQException("La compra no tiene una tarjeta asociada para el reembolso.");
        }
        tarjeta.setSaldo(tarjeta.getSaldo() + valorReembolso);
        tarjeta.registrarMovimiento(new Movimiento(
                catalogo.siguienteIdMovimiento(), LocalDateTime.now(),
                TipoMovimiento.REEMBOLSO, valorReembolso, tarjeta.getSaldo()));

        compra.cambiarEstado(EstadoCompra.REEMBOLSADA);
        liberarAsientos(compra);

        notificador.notificar("Compra " + compra.getIdCompra() + " reembolsada: $"
                + valorReembolso + " (" + estrategia.getDescripcion() + ")");
        catalogo.guardar();
        return valorReembolso;
    }

    private long horasHastaFuncion(Compra compra) {
        LocalDateTime proxima = null;
        for (Entrada entrada : compra.getEntradas()) {
            Funcion funcion = entrada.getFuncion();
            if (funcion != null && (proxima == null || funcion.getFechaHora().isBefore(proxima))) {
                proxima = funcion.getFechaHora();
            }
        }
        if (proxima == null) {
            return Long.MAX_VALUE;
        }
        return Duration.between(LocalDateTime.now(), proxima).toHours();
    }

    private boolean funcionCancelada(Compra compra) {
        for (Entrada entrada : compra.getEntradas()) {
            Funcion funcion = entrada.getFuncion();
            if (funcion != null && funcion.getEstado() == EstadoFuncion.CANCELADA) {
                return true;
            }
        }
        return false;
    }

    private void liberarAsientos(Compra compra) {
        for (Entrada entrada : compra.getEntradas()) {
            Funcion funcion = entrada.getFuncion();
            if (funcion != null && funcion.getEntradasVendidas() != null) {
                funcion.getEntradasVendidas().remove(entrada);
            }
        }
    }
}
