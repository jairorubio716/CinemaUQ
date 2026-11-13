package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.CategoriaAsiento;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.model.TipoPromocion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Operaciones de catálogo reservadas al administrador (RF-011 a RF-016, RN-16).
 */
public class CatalogoAdminService {

    private final CatalogoService catalogo;

    public CatalogoAdminService(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    public Pelicula crearPelicula(Administrador admin, String titulo, String genero, int duracionMin,
                                  Clasificacion clasificacion, String sinopsis, String poster,
                                  boolean enCartelera) {
        validarAdmin(admin);
        validarTexto(titulo, "El título de la película es obligatorio.");
        validarTexto(genero, "El género de la película es obligatorio.");
        if (duracionMin <= 0) {
            throw new CinemaUQException("La duración de la película debe ser mayor que cero.");
        }
        String id = "PEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Pelicula pelicula = new Pelicula(id, titulo, genero, duracionMin, clasificacion,
                sinopsis, poster, enCartelera);
        catalogo.agregarPelicula(pelicula);
        catalogo.guardar();
        return pelicula;
    }

    public void actualizarPelicula(Administrador admin, Pelicula pelicula) {
        validarAdmin(admin);
        if (pelicula == null || catalogo.buscarPeliculaPorId(pelicula.getIdPelicula()) == null) {
            throw new CinemaUQException("La película no existe en el catálogo.");
        }
        validarTexto(pelicula.getTitulo(), "El título de la película es obligatorio.");
        validarTexto(pelicula.getGenero(), "El género de la película es obligatorio.");
        if (pelicula.getDuracionMin() <= 0) {
            throw new CinemaUQException("La duración de la película debe ser mayor que cero.");
        }
        catalogo.guardar();
    }

    public void actualizarSala(Administrador admin, Sala sala) {
        validarAdmin(admin);
        if (sala == null || catalogo.buscarSalaPorId(sala.getIdSala()) == null) {
            throw new CinemaUQException("La sala no existe.");
        }
        if (sala.getFilas() <= 0 || sala.getColumnas() <= 0) {
            throw new CinemaUQException("Las filas y columnas de la sala deben ser mayores que cero.");
        }
        validarTexto(sala.getTipoPantalla(), "El tipo de pantalla es obligatorio.");
        catalogo.guardar();
    }

    public void actualizarFuncion(Administrador admin, Funcion funcion) {
        validarAdmin(admin);
        if (funcion == null || catalogo.buscarFuncionPorId(funcion.getIdFuncion()) == null) {
            throw new CinemaUQException("La función no existe.");
        }
        if (funcion.getPelicula() == null
                || catalogo.buscarPeliculaPorId(funcion.getPelicula().getIdPelicula()) == null) {
            throw new CinemaUQException("La película de la función no existe.");
        }
        if (funcion.getSala() == null
                || catalogo.buscarSalaPorId(funcion.getSala().getIdSala()) == null) {
            throw new CinemaUQException("La sala de la función no existe.");
        }
        if (funcion.getFechaHora() == null) {
            throw new CinemaUQException("La función requiere fecha y hora (RN-01).");
        }
        if (funcion.getPrecioEntrada() <= 0) {
            throw new CinemaUQException("El precio de la entrada debe ser mayor que cero.");
        }
        catalogo.guardar();
    }

    public void eliminarPelicula(Administrador admin, String idPelicula) {
        validarAdmin(admin);
        Pelicula pelicula = catalogo.buscarPeliculaPorId(idPelicula);
        if (pelicula == null) {
            throw new CinemaUQException("La película no existe en el catálogo.");
        }
        if (!catalogo.funcionesDePelicula(pelicula).isEmpty()) {
            throw new CinemaUQException("No se puede eliminar la película: tiene funciones asociadas.");
        }
        catalogo.getPeliculas().remove(pelicula);
        catalogo.guardar();
    }

    public Sala crearSala(Administrador admin, int numeroSala, int filas, int columnas,
                          String tipoPantalla) {
        validarAdmin(admin);
        if (filas <= 0 || columnas <= 0) {
            throw new CinemaUQException("Las filas y columnas de la sala deben ser mayores que cero.");
        }
        int id = siguienteIdSala();
        Sala sala = new Sala(id, numeroSala, filas, columnas, tipoPantalla);
        generarAsientos(sala);
        catalogo.agregarSala(sala);
        catalogo.guardar();
        return sala;
    }

