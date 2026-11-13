package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.service.ExportadorReporte;
import co.edu.uniquindio.cinemauq.service.Reporte;
import co.edu.uniquindio.cinemauq.service.TipoReporte;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminReportesController implements VistaController {

    @FXML private ComboBox<TipoReporte> tipoReporte;
    @FXML private DatePicker fechaInicio;
    @FXML private DatePicker fechaFin;
    @FXML private Label resumen;
    @FXML private HBox metricas;
    @FXML private TabPane tabs;
    @FXML private TableView<ObservableList<String>> tablaReporte;
    @FXML private BarChart<String, Number> graficoBarras;
    @FXML private CategoryAxis ejeBarrasX;
    @FXML private NumberAxis ejeBarrasY;
    @FXML private PieChart graficoPastel;
    @FXML private LineChart<String, Number> graficoLineas;
    @FXML private CategoryAxis ejeLineasX;
    @FXML private NumberAxis ejeLineasY;

    private Reporte reporteActual;

    @FXML
    public void initialize() {
        tipoReporte.getItems().setAll(TipoReporte.values());
        tipoReporte.setValue(TipoReporte.RPT_001);
        tablaReporte.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tipoReporte.setOnAction(evento -> {
            if (fechaInicio.getValue() != null && fechaFin.getValue() != null) {
                generar();
            }
        });
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
        if (tipoReporte.getValue() == null) {
            tipoReporte.setValue(TipoReporte.RPT_001);
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
        try {
            reporteActual = AppContext.get().getFacade()
                    .generarReporte(admin(), tipoReporte.getValue(), inicio, fin);
            renderizar(reporteActual);
        } catch (RuntimeException ex) {
            Dialogos.error(ex.getMessage() == null ? "No fue posible generar el reporte." : ex.getMessage());
        }
    }

    @FXML
    private void exportarTxt() {
        exportar(true);
    }

    @FXML
    private void exportarPdf() {
        exportar(false);
    }

    private void renderizar(Reporte reporte) {
        cargarMetricas(reporte);
        cargarTabla(reporte);
        cargarGraficos(reporte);
        resumen.setText(reporte.tipo().getCodigo() + "  ·  " + reporte.periodo()
                + "  ·  " + reporte.filas().size() + " registros");
    }

    private void cargarMetricas(Reporte reporte) {
        metricas.getChildren().clear();
        for (Reporte.Metrica metrica : reporte.metricas()) {
            Label etiqueta = new Label(metrica.etiqueta().toUpperCase(Locale.ROOT));
            etiqueta.getStyleClass().add("form-label");

            Label valor = new Label(metrica.valor());
            valor.getStyleClass().add("summary-total");
            valor.setWrapText(true);

            VBox tarjeta = new VBox(4, etiqueta, valor);
            tarjeta.getStyleClass().add("panel");
            tarjeta.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(tarjeta, Priority.ALWAYS);
            metricas.getChildren().add(tarjeta);
        }
    }

    private void cargarTabla(Reporte reporte) {
        tablaReporte.getColumns().clear();
        List<String> columnas = reporte.columnas();
        for (int i = 0; i < columnas.size(); i++) {
            final int indice = i;
            TableColumn<ObservableList<String>, String> columna =
                    new TableColumn<>(columnas.get(i));
            columna.setSortable(false);
            columna.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    cd.getValue() != null && indice < cd.getValue().size()
                            ? cd.getValue().get(indice) : ""));
            columna.setPrefWidth(1000.0 / Math.max(1, columnas.size()));
            tablaReporte.getColumns().add(columna);
        }

        List<ObservableList<String>> filas = new ArrayList<>();
        for (List<String> fila : reporte.filas()) {
            filas.add(FXCollections.observableArrayList(fila));
        }
        tablaReporte.setItems(FXCollections.observableArrayList(filas));
    }

    private void cargarGraficos(Reporte reporte) {
        graficoBarras.getData().clear();
        XYChart.Series<String, Number> barras = new XYChart.Series<>();
        barras.setName(reporte.tipo().getTitulo());
        for (Map.Entry<String, Double> entry : reporte.datosBarras().entrySet()) {
            barras.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        graficoBarras.getData().add(barras);
        graficoBarras.setTitle(reporte.tipo().getTitulo());
        ejeBarrasX.setLabel("Categoría");
        ejeBarrasY.setLabel("Valor");

        graficoPastel.getData().clear();
        for (Map.Entry<String, Double> entry : reporte.datosPastel().entrySet()) {
            graficoPastel.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        graficoPastel.setTitle("Distribución");

        graficoLineas.getData().clear();
        XYChart.Series<String, Number> lineas = new XYChart.Series<>();
        lineas.setName(reporte.tipo().getTitulo());
        for (Reporte.PuntoLinea punto : reporte.datosLinea()) {
            lineas.getData().add(new XYChart.Data<>(punto.etiqueta(), punto.valor()));
        }
        graficoLineas.getData().add(lineas);
        graficoLineas.setTitle("Evolución");
        ejeLineasX.setLabel("Periodo / categoría");
        ejeLineasY.setLabel("Valor");
    }

    private void exportar(boolean textoPlano) {
        if (reporteActual == null) {
            Dialogos.info("Sin reporte", "Genera un reporte antes de exportarlo.");
            return;
        }
        String extension = textoPlano ? ".txt" : ".pdf";
        FileChooser selector = new FileChooser();
        selector.setTitle(textoPlano ? "Guardar reporte TXT" : "Guardar reporte PDF");
        selector.setInitialFileName(reporteActual.nombreArchivo() + extension);
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                textoPlano ? "Archivo de texto (*.txt)" : "Documento PDF (*.pdf)",
                "*" + extension));

        File archivo = selector.showSaveDialog(fechaInicio.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        Path destino = archivo.toPath();
        if (!destino.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(extension)) {
            destino = Path.of(destino + extension);
        }

        try {
            if (textoPlano) {
                ExportadorReporte.exportarTxt(reporteActual, destino);
            } else {
                ExportadorReporte.exportarPdf(reporteActual, destino);
            }
            Dialogos.exito("Reporte exportado",
                    "Archivo generado en:\n" + destino.toAbsolutePath());
        } catch (IOException | RuntimeException ex) {
            Dialogos.error("No fue posible exportar el reporte: " + ex.getMessage());
        }
    }

    private Administrador admin() {
        return AppContext.get().getSesion().getAdministrador();
    }
}
