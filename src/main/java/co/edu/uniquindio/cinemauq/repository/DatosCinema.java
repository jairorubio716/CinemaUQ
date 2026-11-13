package co.edu.uniquindio.cinemauq.repository;

import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Redencion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor raíz para la serialización JSON con Gson.
 */
public class DatosCinema {

    public List<Pelicula> peliculas = new ArrayList<>();
    public List<Sala> salas = new ArrayList<>();
    public List<Funcion> funciones = new ArrayList<>();
    public List<Cliente> clientes = new ArrayList<>();
    public List<Administrador> administradores = new ArrayList<>();
    public List<Producto> productos = new ArrayList<>();
    public List<Combo> combos = new ArrayList<>();
    public List<Promocion> promociones = new ArrayList<>();
    public List<Compra> compras = new ArrayList<>();
    public List<SolicitudRecarga> solicitudesRecarga = new ArrayList<>();
    public List<Redencion> redenciones = new ArrayList<>();
    public int secuenciaMovimiento = 1;
}
