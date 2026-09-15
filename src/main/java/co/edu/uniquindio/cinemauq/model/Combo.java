package co.edu.uniquindio.cinemauq.model;

public class Combo {

    private String idCombo;
    private String nombre;
    private double precio;

    public Combo(String idCombo, String nombre, double precio) {
        this.idCombo = idCombo;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(String idCombo) {
        this.idCombo = idCombo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Combo{" +
                "idCombo='" + idCombo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }
}