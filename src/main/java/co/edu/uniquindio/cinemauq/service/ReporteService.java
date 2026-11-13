package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.PuntoAcumulado;
import co.edu.uniquindio.cinemauq.model.Redencion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ReporteService {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DIA_HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CatalogoService catalogo;

    public ReporteService(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    public Reporte generar(TipoReporte tipo, LocalDate inicio, LocalDate fin) {
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona un tipo de reporte.");
        }
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Selecciona el periodo del reporte.");
        }
        if (fin.isBefore(inicio)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial.");
        }
        return switch (tipo) {
            case RPT_001 -> ventasPorPeriodo(inicio, fin);
            case RPT_002 -> entradasPorPelicula(inicio, fin);
            case RPT_003 -> ocupacionSalas(inicio, fin);
            case RPT_004 -> productosVendidos(inicio, fin);
            case RPT_005 -> recargasRealizadas(inicio, fin);
            case RPT_006 -> cancelacionesReembolsos(inicio, fin);
            case RPT_007 -> puntosPorCliente(inicio, fin);
        };
    }

    private Reporte ventasPorPeriodo(LocalDate inicio, LocalDate fin) {
        Map<LocalDate, VentaDia> porDia = new TreeMap<>();
        double totalVentas = 0;
        double totalConfiteria = 0;
        double totalEntradasValor = 0;
        double totalDescuento = 0;
        int totalEntradas = 0;
        int transacciones = 0;

        for (Compra compra : comprasPagadas(inicio, fin)) {
            LocalDate fecha = compra.getFecha().toLocalDate();
            VentaDia dia = porDia.computeIfAbsent(fecha, clave -> new VentaDia());
            double confiteria = valorConfiteria(compra);
            double entradasValor = valorEntradas(compra);
            dia.compras++;
            dia.entradas += compra.getEntradas().size();
            dia.confiteria += confiteria;
            dia.descuento += compra.getDescuento();
            dia.total += compra.getTotal();

            transacciones++;
            totalEntradas += compra.getEntradas().size();
            totalConfiteria += confiteria;
            totalEntradasValor += entradasValor;
            totalDescuento += compra.getDescuento();
            totalVentas += compra.getTotal();
        }

        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> barras = new LinkedHashMap<>();
        List<Reporte.PuntoLinea> lineas = new ArrayList<>();
        for (Map.Entry<LocalDate, VentaDia> entry : porDia.entrySet()) {
            VentaDia dia = entry.getValue();
            filas.add(List.of(FECHA.format(entry.getKey()), String.valueOf(dia.compras),
                    String.valueOf(dia.entradas), Reporte.moneda(dia.confiteria),
                    Reporte.moneda(dia.descuento), Reporte.moneda(dia.total)));
            String etiqueta = DIA.format(entry.getKey());
            barras.put(etiqueta, dia.total);
            lineas.add(new Reporte.PuntoLinea(etiqueta, dia.total));
        }

        Map<String, Double> pastel = new LinkedHashMap<>();
        agregarSiPositivo(pastel, "Entradas", totalEntradasValor);
        agregarSiPositivo(pastel, "Confitería", totalConfiteria);

        List<Reporte.Metrica> metricas = List.of(
                new Reporte.Metrica("Ventas totales", Reporte.moneda(totalVentas)),
                new Reporte.Metrica("Transacciones", String.valueOf(transacciones)),
                new Reporte.Metrica("Entradas", String.valueOf(totalEntradas)),
                new Reporte.Metrica("Ticket promedio",
                        Reporte.moneda(transacciones == 0 ? 0 : totalVentas / transacciones)));

        return crear(TipoReporte.RPT_001, inicio, fin, metricas,
                List.of("Fecha", "Compras", "Entradas", "Confitería", "Descuento", "Total"),
                filas, limitar(barras, 20), pastel, limitarLineas(lineas, 31));
    }

    private Reporte entradasPorPelicula(LocalDate inicio, LocalDate fin) {
        Map<String, PeliculaVenta> porPelicula = new HashMap<>();
        int totalEntradas = 0;
        double totalIngresos = 0;

        for (Compra compra : comprasPagadas(inicio, fin)) {
            for (Entrada entrada : compra.getEntradas()) {
                Funcion funcion = entrada.getFuncion();
                if (funcion == null || funcion.getPelicula() == null) {
                    continue;
                }
                String titulo = funcion.getPelicula().getTitulo();
                if (titulo == null || titulo.isBlank()) {
                    titulo = "Sin título";
                }
                PeliculaVenta venta = porPelicula.computeIfAbsent(titulo, clave -> new PeliculaVenta());
                venta.entradas++;
                venta.ingresos += entrada.getPrecio();
                if (funcion.getIdFuncion() != null) {
                    venta.funciones.add(funcion.getIdFuncion());
                }
                totalEntradas++;
                totalIngresos += entrada.getPrecio();
            }
        }

        List<Map.Entry<String, PeliculaVenta>> ordenadas = new ArrayList<>(porPelicula.entrySet());
        ordenadas.sort(Comparator.comparingInt((Map.Entry<String, PeliculaVenta> e) ->
                e.getValue().entradas).reversed());

        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> barras = new LinkedHashMap<>();
        Map<String, Double> pastel = new LinkedHashMap<>();
        List<Reporte.PuntoLinea> lineas = new ArrayList<>();
        for (Map.Entry<String, PeliculaVenta> entry : ordenadas) {
            PeliculaVenta venta = entry.getValue();
            filas.add(List.of(entry.getKey(), String.valueOf(venta.funciones.size()),
                    String.valueOf(venta.entradas), Reporte.moneda(venta.ingresos)));
            barras.put(entry.getKey(), (double) venta.entradas);
            pastel.put(entry.getKey(), venta.ingresos);
            lineas.add(new Reporte.PuntoLinea(entry.getKey(), venta.entradas));
        }

        String masVendida = ordenadas.isEmpty() ? "Sin datos" : ordenadas.get(0).getKey();
        List<Reporte.Metrica> metricas = List.of(
                new Reporte.Metrica("Entradas vendidas", String.valueOf(totalEntradas)),
                new Reporte.Metrica("Películas", String.valueOf(porPelicula.size())),
                new Reporte.Metrica("Ingresos", Reporte.moneda(totalIngresos)),
                new Reporte.Metrica("Más vendida", masVendida));

        return crear(TipoReporte.RPT_002, inicio, fin, metricas,
                List.of("Película", "Funciones", "Entradas", "Ingresos"),
                filas, limitar(barras, 15), limitar(pastel, 8), limitarLineas(lineas, 15));
    }

    private Reporte ocupacionSalas(LocalDate inicio, LocalDate fin) {
        Map<String, Integer> vendidasPorFuncion = entradasPorFuncion();
        Map<String, SalaVenta> porSala = new TreeMap<>();
        List<Reporte.PuntoLinea> lineas = new ArrayList<>();
        int totalFunciones = 0;
        int totalVendidas = 0;
        int totalCapacidad = 0;

        for (Funcion funcion : catalogo.getFunciones()) {
            if (funcion == null || funcion.getFechaHora() == null || funcion.getSala() == null
                    || funcion.getEstado() == EstadoFuncion.CANCELADA) {
                continue;
            }
            if (!dentro(funcion.getFechaHora().toLocalDate(), inicio, fin)) {
                continue;
            }
            Sala sala = funcion.getSala();
            int capacidad = sala.getCapacidad();
            if (capacidad <= 0) {
                continue;
            }
            int vendidas = Math.min(capacidad,
                    vendidasPorFuncion.getOrDefault(funcion.getIdFuncion(), 0));
            String nombreSala = "Sala " + sala.getNumeroSala();
            SalaVenta venta = porSala.computeIfAbsent(nombreSala, clave -> new SalaVenta());
            venta.funciones++;
            venta.vendidas += vendidas;
            venta.capacidad += capacidad;

            totalFunciones++;
            totalVendidas += vendidas;
            totalCapacidad += capacidad;
            lineas.add(new Reporte.PuntoLinea(DIA_HORA.format(funcion.getFechaHora()),
                    vendidas * 100.0 / capacidad));
        }

        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> barras = new LinkedHashMap<>();
        for (Map.Entry<String, SalaVenta> entry : porSala.entrySet()) {
            SalaVenta venta = entry.getValue();
            double ocupacion = venta.capacidad == 0 ? 0 : venta.vendidas * 100.0 / venta.capacidad;
            filas.add(List.of(entry.getKey(), String.valueOf(venta.funciones),
                    String.valueOf(venta.vendidas), String.valueOf(venta.capacidad),
                    porcentaje(ocupacion)));
            barras.put(entry.getKey(), ocupacion);
        }

        Map<String, Double> pastel = new LinkedHashMap<>();
        agregarSiPositivo(pastel, "Vendidas", totalVendidas);
        agregarSiPositivo(pastel, "Disponibles", Math.max(0, totalCapacidad - totalVendidas));

        double promedio = totalCapacidad == 0 ? 0 : totalVendidas * 100.0 / totalCapacidad;
        List<Reporte.Metrica> metricas = List.of(
                new Reporte.Metrica("Funciones", String.valueOf(totalFunciones)),
                new Reporte.Metrica("Entradas", String.valueOf(totalVendidas)),
                new Reporte.Metrica("Capacidad", String.valueOf(totalCapacidad)),
                new Reporte.Metrica("Ocupación promedio", porcentaje(promedio)));

        return crear(TipoReporte.RPT_003, inicio, fin, metricas,
                List.of("Sala", "Funciones", "Vendidas", "Capacidad", "Ocupación"),
                filas, barras, pastel, limitarLineas(lineas, 30));
    }

    private Reporte productosVendidos(LocalDate inicio, LocalDate fin) {
        Map<String, ItemVenta> porItem = new HashMap<>();
        int totalUnidades = 0;
        double totalIngresos = 0;

        for (Compra compra : comprasPagadas(inicio, fin)) {
            for (IItemVendible item : compra.getConfiteria()) {
                String nombre = item.descripcion() == null || item.descripcion().isBlank()
                        ? item.getId() : item.descripcion();
                ItemVenta venta = porItem.computeIfAbsent(nombre, clave -> new ItemVenta());
                venta.unidades++;
                venta.ingresos += item.obtenerPrecio();
                totalUnidades++;
                totalIngresos += item.obtenerPrecio();
            }
        }

        List<Map.Entry<String, ItemVenta>> ordenadas = new ArrayList<>(porItem.entrySet());
        ordenadas.sort(Comparator.comparingDouble((Map.Entry<String, ItemVenta> e) ->
                e.getValue().ingresos).reversed());

        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> barras = new LinkedHashMap<>();
        Map<String, Double> pastel = new LinkedHashMap<>();
        List<Reporte.PuntoLinea> lineas = new ArrayList<>();
        for (Map.Entry<String, ItemVenta> entry : ordenadas) {
            ItemVenta venta = entry.getValue();
            double participacion = totalIngresos == 0 ? 0 : venta.ingresos * 100.0 / totalIngresos;
            filas.add(List.of(entry.getKey(), String.valueOf(venta.unidades),
                    Reporte.moneda(venta.ingresos), porcentaje(participacion)));
            barras.put(entry.getKey(), (double) venta.unidades);
            pastel.put(entry.getKey(), venta.ingresos);
            lineas.add(new Reporte.PuntoLinea(entry.getKey(), venta.unidades));
        }

        String masVendido = ordenadas.isEmpty() ? "Sin datos" : ordenadas.get(0).getKey();
        List<Reporte.Metrica> metricas = List.of(
                new Reporte.Metrica("Unidades vendidas", String.valueOf(totalUnidades)),
                new Reporte.Metrica("Referencias", String.valueOf(porItem.size())),
                new Reporte.Metrica("Ingresos", Reporte.moneda(totalIngresos)),
                new Reporte.Metrica("Más vendido", masVendido));

        return crear(TipoReporte.RPT_004, inicio, fin, metricas,
                List.of("Producto / combo", "Unidades", "Ingresos", "Participación"),
                filas, limitar(barras, 15), limitar(pastel, 8), limitarLineas(lineas, 15));
    }

    private Reporte recargasRealizadas(LocalDate inicio, LocalDate fin) {
        Map<String, Cliente> clientesPorTarjeta = new HashMap<>();
        for (Cliente cliente : catalogo.getClientes()) {
            TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
            if (tarjeta != null && tarjeta.getNroTarjeta() != null) {
                clientesPorTarjeta.put(tarjeta.getNroTarjeta(), cliente);
            }
        }

        List<RecargaVenta> recargas = new ArrayList<>();
        double total = 0;
        Set<String> clientes = new HashSet<>();
        for (SolicitudRecarga solicitud : catalogo.getSolicitudesRecarga()) {
            if (solicitud.getEstado() != EstadoSolicitudRecarga.APROBADA
                    || solicitud.getFecha() == null
                    || !dentro(solicitud.getFecha().toLocalDate(), inicio, fin)) {
                continue;
            }
            String nombre = nombreCliente(solicitud, clientesPorTarjeta);
            String tarjeta = solicitud.getTarjeta() == null ? "Sin tarjeta"
                    : solicitud.getTarjeta().getNroTarjeta();
            recargas.add(new RecargaVenta(nombre, solicitud.getFecha(), tarjeta, solicitud.getMonto()));
            total += solicitud.getMonto();
            clientes.add(nombre);
        }
        recargas.sort(Comparator.comparing(RecargaVenta::fecha));

        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> porCliente = new LinkedHashMap<>();
        Map<String, Double> porDia = new TreeMap<>();
        for (RecargaVenta recarga : recargas) {
            filas.add(List.of(recarga.cliente(), FECHA.format(recarga.fecha().toLocalDate()),
                    recarga.tarjeta(), Reporte.moneda(recarga.monto())));
            porCliente.merge(recarga.cliente(), recarga.monto(), Double::sum);
            porDia.merge(DIA.format(recarga.fecha().toLocalDate()), recarga.monto(), Double::sum);
        }

        Map<String, Double> barras = new LinkedHashMap<>(porCliente);
        List<Reporte.PuntoLinea> lineas = new ArrayList<>();
        for (Map.Entry<String, Double> entry : porDia.entrySet()) {
            lineas.add(new Reporte.PuntoLinea(entry.getKey(), entry.getValue()));
        }

        List<Reporte.Metrica> metricas = List.of(
                new Reporte.Metrica("Total recargado", Reporte.moneda(total)),
                new Reporte.Metrica("Recargas", String.valueOf(recargas.size())),
                new Reporte.Metrica("Clientes", String.valueOf(clientes.size())),
                new Reporte.Metrica("Promedio",
                        Reporte.moneda(recargas.isEmpty() ? 0 : total / recargas.size())));

        return crear(TipoReporte.RPT_005, inicio, fin, metricas,
                List.of("Cliente", "Fecha", "Tarjeta", "Monto"),
                filas, limitar(barras, 15), limitar(porCliente, 8), limitarLineas(lineas, 31));
    }

    private Reporte cancelacionesReembolsos(LocalDate inicio, LocalDate fin) {
        List<Compra> afectadas = new ArrayList<>();
        for (Compra compra : catalogo.getCompras()) {
            if (compra.getFecha() == null
                    || (compra.getEstado() != EstadoCompra.CANCELADA
                    && compra.getEstado() != EstadoCompra.REEMBOLSADA)) {
                continue;
            }
            if (dentro(compra.getFecha().toLocalDate(), inicio, fin)) {
                afectadas.add(compra);
            }
        }
        afectadas.sort(Comparator.comparing(Compra::getFecha));

        double totalReembolsado = 0;
        int reembolsos = 0;
        int cancelaciones = 0;
        int entradas = 0;
        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> porDia = new TreeMap<>();
        Map<String, Double> porEstado = new LinkedHashMap<>();

        for (Compra compra : afectadas) {
            String cliente = compra.getCliente() == null ? "Sin cliente"
                    : compra.getCliente().getNombre();
            String estado = compra.getEstado() == EstadoCompra.REEMBOLSADA
                    ? "Reembolsada" : "Cancelada";
            filas.add(List.of(cliente, compra.getIdCompra(),
                    FECHA.format(compra.getFecha().toLocalDate()), estado,
                    String.valueOf(compra.getEntradas().size()), Reporte.moneda(compra.getTotal())));
            entradas += compra.getEntradas().size();
            if (compra.getEstado() == EstadoCompra.REEMBOLSADA) {
                totalReembolsado += compra.getTotal();
                reembolsos++;
            } else {
                cancelaciones++;
            }
            porDia.merge(DIA.format(compra.getFecha().toLocalDate()), compra.getTotal(), Double::sum);
            porEstado.merge(estado, compra.getTotal(), Double::sum);
        }

        Map<String, Double> barras = new LinkedHashMap<>(porDia);
        List<Reporte.PuntoLinea> lineas = new ArrayList<>();
        for (Map.Entry<String, Double> entry : porDia.entrySet()) {
            lineas.add(new Reporte.PuntoLinea(entry.getKey(), entry.getValue()));
        }

        List<Reporte.Metrica> metricas = List.of(
                new Reporte.Metrica("Total reembolsado", Reporte.moneda(totalReembolsado)),
                new Reporte.Metrica("Reembolsos", String.valueOf(reembolsos)),
                new Reporte.Metrica("Cancelaciones", String.valueOf(cancelaciones)),
                new Reporte.Metrica("Promedio reembolso",
                        Reporte.moneda(reembolsos == 0 ? 0 : totalReembolsado / reembolsos)));

        return crear(TipoReporte.RPT_006, inicio, fin, metricas,
                List.of("Cliente", "Compra", "Fecha", "Estado", "Entradas", "Total"),
                filas, limitar(barras, 20), limitar(porEstado, 8), limitarLineas(lineas, 31));
    }

    private Reporte puntosPorCliente(LocalDate inicio, LocalDate fin) {
        Map<String, Integer> redimidosPeriodo = new HashMap<>();
        Map<String, Integer> redimidosHistoricos = new HashMap<>();
        Map<String, Double> acumuladosPorDia = new TreeMap<>();

        for (Redencion redencion : catalogo.getRedenciones()) {
            if (redencion.getIdCliente() == null) {
                continue;
            }
            redimidosHistoricos.merge(redencion.getIdCliente(), redencion.getPuntos(), Integer::sum);
            if (redencion.getFecha() != null
                    && dentro(redencion.getFecha().toLocalDate(), inicio, fin)) {
                redimidosPeriodo.merge(redencion.getIdCliente(), redencion.getPuntos(), Integer::sum);
            }
        }

        int totalVigentes = 0;
        int totalAcumulados = 0;
        int totalRedimidos = 0;
        int clientesConMovimiento = 0;
        List<ClientePuntos> clientes = new ArrayList<>();

        for (Cliente cliente : catalogo.getClientes()) {
            CuentaPuntos cuenta = cliente.getCuentaPuntos();
            int vigentes = cuenta == null ? 0 : cuenta.totalPuntosVigentes();
            int acumulados = 0;
            if (cuenta != null) {
                for (PuntoAcumulado punto : cuenta.getPuntos()) {
                    if (punto.getFechaObtencion() != null
                            && dentro(punto.getFechaObtencion(), inicio, fin)) {
                        acumulados += punto.getCantidad();
                        acumuladosPorDia.merge(DIA.format(punto.getFechaObtencion()),
                                (double) punto.getCantidad(), Double::sum);
                    }
                }
            }
            int redimidos = redimidosPeriodo.getOrDefault(cliente.getIdCliente(), 0);
            int historicos = redimidosHistoricos.getOrDefault(cliente.getIdCliente(), 0);
            if (acumulados > 0 || redimidos > 0 || vigentes > 0) {
                clientesConMovimiento++;
            }
            clientes.add(new ClientePuntos(cliente.getNombre(), vigentes, acumulados,
                    redimidos, historicos));
            totalVigentes += vigentes;
            totalAcumulados += acumulados;
            totalRedimidos += redimidos;
        }

        clientes.sort(Comparator.comparingInt((ClientePuntos c) ->
                c.vigentes() + c.redimidosPeriodo()).reversed());

        List<List<String>> filas = new ArrayList<>();
        Map<String, Double> barras = new LinkedHashMap<>();
        Map<String, Double> pastel = new LinkedHashMap<>();
        for (ClientePuntos cliente : clientes) {
            filas.add(List.of(cliente.nombre(), String.valueOf(cliente.vigentes()),
                    String.valueOf(cliente.acumuladosPeriodo()),
                    String.valueOf(cliente.redimidosPeriodo()),
                    String.valueOf(cliente.redimidosHistoricos())));
            if (cliente.acumuladosPeriodo() > 0) {
                barras.put(cliente.nombre(), (double) cliente.acumuladosPeriodo());
            }
            if (cliente.vigentes() > 0) {
                pastel.merge("Vigentes", (double) cliente.vigentes(), Double::sum);
            }
        }
        agregarSiPositivo(pastel, "Redimidos en el periodo", totalRedimidos);

        List<Reporte.PuntoLinea> lineas = new ArrayList<>();
        for (Map.Entry<String, Double> entry : acumuladosPorDia.entrySet()) {
            lineas.add(new Reporte.PuntoLinea(entry.getKey(), entry.getValue()));
        }

        List<Reporte.Metrica> metricas = List.of(
                new Reporte.Metrica("Puntos vigentes", String.valueOf(totalVigentes)),
                new Reporte.Metrica("Acumulados en periodo", String.valueOf(totalAcumulados)),
                new Reporte.Metrica("Redimidos en periodo", String.valueOf(totalRedimidos)),
                new Reporte.Metrica("Clientes con movimientos",
                        String.valueOf(clientesConMovimiento)));

        return crear(TipoReporte.RPT_007, inicio, fin, metricas,
                List.of("Cliente", "Puntos vigentes", "Acumulados periodo",
                        "Redimidos periodo", "Redimidos histórico"),
                filas, limitar(barras, 15), limitar(pastel, 8), limitarLineas(lineas, 31));
    }

    private Reporte crear(TipoReporte tipo, LocalDate inicio, LocalDate fin,
                          List<Reporte.Metrica> metricas, List<String> columnas,
                          List<List<String>> filas, Map<String, Double> barras,
                          Map<String, Double> pastel, List<Reporte.PuntoLinea> lineas) {
        return new Reporte(tipo, inicio, fin, LocalDateTime.now(), metricas, columnas, filas,
                barras, pastel, lineas);
    }

    private List<Compra> comprasPagadas(LocalDate inicio, LocalDate fin) {
        return comprasPorEstado(EstadoCompra.PAGADA, inicio, fin);
    }

    private List<Compra> comprasPorEstado(EstadoCompra estado, LocalDate inicio, LocalDate fin) {
        List<Compra> resultado = new ArrayList<>();
        for (Compra compra : catalogo.getCompras()) {
            if (compra.getEstado() == estado && compra.getFecha() != null
                    && dentro(compra.getFecha().toLocalDate(), inicio, fin)) {
                resultado.add(compra);
            }
        }
        resultado.sort(Comparator.comparing(Compra::getFecha));
        return resultado;
    }

    private Map<String, Integer> entradasPorFuncion() {
        Map<String, Integer> vendidas = new HashMap<>();
        for (Compra compra : catalogo.getCompras()) {
            if (compra.getEstado() != EstadoCompra.PAGADA) {
                continue;
            }
            for (Entrada entrada : compra.getEntradas()) {
                if (entrada.getFuncion() != null && entrada.getFuncion().getIdFuncion() != null) {
                    vendidas.merge(entrada.getFuncion().getIdFuncion(), 1, Integer::sum);
                }
            }
        }
        return vendidas;
    }

    private double valorConfiteria(Compra compra) {
        double total = 0;
        for (IItemVendible item : compra.getConfiteria()) {
            total += item.obtenerPrecio();
        }
        return total;
    }

    private double valorEntradas(Compra compra) {
        double total = 0;
        for (Entrada entrada : compra.getEntradas()) {
            total += entrada.getPrecio();
        }
        return total;
    }

    private String nombreCliente(SolicitudRecarga solicitud,
                                 Map<String, Cliente> clientesPorTarjeta) {
        if (solicitud.getTarjeta() == null || solicitud.getTarjeta().getNroTarjeta() == null) {
            return "Cliente no identificado";
        }
        Cliente cliente = clientesPorTarjeta.get(solicitud.getTarjeta().getNroTarjeta());
        return cliente == null ? "Cliente no identificado" : cliente.getNombre();
    }

    private boolean dentro(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        return fecha != null && !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

    private String porcentaje(double valor) {
        return String.format(COLOMBIA, "%.1f%%", valor);
    }

    private void agregarSiPositivo(Map<String, Double> mapa, String etiqueta, double valor) {
        if (valor > 0) {
            mapa.put(etiqueta, valor);
        }
    }

    private Map<String, Double> limitar(Map<String, Double> datos, int maximo) {
        if (datos.size() <= maximo) {
            return new LinkedHashMap<>(datos);
        }
        Map<String, Double> resultado = new LinkedHashMap<>();
        int contador = 0;
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            if (contador++ >= maximo) {
                break;
            }
            resultado.put(entry.getKey(), entry.getValue());
        }
        return resultado;
    }

    private List<Reporte.PuntoLinea> limitarLineas(List<Reporte.PuntoLinea> lineas, int maximo) {
        if (lineas.size() <= maximo) {
            return lineas;
        }
        return new ArrayList<>(lineas.subList(lineas.size() - maximo, lineas.size()));
    }

    private static final class VentaDia {
        private int compras;
        private int entradas;
        private double confiteria;
        private double descuento;
        private double total;
    }

    private static final class PeliculaVenta {
        private final Set<String> funciones = new HashSet<>();
        private int entradas;
        private double ingresos;
    }

    private static final class SalaVenta {
        private int funciones;
        private int vendidas;
        private int capacidad;
    }

    private static final class ItemVenta {
        private int unidades;
        private double ingresos;
    }

    private record RecargaVenta(String cliente, LocalDateTime fecha, String tarjeta, double monto) {
    }

    private record ClientePuntos(String nombre, int vigentes, int acumuladosPeriodo,
                                 int redimidosPeriodo, int redimidosHistoricos) {
    }
}
