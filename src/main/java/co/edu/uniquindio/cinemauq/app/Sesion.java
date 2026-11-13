package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Persona;

public class Sesion {

    private Persona persona;

    public void iniciar(Persona persona) {
        this.persona = persona;
    }

    public void cerrar() {
        this.persona = null;
    }

    public Persona getPersona() {
        return persona;
    }

    public boolean haySesion() {
        return persona != null;
    }

    public Cliente getCliente() {
        return persona instanceof Cliente cliente ? cliente : null;
    }

    public Administrador getAdministrador() {
        return persona instanceof Administrador administrador ? administrador : null;
    }

    public boolean esCliente() {
        return getCliente() != null;
    }

    public boolean esAdministrador() {
        return getAdministrador() != null;
    }

    public String nombre() {
        return persona != null ? persona.getNombre() : "";
    }
}
