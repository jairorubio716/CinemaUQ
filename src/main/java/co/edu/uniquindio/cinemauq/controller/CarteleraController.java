package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.Posters;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CarteleraController implements VistaController {

    @FXML private HBox hero;
    @FXML private Label heroKicker;
    @FXML private Label heroTitulo;
    @FXML private Label heroMeta;
    @FXML private Label heroSinopsis;
    @FXML private StackPane heroPoster;
    @FXML private Button btnHeroComprar;
    @FXML private Button btnHeroDetalle;
    @FXML private TextField busqueda;
    @FXML private ComboBox<String> genero;
    @FXML private FlowPane gridCartelera;
    @FXML private FlowPane gridProximamente;
    @FXML private Label contador;

    private final List<Pelicula> cartelera = new ArrayList<>();
    private final List<Pelicula> proximamente = new ArrayList<>();
    private Pelicula destacada;

    @FXML
    public void initialize() {
        genero.setItems(FXCollections.observableArrayList("Todos los géneros"));
        genero.getSelectionModel().selectFirst();
        busqueda.textProperty().addListener((obs, anterior, nuevo) -> renderizar());
        genero.valueProperty().addListener((obs, anterior, nuevo) -> renderizar());
    }

    @Override
    public void alMostrar(Object dato) {
        cartelera.clear();
        cartelera.addAll(AppContext.get().getFacade().consultarCartelera());
        proximamente.clear();
        for (Pelicula pelicula : AppContext.get().getFacade().getPeliculas()) {
            if (!pelicula.isEnCartelera()) {
                proximamente.add(pelicula);
            }
        }

        Set<String> generos = new LinkedHashSet<>();
        generos.add("Todos los géneros");
        for (Pelicula pelicula : cartelera) {
            generos.add(pelicula.getGenero());
        }
        genero.setItems(FXCollections.observableArrayList(generos));
        genero.getSelectionModel().selectFirst();

        destacada = cartelera.isEmpty() ? null : cartelera.get(0);
        mostrarHero(destacada);
        renderizar();
    }

    private void mostrarHero(Pelicula pelicula) {
        if (pelicula == null) {
            hero.setVisible(false);
            hero.setManaged(false);
            return;
        }
        hero.setVisible(true);
        hero.setManaged(true);
        Posters.aplicarFondo(hero, pelicula);
        heroKicker.setText(pelicula.isEnCartelera() ? "DESTACADA EN CARTELERA" : "PRÓXIMAMENTE");
        heroTitulo.setText(pelicula.getTitulo());
        heroMeta.setText(pelicula.getGenero() + "  ·  " + pelicula.getDuracionMin()
                + " min  ·  " + Formato.clasificacion(pelicula.getClasificacion()));
        heroSinopsis.setText(pelicula.getSinopsis());
        heroPoster.getChildren().setAll(Posters.crearPoster(pelicula, 190, 280));
        btnHeroComprar.setOnAction(e -> abrirPelicula(pelicula));
        btnHeroDetalle.setOnAction(e -> abrirPelicula(pelicula));
        btnHeroComprar.setText(pelicula.isEnCartelera() ? "Comprar entradas" : "Ver detalle");
    }

    private void renderizar() {
        if (gridCartelera == null) {
            return;
        }
        String texto = busqueda.getText() == null ? "" : busqueda.getText().trim().toLowerCase(Locale.ROOT);
        String generoSeleccionado = genero.getValue() == null ? "Todos los géneros" : genero.getValue();

        gridCartelera.getChildren().clear();
        int visibles = 0;
        Pelicula primera = null;
        for (Pelicula pelicula : cartelera) {
            if (!coincide(pelicula, texto, generoSeleccionado)) {
                continue;
            }
            if (primera == null) {
                primera = pelicula;
            }
            gridCartelera.getChildren().add(Posters.crearTarjeta(pelicula, () -> abrirPelicula(pelicula)));
            visibles++;
        }
        if (visibles == 0) {
            gridCartelera.getChildren().add(estadoVacio("No encontramos películas con ese filtro."));
        }
        if (primera != null && (destacada == null || !coincide(destacada, texto, generoSeleccionado))) {
            destacada = primera;
            mostrarHero(primera);
        }

        gridProximamente.getChildren().clear();
        for (Pelicula pelicula : proximamente) {
            if (coincide(pelicula, texto, generoSeleccionado)) {
                gridProximamente.getChildren().add(Posters.crearTarjeta(pelicula, () -> abrirPelicula(pelicula)));
            }
        }
        if (gridProximamente.getChildren().isEmpty()) {
            gridProximamente.getChildren().add(estadoVacio("Muy pronto anunciaremos nuevas películas."));
        }
        contador.setText(visibles + " películas disponibles");
    }

    private boolean coincide(Pelicula pelicula, String texto, String generoSeleccionado) {
        boolean porTexto = texto.isBlank() || pelicula.getTitulo().toLowerCase(Locale.ROOT).contains(texto);
        boolean porGenero = "Todos los géneros".equals(generoSeleccionado)
                || pelicula.getGenero().equalsIgnoreCase(generoSeleccionado);
        return porTexto && porGenero;
    }

    private VBox estadoVacio(String mensaje) {
        Label icono = new Label("🎬");
        icono.getStyleClass().add("empty-icon");
        Label texto = new Label(mensaje);
        texto.getStyleClass().add("empty-text");
        VBox caja = new VBox(8, icono, texto);
        caja.setAlignment(Pos.CENTER);
        caja.getStyleClass().add("empty-state");
        caja.setPrefWidth(520);
        return caja;
    }

    private void abrirPelicula(Pelicula pelicula) {
        AppContext.get().getNavegacion().irA("pelicula", pelicula);
    }
}
