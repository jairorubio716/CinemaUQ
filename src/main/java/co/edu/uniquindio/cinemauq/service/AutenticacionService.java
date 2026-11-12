package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
import co.edu.uniquindio.cinemauq.model.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.model.Persona;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;

import java.time.LocalDate;

public class AutenticacionService {

    private final CatalogoService catalogo;

    public AutenticacionService(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    public Persona iniciarSesion(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new CinemaUQException("Debe ingresar usuario y contraseña.");
        }
        Cliente cliente = catalogo.buscarClientePorUsuario(usuario);
        if (cliente != null && cliente.getContrasena().equals(contrasena)) {
            return cliente;
        }
        Administrador administrador = catalogo.buscarAdministradorPorUsuario(usuario);
        if (administrador != null && administrador.getContrasena().equals(contrasena)) {
            return administrador;
        }
        throw new CinemaUQException("Usuario o contraseña incorrectos.");
    }

    public Cliente registrarCliente(String nombre, String email, String celular,
                                    String usuario, String contrasena) {
        validarTexto(nombre, "El nombre es obligatorio.");
        validarTexto(email, "El email es obligatorio.");
        validarTexto(usuario, "El usuario es obligatorio.");
        validarTexto(contrasena, "La contraseña es obligatoria.");
        if (catalogo.existeUsuario(usuario)) {
            throw new CinemaUQException("El usuario '" + usuario + "' ya existe.");
        }
        for (Cliente existente : catalogo.getClientes()) {
            if (existente.getEmail().equalsIgnoreCase(email)) {
                throw new CinemaUQException("El email '" + email + "' ya está registrado.");
            }
        }
        String idCliente = "CLI-" + (catalogo.getClientes().size() + 1);
        Cliente cliente = new Cliente(nombre, email, celular, usuario, contrasena, idCliente, LocalDate.now());
        TarjetaVirtual tarjeta = new TarjetaVirtual("TV-" + idCliente, 0, EstadoTarjeta.ACTIVA);
        cliente.setTarjetaVirtual(tarjeta);
        cliente.setCuentaPuntos(new CuentaPuntos("CP-" + idCliente));
        catalogo.agregarCliente(cliente);
        catalogo.guardar();
        return cliente;
    }

    private void validarTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new CinemaUQException(mensaje);
        }
    }
}
