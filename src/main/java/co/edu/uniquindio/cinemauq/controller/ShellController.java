package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Navegacion;
import co.edu.uniquindio.cinemauq.app.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShellController {

    @FXML private StackPane raiz;
    @FXML private HBox windowBar;
    @FXML private HBox barraSuperior;
    @FXML private HBox navCliente;
    @FXML private VBox sidebar;
    @FXML private StackPane contenido;
    @FXML private VBox toastLayer;
    @FXML private Label usuarioLabel;
    @FXML private Label rolLabel;

    @FXML private Button btnCartelera;
    @FXML private Button btnMisCompras;
    @FXML private Button btnTarjeta;
    @FXML private Button btnPuntos;
    @FXML private Button btnMaximizar;
    @FXML private Button btnDashboard;
    @FXML private Button btnCatalogo;
    @FXML private Button btnFunciones;
    @FXML private Button btnRecargas;
    @FXML private Button btnReportes;

    private final Map<String, Button> botones = new LinkedHashMap<>();
    private double desplazamientoX;
    private double desplazamientoY;

    @FXML
    public void initialize() {
        AppContext contexto = AppContext.get();
        Navegacion navegacion = new Navegacion(contenido);
        contexto.setNavegacion(navegacion);
        contexto.getToastNotificador().conectar(toastLayer);
        configurarVentana();

        botones.put("cartelera", btnCartelera);
        botones.put("mis-compras", btnMisCompras);
        botones.put("tarjeta", btnTarjeta);
        botones.put("puntos", btnPuntos);
        botones.put("admin-dashboard", btnDashboard);
        botones.put("admin-catalogo", btnCatalogo);
        botones.put("admin-funciones", btnFunciones);
        botones.put("admin-recargas", btnRecargas);
        botones.put("admin-reportes", btnReportes);

        actualizarChrome();
    }

    public void actualizarChrome() {
        Sesion sesion = AppContext.get().getSesion();
        boolean activa = sesion.haySesion();
        barraSuperior.setVisible(activa);
        barraSuperior.setManaged(activa);

        if (!activa) {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            return;
        }

        usuarioLabel.setText(sesion.nombre());
        boolean admin = sesion.esAdministrador();
        rolLabel.setText(admin ? "Administrador" : "Cliente CinemaUQ");
        navCliente.setVisible(!admin);
        navCliente.setManaged(!admin);
        sidebar.setVisible(admin);
        sidebar.setManaged(admin);

        Navegacion navegacion = AppContext.get().getNavegacion();
        String actual = navegacion != null ? navegacion.getVistaActual() : "";
        for (Map.Entry<String, Button> entrada : botones.entrySet()) {
            if (entrada.getValue() == null) {
                continue;
            }
            entrada.getValue().getStyleClass().remove("nav-button-active");
            entrada.getValue().getStyleClass().remove("sidebar-button-active");
            if (entrada.getKey().equals(actual)) {
                String clase = entrada.getKey().startsWith("admin-")
                        ? "sidebar-button-active" : "nav-button-active";
                entrada.getValue().getStyleClass().add(clase);
            }
        }
    }

    public VBox getToastLayer() {
        return toastLayer;
    }

    @FXML
    private void irInicio() {
        Sesion sesion = AppContext.get().getSesion();
        if (sesion.esAdministrador()) {
            AppContext.get().getNavegacion().irA("admin-dashboard");
        } else if (sesion.esCliente()) {
            AppContext.get().getNavegacion().irA("cartelera");
        } else {
            AppContext.get().getNavegacion().irA("login");
        }
    }

    @FXML
    private void irCartelera() {
        AppContext.get().getNavegacion().irA("cartelera");
    }

    @FXML
    private void irMisCompras() {
        AppContext.get().getNavegacion().irA("mis-compras");
    }

    @FXML
    private void irTarjeta() {
        AppContext.get().getNavegacion().irA("tarjeta");
    }

    @FXML
    private void irPuntos() {
        AppContext.get().getNavegacion().irA("puntos");
    }

    @FXML
    private void irDashboard() {
        AppContext.get().getNavegacion().irA("admin-dashboard");
    }

    @FXML
    private void irCatalogo() {
        AppContext.get().getNavegacion().irA("admin-catalogo");
    }

    @FXML
    private void irFuncionesAdmin() {
        AppContext.get().getNavegacion().irA("admin-funciones");
    }

    @FXML
    private void irRecargas() {
        AppContext.get().getNavegacion().irA("admin-recargas");
    }

    @FXML
    private void irReportes() {
        AppContext.get().getNavegacion().irA("admin-reportes");
    }

    private void configurarVentana() {
        Stage stage = AppContext.get().getStage();
        if (stage == null || windowBar == null) {
            return;
        }
        windowBar.setOnMousePressed(e -> {
            desplazamientoX = e.getScreenX() - stage.getX();
            desplazamientoY = e.getScreenY() - stage.getY();
        });
        windowBar.setOnMouseDragged(e -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - desplazamientoX);
                stage.setY(e.getScreenY() - desplazamientoY);
            }
        });
        windowBar.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                maximizar();
            }
        });
        stage.maximizedProperty().addListener((obs, anterior, maximizado) ->
                actualizarIconoMaximizar(maximizado));
        actualizarIconoMaximizar(stage.isMaximized());
    }

    @FXML
    private void minimizar() {
        Stage stage = AppContext.get().getStage();
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    @FXML
    private void maximizar() {
        Stage stage = AppContext.get().getStage();
        if (stage != null) {
            stage.setMaximized(!stage.isMaximized());
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = AppContext.get().getStage();
        if (stage != null) {
            stage.close();
        }
    }

    private void actualizarIconoMaximizar(boolean maximizado) {
        if (btnMaximizar != null) {
            btnMaximizar.setText(maximizado ? "❐" : "□");
        }
    }

    @FXML
    private void salir() {
        AppContext.get().getSesion().cerrar();
        AppContext.get().getCompraFlow().limpiar();
        AppContext.get().getNavegacion().irA("login");
    }
}
