package co.edu.uniquindio.cinemauq.patrones.facade;

import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Persona;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.patrones.adapter.PagoAdapter;
import co.edu.uniquindio.cinemauq.patrones.adapter.PasarelaExterna;
import co.edu.uniquindio.cinemauq.patrones.factory.FabricaEntrada;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;
import co.edu.uniquindio.cinemauq.patrones.strategy.PuntosPorValor;
import co.edu.uniquindio.cinemauq.repository.RepositorioDatos;
import co.edu.uniquindio.cinemauq.service.AutenticacionService;
import co.edu.uniquindio.cinemauq.service.CatalogoService;
import co.edu.uniquindio.cinemauq.service.CompraService;
import co.edu.uniquindio.cinemauq.service.PuntoService;
import co.edu.uniquindio.cinemauq.service.RecargaService;
import co.edu.uniquindio.cinemauq.service.ReembolsoService;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Facade: único punto de acceso a la lógica de negocio para controladores/vistas.
 */
public class CinemaFacade {

    private static CinemaFacade instancia;

    private final CatalogoService catalogo;
    private final NotificadorObservable notificador;
    private final AutenticacionService autenticacionService;
    private final CompraService compraService;
    private final PuntoService puntoService;
    private final ReembolsoService reembolsoService;
    private final RecargaService recargaService;

    private CinemaFacade() {
        catalogo = CatalogoService.getInstance();
        catalogo.configurarRepositorio(new RepositorioDatos(Paths.get("datos")));
        catalogo.cargar();

        notificador = new NotificadorObservable();
        autenticacionService = new AutenticacionService(catalogo);
        puntoService = new PuntoService(new PuntosPorValor(1000));
        compraService = new CompraService(catalogo, new FabricaEntrada(),
                puntoService, notificador);
        reembolsoService = new ReembolsoService(catalogo, notificador);
        recargaService = new RecargaService(catalogo,
                new PagoAdapter(new PasarelaExterna()), notificador);
    }

    public static synchronized CinemaFacade getInstance() {
        if (instancia == null) {
            instancia = new CinemaFacade();
        }
        return instancia;
    }

    public CatalogoService getCatalogo() {
        return catalogo;
    }

    public NotificadorObservable getNotificador() {
        return notificador;
    }

    public AutenticacionService getAutenticacionService() {
        return autenticacionService;
    }

    public CompraService getCompraService() {
        return compraService;
    }

    public PuntoService getPuntoService() {
        return puntoService;
    }

    public ReembolsoService getReembolsoService() {
        return reembolsoService;
    }

    public RecargaService getRecargaService() {
        return recargaService;
    }

    public Persona iniciarSesion(String usuario, String contrasena) {
        return autenticacionService.iniciarSesion(usuario, contrasena);
    }

    public Cliente registrarCliente(String nombre, String email, String celular,
                                    String usuario, String contrasena) {
        return autenticacionService.registrarCliente(nombre, email, celular, usuario, contrasena);
    }

    public List<Pelicula> consultarCartelera() {
        return catalogo.peliculasEnCartelera();
    }

    public List<Funcion> consultarFunciones(Pelicula pelicula) {
        return catalogo.funcionesDePelicula(pelicula);
    }

    public List<Asiento> consultarAsientosDisponibles(Funcion funcion) {
        return funcion != null ? funcion.getAsientosDisponibles() : new ArrayList<>();
    }

    public Compra comprar(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                          List<IItemVendible> confiteria, List<Promocion> promociones) {
        return compraService.comprar(cliente, funcion, asientos, confiteria, promociones);
    }

    public double cancelarCompra(Compra compra) {
        return reembolsoService.cancelarCompra(compra);
    }

    public SolicitudRecarga solicitarRecarga(Cliente cliente, double monto) {
        return recargaService.solicitarRecarga(cliente, monto);
    }

    public void aprobarRecarga(Administrador administrador, SolicitudRecarga solicitud) {
        recargaService.aprobarRecarga(administrador, solicitud);
    }

    public void rechazarRecarga(Administrador administrador, SolicitudRecarga solicitud) {
        recargaService.rechazarRecarga(administrador, solicitud);
    }

    public int consultarPuntos(Cliente cliente) {
        return puntoService.totalVigente(cliente);
    }

    public Map<String, Integer> equivalenciasPuntos() {
        return puntoService.equivalencias();
    }

    public int redimirPuntos(Cliente cliente, int puntosRequeridos) {
        return puntoService.redimir(cliente, puntosRequeridos);
    }

    public List<Compra> historialCompras(Cliente cliente) {
        return cliente != null && cliente.getCompras() != null
                ? cliente.getCompras() : new ArrayList<>();
    }

    public List<SolicitudRecarga> solicitudesPendientes() {
        List<SolicitudRecarga> pendientes = new ArrayList<>();
        for (SolicitudRecarga solicitud : catalogo.getSolicitudesRecarga()) {
            if (solicitud.getEstado() == co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga.PENDIENTE) {
                pendientes.add(solicitud);
            }
        }
        return pendientes;
    }

    public void guardarDatos() {
        catalogo.guardar();
    }
}
