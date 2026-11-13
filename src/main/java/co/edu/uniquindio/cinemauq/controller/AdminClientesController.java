package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;

public class AdminClientesController implements VistaController {

    @FXML private Label totalClientes;
    @FXML private Label clientesConTarjeta;
    @FXML private Label totalCompras;
    @FXML private Label ingresos;
    @FXML private Label resumenClientes;
    @FXML private Label resumenCompras;

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colClienteId;
    @FXML private TableColumn<Cliente, String> colClienteNombre;
    @FXML private TableColumn<Cliente, String> colClienteUsuario;
    @FXML private TableColumn<Cliente, String> colClienteEmail;
    @FXML private TableColumn<Cliente, String> colClienteTarjeta;
    @FXML private TableColumn<Cliente, String> colClienteSaldo;
    @FXML private TableColumn<Cliente, String> colClientePuntos;
    @FXML private TableColumn<Cliente, String> colClienteCompras;

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colCompraId;
    @FXML private TableColumn<Compra, String> colCompraCliente;
    @FXML private TableColumn<Compra, String> colCompraFecha;
    @FXML private TableColumn<Compra, String> colCompraEntradas;
    @FXML private TableColumn<Compra, String> colCompraConfiteria;
    @FXML private TableColumn<Compra, String> colCompraTotal;
    @FXML private TableColumn<Compra, String> colCompraEstado;

    @FXML
    public void initialize() {
        colClienteId.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getIdCliente()));
        colClienteNombre.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
        colClienteUsuario.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getUsuario()));
        colClienteEmail.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getEmail()));
        colClienteTarjeta.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getTarjetaVirtual() != null
                        ? cd.getValue().getTarjetaVirtual().getNroTarjeta() : "Sin tarjeta"));
        colClienteSaldo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getTarjetaVirtual() != null
                        ? Formato.moneda(cd.getValue().getTarjetaVirtual().getSaldo()) : "—"));
        colClientePuntos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                String.valueOf(AppContext.get().getFacade().consultarPuntos(cd.getValue()))));
        colClienteCompras.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                String.valueOf(comprasDe(cd.getValue()).size())));

        colCompraId.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getIdCompra()));
        colCompraCliente.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getCliente() != null ? cd.getValue().getCliente().getNombre() : "—"));
        colCompraFecha.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.fechaHora(cd.getValue().getFecha())));
        colCompraEntradas.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                String.valueOf(entradasDe(cd.getValue()).size())));
        colCompraConfiteria.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                String.valueOf(confiteriaDe(cd.getValue()).size())));
        colCompraTotal.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.moneda(cd.getValue().getTotal())));
        colCompraEstado.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.texto(cd.getValue().getEstado())));
    }

    @Override
    public void alMostrar(Object dato) {
        if (admin() == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        recargar();
    }

    @FXML
    private void actualizar() {
        recargar();
    }

    private void recargar() {
        var facade = AppContext.get().getFacade();
        List<Cliente> clientes = facade.consultarClientes(admin());
        List<Compra> compras = facade.consultarCompras(admin());

        clientes.sort(Comparator.comparing(Cliente::getNombre,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        compras.sort(Comparator.comparing(Compra::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        tablaClientes.setItems(FXCollections.observableArrayList(clientes));
        tablaCompras.setItems(FXCollections.observableArrayList(compras));

        long conTarjeta = clientes.stream()
                .filter(cliente -> cliente.getTarjetaVirtual() != null).count();
        double totalPagado = compras.stream()
                .filter(compra -> compra.getEstado() == EstadoCompra.PAGADA)
                .mapToDouble(Compra::getTotal).sum();

        totalClientes.setText(String.valueOf(clientes.size()));
        clientesConTarjeta.setText(String.valueOf(conTarjeta));
        totalCompras.setText(String.valueOf(compras.size()));
        ingresos.setText(Formato.moneda(totalPagado));
        resumenClientes.setText(clientes.size() + " clientes registrados");
        resumenCompras.setText(compras.size() + " compras en total");
    }

    private List<Compra> comprasDe(Cliente cliente) {
        return cliente.getCompras() != null ? cliente.getCompras() : List.of();
    }

    private List<?> entradasDe(Compra compra) {
        return compra.getEntradas() != null ? compra.getEntradas() : List.of();
    }

    private List<?> confiteriaDe(Compra compra) {
        return compra.getConfiteria() != null ? compra.getConfiteria() : List.of();
    }

    private Administrador admin() {
        return AppContext.get().getSesion().getAdministrador();
    }
}
