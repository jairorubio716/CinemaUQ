package co.edu.uniquindio.cinemauq.patrones.facade;

import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Persona;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Redencion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.model.TipoPromocion;
import co.edu.uniquindio.cinemauq.patrones.adapter.PagoAdapter;
import co.edu.uniquindio.cinemauq.patrones.adapter.PasarelaExterna;
import co.edu.uniquindio.cinemauq.patrones.factory.FabricaEntrada;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;
import co.edu.uniquindio.cinemauq.patrones.strategy.PuntosPorValor;
import co.edu.uniquindio.cinemauq.repository.RepositorioDatos;
import co.edu.uniquindio.cinemauq.seed.SeedData;
import co.edu.uniquindio.cinemauq.service.AutenticacionService;
import co.edu.uniquindio.cinemauq.service.CatalogoAdminService;
import co.edu.uniquindio.cinemauq.service.CatalogoService;
import co.edu.uniquindio.cinemauq.service.CompraService;
import co.edu.uniquindio.cinemauq.service.CotizacionCompra;
import co.edu.uniquindio.cinemauq.service.FuncionService;
import co.edu.uniquindio.cinemauq.service.PuntoService;
import co.edu.uniquindio.cinemauq.service.RecargaService;
import co.edu.uniquindio.cinemauq.service.ReembolsoService;
import co.edu.uniquindio.cinemauq.service.Reporte;
import co.edu.uniquindio.cinemauq.service.ReporteService;
import co.edu.uniquindio.cinemauq.service.TipoReporte;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final CatalogoAdminService catalogoAdminService;
    private final FuncionService funcionService;
    private final ReporteService reporteService;

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
        catalogoAdminService = new CatalogoAdminService(catalogo);
        funcionService = new FuncionService(catalogo, reembolsoService);
        reporteService = new ReporteService(catalogo);
        SeedData.inicializar(catalogo, catalogoAdminService, autenticacionService);
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

    public CatalogoAdminService getCatalogoAdminService() {
        return catalogoAdminService;
    }

    public FuncionService getFuncionService() {
        return funcionService;
    }

    public List<Pelicula> getPeliculas() {
        return new ArrayList<>(catalogo.getPeliculas());
    }

    public List<Sala> getSalas() {
        return new ArrayList<>(catalogo.getSalas());
    }

    public List<Funcion> getFunciones() {
        return new ArrayList<>(catalogo.getFunciones());
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(catalogo.getProductos());
    }

    public List<Combo> getCombos() {
        return new ArrayList<>(catalogo.getCombos());
    }

    public List<Promocion> getPromociones() {
        return new ArrayList<>(catalogo.getPromociones());
    }

    public List<SolicitudRecarga> getSolicitudesRecarga() {
        return new ArrayList<>(catalogo.getSolicitudesRecarga());
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(catalogo.getClientes());
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

    public CotizacionCompra cotizarCompra(Funcion funcion, List<Asiento> asientos,
                                          List<IItemVendible> confiteria,
                                          List<Promocion> promociones) {
        return compraService.cotizar(funcion, asientos, confiteria, promociones);
    }

    public int calcularPuntos(double monto) {
        return puntoService.calcularPuntos(monto);
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

    public int redimirBeneficio(Cliente cliente, String beneficio) {
        int puntos = puntoService.redimirBeneficio(cliente, beneficio);
        Redencion redencion = new Redencion(
                "RED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "BEN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                beneficio, puntos, LocalDateTime.now(), cliente.getIdCliente());
        catalogo.agregarRedencion(redencion);
        catalogo.guardar();
        return puntos;
    }

    public List<Redencion> getRedenciones(Cliente cliente) {
        if (cliente == null) {
            return new ArrayList<>();
        }
        return catalogo.redencionesDeCliente(cliente.getIdCliente());
    }

    public boolean puedeRedimir(Cliente cliente, String beneficio) {
        return puntoService.puedeRedimir(cliente, beneficio);
    }

    public Pelicula crearPelicula(Administrador admin, String titulo, String genero, int duracionMin,
                                  Clasificacion clasificacion, String sinopsis, String poster,
                                  boolean enCartelera) {
        return catalogoAdminService.crearPelicula(admin, titulo, genero, duracionMin,
                clasificacion, sinopsis, poster, enCartelera);
    }

    public void actualizarPelicula(Administrador admin, Pelicula pelicula) {
        catalogoAdminService.actualizarPelicula(admin, pelicula);
    }

    public void actualizarSala(Administrador admin, Sala sala) {
        catalogoAdminService.actualizarSala(admin, sala);
    }

    public void actualizarFuncion(Administrador admin, Funcion funcion) {
        catalogoAdminService.actualizarFuncion(admin, funcion);
    }

    public void actualizarProducto(Administrador admin, Producto producto) {
        catalogoAdminService.actualizarProducto(admin, producto);
    }

    public void actualizarCombo(Administrador admin, Combo combo) {
        catalogoAdminService.actualizarCombo(admin, combo);
    }

    public void actualizarPromocion(Administrador admin, Promocion promocion) {
        catalogoAdminService.actualizarPromocion(admin, promocion);
    }

    public void eliminarPelicula(Administrador admin, String idPelicula) {
        catalogoAdminService.eliminarPelicula(admin, idPelicula);
    }

    public Sala crearSala(Administrador admin, int numeroSala, int filas, int columnas,
                          String tipoPantalla) {
        return catalogoAdminService.crearSala(admin, numeroSala, filas, columnas, tipoPantalla);
    }

    public void eliminarSala(Administrador admin, int idSala) {
        catalogoAdminService.eliminarSala(admin, idSala);
    }

    public Funcion crearFuncion(Administrador admin, Pelicula pelicula, Sala sala,
                                LocalDateTime fechaHora, double precioEntrada, EstadoFuncion estado) {
        return catalogoAdminService.crearFuncion(admin, pelicula, sala, fechaHora, precioEntrada, estado);
    }

    public void eliminarFuncion(Administrador admin, String idFuncion) {
        catalogoAdminService.eliminarFuncion(admin, idFuncion);
    }

    public double cancelarFuncion(Administrador admin, Funcion funcion) {
        return funcionService.cancelarFuncion(admin, funcion);
    }

    public void activarVentaFuncion(Administrador admin, Funcion funcion) {
        funcionService.activarVenta(admin, funcion);
    }

    public Producto crearProducto(Administrador admin, String nombre, double precio, TipoProducto tipo) {
        return catalogoAdminService.crearProducto(admin, nombre, precio, tipo);
    }

    public void eliminarProducto(Administrador admin, String idProducto) {
        catalogoAdminService.eliminarProducto(admin, idProducto);
    }

    public Combo crearCombo(Administrador admin, String nombre, List<IItemVendible> items) {
        return catalogoAdminService.crearCombo(admin, nombre, items);
    }

    public void eliminarCombo(Administrador admin, String idCombo) {
        catalogoAdminService.eliminarCombo(admin, idCombo);
    }

    public Promocion crearPromocion(Administrador admin, String nombre, TipoPromocion tipo,
                                    double porcentajeOValor, LocalDate inicio, LocalDate fin) {
        return catalogoAdminService.crearPromocion(admin, nombre, tipo, porcentajeOValor, inicio, fin);
    }

    public void eliminarPromocion(Administrador admin, String idPromocion) {
        catalogoAdminService.eliminarPromocion(admin, idPromocion);
    }

    public List<Cliente> consultarClientes(Administrador admin) {
        return catalogoAdminService.consultarClientes(admin);
    }

    public List<Compra> consultarCompras(Administrador admin) {
        return catalogoAdminService.consultarCompras(admin);
    }

    public List<Entrada> consultarEntradasVendidas(Administrador admin) {
        return catalogoAdminService.consultarEntradasVendidas(admin);
    }

    public Reporte generarReporte(Administrador admin, TipoReporte tipo,
                                  LocalDate inicio, LocalDate fin) {
        if (admin == null) {
            throw new IllegalArgumentException("Solo un administrador puede generar reportes.");
        }
        return reporteService.generar(tipo, inicio, fin);
    }

    public void guardarDatos() {
        catalogo.guardar();
    }
}
