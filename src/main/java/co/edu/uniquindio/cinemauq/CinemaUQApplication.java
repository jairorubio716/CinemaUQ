package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.controller.ShellController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public class CinemaUQApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AppContext contexto = AppContext.get();
        contexto.setStage(stage);

        URL shell = CinemaUQApplication.class.getResource(AppContext.VISTAS + "shell.fxml");
        if (shell == null) {
            throw new IllegalStateException("No se encontró shell.fxml");
        }
        FXMLLoader loader = new FXMLLoader(shell);
        Parent root = loader.load();
        ShellController controlador = loader.getController();
        contexto.setShell(controlador);

        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
        double ancho = Math.min(1366, pantalla.getWidth() - 40);
        double alto = Math.min(820, pantalla.getHeight() - 80);

        Scene escena = new Scene(root, ancho, alto);
        escena.getStylesheets().add(AppContext.CSS);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("CinemaUQ · Tu próxima historia empieza aquí");
        stage.setMinWidth(Math.min(1180, ancho));
        stage.setMinHeight(Math.min(720, alto));
        stage.setResizable(true);
        stage.setScene(escena);
        stage.setX(pantalla.getMinX() + (pantalla.getWidth() - ancho) / 2);
        stage.setY(pantalla.getMinY() + (pantalla.getHeight() - alto) / 2);
        stage.setOnCloseRequest(e -> contexto.getFacade().guardarDatos());
        stage.show();

        contexto.getNavegacion().irA("login");
    }
}
