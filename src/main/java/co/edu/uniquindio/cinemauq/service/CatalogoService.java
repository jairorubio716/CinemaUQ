package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Redencion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.repository.DatosCinema;
import co.edu.uniquindio.cinemauq.repository.RepositorioDatos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Singleton: única instancia del catálogo y punto central de persistencia.
 */
public class CatalogoService {

    private static CatalogoService instancia;

    private List<Pelicula> peliculas = new ArrayList<>();
    private List<Sala> salas = new ArrayList<>();
    private List<Funcion> funciones = new ArrayList<>();
    private List<Cliente> clientes = new ArrayList<>();
    private List<Administrador> administradores = new ArrayList<>();
    private List<Producto> productos = new ArrayList<>();
    private List<Combo> combos = new ArrayList<>();
    private List<Promocion> promociones = new ArrayList<>();
    private List<Compra> compras = new ArrayList<>();
    private List<SolicitudRecarga> solicitudesRecarga = new ArrayList<>();
    private List<Redencion> redenciones = new ArrayList<>();
    private int secuenciaMovimiento = 1;

    private RepositorioDatos repositorio;

    private CatalogoService() {
    }

    public static synchronized CatalogoService getInstance() {
        if (instancia == null) {
            instancia = new CatalogoService();
        }
        return instancia;
    }

    public void configurarRepositorio(RepositorioDatos repositorio) {
        this.repositorio = repositorio;
    }

    public RepositorioDatos getRepositorio() {
        return repositorio;
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas != null ? peliculas : new ArrayList<>();
    }

    public List<Sala> getSalas() {
        return salas;
    }

    public void setSalas(List<Sala> salas) {
        this.salas = salas != null ? salas : new ArrayList<>();
    }

    public List<Funcion> getFunciones() {
        return funciones;
    }

