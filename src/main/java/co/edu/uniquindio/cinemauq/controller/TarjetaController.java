package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.TipoMovimiento;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TarjetaController implements VistaController {

    @FXML private Label tarjetaNumero;
    @FXML private Label tarjetaSaldo;
    @FXML private Label tarjetaEstado;
    @FXML private TableView<Movimiento> tablaMovimientos;
    @FXML private TableColumn<Movimiento, String> colFecha;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, String> colMonto;
    @FXML private TableColumn<Movimiento, String> colSaldo;
    @FXML private VBox solicitudes;
    @FXML private Label pendientes;

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(cd -> new ReadOnlyStringWrapper(Formato.fechaHora(cd.getValue().getFecha())));
        colTipo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(Formato.texto(cd.getValue().getTipo())));
        colMonto.setCellValueFactory(cd -> new ReadOnlyStringWrapper(montoConSigno(cd.getValue())));
        colSaldo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(Formato.moneda(cd.getValue().getSaldoResultante())));
    }

    @Override
    public void alMostrar(Object dato) {
        Cliente cliente = AppContext.get().getSesion().getCliente();
        if (cliente == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
        if (tarjeta == null) {
            tarjetaNumero.setText("•••• •••• •••• ----");
            tarjetaSaldo.setText("$ 0");
            tarjetaEstado.setText("Sin tarjeta");
            return;
        }
        String numero = tarjeta.getNroTarjeta();
        tarjetaNumero.setText("•••• •••• •••• " + (numero.length() > 4 ? numero.substring(numero.length() - 4) : numero));
        tarjetaSaldo.setText(Formato.moneda(tarjeta.getSaldo()));
        tarjetaEstado.setText(Formato.texto(tarjeta.getEstado()));

        List<Movimiento> movimientos = new ArrayList<>(tarjeta.getMovimientos());
        movimientos.sort(Comparator.comparing(Movimiento::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        tablaMovimientos.setItems(FXCollections.observableArrayList(movimientos));
        renderizarSolicitudes(cliente);
    }

    private void renderizarSolicitudes(Cliente cliente) {
        solicitudes.getChildren().clear();
        List<SolicitudRecarga> lista = new ArrayList<>(cliente.getSolicitudesRecarga());
        lista.sort(Comparator.comparing(SolicitudRecarga::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        long pendientesCount = lista.stream()
                .filter(s -> s.getEstado() == co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga.PENDIENTE)
                .count();
        pendientes.setText(pendientesCount + " solicitud(es) pendiente(s)");
        if (lista.isEmpty()) {
            Label vacio = new Label("No tienes solicitudes de recarga.");
            vacio.getStyleClass().add("hint");
            solicitudes.getChildren().add(vacio);
            return;
        }
        int limite = Math.min(lista.size(), 5);
        for (int i = 0; i < limite; i++) {
            SolicitudRecarga solicitud = lista.get(i);
            Label id = new Label(solicitud.getIdSolicitud());
            id.getStyleClass().add("summary-row");
            Label fecha = new Label(Formato.fechaHora(solicitud.getFecha()));
            fecha.getStyleClass().add("hint");
            Label monto = new Label(Formato.moneda(solicitud.getMonto()));
            monto.getStyleClass().add("function-price");
            Label estado = new Label(Formato.texto(solicitud.getEstado()));
            estado.getStyleClass().addAll("badge", claseSolicitud(solicitud));
            Region espacio = new Region();
            HBox.setHgrow(espacio, javafx.scene.layout.Priority.ALWAYS);
            HBox fila = new HBox(10, id, fecha, espacio, monto, estado);
            fila.setAlignment(Pos.CENTER_LEFT);
            solicitudes.getChildren().add(fila);
        }
    }

    private String claseSolicitud(SolicitudRecarga solicitud) {
        return switch (solicitud.getEstado()) {
            case PENDIENTE -> "badge-warning";
            case APROBADA -> "badge-success";
            case RECHAZADA -> "badge-danger";
        };
    }

    @FXML
    private void solicitarRecarga() {
        Cliente cliente = AppContext.get().getSesion().getCliente();
        if (cliente == null) {
            return;
        }
        Dialog<Double> dialogo = new Dialog<>();
        dialogo.setTitle("Solicitar recarga");
        dialogo.setHeaderText("¿Cuánto deseas recargar?");
        dialogo.initOwner(AppContext.get().getStage());
        Dialogos.aplicarEstilo(dialogo.getDialogPane());

        TextField monto = new TextField("50000");
        monto.setPromptText("Monto en COP");
        Label error = new Label();
        error.getStyleClass().add("form-error");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Monto"), 0, 0);
        grid.add(monto, 1, 0);
        grid.add(error, 1, 1);
        dialogo.getDialogPane().setContent(grid);

        ButtonType aceptar = new ButtonType("Solicitar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(aceptar, ButtonType.CANCEL);
        dialogo.setResultConverter(boton -> {
            if (boton != aceptar) {
                return null;
            }
            try {
                double valor = Double.parseDouble(monto.getText().replace(".", "").replace(",", "").trim());
                if (valor <= 0) {
                    error.setText("El monto debe ser mayor que cero.");
                    return null;
                }
                return valor;
            } catch (NumberFormatException e) {
                error.setText("Ingresa un monto válido.");
                return null;
            }
        });

        dialogo.showAndWait().ifPresent(valor -> {
            try {
                AppContext.get().getFacade().solicitarRecarga(cliente, valor);
                Dialogos.info("Solicitud creada",
                        "Tu solicitud de recarga por " + Formato.moneda(valor)
                                + " quedó pendiente de aprobación por un administrador.");
                alMostrar(null);
            } catch (CinemaUQException e) {
                Dialogos.error(e.getMessage());
            }
        });
    }

    private String montoConSigno(Movimiento movimiento) {
        String signo = movimiento.getTipo() == TipoMovimiento.COMPRA ? "− " : "+ ";
        return signo + Formato.moneda(movimiento.getMonto());
    }
}
