package co.edu.uniquindio.cinemauq.model;

public class Entrada {

    private String idEntrada;
    private String codigo;
    private double precio;
    private Funcion funcion;
    private Asiento asiento;

    public Entrada(String idEntrada, String codigo, double precio, Funcion funcion, Asiento asiento) {
        this.idEntrada = idEntrada;
        this.codigo = codigo;
        this.precio = precio;
        this.funcion = funcion;
        this.asiento = asiento;
    }

    public String getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(String idEntrada) {
        this.idEntrada = idEntrada;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    @Override
    public String toString() {
        return "Entrada{" +
                "idEntrada='" + idEntrada + '\'' +
                ", codigo='" + codigo + '\'' +
                ", precio=" + precio +
                '}';
    }
}