    public void eliminarSala(Administrador admin, int idSala) {
        validarAdmin(admin);
        Sala sala = catalogo.buscarSalaPorId(idSala);
        if (sala == null) {
            throw new CinemaUQException("La sala no existe.");
        }
        for (Funcion funcion : catalogo.getFunciones()) {
            if (funcion.getSala() != null && funcion.getSala().getIdSala() == idSala) {
                throw new CinemaUQException("No se puede eliminar la sala: tiene funciones asociadas.");
            }
        }
        catalogo.getSalas().remove(sala);
        catalogo.guardar();
    }

    public Funcion crearFuncion(Administrador admin, Pelicula pelicula, Sala sala,
                                LocalDateTime fechaHora, double precioEntrada,
                                EstadoFuncion estado) {
        validarAdmin(admin);
        if (pelicula == null || catalogo.buscarPeliculaPorId(pelicula.getIdPelicula()) == null) {
            throw new CinemaUQException("La película de la función no existe.");
        }
        if (sala == null || catalogo.buscarSalaPorId(sala.getIdSala()) == null) {
            throw new CinemaUQException("La sala de la función no existe.");
        }
        if (fechaHora == null) {
            throw new CinemaUQException("La función requiere fecha y hora (RN-01).");
        }
        if (precioEntrada <= 0) {
            throw new CinemaUQException("El precio de la entrada debe ser mayor que cero.");
        }
        String id = "FUN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Funcion funcion = new Funcion(id, fechaHora, precioEntrada,
                estado != null ? estado : EstadoFuncion.PROGRAMADA, pelicula, sala);
        catalogo.agregarFuncion(funcion);
        catalogo.guardar();
        return funcion;
    }

    public void eliminarFuncion(Administrador admin, String idFuncion) {
        validarAdmin(admin);
        Funcion funcion = catalogo.buscarFuncionPorId(idFuncion);
        if (funcion == null) {
            throw new CinemaUQException("La función no existe.");
        }
        if (funcion.getAsientosVendidos() > 0) {
            throw new CinemaUQException("No se puede eliminar una función con entradas vendidas; "
                    + "use la cancelación de función.");
        }
        catalogo.getFunciones().remove(funcion);
        if (funcion.getPelicula() != null) {
            funcion.getPelicula().getFunciones().remove(funcion);
        }
        catalogo.guardar();
    }

    public Producto crearProducto(Administrador admin, String nombre, double precio, TipoProducto tipo) {
        validarAdmin(admin);
        validarTexto(nombre, "El nombre del producto es obligatorio.");
        if (precio <= 0) {
            throw new CinemaUQException("El precio del producto debe ser mayor que cero.");
        }
        String id = "PRO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Producto producto = new Producto(id, nombre, precio, tipo);
        catalogo.agregarProducto(producto);
        catalogo.guardar();
        return producto;
    }

    public void actualizarProducto(Administrador admin, Producto producto) {
        validarAdmin(admin);
        if (producto == null || catalogo.buscarProductoPorId(producto.getIdProducto()) == null) {
            throw new CinemaUQException("El producto no existe.");
        }
        validarTexto(producto.getNombre(), "El nombre del producto es obligatorio.");
        if (producto.getPrecio() <= 0) {
            throw new CinemaUQException("El precio del producto debe ser mayor que cero.");
        }
        catalogo.guardar();
    }

    public void eliminarProducto(Administrador admin, String idProducto) {
        validarAdmin(admin);
        Producto producto = catalogo.buscarProductoPorId(idProducto);
        if (producto == null) {
            throw new CinemaUQException("El producto no existe.");
        }
        for (Combo combo : catalogo.getCombos()) {
            for (IItemVendible item : combo.getItems()) {
                if (item.getId() != null && item.getId().equals(idProducto)) {
                    throw new CinemaUQException("El producto pertenece al combo '" + combo.getNombre() + "'.");
                }
            }
        }
        catalogo.getProductos().remove(producto);
        catalogo.guardar();
    }

    public Combo crearCombo(Administrador admin, String nombre, List<IItemVendible> items) {
        validarAdmin(admin);
        validarTexto(nombre, "El nombre del combo es obligatorio.");
        if (items == null || items.isEmpty()) {
            throw new CinemaUQException("El combo debe tener al menos un ítem.");
        }
        String id = "COM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Combo combo = new Combo(id, nombre, 0);
        for (IItemVendible item : items) {
            combo.agregarItem(item);
        }
        catalogo.agregarCombo(combo);
        catalogo.guardar();
        return combo;
    }

    public void actualizarCombo(Administrador admin, Combo combo) {
        validarAdmin(admin);
        if (combo == null || catalogo.buscarComboPorId(combo.getIdCombo()) == null) {
            throw new CinemaUQException("El combo no existe.");
        }
        validarTexto(combo.getNombre(), "El nombre del combo es obligatorio.");
        if (combo.getItems() == null || combo.getItems().isEmpty()) {
            throw new CinemaUQException("El combo debe tener al menos un ítem.");
        }
        catalogo.guardar();
    }

    public void eliminarCombo(Administrador admin, String idCombo) {
        validarAdmin(admin);
        Combo combo = catalogo.buscarComboPorId(idCombo);
        if (combo == null) {
            throw new CinemaUQException("El combo no existe.");
        }
        catalogo.getCombos().remove(combo);
        catalogo.guardar();
    }

    public Promocion crearPromocion(Administrador admin, String nombre, TipoPromocion tipo,
                                    double porcentajeOValor, LocalDate inicio, LocalDate fin) {
        validarAdmin(admin);
        validarTexto(nombre, "El nombre de la promoción es obligatorio.");
        if (tipo == null) {
            throw new CinemaUQException("La promoción requiere un tipo.");
        }
        if (porcentajeOValor <= 0) {
            throw new CinemaUQException("El valor de la promoción debe ser mayor que cero.");
        }
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new CinemaUQException("La fecha final no puede ser anterior a la inicial.");
        }
        String id = "PRM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Promocion promocion = new Promocion(id, nombre, tipo, porcentajeOValor, inicio, fin);
        catalogo.agregarPromocion(promocion);
        catalogo.guardar();
        return promocion;
    }

    public void actualizarPromocion(Administrador admin, Promocion promocion) {
        validarAdmin(admin);
        if (promocion == null || catalogo.buscarPromocionPorId(promocion.getIdPromocion()) == null) {
            throw new CinemaUQException("La promoción no existe.");
        }
        validarTexto(promocion.getNombre(), "El nombre de la promoción es obligatorio.");
        if (promocion.getTipo() == null) {
            throw new CinemaUQException("La promoción requiere un tipo.");
        }
        if (promocion.getPorcentajeOValor() <= 0) {
            throw new CinemaUQException("El valor de la promoción debe ser mayor que cero.");
        }
        if (promocion.getFechaInicio() != null && promocion.getFechaFin() != null
                && promocion.getFechaFin().isBefore(promocion.getFechaInicio())) {
            throw new CinemaUQException("La fecha final no puede ser anterior a la inicial.");
        }
        catalogo.guardar();
    }

    public void eliminarPromocion(Administrador admin, String idPromocion) {
        validarAdmin(admin);
        Promocion promocion = catalogo.buscarPromocionPorId(idPromocion);
        if (promocion == null) {
            throw new CinemaUQException("La promoción no existe.");
        }
        catalogo.getPromociones().remove(promocion);
        catalogo.guardar();
    }

    public List<Cliente> consultarClientes(Administrador admin) {
        validarAdmin(admin);
        return new ArrayList<>(catalogo.getClientes());
    }

    public List<Compra> consultarCompras(Administrador admin) {
        validarAdmin(admin);
        return new ArrayList<>(catalogo.getCompras());
    }

    public List<Entrada> consultarEntradasVendidas(Administrador admin) {
        validarAdmin(admin);
        List<Entrada> entradas = new ArrayList<>();
        for (Compra compra : catalogo.getCompras()) {
            entradas.addAll(compra.getEntradas());
        }
        return entradas;
    }

    private int siguienteIdSala() {
        int max = 0;
        for (Sala sala : catalogo.getSalas()) {
            max = Math.max(max, sala.getIdSala());
        }
        return max + 1;
    }

    private void generarAsientos(Sala sala) {
        int id = 1;
        for (int fila = 0; fila < sala.getFilas(); fila++) {
            String letra = String.valueOf((char) ('A' + fila));
            for (int columna = 1; columna <= sala.getColumnas(); columna++) {
                CategoriaAsiento categoria = CategoriaAsiento.GENERAL;
                if (fila == 0) {
                    categoria = CategoriaAsiento.PREMIUM;
                } else if (fila == sala.getFilas() - 1) {
                    categoria = CategoriaAsiento.VIP;
                }
                sala.agregarAsiento(new Asiento(id++, letra, columna, columna, categoria));
            }
        }
    }

    private void validarAdmin(Administrador admin) {
        if (admin == null) {
            throw new CinemaUQException("Solo un administrador puede gestionar el catálogo (RN-16).");
        }
    }

    private void validarTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new CinemaUQException(mensaje);
        }
    }
}
