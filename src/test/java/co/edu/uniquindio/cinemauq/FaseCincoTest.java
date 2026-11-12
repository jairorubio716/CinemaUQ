package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.FuncionNoVendibleException;
import co.edu.uniquindio.cinemauq.excepciones.MontoInvalidoException;
import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.CategoriaAsiento;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.PuntoAcumulado;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.patrones.adapter.PagoAdapter;
import co.edu.uniquindio.cinemauq.patrones.adapter.PasarelaExterna;
import co.edu.uniquindio.cinemauq.patrones.factory.FabricaEntrada;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;
import co.edu.uniquindio.cinemauq.patrones.strategy.PuntosPorValor;
import co.edu.uniquindio.cinemauq.service.CatalogoAdminService;
import co.edu.uniquindio.cinemauq.service.CatalogoService;
import co.edu.uniquindio.cinemauq.service.CompraService;
import co.edu.uniquindio.cinemauq.service.FuncionService;
import co.edu.uniquindio.cinemauq.service.PuntoService;
import co.edu.uniquindio.cinemauq.service.RecargaService;
import co.edu.uniquindio.cinemauq.service.ReembolsoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaseCincoTest {

    private CatalogoService catalogo;
    private NotificadorObservable notificador;
    private CompraService compraService;
    private PuntoService puntoService;
    private ReembolsoService reembolsoService;
    private RecargaService recargaService;
    private FuncionService funcionService;

    @BeforeEach
    void setUp() {
        catalogo = CatalogoService.getInstance();
        catalogo.setPeliculas(new ArrayList<>());
        catalogo.setSalas(new ArrayList<>());
        catalogo.setFunciones(new ArrayList<>());
        catalogo.setClientes(new ArrayList<>());
        catalogo.setAdministradores(new ArrayList<>());
        catalogo.setProductos(new ArrayList<>());
        catalogo.setCombos(new ArrayList<>());
        catalogo.setPromociones(new ArrayList<>());
        catalogo.setCompras(new ArrayList<>());
        catalogo.setSolicitudesRecarga(new ArrayList<>());
        catalogo.setSecuenciaMovimiento(1);

        notificador = new NotificadorObservable();
        puntoService = new PuntoService(new PuntosPorValor(1000));
        compraService = new CompraService(catalogo, new FabricaEntrada(), puntoService, notificador);
        reembolsoService = new ReembolsoService(catalogo, notificador);
        recargaService = new RecargaService(catalogo, new PagoAdapter(new PasarelaExterna()), notificador);
        funcionService = new FuncionService(catalogo, reembolsoService);
    }

    @Test
    void noPermiteVenderElMismoAsientoDosVeces() {
        Funcion funcion = crearFuncion(EstadoFuncion.EN_VENTA, 10000, LocalDateTime.now().plusDays(3));
        Cliente cliente = crearCliente(50000);
        Asiento asiento = funcion.getSala().getAsientos().get(0);

        compraService.comprar(cliente, funcion, List.of(asiento), List.of(), List.of());

        assertThrows(AsientoNoDisponibleException.class,
                () -> compraService.comprar(cliente, funcion, List.of(asiento), List.of(), List.of()));
    }

    @Test
    void noPermiteComprarSinSaldoSuficiente() {
        Funcion funcion = crearFuncion(EstadoFuncion.EN_VENTA, 10000, LocalDateTime.now().plusDays(3));
        Cliente cliente = crearCliente(1000);
        Asiento asiento = funcion.getSala().getAsientos().get(0);

        assertThrows(SaldoInsuficienteException.class,
                () -> compraService.comprar(cliente, funcion, List.of(asiento), List.of(), List.of()));
    }

    @Test
    void noPermiteVenderFuncionNoVendible() {
        Funcion funcion = crearFuncion(EstadoFuncion.PROGRAMADA, 10000, LocalDateTime.now().plusDays(3));
        Cliente cliente = crearCliente(50000);
        Asiento asiento = funcion.getSala().getAsientos().get(0);

        assertThrows(FuncionNoVendibleException.class,
                () -> compraService.comprar(cliente, funcion, List.of(asiento), List.of(), List.of()));
    }

    @Test
    void cancelarFuncionReembolsaElCienPorCiento() {
        Funcion funcion = crearFuncion(EstadoFuncion.EN_VENTA, 10000, LocalDateTime.now().plusDays(3));
        Cliente cliente = crearCliente(50000);
        Asiento asiento = funcion.getSala().getAsientos().get(0);
        Compra compra = compraService.comprar(cliente, funcion, List.of(asiento), List.of(), List.of());
        assertEquals(40000, cliente.getTarjetaVirtual().getSaldo(), 0.01);

        double reembolso = funcionService.cancelarFuncion(crearAdmin(), funcion);

        assertEquals(10000, reembolso, 0.01);
        assertEquals(50000, cliente.getTarjetaVirtual().getSaldo(), 0.01);
        assertEquals(EstadoCompra.REEMBOLSADA, compra.getEstado());
        assertEquals(EstadoFuncion.CANCELADA, funcion.getEstado());
        assertTrue(funcion.estaDisponible(asiento));
    }

    @Test
    void redimePuntosAplicandoFEFO() {
        Cliente cliente = crearCliente(0);
        CuentaPuntos cuenta = cliente.getCuentaPuntos();
        LocalDate hoy = LocalDate.now();
        PuntoAcumulado vencePrimero = new PuntoAcumulado(1, 60, hoy.minusDays(300), hoy.plusDays(20));
        PuntoAcumulado venceDespues = new PuntoAcumulado(2, 100, hoy.minusDays(10), hoy.plusDays(300));
        cuenta.agregarPunto(vencePrimero);
        cuenta.agregarPunto(venceDespues);

        int usados = puntoService.redimirBeneficio(cliente, "ENTRADA");

        assertEquals(100, usados);
        assertEquals(0, vencePrimero.getCantidad());
        assertEquals(60, venceDespues.getCantidad());
        assertEquals(60, puntoService.totalVigente(cliente));
    }

    @Test
    void recargaExigeMontoPositivoYAprobacionDeAdmin() {
        Cliente cliente = crearCliente(0);
        assertThrows(MontoInvalidoException.class, () -> recargaService.solicitarRecarga(cliente, 0));

        SolicitudRecarga solicitud = recargaService.solicitarRecarga(cliente, 20000);
        assertEquals(EstadoSolicitudRecarga.PENDIENTE, solicitud.getEstado());
        recargaService.aprobarRecarga(crearAdmin(), solicitud);

        assertEquals(EstadoSolicitudRecarga.APROBADA, solicitud.getEstado());
        assertEquals(20000, cliente.getTarjetaVirtual().getSaldo(), 0.01);
    }

    @Test
    void gestionDeCatalogoRequiereAdministrador() {
        CatalogoAdminService adminService = new CatalogoAdminService(catalogo);
        assertThrows(CinemaUQException.class, () -> adminService.crearPelicula(
                null, "Titulo", "Genero", 100, Clasificacion.R, "", "", true));
    }

    private Cliente crearCliente(double saldo) {
        int n = catalogo.getClientes().size() + 1;
        Cliente cliente = new Cliente("Cliente " + n, "cliente" + n + "@uq.edu.co",
                "3000000000", "cliente" + n, "1234", "CLI-" + n, LocalDate.now());
        cliente.setTarjetaVirtual(new TarjetaVirtual("TV-CLI-" + n, saldo, EstadoTarjeta.ACTIVA));
        cliente.setCuentaPuntos(new CuentaPuntos("CP-CLI-" + n));
        catalogo.agregarCliente(cliente);
        return cliente;
    }

    private Administrador crearAdmin() {
        Administrador admin = new Administrador("Admin", "admin@uq.edu.co", "3000000000",
                "admin", "1234", "ADM-1");
        catalogo.agregarAdministrador(admin);
        return admin;
    }

    private Funcion crearFuncion(EstadoFuncion estado, double precio, LocalDateTime fechaHora) {
        Pelicula pelicula = new Pelicula("PEL-1", "Pelicula de prueba", "Acción", 110,
                Clasificacion.R, "Sinopsis", "", true);
        catalogo.agregarPelicula(pelicula);
        Sala sala = new Sala(1, 1, 2, 2, "2D");
        int id = 1;
        for (int fila = 0; fila < 2; fila++) {
            for (int columna = 1; columna <= 2; columna++) {
                sala.agregarAsiento(new Asiento(id++, String.valueOf((char) ('A' + fila)),
                        columna, columna, CategoriaAsiento.GENERAL));
            }
        }
        catalogo.agregarSala(sala);
        Funcion funcion = new Funcion("FUN-1", fechaHora, precio, estado, pelicula, sala);
        catalogo.agregarFuncion(funcion);
        return funcion;
    }
}
