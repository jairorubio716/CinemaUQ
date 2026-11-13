package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.PuntoAcumulado;
import co.edu.uniquindio.cinemauq.model.Redencion;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PuntosController implements VistaController {

    @FXML private Label saldo;
    @FXML private Label estrategia;
    @FXML private HBox beneficios;
    @FXML private TableView<PuntoAcumulado> tablaLotes;
    @FXML private TableColumn<PuntoAcumulado, String> colCantidad;
    @FXML private TableColumn<PuntoAcumulado, String> colObtencion;
    @FXML private TableColumn<PuntoAcumulado, String> colVencimiento;
    @FXML private TableColumn<PuntoAcumulado, String> colEstado;
    @FXML private VBox canjes;

    @FXML
    public void initialize() {
        colCantidad.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getCantidad() + " pts"));
        colObtencion.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getFechaObtencion() != null ? Formato.fecha(cd.getValue().getFechaObtencion()) : "—"));
        colVencimiento.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getFechaVencimiento() != null ? Formato.fecha(cd.getValue().getFechaVencimiento()) : "—"));
        colEstado.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().estaVigente() ? "Vigente" : "Vencido"));
    }

    @Override
    public void alMostrar(Object dato) {
        Cliente cliente = AppContext.get().getSesion().getCliente();
        if (cliente == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        int total = AppContext.get().getFacade().consultarPuntos(cliente);
        saldo.setText(String.valueOf(total));
        estrategia.setText("Canjea tus puntos por entradas, productos o combos.");
        construirBeneficios(cliente);
        cargarLotes(cliente);
        cargarCanjes(cliente);
    }

    private void construirBeneficios(Cliente cliente) {
        beneficios.getChildren().clear();
        Map<String, Integer> equivalencias = AppContext.get().getFacade().equivalenciasPuntos();
        for (Map.Entry<String, Integer> entrada : equivalencias.entrySet()) {
            String codigo = codigoBeneficio(entrada.getKey());
            boolean disponible = AppContext.get().getFacade().puedeRedimir(cliente, codigo);
            VBox tarjeta = new VBox(7);
            tarjeta.getStyleClass().add("panel");
            tarjeta.setPrefWidth(228);
            Label icono = new Label(iconoBeneficio(codigo));
            icono.getStyleClass().add("empty-icon");
            Label titulo = new Label(entrada.getKey());
            titulo.getStyleClass().add("card-titulo");
            Label puntos = new Label(entrada.getValue() + " puntos");
            puntos.getStyleClass().add("price");
            Label detalle = new Label(detalleBeneficio(codigo));
            detalle.getStyleClass().add("card-meta");
            detalle.setWrapText(true);
            Button canjear = new Button(disponible ? "Canjear" : "Puntos insuficientes");
            canjear.getStyleClass().add(disponible ? "primary-button" : "ghost-button");
            canjear.setDisable(!disponible);
            canjear.setOnAction(e -> canjear(cliente, codigo, entrada.getValue()));
            tarjeta.getChildren().addAll(icono, titulo, puntos, detalle, canjear);
            beneficios.getChildren().add(tarjeta);
        }
    }

    private void cargarLotes(Cliente cliente) {
        List<PuntoAcumulado> lotes = new ArrayList<>();
        if (cliente.getCuentaPuntos() != null) {
            lotes.addAll(cliente.getCuentaPuntos().getPuntos());
        }
        lotes.sort(Comparator.comparing(PuntoAcumulado::getFechaVencimiento,
                Comparator.nullsLast(Comparator.naturalOrder())));
        tablaLotes.setItems(FXCollections.observableArrayList(lotes));
    }

    private void cargarCanjes(Cliente cliente) {
        canjes.getChildren().clear();
        List<Redencion> redenciones = AppContext.get().getFacade().getRedenciones(cliente);
        if (redenciones.isEmpty()) {
            Label vacio = new Label("Aún no has canjeado puntos.");
            vacio.getStyleClass().add("hint");
            canjes.getChildren().add(vacio);
            return;
        }
        int limite = Math.min(redenciones.size(), 6);
        for (int i = 0; i < limite; i++) {
            Redencion redencion = redenciones.get(i);
            Label codigo = new Label(redencion.getCodigo());
            codigo.getStyleClass().add("ticket-code");
            Label beneficio = new Label(Formato.capitalizar(redencion.getBeneficio().toLowerCase()) + "  ·  "
                    + redencion.getPuntos() + " pts");
            beneficio.getStyleClass().add("summary-row");
            Region espacio = new Region();
            HBox.setHgrow(espacio, javafx.scene.layout.Priority.ALWAYS);
            Label fecha = new Label(Formato.fechaHora(redencion.getFecha()));
            fecha.getStyleClass().add("hint");
            HBox fila = new HBox(12, codigo, beneficio, espacio, fecha);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.getStyleClass().add("panel");
            canjes.getChildren().add(fila);
        }
    }

    private void canjear(Cliente cliente, String codigo, int puntos) {
        boolean confirmar = Dialogos.confirmar("Canjear puntos",
                "¿Canjear " + puntos + " puntos por " + Formato.capitalizar(codigo.toLowerCase()) + "?");
        if (!confirmar) {
            return;
        }
        try {
            AppContext.get().getFacade().redimirBeneficio(cliente, codigo);
            List<Redencion> redenciones = AppContext.get().getFacade().getRedenciones(cliente);
            String comprobante = redenciones.isEmpty() ? "—" : redenciones.get(0).getCodigo();
            Dialogos.exito("Canje exitoso",
                    "Tu comprobante es " + comprobante + ". Preséntalo en taquilla.");
            alMostrar(null);
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        alMostrar(null);
    }

    private String codigoBeneficio(String nombre) {
        if (nombre == null) {
            return "";
        }
        String texto = nombre.toUpperCase();
        if (texto.startsWith("ENTRADA")) {
            return "ENTRADA";
        }
        if (texto.startsWith("PRODUCTO")) {
            return "PRODUCTO";
        }
        return "COMBO";
    }

    private String iconoBeneficio(String codigo) {
        return switch (codigo) {
            case "ENTRADA" -> "🎟";
            case "PRODUCTO" -> "🍿";
            default -> "🎬";
        };
    }

    private String detalleBeneficio(String codigo) {
        return switch (codigo) {
            case "ENTRADA" -> "Una boleta general para cualquier función 2D.";
            case "PRODUCTO" -> "Un producto de confitería a elección.";
            default -> "Combo personal: crispetas + bebida.";
        };
    }
}
