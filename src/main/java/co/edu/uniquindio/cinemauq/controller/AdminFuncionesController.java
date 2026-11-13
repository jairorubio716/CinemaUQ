package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Sala;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminFuncionesController implements VistaController {

    @FXML private ComboBox<String> filtroPelicula;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, String> colFuncionPelicula;
    @FXML private TableColumn<Funcion, String> colFuncionSala;
    @FXML private TableColumn<Funcion, String> colFuncionFecha;
    @FXML private TableColumn<Funcion, String> colFuncionPrecio;
    @FXML private TableColumn<Funcion, String> colFuncionEstado;
    @FXML private TableColumn<Funcion, String> colFuncionVendidas;
    @FXML private TableColumn<Funcion, String> colFuncionOcupacion;
    @FXML private Label resumen;

    @FXML
    public void initialize() {
        colFuncionPelicula.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getPelicula() != null ? cd.getValue().getPelicula().getTitulo() : "—"));
        colFuncionSala.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getSala() != null ? "Sala " + cd.getValue().getSala().getNumeroSala() : "—"));
        colFuncionFecha.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.fechaHora(cd.getValue().getFechaHora())));
        colFuncionPrecio.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.moneda(cd.getValue().getPrecioEntrada())));
        colFuncionEstado.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.texto(cd.getValue().getEstado())));
        colFuncionVendidas.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                String.valueOf(cd.getValue().getAsientosVendidos())));
        colFuncionOcupacion.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                ocupacion(cd.getValue())));
        filtroPelicula.valueProperty().addListener((obs, old, val) -> aplicarFiltros());
        filtroEstado.valueProperty().addListener((obs, old, val) -> aplicarFiltros());
    }

    @Override
    public void alMostrar(Object dato) {
        if (admin() == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        cargarFiltros();
        recargar();
    }

    private void cargarFiltros() {
        String peliculaActual = filtroPelicula.getValue();
        String estadoActual = filtroEstado.getValue();
        List<String> peliculas = new ArrayList<>();
        peliculas.add("Todas");
        for (Pelicula pelicula : AppContext.get().getFacade().getPeliculas()) {
            peliculas.add(pelicula.getTitulo());
        }
        filtroPelicula.setItems(FXCollections.observableArrayList(peliculas));
        filtroPelicula.setValue(peliculaActual != null && peliculas.contains(peliculaActual)
                ? peliculaActual : "Todas");

        List<String> estados = new ArrayList<>();
        estados.add("Todos");
        for (EstadoFuncion estado : EstadoFuncion.values()) {
            estados.add(Formato.texto(estado));
        }
        filtroEstado.setItems(FXCollections.observableArrayList(estados));
        filtroEstado.setValue(estadoActual != null && estados.contains(estadoActual)
                ? estadoActual : "Todos");
    }

    private void recargar() {
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (tablaFunciones == null) {
            return;
        }
        List<Funcion> funciones = new ArrayList<>(AppContext.get().getFacade().getFunciones());
        String pelicula = filtroPelicula.getValue();
        String estado = filtroEstado.getValue();
        funciones.removeIf(f -> {
            boolean okPelicula = pelicula == null || pelicula.equals("Todas")
                    || (f.getPelicula() != null && pelicula.equals(f.getPelicula().getTitulo()));
            boolean okEstado = estado == null || estado.equals("Todos")
                    || Formato.texto(f.getEstado()).equals(estado);
            return !okPelicula || !okEstado;
        });
        funciones.sort(java.util.Comparator.comparing(Funcion::getFechaHora,
                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));
        tablaFunciones.setItems(FXCollections.observableArrayList(funciones));
        resumen.setText(funciones.size() + " funciones · "
                + funciones.stream().filter(f -> f.getEstado() == EstadoFuncion.EN_VENTA).count()
                + " con venta activa");
    }

    @FXML
    private void nuevaFuncion() {
        formularioFuncion(null);
    }

    @FXML
    private void editarFuncion() {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Dialogos.info("Selecciona una función", "Elige una función de la tabla para editarla.");
            return;
        }
        formularioFuncion(funcion);
    }

    @FXML
    private void activarVenta() {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Dialogos.info("Selecciona una función", "Elige la función que deseas habilitar.");
            return;
        }
        if (!Dialogos.confirmar("Habilitar venta",
                "¿Habilitar la venta de entradas para esta función?")) {
            return;
        }
        ejecutar(() -> AppContext.get().getFacade().activarVentaFuncion(admin(), funcion));
    }

    @FXML
    private void cancelarFuncion() {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Dialogos.info("Selecciona una función", "Elige la función que deseas cancelar.");
            return;
        }
        if (!Dialogos.confirmar("Cancelar función",
                "Se reembolsará el 100% a las compras pagadas afectadas (RN-06).\n"
                        + "¿Cancelar la función del " + Formato.fechaHora(funcion.getFechaHora()) + "?")) {
            return;
        }
        try {
            double total = AppContext.get().getFacade().cancelarFuncion(admin(), funcion);
            Dialogos.exito("Función cancelada",
                    "Se reembolsaron " + Formato.moneda(total) + " a los clientes afectados.");
            aplicarFiltros();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void eliminarFuncion() {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Dialogos.info("Selecciona una función", "Elige la función que deseas eliminar.");
            return;
        }
        if (!Dialogos.confirmar("Eliminar función",
                "Solo se pueden eliminar funciones sin entradas vendidas.\n¿Deseas continuar?")) {
            return;
        }
        ejecutar(() -> AppContext.get().getFacade().eliminarFuncion(admin(), funcion.getIdFuncion()));
    }

    @FXML
    private void actualizar() {
        cargarFiltros();
        aplicarFiltros();
    }

    private void formularioFuncion(Funcion existente) {
        ComboBox<Pelicula> pelicula = new ComboBox<>(
                FXCollections.observableArrayList(AppContext.get().getFacade().getPeliculas()));
        pelicula.setValue(existente != null ? existente.getPelicula() : null);
        ComboBox<Sala> sala = new ComboBox<>(
                FXCollections.observableArrayList(AppContext.get().getFacade().getSalas()));
        sala.setValue(existente != null ? existente.getSala() : null);
        DatePicker fecha = new DatePicker(existente != null && existente.getFechaHora() != null
                ? existente.getFechaHora().toLocalDate() : LocalDate.now().plusDays(1));
        TextField hora = new TextField(existente != null && existente.getFechaHora() != null
                ? existente.getFechaHora().toLocalTime().toString() : "19:30");
        TextField precio = new TextField(existente != null ? String.valueOf(existente.getPrecioEntrada()) : "18000");
        ComboBox<EstadoFuncion> estado = new ComboBox<>(
                FXCollections.observableArrayList(EstadoFuncion.values()));
        estado.setValue(existente != null ? existente.getEstado() : EstadoFuncion.PROGRAMADA);

        GridPane grid = grid();
        agregar(grid, 0, "Película", pelicula);
        agregar(grid, 1, "Sala", sala);
        agregar(grid, 2, "Fecha", fecha);
        agregar(grid, 3, "Hora (HH:mm)", hora);
        agregar(grid, 4, "Precio entrada", precio);
        agregar(grid, 5, "Estado", estado);

        if (!mostrar(existente == null ? "Nueva función" : "Editar función", grid)) {
            return;
        }
        try {
            if (pelicula.getValue() == null || sala.getValue() == null || fecha.getValue() == null) {
                throw new IllegalArgumentException("Selecciona película, sala y fecha.");
            }
            LocalTime tiempo;
            try {
                tiempo = LocalTime.parse(hora.getText().trim());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("La hora debe tener formato HH:mm, por ejemplo 19:30.");
            }
            LocalDateTime fechaHora = LocalDateTime.of(fecha.getValue(), tiempo);
            double valor = decimal(precio.getText(), "El precio");
            if (existente == null) {
                AppContext.get().getFacade().crearFuncion(admin(), pelicula.getValue(), sala.getValue(),
                        fechaHora, valor, estado.getValue());
            } else {
                existente.setPelicula(pelicula.getValue());
                existente.setSala(sala.getValue());
                existente.setFechaHora(fechaHora);
                existente.setPrecioEntrada(valor);
                existente.setEstado(estado.getValue());
                AppContext.get().getFacade().actualizarFuncion(admin(), existente);
            }
            cargarFiltros();
            aplicarFiltros();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private String ocupacion(Funcion funcion) {
        if (funcion.getSala() == null || funcion.getSala().getCapacidad() <= 0) {
            return "—";
        }
        double porcentaje = funcion.getAsientosVendidos() * 100.0 / funcion.getSala().getCapacidad();
        return String.format("%.0f%%", porcentaje);
    }

    private boolean mostrar(String titulo, GridPane grid) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        dialogo.initOwner(AppContext.get().getStage());
        Dialogos.aplicarEstilo(dialogo.getDialogPane());
        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
        Optional<ButtonType> resultado = dialogo.showAndWait();
        return resultado.isPresent() && resultado.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    private GridPane grid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(8, 4, 4, 4));
        grid.setPrefWidth(460);
        return grid;
    }

    private void agregar(GridPane grid, int fila, String etiqueta, Node control) {
        Label label = new Label(etiqueta);
        label.getStyleClass().add("form-label");
        grid.add(label, 0, fila);
        grid.add(control, 1, fila);
        if (control instanceof TextField || control instanceof ComboBox || control instanceof DatePicker) {
            control.setStyle("-fx-pref-width: 290;");
        }
    }

    private void ejecutar(Runnable accion) {
        try {
            accion.run();
            aplicarFiltros();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private double decimal(String texto, String campo) {
        try {
            double valor = Double.parseDouble(texto.replace(".", "").replace(",", ".").trim());
            if (valor <= 0) {
                throw new NumberFormatException();
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser mayor que cero.");
        }
    }

    private Administrador admin() {
        return AppContext.get().getSesion().getAdministrador();
    }

    private String mensaje(RuntimeException e) {
        return e.getMessage() != null ? e.getMessage() : "No fue posible guardar los cambios.";
    }
}
