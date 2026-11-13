package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdminRecargasController implements VistaController {

    @FXML private Label pendientes;
    @FXML private Label montoPendiente;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private TableView<SolicitudRecarga> tablaSolicitudes;
    @FXML private TableColumn<SolicitudRecarga, String> colSolicitudId;
    @FXML private TableColumn<SolicitudRecarga, String> colSolicitudCliente;
    @FXML private TableColumn<SolicitudRecarga, String> colSolicitudFecha;
    @FXML private TableColumn<SolicitudRecarga, String> colSolicitudMonto;
    @FXML private TableColumn<SolicitudRecarga, String> colSolicitudEstado;

    @FXML
    public void initialize() {
        colSolicitudId.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getIdSolicitud()));
        colSolicitudCliente.setCellValueFactory(cd -> new ReadOnlyStringWrapper(clienteDe(cd.getValue())));
        colSolicitudFecha.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.fechaHora(cd.getValue().getFecha())));
        colSolicitudMonto.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.moneda(cd.getValue().getMonto())));
        colSolicitudEstado.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.texto(cd.getValue().getEstado())));
        filtroEstado.valueProperty().addListener((obs, old, val) -> aplicarFiltro());
    }

    @Override
    public void alMostrar(Object dato) {
        if (admin() == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        List<String> estados = new ArrayList<>();
        estados.add("Todas");
        for (EstadoSolicitudRecarga estado : EstadoSolicitudRecarga.values()) {
            estados.add(Formato.texto(estado));
        }
        String actual = filtroEstado.getValue();
        filtroEstado.setItems(FXCollections.observableArrayList(estados));
        filtroEstado.setValue(actual != null && estados.contains(actual) ? actual : "Todas");
        actualizar();
    }

    @FXML
    private void actualizar() {
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        if (tablaSolicitudes == null) {
            return;
        }
        List<SolicitudRecarga> solicitudes = new ArrayList<>(AppContext.get().getFacade().getSolicitudesRecarga());
        String estado = filtroEstado.getValue();
        if (estado != null && !estado.equals("Todas")) {
            solicitudes.removeIf(s -> !Formato.texto(s.getEstado()).equals(estado));
        }
        solicitudes.sort(Comparator.comparing(SolicitudRecarga::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        tablaSolicitudes.setItems(FXCollections.observableArrayList(solicitudes));

        long count = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitudRecarga.PENDIENTE).count();
        double total = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitudRecarga.PENDIENTE)
                .mapToDouble(SolicitudRecarga::getMonto).sum();
        pendientes.setText(String.valueOf(count));
        montoPendiente.setText(Formato.moneda(total));
    }

    @FXML
    private void aprobar() {
        SolicitudRecarga solicitud = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            Dialogos.info("Selecciona una solicitud", "Elige una solicitud pendiente para aprobarla.");
            return;
        }
        if (solicitud.getEstado() != EstadoSolicitudRecarga.PENDIENTE) {
            Dialogos.info("Solicitud procesada", "Esta solicitud ya fue aprobada o rechazada.");
            return;
        }
        if (!Dialogos.confirmar("Aprobar recarga",
                "¿Aprobar la recarga de " + Formato.moneda(solicitud.getMonto())
                        + " para " + clienteDe(solicitud) + "?")) {
            return;
        }
        try {
            AppContext.get().getFacade().aprobarRecarga(admin(), solicitud);
            Dialogos.exito("Recarga aprobada", "El saldo fue abonado a la tarjeta del cliente.");
            aplicarFiltro();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void rechazar() {
        SolicitudRecarga solicitud = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            Dialogos.info("Selecciona una solicitud", "Elige una solicitud pendiente para rechazarla.");
            return;
        }
        if (solicitud.getEstado() != EstadoSolicitudRecarga.PENDIENTE) {
            Dialogos.info("Solicitud procesada", "Esta solicitud ya fue aprobada o rechazada.");
            return;
        }
        if (!Dialogos.confirmar("Rechazar recarga",
                "¿Rechazar la solicitud de " + clienteDe(solicitud) + "?")) {
            return;
        }
        try {
            AppContext.get().getFacade().rechazarRecarga(admin(), solicitud);
            Dialogos.info("Recarga rechazada", "La solicitud quedó marcada como rechazada.");
            aplicarFiltro();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    private String clienteDe(SolicitudRecarga solicitud) {
        if (solicitud.getTarjeta() == null) {
            return "—";
        }
        String numero = solicitud.getTarjeta().getNroTarjeta();
        for (Cliente cliente : AppContext.get().getFacade().getClientes()) {
            if (cliente.getTarjetaVirtual() != null
                    && numero.equals(cliente.getTarjetaVirtual().getNroTarjeta())) {
                return cliente.getNombre();
            }
        }
        return "Tarjeta " + (numero.length() > 4 ? numero.substring(numero.length() - 4) : numero);
    }

    private Administrador admin() {
        return AppContext.get().getSesion().getAdministrador();
    }
}
