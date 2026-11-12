package co.edu.uniquindio.cinemauq.model;

import java.util.ArrayList;
import java.util.List;

public class Combo implements IItemVendible {

    private String idCombo;
    private String nombre;
    private double precio;
    private List<IItemVendible> items = new ArrayList<>();

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

    public List<IItemVendible> getItems() {
        return items;
    }

    public void setItems(List<IItemVendible> items) {
        this.items = items;
    }

    public void agregarItem(IItemVendible item) {
        if (item != null) {
            items.add(item);
        }
    }

    public void removerItem(IItemVendible item) {
        items.remove(item);
    }

    @Override
    public String getId() {
        return idCombo;
    }

    @Override
    public double obtenerPrecio() {
        if (items.isEmpty()) {
            return precio;
        }
        double suma = 0;
        for (IItemVendible item : items) {
            suma += item.obtenerPrecio();
        }
        return suma;
    }

    @Override
    public String descripcion() {
        return nombre + " [combo de " + items.size() + " items]";
    }

    @Override
    public String toString() {
        return "Combo{" +
                "idCombo='" + idCombo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + obtenerPrecio() +
                ", items=" + items.size() +
                '}';
    }
}
