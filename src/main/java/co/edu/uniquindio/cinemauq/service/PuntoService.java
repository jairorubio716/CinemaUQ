package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.FechaExpiradaException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
import co.edu.uniquindio.cinemauq.model.PuntoAcumulado;
import co.edu.uniquindio.cinemauq.patrones.strategy.IStrategyPuntos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PuntoService {

    public static final int PUNTOS_ENTRADA = 100;
    public static final int PUNTOS_PRODUCTO = 50;
    public static final int PUNTOS_COMBO = 250;

    private final IStrategyPuntos estrategia;

    public PuntoService(IStrategyPuntos estrategia) {
        this.estrategia = estrategia;
    }

    public int acumular(Cliente cliente, double monto) {
        if (cliente == null) {
            throw new CinemaUQException("Se requiere un cliente para acumular puntos.");
        }
        CuentaPuntos cuenta = obtenerOCrearCuenta(cliente);
        int puntos = estrategia.calcularPuntos(monto);
        if (puntos <= 0) {
            return 0;
        }
        LocalDate hoy = LocalDate.now();
        PuntoAcumulado lote = new PuntoAcumulado(
                cuenta.getPuntos().size() + 1, puntos, hoy, hoy.plusYears(1));
        cuenta.agregarPunto(lote);
        return puntos;
    }

    public int totalVigente(Cliente cliente) {
        if (cliente == null || cliente.getCuentaPuntos() == null) {
            return 0;
        }
        return cliente.getCuentaPuntos().totalPuntosVigentes();
    }

    public Map<String, Integer> equivalencias() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        mapa.put("Entrada general", PUNTOS_ENTRADA);
        mapa.put("Producto de confitería", PUNTOS_PRODUCTO);
        mapa.put("Combo personal", PUNTOS_COMBO);
        return mapa;
    }

    /**
     * Redime puntos aplicando FEFO: primero los lotes próximos a vencer (RN-08/RN-18).
     */
    public int redimir(Cliente cliente, int puntosRequeridos) {
        if (cliente == null) {
            throw new CinemaUQException("Se requiere un cliente para redimir puntos.");
        }
        if (puntosRequeridos <= 0) {
            throw new CinemaUQException("La cantidad de puntos a redimir debe ser mayor que cero.");
        }
        CuentaPuntos cuenta = cliente.getCuentaPuntos();
        if (cuenta == null) {
            throw new CinemaUQException("El cliente no tiene cuenta de puntos.");
        }
        List<PuntoAcumulado> vigentes = new ArrayList<>();
        for (PuntoAcumulado punto : cuenta.getPuntos()) {
            if (punto.estaVigente() && punto.getCantidad() > 0) {
                vigentes.add(punto);
            }
        }
        vigentes.sort(Comparator.comparing(PuntoAcumulado::getFechaVencimiento,
                Comparator.nullsLast(Comparator.naturalOrder())));
        int disponibles = 0;
        for (PuntoAcumulado punto : vigentes) {
            disponibles += punto.getCantidad();
        }
        if (disponibles == 0) {
            throw new FechaExpiradaException("No hay puntos vigentes para redimir.");
        }
        if (disponibles < puntosRequeridos) {
            throw new CinemaUQException("Puntos vigentes insuficientes: tiene " + disponibles
                    + " y requiere " + puntosRequeridos + ".");
        }
        int restante = puntosRequeridos;
        for (PuntoAcumulado punto : vigentes) {
            if (restante <= 0) {
                break;
            }
            restante -= punto.consumir(restante);
        }
        return puntosRequeridos;
    }

    private CuentaPuntos obtenerOCrearCuenta(Cliente cliente) {
        if (cliente.getCuentaPuntos() == null) {
            cliente.setCuentaPuntos(new CuentaPuntos("CP-" + cliente.getIdCliente()));
        }
        return cliente.getCuentaPuntos();
    }
}
