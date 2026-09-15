package co.edu.uniquindio.cinemauq.model;

public class Sala {

    private int idSala;
    private int numeroSala;
    private int filas;
    private int columnas;
    private String tipoPantalla;

    public Sala(int idSala, int numeroSala, int filas, int columnas, String tipoPantalla) {
        this.idSala = idSala;
        this.numeroSala = numeroSala;
        this.filas = filas;
        this.columnas = columnas;
        this.tipoPantalla = tipoPantalla;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public int getNumeroSala() {
        return numeroSala;
    }

    public void setNumeroSala(int numeroSala) {
        this.numeroSala = numeroSala;
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public String getTipoPantalla() {
        return tipoPantalla;
    }

    public void setTipoPantalla(String tipoPantalla) {
        this.tipoPantalla = tipoPantalla;
    }

    @Override
    public String toString() {
        return "Sala{" +
                "idSala=" + idSala +
                ", numeroSala=" + numeroSala +
                ", filas=" + filas +
                ", columnas=" + columnas +
                ", tipoPantalla='" + tipoPantalla + '\'' +
                '}';
    }
}