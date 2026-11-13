package co.edu.uniquindio.cinemauq.service;

public enum TipoReporte {

    RPT_001("RPT-001", "Ventas por periodo", "ventas-por-periodo"),
    RPT_002("RPT-002", "Entradas vendidas por película", "entradas-por-pelicula"),
    RPT_003("RPT-003", "Ocupación de las salas", "ocupacion-de-salas"),
    RPT_004("RPT-004", "Productos y combos vendidos", "productos-y-combos"),
    RPT_005("RPT-005", "Recargas realizadas", "recargas-realizadas"),
    RPT_006("RPT-006", "Cancelaciones y reembolsos", "cancelaciones-y-reembolsos"),
    RPT_007("RPT-007", "Puntos acumulados y redimidos", "puntos-por-cliente");

    private final String codigo;
    private final String titulo;
    private final String slug;

    TipoReporte(String codigo, String titulo, String slug) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.slug = slug;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public String toString() {
        return codigo + " - " + titulo;
    }
}
