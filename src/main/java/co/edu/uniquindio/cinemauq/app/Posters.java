package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.model.Pelicula;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.net.URL;

public final class Posters {

    private static final Color[][] PALETA = {
            {Color.web("#3B0D16"), Color.web("#E5092C")},
            {Color.web("#0D1B3B"), Color.web("#2F6BFF")},
            {Color.web("#1B0D2E"), Color.web("#8B3DFF")},
            {Color.web("#0D2B2B"), Color.web("#12B886")},
            {Color.web("#2B1B0D"), Color.web("#F5B841")},
            {Color.web("#2B0D2B"), Color.web("#E83E8C")},
            {Color.web("#11202B"), Color.web("#22B8CF")},
            {Color.web("#2B0D0D"), Color.web("#FF6B35")}
    };

    private Posters() {
    }

    public static StackPane crearPoster(Pelicula pelicula, double ancho, double alto) {
        StackPane poster = new StackPane();
        poster.setPrefSize(ancho, alto);
        poster.setMinSize(ancho, alto);
        poster.setMaxSize(ancho, alto);
        poster.getStyleClass().add("poster");

        Color[] colores = PALETA[Math.abs(pelicula.getIdPelicula().hashCode()) % PALETA.length];
        poster.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, colores[0]), new Stop(1, colores[1])),
                new CornerRadii(18), Insets.EMPTY)));

        Region brillo = new Region();
        brillo.setBackground(new Background(new BackgroundFill(
                new javafx.scene.paint.RadialGradient(0, 0, 0.9, 0.2, 0.7, true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#FFFFFF", 0.34)),
                        new Stop(1, Color.TRANSPARENT)),
                CornerRadii.EMPTY, Insets.EMPTY)));
        brillo.setMouseTransparent(true);

        Label letra = new Label(pelicula.getTitulo().substring(0, 1).toUpperCase());
        letra.getStyleClass().add("poster-letter");
        letra.setStyle("-fx-font-size: " + Math.round(alto * 0.52) + "px;");
        letra.setMouseTransparent(true);

        ImageView imagen = cargarImagen(pelicula, ancho, alto);
        if (imagen != null) {
            poster.getChildren().add(imagen);
            Region velo = new Region();
            velo.setBackground(new Background(new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0.35, Color.TRANSPARENT),
                            new Stop(1, Color.web("#07070C", 0.94))),
                    CornerRadii.EMPTY, Insets.EMPTY)));
            poster.getChildren().add(velo);
        } else {
            poster.getChildren().addAll(brillo, letra);
        }

        Label clasificacion = new Label(Formato.clasificacion(pelicula.getClasificacion()));
        clasificacion.getStyleClass().add("badge-clasificacion");
        StackPane.setAlignment(clasificacion, Pos.TOP_RIGHT);
        StackPane.setMargin(clasificacion, new Insets(10));

        VBox info = new VBox(4);
        info.setAlignment(Pos.BOTTOM_LEFT);
        info.setPadding(new Insets(14));
        info.setMouseTransparent(true);
        Label genero = new Label(pelicula.getGenero().toUpperCase());
        genero.getStyleClass().add("poster-genero");
        Label titulo = new Label(pelicula.getTitulo());
        titulo.getStyleClass().add("poster-titulo");
        titulo.setWrapText(true);
        info.getChildren().addAll(genero, titulo);
        if (!pelicula.isEnCartelera()) {
            Label proximo = new Label("PRÓXIMAMENTE");
            proximo.getStyleClass().add("badge-proximo");
            info.getChildren().add(proximo);
        }
        StackPane.setAlignment(info, Pos.BOTTOM_LEFT);
        poster.getChildren().addAll(clasificacion, info);

        Rectangle clip = new Rectangle(ancho, alto);
        clip.setArcWidth(36);
        clip.setArcHeight(36);
        poster.setClip(clip);

        poster.getStyleClass().add(pelicula.isEnCartelera() ? "poster-cartelera" : "poster-proximo");
        return poster;
    }

    public static VBox crearTarjeta(Pelicula pelicula, Runnable accion) {
        StackPane poster = crearPoster(pelicula, 190, 280);
        Label titulo = new Label(pelicula.getTitulo());
        titulo.getStyleClass().add("card-titulo");
        titulo.setWrapText(true);
        Label meta = new Label(pelicula.getGenero() + " · " + pelicula.getDuracionMin() + " min");
        meta.getStyleClass().add("card-meta");

        VBox tarjeta = new VBox(10, poster, titulo, meta);
        tarjeta.setPrefWidth(190);
        tarjeta.setMaxWidth(190);
        tarjeta.getStyleClass().add("poster-card");
        tarjeta.setOnMouseClicked(e -> {
            if (accion != null) {
                accion.run();
            }
        });
        return tarjeta;
    }

    public static void aplicarFondo(Pane pane, Pelicula pelicula) {
        if (pane == null || pelicula == null) {
            return;
        }
        Color[] colores = PALETA[Math.abs(pelicula.getIdPelicula().hashCode()) % PALETA.length];
        pane.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0.8, true, CycleMethod.NO_CYCLE,
                        new Stop(0, colores[0]),
                        new Stop(0.55, Color.web("#0B0B10", 0.85)),
                        new Stop(1, Color.web("#0B0B10"))),
                CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public static StackPane qr(String codigo, double tamano) {
        int celdas = 15;
        double celda = tamano / celdas;
        GridPane grid = new GridPane();
        for (int y = 0; y < celdas; y++) {
            for (int x = 0; x < celdas; x++) {
                Region punto = new Region();
                punto.setPrefSize(celda, celda);
                punto.setMinSize(celda, celda);
                boolean negro = esFinder(x, y, celdas) || patron(codigo, x, y);
                punto.setBackground(new Background(new BackgroundFill(
                        negro ? Color.web("#0B0B10") : Color.WHITE,
                        CornerRadii.EMPTY, Insets.EMPTY)));
                grid.add(punto, x, y);
            }
        }
        StackPane marco = new StackPane(grid);
        marco.setPadding(new Insets(10));
        marco.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        marco.getStyleClass().add("qr");
        marco.setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
        return marco;
    }

    private static boolean patron(String codigo, int x, int y) {
        int hash = (codigo == null ? 0 : codigo.hashCode());
        int valor = Math.abs(hash + x * 31 + y * 17 + x * y * 7);
        return valor % 11 < 5;
    }

    private static boolean esFinder(int x, int y, int celdas) {
        return finder(x, y, 0, 0) || finder(x, y, celdas - 5, 0) || finder(x, y, 0, celdas - 5);
    }

    private static boolean finder(int x, int y, int inicioX, int inicioY) {
        int dx = x - inicioX;
        int dy = y - inicioY;
        if (dx < 0 || dy < 0 || dx > 4 || dy > 4) {
            return false;
        }
        return dx == 0 || dy == 0 || dx == 4 || dy == 4 || (dx == 2 && dy == 2);
    }

    private static ImageView cargarImagen(Pelicula pelicula, double ancho, double alto) {
        String nombre = pelicula.getImagenPoster();
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        URL recurso = Posters.class.getResource("/co/edu/uniquindio/cinemauq/img/" + nombre);
        if (recurso == null && !nombre.contains(".")) {
            recurso = Posters.class.getResource("/co/edu/uniquindio/cinemauq/img/" + nombre + ".png");
        }
        if (recurso == null) {
            return null;
        }
        ImageView vista = new ImageView(new Image(recurso.toExternalForm(), ancho, alto, false, true, true));
        vista.setFitWidth(ancho);
        vista.setFitHeight(alto);
        vista.setSmooth(true);
        return vista;
    }
}
