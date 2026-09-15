package co.edu.uniquindio.cinemauq.model;

public class Pelicula {

    private String idPelicula;
    private String titulo;
    private String genero;
    private int duracionMin;
    private Clasificacion clasificacion;
    private String sinopsis;
    private String imagenPoster;
    private boolean enCartelera;

    public Pelicula(String idPelicula, String titulo, String genero, int duracionMin,
                    Clasificacion clasificacion, String sinopsis, String imagenPoster,
                    boolean enCartelera) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMin = duracionMin;
        this.clasificacion = clasificacion;
        this.sinopsis = sinopsis;
        this.imagenPoster = imagenPoster;
        this.enCartelera = enCartelera;
    }

    public String getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(String idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = duracionMin;
    }

    public Clasificacion getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getImagenPoster() {
        return imagenPoster;
    }

    public void setImagenPoster(String imagenPoster) {
        this.imagenPoster = imagenPoster;
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    public void setEnCartelera(boolean enCartelera) {
        this.enCartelera = enCartelera;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "idPelicula='" + idPelicula + '\'' +
                ", titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", duracionMin=" + duracionMin +
                ", clasificacion=" + clasificacion +
                ", sinopsis='" + sinopsis + '\'' +
                ", enCartelera=" + enCartelera +
                '}';
    }
}