package co.edu.uniquindio.cinemauq.app;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

public final class Dialogos {

    private Dialogos() {
    }

    public static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "No fue posible completar la operación", mensaje);
    }

    public static void exito(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = crear(Alert.AlertType.CONFIRMATION, titulo, mensaje);
        alerta.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);
        alerta.getDialogPane().lookupButton(ButtonType.OK).setStyle("-fx-background-color: #E5092C;");
        Optional<ButtonType> resultado = alerta.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    public static Optional<String> pedirTexto(String titulo, String mensaje, String valorInicial) {
        TextInputDialog dialogo = new TextInputDialog(valorInicial);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(mensaje);
        dialogo.initOwner(owner());
        aplicarEstilo(dialogo.getDialogPane());
        return dialogo.showAndWait();
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        crear(tipo, titulo, mensaje).showAndWait();
    }

    private static Alert crear(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        Label contenido = new Label(mensaje);
        contenido.setWrapText(true);
        contenido.setMaxWidth(440);
        alerta.getDialogPane().setContent(contenido);
        alerta.initOwner(owner());
        aplicarEstilo(alerta.getDialogPane());
        return alerta;
    }

    private static Stage owner() {
        return AppContext.get().getStage();
    }

    public static void aplicarEstilo(javafx.scene.control.DialogPane pane) {
        pane.getStylesheets().add(AppContext.CSS);
        pane.getStyleClass().add("dialogo-oscuro");
    }
}
