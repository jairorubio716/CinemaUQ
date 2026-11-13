package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.Posters;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PeliculaController implements VistaController {

    @FXML private Button btnVolver;
    @FXML private StackPane poster;
    @FXML private Label titulo;
    @FXML private Label meta;
    @FXML private Label sinopsis;
    @FXML private HBox badges;
    @FXML private FlowPane fechas;
    @FXML private FlowPane funciones;
    @FXML private VBox sinFunciones;

    private Pelicula pelicula;
    private final List<Funcion> funcionesPelicula = new ArrayList<>();
    private final Map<LocalDate, Button> botonesFecha = new LinkedHashMap<>();
    private LocalDate fechaSeleccionada;

    @Override
    public void alMostrar(Object dato) {
        if (!(dato instanceof Pelicula p)) {
            AppContext.get().getNavegacion().irA("cartelera");
            return;
        }
        pelicula = p;
        poster.getChildren().setAll(Posters.crearPoster(pelicula, 220, 325));
        titulo.setText(pelicula.getTitulo());
        meta.setText(pelicula.getGenero() + "  ·  " + pelicula.getDuracionMin()
                + " min  ·  " + Formato.clasificacion(pelicula.getClasificacion()));
        sinopsis.setText(pelicula.getSinopsis());
        badges.getChildren().setAll(
                badge(pelicula.getGenero(), "badge-info"),
                badge(pelicula.getDuracionMin() + " min", "badge"),
                badge("Clasificación " + Formato.clasificacion(pelicula.getClasificacion()), "badge-warning"),
                badge(pelicula.isEnCartelera() ? "En cartelera" : "Próximamente",
                        pelicula.isEnCartelera() ? "badge-success" : "badge-warning"));

        funcionesPelicula.clear();
        funcionesPelicula.addAll(AppContext.get().getFacade().consultarFunciones(pelicula));
        construirFechas();
    }

    @FXML
    private void volver() {
        AppContext.get().getNavegacion().volver();
    }

    private void construirFechas() {
        fechas.getChildren().clear();
        botonesFecha.clear();
        Map<LocalDate, List<Funcion>> porFecha = new LinkedHashMap<>();
        for (Funcion funcion : funcionesPelicula) {
            if (funcion.getFechaHora() == null) {
                continue;
            }
            porFecha.computeIfAbsent(funcion.getFechaHora().toLocalDate(), k -> new ArrayList<>()).add(funcion);
        }

        if (porFecha.isEmpty()) {
            fechaSeleccionada = null;
            funciones.getChildren().clear();
            sinFunciones.setVisible(true);
            sinFunciones.setManaged(true);
            return;
        }
        sinFunciones.setVisible(false);
        sinFunciones.setManaged(false);
        porFecha.keySet().stream().sorted().forEach(fecha -> {
            Button boton = new Button(Formato.dia(fecha));
            boton.getStyleClass().add("date-chip");
            boton.setOnAction(e -> seleccionarFecha(fecha));
            botonesFecha.put(fecha, boton);
            fechas.getChildren().add(boton);
        });
        seleccionarFecha(porFecha.keySet().stream().sorted().findFirst().orElse(null));
    }

    private void seleccionarFecha(LocalDate fecha) {
        fechaSeleccionada = fecha;
        for (Map.Entry<LocalDate, Button> entrada : botonesFecha.entrySet()) {
            entrada.getValue().getStyleClass().remove("date-chip-active");
            if (entrada.getKey().equals(fecha)) {
                entrada.getValue().getStyleClass().add("date-chip-active");
            }
        }
        funciones.getChildren().clear();
        for (Funcion funcion : funcionesPelicula) {
            if (funcion.getFechaHora() != null && funcion.getFechaHora().toLocalDate().equals(fecha)) {
                funciones.getChildren().add(crearTarjetaFuncion(funcion));
            }
        }
    }

    private VBox crearTarjetaFuncion(Funcion funcion) {
        Label hora = new Label(Formato.hora(funcion.getFechaHora()));
        hora.getStyleClass().add("function-time");
        Label sala = new Label("Sala " + (funcion.getSala() != null ? funcion.getSala().getNumeroSala() : "—")
                + " · " + (funcion.getSala() != null ? funcion.getSala().getTipoPantalla() : "—"));
        sala.getStyleClass().add("function-room");
        Label precio = new Label(Formato.moneda(funcion.getPrecioEntrada()));
        precio.getStyleClass().add("function-price");
        Label disponibles = new Label(funcion.getAsientosDisponibles().size() + " asientos disponibles");
        disponibles.getStyleClass().add("function-room");
        Label estado = new Label(Formato.texto(funcion.getEstado()));
        estado.getStyleClass().addAll("badge", estadoClass(funcion));

        Button elegir = new Button(funcion.estaVendible() ? "Elegir asientos" : "No disponible");
        elegir.getStyleClass().add(funcion.estaVendible() ? "primary-button" : "secondary-button");
        elegir.setDisable(!funcion.estaVendible());
        elegir.setOnAction(e -> {
            AppContext.get().getCompraFlow().iniciar(pelicula, funcion);
            AppContext.get().getNavegacion().irA("asientos");
        });

        VBox tarjeta = new VBox(7, hora, sala, disponibles, precio, estado, elegir);
        tarjeta.setPrefWidth(220);
        tarjeta.setAlignment(Pos.TOP_LEFT);
        tarjeta.getStyleClass().add("panel");
        return tarjeta;
    }

    private String estadoClass(Funcion funcion) {
        return switch (funcion.getEstado()) {
            case EN_VENTA -> "badge-success";
            case PROGRAMADA -> "badge-warning";
            case CANCELADA -> "badge-danger";
            case FINALIZADA -> "badge";
        };
    }

    private Label badge(String texto, String clase) {
        Label label = new Label(texto);
        label.getStyleClass().addAll("badge", clase);
        return label;
    }
}
