package co.edu.uniquindio.cinemauq.model;

public abstract class Persona {

    private String nombre;
    private String email;
    private String celular;
    private String usuario;
    private String contrasena;

    public Persona(String nombre, String email, String celular, String usuario, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }
    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", celular='" + celular + '\'' +
                ", usuario='" + usuario + '\'' +
                ", contrasena='" + contrasena + '\'' +
                '}';
    }
}