    public void setFunciones(List<Funcion> funciones) {
        this.funciones = funciones != null ? funciones : new ArrayList<>();
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes != null ? clientes : new ArrayList<>();
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public void setAdministradores(List<Administrador> administradores) {
        this.administradores = administradores != null ? administradores : new ArrayList<>();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
    }

    public List<Combo> getCombos() {
        return combos;
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos != null ? combos : new ArrayList<>();
    }

    public List<Promocion> getPromociones() {
        return promociones;
    }

    public void setPromociones(List<Promocion> promociones) {
        this.promociones = promociones != null ? promociones : new ArrayList<>();
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras != null ? compras : new ArrayList<>();
    }

    public List<SolicitudRecarga> getSolicitudesRecarga() {
        return solicitudesRecarga;
    }

    public void setSolicitudesRecarga(List<SolicitudRecarga> solicitudesRecarga) {
        this.solicitudesRecarga = solicitudesRecarga != null ? solicitudesRecarga : new ArrayList<>();
    }

    public List<Redencion> getRedenciones() {
        return redenciones;
    }

    public void setRedenciones(List<Redencion> redenciones) {
        this.redenciones = redenciones != null ? redenciones : new ArrayList<>();
    }

    public int getSecuenciaMovimiento() {
        return secuenciaMovimiento;
    }

    public void setSecuenciaMovimiento(int secuenciaMovimiento) {
        this.secuenciaMovimiento = Math.max(1, secuenciaMovimiento);
    }

    public synchronized int siguienteIdMovimiento() {
        return secuenciaMovimiento++;
    }

    public void agregarPelicula(Pelicula pelicula) {
        if (pelicula != null && !peliculas.contains(pelicula)) {
            peliculas.add(pelicula);
        }
    }

    public void agregarSala(Sala sala) {
        if (sala != null && !salas.contains(sala)) {
            salas.add(sala);
        }
    }

    public void agregarFuncion(Funcion funcion) {
        if (funcion != null && !funciones.contains(funcion)) {
            funciones.add(funcion);
            if (funcion.getPelicula() != null && !funcion.getPelicula().getFunciones().contains(funcion)) {
                funcion.getPelicula().agregarFuncion(funcion);
            }
        }
    }

    public void agregarCliente(Cliente cliente) {
        if (cliente != null && !clientes.contains(cliente)) {
            clientes.add(cliente);
        }
    }

    public void agregarAdministrador(Administrador administrador) {
        if (administrador != null && !administradores.contains(administrador)) {
            administradores.add(administrador);
        }
    }

    public void agregarProducto(Producto producto) {
        if (producto != null && !productos.contains(producto)) {
            productos.add(producto);
        }
    }

    public void agregarCombo(Combo combo) {
        if (combo != null && !combos.contains(combo)) {
            combos.add(combo);
        }
    }

    public void agregarPromocion(Promocion promocion) {
        if (promocion != null && !promociones.contains(promocion)) {
            promociones.add(promocion);
        }
    }

    public void agregarCompra(Compra compra) {
        if (compra != null && !compras.contains(compra)) {
            compras.add(compra);
        }
    }

    public void agregarSolicitud(SolicitudRecarga solicitud) {
        if (solicitud != null && !solicitudesRecarga.contains(solicitud)) {
            solicitudesRecarga.add(solicitud);
        }
    }

    public void agregarRedencion(Redencion redencion) {
        if (redencion != null && !redenciones.contains(redencion)) {
            redenciones.add(redencion);
        }
    }

    public List<Redencion> redencionesDeCliente(String idCliente) {
        List<Redencion> resultado = new ArrayList<>();
        for (Redencion redencion : redenciones) {
            if (redencion.getIdCliente() != null && redencion.getIdCliente().equals(idCliente)) {
                resultado.add(redencion);
            }
        }
        resultado.sort(Comparator.comparing(Redencion::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return resultado;
    }

    public List<Pelicula> peliculasEnCartelera() {
        List<Pelicula> resultado = new ArrayList<>();
        for (Pelicula pelicula : peliculas) {
            if (pelicula.isEnCartelera()) {
                resultado.add(pelicula);
            }
        }
        return resultado;
    }

    public List<Funcion> funcionesDePelicula(Pelicula pelicula) {
        List<Funcion> resultado = new ArrayList<>();
        if (pelicula == null) {
            return resultado;
        }
        for (Funcion funcion : funciones) {
            if (funcion.getPelicula() != null
                    && funcion.getPelicula().getIdPelicula().equals(pelicula.getIdPelicula())) {
                resultado.add(funcion);
            }
        }
        return resultado;
    }

    public Pelicula buscarPeliculaPorId(String id) {
        for (Pelicula pelicula : peliculas) {
            if (pelicula.getIdPelicula().equals(id)) {
                return pelicula;
            }
        }
        return null;
    }

    public Sala buscarSalaPorId(int id) {
        for (Sala sala : salas) {
            if (sala.getIdSala() == id) {
                return sala;
            }
        }
        return null;
    }

    public Funcion buscarFuncionPorId(String id) {
        for (Funcion funcion : funciones) {
            if (funcion.getIdFuncion().equals(id)) {
                return funcion;
            }
        }
        return null;
    }

    public Cliente buscarClientePorId(String id) {
        for (Cliente cliente : clientes) {
            if (cliente.getIdCliente().equals(id)) {
                return cliente;
            }
        }
        return null;
    }

    public Cliente buscarClientePorUsuario(String usuario) {
        for (Cliente cliente : clientes) {
            if (cliente.getUsuario().equalsIgnoreCase(usuario)) {
                return cliente;
            }
        }
        return null;
    }

    public Administrador buscarAdministradorPorUsuario(String usuario) {
        for (Administrador administrador : administradores) {
            if (administrador.getUsuario().equalsIgnoreCase(usuario)) {
                return administrador;
            }
        }
        return null;
    }

    public Cliente buscarClientePorTarjeta(String nroTarjeta) {
        for (Cliente cliente : clientes) {
            TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
            if (tarjeta != null && tarjeta.getNroTarjeta().equals(nroTarjeta)) {
                return cliente;
            }
        }
        return null;
    }

    public Producto buscarProductoPorId(String id) {
        for (Producto producto : productos) {
            if (producto.getIdProducto().equals(id)) {
                return producto;
            }
        }
        return null;
    }

    public Combo buscarComboPorId(String id) {
        for (Combo combo : combos) {
            if (combo.getIdCombo().equals(id)) {
                return combo;
            }
        }
        return null;
    }

    public Promocion buscarPromocionPorId(String id) {
        for (Promocion promocion : promociones) {
            if (promocion.getIdPromocion().equals(id)) {
                return promocion;
            }
        }
        return null;
    }

    public boolean existeUsuario(String usuario) {
        return buscarClientePorUsuario(usuario) != null || buscarAdministradorPorUsuario(usuario) != null;
    }

    public void guardar() {
        if (repositorio == null) {
            return;
        }
        DatosCinema datos = new DatosCinema();
        datos.peliculas = peliculas;
        datos.salas = salas;
        datos.funciones = funciones;
        datos.clientes = clientes;
        datos.administradores = administradores;
        datos.productos = productos;
        datos.combos = combos;
        datos.promociones = promociones;
        datos.compras = compras;
        datos.solicitudesRecarga = solicitudesRecarga;
        datos.redenciones = redenciones;
        datos.secuenciaMovimiento = secuenciaMovimiento;
        repositorio.guardar(datos);
    }

    public void cargar() {
        if (repositorio == null) {
            return;
        }
        DatosCinema datos = repositorio.cargar();
        this.peliculas = datos.peliculas != null ? datos.peliculas : new ArrayList<>();
        this.salas = datos.salas != null ? datos.salas : new ArrayList<>();
        this.funciones = datos.funciones != null ? datos.funciones : new ArrayList<>();
        this.clientes = datos.clientes != null ? datos.clientes : new ArrayList<>();
        this.administradores = datos.administradores != null ? datos.administradores : new ArrayList<>();
        this.productos = datos.productos != null ? datos.productos : new ArrayList<>();
        this.combos = datos.combos != null ? datos.combos : new ArrayList<>();
        this.promociones = datos.promociones != null ? datos.promociones : new ArrayList<>();
        this.compras = datos.compras != null ? datos.compras : new ArrayList<>();
        this.solicitudesRecarga = datos.solicitudesRecarga != null ? datos.solicitudesRecarga : new ArrayList<>();
        this.redenciones = datos.redenciones != null ? datos.redenciones : new ArrayList<>();
        this.secuenciaMovimiento = Math.max(1, datos.secuenciaMovimiento);
        reconstruirRelaciones();
    }

    private void reconstruirRelaciones() {
        for (Pelicula pelicula : peliculas) {
            if (pelicula.getFunciones() == null) {
                pelicula.setFunciones(new ArrayList<>());
            } else {
                pelicula.getFunciones().clear();
            }
        }
        for (Funcion funcion : funciones) {
            if (funcion.getEntradasVendidas() == null) {
                funcion.setEntradasVendidas(new ArrayList<>());
            } else {
                funcion.getEntradasVendidas().clear();
            }
            if (funcion.getPelicula() != null) {
                Pelicula pelicula = buscarPeliculaPorId(funcion.getPelicula().getIdPelicula());
                if (pelicula != null) {
                    funcion.setPelicula(pelicula);
                    pelicula.agregarFuncion(funcion);
                }
            }
            if (funcion.getSala() != null) {
                Sala sala = buscarSalaPorId(funcion.getSala().getIdSala());
                if (sala != null) {
                    funcion.setSala(sala);
                }
            }
        }
        for (Cliente cliente : clientes) {
            if (cliente.getCompras() == null) {
                cliente.setCompras(new ArrayList<>());
            } else {
                cliente.getCompras().clear();
            }
            if (cliente.getSolicitudesRecarga() == null) {
                cliente.setSolicitudesRecarga(new ArrayList<>());
            } else {
                cliente.getSolicitudesRecarga().clear();
            }
        }
        for (Compra compra : compras) {
            Cliente cliente = compra.getCliente() != null
                    ? buscarClientePorId(compra.getCliente().getIdCliente())
                    : null;
            if (cliente != null) {
                compra.setCliente(cliente);
                cliente.agregarCompra(compra);
            }
            for (Entrada entrada : compra.getEntradas()) {
                if (entrada.getFuncion() != null) {
                    Funcion funcion = buscarFuncionPorId(entrada.getFuncion().getIdFuncion());
                    if (funcion != null) {
                        entrada.setFuncion(funcion);
                        funcion.agregarEntrada(entrada);
                    }
                }
            }
        }
        for (SolicitudRecarga solicitud : solicitudesRecarga) {
            if (solicitud.getTarjeta() != null) {
                Cliente cliente = buscarClientePorTarjeta(solicitud.getTarjeta().getNroTarjeta());
                if (cliente != null) {
                    solicitud.setTarjeta(cliente.getTarjetaVirtual());
                    cliente.agregarSolicitudRecarga(solicitud);
                }
            }
        }
    }
}
