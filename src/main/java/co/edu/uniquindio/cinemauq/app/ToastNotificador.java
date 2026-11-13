package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.patrones.observer.INotificable;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Observador del notificador de negocio que muestra toasts dentro de la interfaz.
 */
public class ToastNotificador implements INotificable {

    private VBox capa;

    public void conectar(VBox capa) {
        this.capa = capa;
    }

    @Override
    public void actualizar(String evento) {
        if (evento == null || evento.isBlank()) {
            return;
        }
        Platform.runLater(() -> mostrar(evento));
    }

    public void mostrar(String mensaje) {
        if (capa == null) {
            return;
        }
        Label icono = new Label("✓");
        icono.getStyleClass().add("toast-icon");
        Label texto = new Label(mensaje);
        texto.setWrapText(true);
        texto.getStyleClass().add("toast-text");
        HBox.setHgrow(texto, Priority.ALWAYS);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox toast = new HBox(12, icono, texto, espacio);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setMaxWidth(460);
        toast.getStyleClass().add("toast");

        capa.getChildren().add(toast);

        FadeTransition entrada = new FadeTransition(Duration.millis(220), toast);
        entrada.setFromValue(0);
        entrada.setToValue(1);
        PauseTransition pausa = new PauseTransition(Duration.seconds(4.2));
        FadeTransition salida = new FadeTransition(Duration.millis(260), toast);
        salida.setFromValue(1);
        salida.setToValue(0);
        SequentialTransition secuencia = new SequentialTransition(entrada, pausa, salida);
        secuencia.setOnFinished(e -> capa.getChildren().remove(toast));
        secuencia.play();
    }
}
