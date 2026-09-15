package co.edu.uniquindio.cinemauq.model;

public class Asiento {

    private int idAsiento;
    private String fila;
    private int columna;
    private int numero;
    private CategoriaAsiento categoria;

    public Asiento(int idAsiento, String fila, int columna, int numero, CategoriaAsiento categoria) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.columna = columna;
        this.numero = numero;
        this.categoria = categoria;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public CategoriaAsiento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaAsiento categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "idAsiento=" + idAsiento +
                ", fila='" + fila + '\'' +
                ", columna=" + columna +
                ", numero=" + numero +
                ", categoria=" + categoria +
                '}';
    }
}