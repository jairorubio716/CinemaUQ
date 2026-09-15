package co.edu.uniquindio.cinemauq.model;

public class Entrada {

    private String idEntrada;
    private String codigo;
    private double precio;

    public Entrada(String idEntrada, String codigo, double precio) {
        this.idEntrada = idEntrada;
        this.codigo = codigo;
        this.precio = precio;
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

    @Override
    public String toString() {
        return "Entrada{" +
                "idEntrada='" + idEntrada + '\'' +
                ", codigo='" + codigo + '\'' +
                ", precio=" + precio +
                '}';
    }
}