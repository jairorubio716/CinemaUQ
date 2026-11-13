package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.CompraFlow;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.CategoriaAsiento;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Sala;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AsientosController implements VistaController {

    @FXML private Label tituloPelicula;
    @FXML private Label infoFuncion;
    @FXML private GridPane mapa;
    @FXML private ScrollPane scrollMapa;
    @FXML private HBox leyenda;
    @FXML private Label resumenFuncion;
    @FXML private Label asientosTexto;
    @FXML private Label totalEntradas;
    @FXML private Label total;
    @FXML private Button btnContinuar;

    private final Scale escala = new Scale(1, 1);
    private CompraFlow flow;

    @FXML
    public void initialize() {
        mapa.getTransforms().add(escala);
        construirLeyenda();
    }

    @Override
    public void alMostrar(Object dato) {
        flow = AppContext.get().getCompraFlow();
        Funcion funcion = flow.getFuncion();
        if (funcion == null || flow.getPelicula() == null) {
            AppContext.get().getNavegacion().irA("cartelera");
            return;
        }
        tituloPelicula.setText(flow.getPelicula().getTitulo());
        infoFuncion.setText(Formato.fechaHora(funcion.getFechaHora()) + "  ·  Sala "
                + (funcion.getSala() != null ? funcion.getSala().getNumeroSala() : "—")
                + "  ·  " + (funcion.getSala() != null ? funcion.getSala().getTipoPantalla() : "—"));
        resumenFuncion.setText(flow.getPelicula().getTitulo() + "\n" + Formato.fechaHora(funcion.getFechaHora()));
        construirMapa(funcion);
        actualizarResumen();
    }

    private void construirMapa(Funcion funcion) {
        mapa.getChildren().clear();
        mapa.setAlignment(Pos.CENTER);
        mapa.setHgap(6);
        mapa.setVgap(6);
        Sala sala = funcion.getSala();
        if (sala == null) {
            return;
        }

        Map<String, List<Asiento>> porFila = new LinkedHashMap<>();
        List<Asiento> ordenados = new ArrayList<>(sala.getAsientos());
        ordenados.sort(Comparator.comparing(Asiento::getFila).thenComparingInt(Asiento::getColumna));
        for (Asiento asiento : ordenados) {
            porFila.computeIfAbsent(asiento.getFila(), k -> new ArrayList<>()).add(asiento);
        }

        int filaGrid = 0;
        for (Map.Entry<String, List<Asiento>> entrada : porFila.entrySet()) {
            Label etiqueta = new Label(entrada.getKey());
            etiqueta.getStyleClass().add("muted");
            etiqueta.setMinWidth(20);
            mapa.add(etiqueta, 0, filaGrid);

            int columnaGrid = 1;
            for (Asiento asiento : entrada.getValue()) {
                Button boton = crearBotonAsiento(asiento, funcion);
                mapa.add(boton, columnaGrid++, filaGrid);
            }
            filaGrid++;
        }
    }

    private Button crearBotonAsiento(Asiento asiento, Funcion funcion) {
        Button boton = new Button(String.valueOf(asiento.getNumero()));
        boton.getStyleClass().add("seat");
        boton.getStyleClass().add(claseCategoria(asiento.getCategoria()));
        boolean disponible = funcion.estaDisponible(asiento);
        if (!disponible) {
            boton.getStyleClass().add("seat-occupied");
            boton.setDisable(true);
        }
        if (flow.estaSeleccionado(asiento)) {
            boton.getStyleClass().add("seat-selected");
        }
        Tooltip.install(boton, new Tooltip("Fila " + asiento.getFila() + " · Asiento " + asiento.getNumero()
                + " · " + Formato.texto(asiento.getCategoria())));
        boton.setOnAction(e -> {
            boolean seleccionado = flow.alternarAsiento(asiento);
            boton.getStyleClass().remove("seat-selected");
            if (seleccionado) {
                boton.getStyleClass().add("seat-selected");
            }
            actualizarResumen();
        });
        return boton;
    }

    private String claseCategoria(CategoriaAsiento categoria) {
        return switch (categoria) {
            case PREMIUM -> "seat-premium";
            case VIP -> "seat-vip";
            case GENERAL -> "seat-general";
        };
    }

    private void construirLeyenda() {
        leyenda.getChildren().setAll(
                leyenda("General", "legend-general"),
                leyenda("Premium", "legend-premium"),
                leyenda("VIP", "legend-vip"),
                leyenda("Seleccionado", "legend-selected"),
                leyenda("Ocupado", "legend-occupied"));
    }

    private HBox leyenda(String texto, String clase) {
        Region punto = new Region();
        punto.getStyleClass().addAll("legend-dot", clase);
        Label label = new Label(texto);
        label.getStyleClass().add("hint");
        HBox caja = new HBox(6, punto, label);
        caja.setAlignment(Pos.CENTER_LEFT);
        return caja;
    }

    private void actualizarResumen() {
        List<Asiento> asientos = flow.getAsientos();
        asientos.sort(Comparator.comparing(Asiento::getFila).thenComparingInt(Asiento::getColumna));
        if (asientos.isEmpty()) {
            asientosTexto.setText("Todavía no has elegido asientos.");
        } else {
            StringBuilder texto = new StringBuilder();
            for (Asiento asiento : asientos) {
                if (texto.length() > 0) {
                    texto.append(", ");
                }
                texto.append(asiento.getFila()).append(asiento.getNumero());
            }
            asientosTexto.setText(texto + "  ·  " + asientos.size() + " entrada(s)");
        }
        totalEntradas.setText(Formato.moneda(flow.totalEntradas()));
        total.setText(Formato.moneda(flow.totalEntradas()));
        btnContinuar.setDisable(asientos.isEmpty());
    }

    @FXML
    private void continuar() {
        if (flow.getAsientos().isEmpty()) {
            return;
        }
        AppContext.get().getNavegacion().irA("confiteria");
    }

    @FXML
    private void volver() {
        AppContext.get().getNavegacion().irA("pelicula", flow.getPelicula());
    }

    @FXML
    private void zoomIn() {
        cambiarZoom(0.1);
    }

    @FXML
    private void zoomOut() {
        cambiarZoom(-0.1);
    }

    @FXML
    private void zoomReset() {
        escala.setX(1);
        escala.setY(1);
    }

    private void cambiarZoom(double delta) {
        double nuevo = Math.max(0.6, Math.min(1.6, escala.getX() + delta));
        escala.setX(nuevo);
        escala.setY(nuevo);
    }
}
