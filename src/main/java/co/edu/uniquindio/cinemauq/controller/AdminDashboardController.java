package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardController implements VistaController {

    @FXML private Label ingresos;
    @FXML private Label entradas;
    @FXML private Label funcionesActivas;
    @FXML private Label clientes;
    @FXML private Label recargasPendientes;
    @FXML private StackPane chartPeliculas;
    @FXML private StackPane chartOcupacion;
    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colCompraId;
    @FXML private TableColumn<Compra, String> colCompraCliente;
    @FXML private TableColumn<Compra, String> colCompraFecha;
    @FXML private TableColumn<Compra, String> colCompraTotal;
    @FXML private TableColumn<Compra, String> colCompraEstado;

    @FXML
    public void initialize() {
        colCompraId.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getIdCompra()));
        colCompraCliente.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getCliente() != null ? cd.getValue().getCliente().getNombre() : "—"));
        colCompraFecha.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.fechaHora(cd.getValue().getFecha())));
        colCompraTotal.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.moneda(cd.getValue().getTotal())));
        colCompraEstado.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.texto(cd.getValue().getEstado())));
    }

    @Override
    public void alMostrar(Object dato) {
        Administrador admin = admin();
        if (admin == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        List<Compra> compras = AppContext.get().getFacade().consultarCompras(admin);
        List<Funcion> funciones = AppContext.get().getFacade().getFunciones();
        List<SolicitudRecarga> solicitudes = AppContext.get().getFacade().getSolicitudesRecarga();

        double total = 0;
        int tickets = 0;
        for (Compra compra : compras) {
            if (compra.getEstado() == EstadoCompra.PAGADA) {
                total += compra.getTotal();
                tickets += compra.getEntradas().size();
            }
        }
        ingresos.setText(Formato.moneda(total));
        entradas.setText(String.valueOf(tickets));
        funcionesActivas.setText(String.valueOf(funciones.stream()
                .filter(f -> f.getEstado() == EstadoFuncion.EN_VENTA).count()));
        clientes.setText(String.valueOf(AppContext.get().getFacade().getClientes().size()));
        long pendientes = solicitudes.stream()
                .filter(s -> s.getEstado() == EstadoSolicitudRecarga.PENDIENTE).count();
        recargasPendientes.setText(String.valueOf(pendientes));

        compras.sort(Comparator.comparing(Compra::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        int limite = Math.min(compras.size(), 8);
        tablaCompras.setItems(FXCollections.observableArrayList(compras.subList(0, limite)));
        graficarPeliculas(compras);
        graficarOcupacion(funciones);
    }

    private void graficarPeliculas(List<Compra> compras) {
        Map<String, Integer> ventas = new HashMap<>();
        for (Compra compra : compras) {
            if (compra.getEstado() != EstadoCompra.PAGADA) {
                continue;
            }
            for (Entrada entrada : compra.getEntradas()) {
                if (entrada.getFuncion() != null && entrada.getFuncion().getPelicula() != null) {
                    String titulo = entrada.getFuncion().getPelicula().getTitulo();
                    ventas.merge(titulo, 1, Integer::sum);
                }
            }
        }
        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(ventas.entrySet());
        ranking.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCategoryGap(16);
        chart.setBarGap(4);
        chart.setPrefHeight(260);
        x.setTickLabelRotation(-22);
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        int limite = Math.min(ranking.size(), 6);
        for (int i = 0; i < limite; i++) {
            Map.Entry<String, Integer> item = ranking.get(i);
            serie.getData().add(new XYChart.Data<>(abreviar(item.getKey(), 18), item.getValue()));
        }
        chart.getData().add(serie);
        chartPeliculas.getChildren().setAll(chart);
    }

    private void graficarOcupacion(List<Funcion> funciones) {
        List<Funcion> ranking = new ArrayList<>(funciones);
        ranking.removeIf(f -> f.getEstado() != EstadoFuncion.EN_VENTA || f.getSala() == null
                || f.getSala().getCapacidad() <= 0);
        ranking.sort(Comparator.comparingInt(Funcion::getAsientosVendidos).reversed());
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis(0, 100, 20);
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCategoryGap(16);
        chart.setBarGap(4);
        chart.setPrefHeight(260);
        x.setTickLabelRotation(-22);
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        int limite = Math.min(ranking.size(), 6);
        for (int i = 0; i < limite; i++) {
            Funcion funcion = ranking.get(i);
            double porcentaje = funcion.getAsientosVendidos() * 100.0 / funcion.getSala().getCapacidad();
            String etiqueta = "S" + funcion.getSala().getNumeroSala() + " · "
                    + (funcion.getFechaHora() != null ? Formato.hora(funcion.getFechaHora()) : "—");
            serie.getData().add(new XYChart.Data<>(etiqueta, Math.round(porcentaje)));
        }
        chart.getData().add(serie);
        chartOcupacion.getChildren().setAll(chart);
    }

    @FXML
    private void irCatalogo() {
        AppContext.get().getNavegacion().irA("admin-catalogo");
    }

    @FXML
    private void irClientes() {
        AppContext.get().getNavegacion().irA("admin-clientes");
    }

    @FXML
    private void irFunciones() {
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

    @FXML
    private void actualizar() {
        alMostrar(null);
    }

    private String abreviar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 1) + "…";
    }

    private Administrador admin() {
        return AppContext.get().getSesion().getAdministrador();
    }
}
