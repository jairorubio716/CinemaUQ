package co.edu.uniquindio.cinemauq.model;

public class Producto implements IItemVendible {

    private String idProducto;
    private String nombre;
    private double precio;
    private TipoProducto tipo;

    public Producto(String idProducto, String nombre, double precio, TipoProducto tipo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
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

    public TipoProducto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProducto tipo) {
        this.tipo = tipo;
    }

    @Override
    public String getId() {
        return idProducto;
    }

    @Override
    public double obtenerPrecio() {
        return precio;
    }

    @Override
    public String descripcion() {
        return nombre + " (" + tipo + ")";
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto='" + idProducto + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", tipo=" + tipo +
                '}';
    }
}
