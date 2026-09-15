package co.edu.uniquindio.cinemauq.model;

public class Administrador extends Persona {
    private String idAdmin;

    public Administrador(String nombre, String email, String celular,
                         String usuario, String contrasena, String idAdmin) {
        super(nombre, email, celular, usuario, contrasena);
        this.idAdmin = idAdmin;
    }

    public String getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(String idAdmin) {
        this.idAdmin = idAdmin;
    }

    @Override
    public String toString() {
        return "Administrador{" + super.toString() + ", idAdmin='" + idAdmin + '\'' + '}';
    }
}