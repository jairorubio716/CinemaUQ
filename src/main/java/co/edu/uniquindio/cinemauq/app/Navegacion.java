package co.edu.uniquindio.cinemauq.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

public class Navegacion {

    private final StackPane contenido;
    private final Deque<String> historial = new ArrayDeque<>();
    private String vistaActual;

    public Navegacion(StackPane contenido) {
        this.contenido = contenido;
    }

    public void irA(String vista) {
        irA(vista, null);
    }

    public void irA(String vista, Object dato) {
        cargar(vista, dato, true);
    }

    public void volver() {
        if (historial.isEmpty()) {
            irA(AppContext.get().getSesion().esAdministrador() ? "admin-dashboard" : "cartelera");
            return;
        }
        String anterior = historial.removeLast();
        cargar(anterior, null, false);
    }

    public void recargar() {
        if (vistaActual != null) {
            cargar(vistaActual, null, false);
        }
    }

    public String getVistaActual() {
        return vistaActual;
    }

    private void cargar(String vista, Object dato, boolean registrar) {
        try {
            URL recurso = Navegacion.class.getResource(AppContext.VISTAS + vista + ".fxml");
            if (recurso == null) {
                throw new IOException("No se encontró la vista " + vista + ".fxml");
            }
            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();
            Object controlador = loader.getController();
            if (controlador instanceof VistaController controladorVista) {
                controladorVista.alMostrar(dato);
            }
            contenido.getChildren().setAll(root);
            if (registrar && vistaActual != null && !vistaActual.equals(vista)) {
                historial.addLast(vistaActual);
            }
            vistaActual = vista;
            if (AppContext.get().getShell() != null) {
                AppContext.get().getShell().actualizarChrome();
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            Dialogos.error("No se pudo abrir la vista '" + vista + "': " + e.getMessage());
        }
    }
}
