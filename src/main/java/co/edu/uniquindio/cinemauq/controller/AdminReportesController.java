package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminReportesController implements VistaController {

    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;
    @FXML private Label ventas;
    @FXML private Label entradas;
    @FXML private Label confiteria;
    @FXML private Label ocupacion;
    @FXML private Label resumen;
    @FXML private TableView<FilaReporte> tablaReporte;
    @FXML private TableColumn<FilaReporte, String> colReportePelicula;
    @FXML private TableColumn<FilaReporte, String> colReporteFunciones;
    @FXML private TableColumn<FilaReporte, String> colReporteEntradas;
    @FXML private TableColumn<FilaReporte, String> colReporteIngresos;

    @FXML
    public void initialize() {
        colReportePelicula.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().titulo()));
        colReporteFunciones.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                String.valueOf(cd.getValue().funciones().size())));
        colReporteEntradas.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                String.valueOf(cd.getValue().entradas())));
        colReporteIngresos.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.moneda(cd.getValue().ingresos())));
    }

    @Override
    public void alMostrar(Object dato) {
        if (admin() == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        if (fechaInicio.getValue() == null) {
            fechaInicio.setValue(LocalDate.now().minusDays(30));
        }
        if (fechaFin.getValue() == null) {
            fechaFin.setValue(LocalDate.now().plusDays(30));
        }
        generar();
    }

    @FXML
    private void generar() {
        LocalDate inicio = fechaInicio.getValue();
        LocalDate fin = fechaFin.getValue();
        if (inicio == null || fin == null) {
            Dialogos.info("Selecciona el periodo", "Indica la fecha inicial y final del reporte.");
            return;
        }
        if (fin.isBefore(inicio)) {
            Dialogos.error("La fecha final no puede ser anterior a la fecha inicial.");
            return;
        }
        List<Compra> compras = AppContext.get().getFacade().consultarCompras(admin());
        Map<String, FilaReporte> filas = new HashMap<>();
        double totalVentas = 0;
        double totalConfiteria = 0;
        int totalEntradas = 0;

        for (Compra compra : compras) {
            if (compra.getEstado() != EstadoCompra.PAGADA || compra.getFecha() == null) {
                continue;
            }
            LocalDate fecha = compra.getFecha().toLocalDate();
            if (fecha.isBefore(inicio) || fecha.isAfter(fin)) {
                continue;
            }
            totalVentas += compra.getTotal();
            for (IItemVendible item : compra.getConfiteria()) {
                totalConfiteria += item.obtenerPrecio();
            }
            for (Entrada entrada : compra.getEntradas()) {
                totalEntradas++;
                Funcion funcion = entrada.getFuncion();
                if (funcion == null || funcion.getPelicula() == null) {
                    continue;
                }
                String titulo = funcion.getPelicula().getTitulo();
                FilaReporte fila = filas.computeIfAbsent(titulo, FilaReporte::new);
                fila.entradas++;
                fila.ingresos += entrada.getPrecio();
                if (funcion.getIdFuncion() != null) {
                    fila.funciones.add(funcion.getIdFuncion());
                }
            }
        }

        List<FilaReporte> resultado = new ArrayList<>(filas.values());
        resultado.sort(Comparator.comparingDouble(FilaReporte::ingresos).reversed());
        tablaReporte.setItems(FXCollections.observableArrayList(resultado));
        ventas.setText(Formato.moneda(totalVentas));
        entradas.setText(String.valueOf(totalEntradas));
        confiteria.setText(Formato.moneda(totalConfiteria));
        ocupacion.setText(String.format("%.0f%%", ocupacionPromedio(inicio, fin)));
        resumen.setText("Periodo: " + Formato.fecha(inicio) + "  –  " + Formato.fecha(fin)
                + "  ·  " + resultado.size() + " películas con ventas");
    }

    private double ocupacionPromedio(LocalDate inicio, LocalDate fin) {
        double suma = 0;
        int cuenta = 0;
        for (Funcion funcion : AppContext.get().getFacade().getFunciones()) {
            if (funcion.getEstado() != EstadoFuncion.EN_VENTA || funcion.getFechaHora() == null
                    || funcion.getSala() == null || funcion.getSala().getCapacidad() <= 0) {
                continue;
            }
            LocalDate fecha = funcion.getFechaHora().toLocalDate();
            if (fecha.isBefore(inicio) || fecha.isAfter(fin)) {
                continue;
            }
            suma += funcion.getAsientosVendidos() * 100.0 / funcion.getSala().getCapacidad();
            cuenta++;
        }
        return cuenta == 0 ? 0 : suma / cuenta;
    }

    private Administrador admin() {
        return AppContext.get().getSesion().getAdministrador();
    }

    public static final class FilaReporte {

        private final String titulo;
        private final Set<String> funciones = new HashSet<>();
        private int entradas;
        private double ingresos;

        public FilaReporte(String titulo) {
            this.titulo = titulo;
        }

        public String titulo() {
            return titulo;
        }

        public Set<String> funciones() {
            return funciones;
        }

        public int entradas() {
            return entradas;
        }

        public double ingresos() {
            return ingresos;
        }
    }
}
