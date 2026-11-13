package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.model.TipoPromocion;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminCatalogoController implements VistaController {

    @FXML private Label contadorPeliculas;
    @FXML private Label contadorSalas;
    @FXML private Label contadorProductos;
    @FXML private Label contadorCombos;
    @FXML private Label contadorPromociones;

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, String> colPeliculaTitulo;
    @FXML private TableColumn<Pelicula, String> colPeliculaGenero;
    @FXML private TableColumn<Pelicula, String> colPeliculaDuracion;
    @FXML private TableColumn<Pelicula, String> colPeliculaClasificacion;
    @FXML private TableColumn<Pelicula, String> colPeliculaCartelera;

    @FXML private TableView<Sala> tablaSalas;
    @FXML private TableColumn<Sala, String> colSalaNumero;
    @FXML private TableColumn<Sala, String> colSalaDimensiones;
    @FXML private TableColumn<Sala, String> colSalaPantalla;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoNombre;
    @FXML private TableColumn<Producto, String> colProductoTipo;
    @FXML private TableColumn<Producto, String> colProductoPrecio;

    @FXML private TableView<Combo> tablaCombos;
    @FXML private TableColumn<Combo, String> colComboNombre;
    @FXML private TableColumn<Combo, String> colComboItems;
    @FXML private TableColumn<Combo, String> colComboPrecio;

    @FXML private TableView<Promocion> tablaPromociones;
    @FXML private TableColumn<Promocion, String> colPromocionNombre;
    @FXML private TableColumn<Promocion, String> colPromocionTipo;
    @FXML private TableColumn<Promocion, String> colPromocionValor;
    @FXML private TableColumn<Promocion, String> colPromocionVigencia;
    @FXML private TableColumn<Promocion, String> colPromocionEstado;

    @FXML
    public void initialize() {
        colPeliculaTitulo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getTitulo()));
        colPeliculaGenero.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getGenero()));
        colPeliculaDuracion.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getDuracionMin() + " min"));
        colPeliculaClasificacion.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.clasificacion(cd.getValue().getClasificacion())));
        colPeliculaCartelera.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().isEnCartelera() ? "En cartelera" : "Próximamente"));

        colSalaNumero.setCellValueFactory(cd -> new ReadOnlyStringWrapper("Sala " + cd.getValue().getNumeroSala()));
        colSalaDimensiones.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().getFilas() + " × " + cd.getValue().getColumnas()
                        + "  (" + cd.getValue().getCapacidad() + " sillas)"));
        colSalaPantalla.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getTipoPantalla()));

        colProductoNombre.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
        colProductoTipo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(Formato.texto(cd.getValue().getTipo())));
        colProductoPrecio.setCellValueFactory(cd -> new ReadOnlyStringWrapper(Formato.moneda(cd.getValue().getPrecio())));

        colComboNombre.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
        colComboItems.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getItems().size() + " ítems"));
        colComboPrecio.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                Formato.moneda(cd.getValue().obtenerPrecio())));

        colPromocionNombre.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombre()));
        colPromocionTipo.setCellValueFactory(cd -> new ReadOnlyStringWrapper(Formato.texto(cd.getValue().getTipo())));
        colPromocionValor.setCellValueFactory(cd -> new ReadOnlyStringWrapper(valorPromocion(cd.getValue())));
        colPromocionVigencia.setCellValueFactory(cd -> new ReadOnlyStringWrapper(vigencia(cd.getValue())));
        colPromocionEstado.setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                cd.getValue().estaVigente() ? "Vigente" : "No vigente"));
    }

    @Override
    public void alMostrar(Object dato) {
        if (admin() == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        recargar();
    }

    private void recargar() {
        var facade = AppContext.get().getFacade();
        tablaPeliculas.setItems(FXCollections.observableArrayList(facade.getPeliculas()));
        tablaSalas.setItems(FXCollections.observableArrayList(facade.getSalas()));
        tablaProductos.setItems(FXCollections.observableArrayList(facade.getProductos()));
        tablaCombos.setItems(FXCollections.observableArrayList(facade.getCombos()));
        tablaPromociones.setItems(FXCollections.observableArrayList(facade.getPromociones()));
        contadorPeliculas.setText(facade.getPeliculas().size() + " películas");
        contadorSalas.setText(facade.getSalas().size() + " salas");
        contadorProductos.setText(facade.getProductos().size() + " productos");
        contadorCombos.setText(facade.getCombos().size() + " combos");
        contadorPromociones.setText(facade.getPromociones().size() + " promociones");
    }

    @FXML
    private void nuevaPelicula() {
        formularioPelicula(null);
    }

    @FXML
    private void editarPelicula() {
        Pelicula pelicula = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (pelicula == null) {
            Dialogos.info("Selecciona una película", "Elige una película de la tabla para editarla.");
            return;
        }
        formularioPelicula(pelicula);
    }

    @FXML
    private void eliminarPelicula() {
        Pelicula pelicula = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (pelicula == null || !Dialogos.confirmar("Eliminar película",
                "¿Eliminar '" + pelicula.getTitulo() + "'? Esta acción no se puede deshacer.")) {
            return;
        }
        ejecutar(() -> AppContext.get().getFacade().eliminarPelicula(admin(), pelicula.getIdPelicula()));
    }

    @FXML
    private void nuevaSala() {
        formularioSala(null);
    }

    @FXML
    private void editarSala() {
        Sala sala = tablaSalas.getSelectionModel().getSelectedItem();
        if (sala == null) {
            Dialogos.info("Selecciona una sala", "Elige una sala de la tabla para editarla.");
            return;
        }
        formularioSala(sala);
    }

    @FXML
    private void eliminarSala() {
        Sala sala = tablaSalas.getSelectionModel().getSelectedItem();
        if (sala == null || !Dialogos.confirmar("Eliminar sala",
                "¿Eliminar la sala " + sala.getNumeroSala() + "?")) {
            return;
        }
        ejecutar(() -> AppContext.get().getFacade().eliminarSala(admin(), sala.getIdSala()));
    }

    @FXML
    private void nuevoProducto() {
        formularioProducto(null);
    }

    @FXML
    private void editarProducto() {
        Producto producto = tablaProductos.getSelectionModel().getSelectedItem();
        if (producto == null) {
            Dialogos.info("Selecciona un producto", "Elige un producto de la tabla para editarlo.");
            return;
        }
        formularioProducto(producto);
    }

    @FXML
    private void eliminarProducto() {
        Producto producto = tablaProductos.getSelectionModel().getSelectedItem();
        if (producto == null || !Dialogos.confirmar("Eliminar producto",
                "¿Eliminar '" + producto.getNombre() + "'?")) {
            return;
        }
        ejecutar(() -> AppContext.get().getFacade().eliminarProducto(admin(), producto.getIdProducto()));
    }

    @FXML
    private void nuevoCombo() {
        formularioCombo(null);
    }

    @FXML
    private void editarCombo() {
        Combo combo = tablaCombos.getSelectionModel().getSelectedItem();
        if (combo == null) {
            Dialogos.info("Selecciona un combo", "Elige un combo de la tabla para editarlo.");
            return;
        }
        formularioCombo(combo);
    }

    @FXML
    private void eliminarCombo() {
        Combo combo = tablaCombos.getSelectionModel().getSelectedItem();
        if (combo == null || !Dialogos.confirmar("Eliminar combo",
                "¿Eliminar '" + combo.getNombre() + "'?")) {
            return;
        }
        ejecutar(() -> AppContext.get().getFacade().eliminarCombo(admin(), combo.getIdCombo()));
    }

    @FXML
    private void nuevaPromocion() {
        formularioPromocion(null);
    }

    @FXML
    private void editarPromocion() {
        Promocion promocion = tablaPromociones.getSelectionModel().getSelectedItem();
        if (promocion == null) {
            Dialogos.info("Selecciona una promoción", "Elige una promoción de la tabla para editarla.");
            return;
        }
        formularioPromocion(promocion);
    }

    @FXML
    private void eliminarPromocion() {
        Promocion promocion = tablaPromociones.getSelectionModel().getSelectedItem();
        if (promocion == null || !Dialogos.confirmar("Eliminar promoción",
                "¿Eliminar '" + promocion.getNombre() + "'?")) {
            return;
        }
        ejecutar(() -> AppContext.get().getFacade().eliminarPromocion(admin(), promocion.getIdPromocion()));
    }

    private void formularioPelicula(Pelicula existente) {
        TextField titulo = new TextField(existente != null ? existente.getTitulo() : "");
        TextField genero = new TextField(existente != null ? existente.getGenero() : "");
        TextField duracion = new TextField(existente != null ? String.valueOf(existente.getDuracionMin()) : "120");
        ComboBox<Clasificacion> clasificacion = new ComboBox<>(FXCollections.observableArrayList(Clasificacion.values()));
        clasificacion.setValue(existente != null && existente.getClasificacion() != null
                ? existente.getClasificacion() : Clasificacion.PG_13);
        TextArea sinopsis = new TextArea(existente != null ? existente.getSinopsis() : "");
        sinopsis.setPrefRowCount(3);
        sinopsis.setWrapText(true);
        TextField poster = new TextField(existente != null ? existente.getImagenPoster() : "");
        poster.setPromptText("URL o clave de póster (opcional)");
        CheckBox cartelera = new CheckBox("En cartelera");
        cartelera.setSelected(existente == null || existente.isEnCartelera());

        GridPane grid = grid();
        agregar(grid, 0, "Título", titulo);
        agregar(grid, 1, "Género", genero);
        agregar(grid, 2, "Duración (min)", duracion);
        agregar(grid, 3, "Clasificación", clasificacion);
        agregar(grid, 4, "Sinopsis", sinopsis);
        agregar(grid, 5, "Póster", poster);
        agregar(grid, 6, "", cartelera);

        if (!mostrar("Película", grid)) {
            return;
        }
        try {
            int minutos = entero(duracion.getText(), "La duración");
            if (existente == null) {
                AppContext.get().getFacade().crearPelicula(admin(), titulo.getText(), genero.getText(),
                        minutos, clasificacion.getValue(), sinopsis.getText(), poster.getText(),
                        cartelera.isSelected());
            } else {
                existente.setTitulo(titulo.getText());
                existente.setGenero(genero.getText());
                existente.setDuracionMin(minutos);
                existente.setClasificacion(clasificacion.getValue());
                existente.setSinopsis(sinopsis.getText());
                existente.setImagenPoster(poster.getText());
                existente.setEnCartelera(cartelera.isSelected());
                AppContext.get().getFacade().actualizarPelicula(admin(), existente);
            }
            recargar();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private void formularioSala(Sala existente) {
        TextField numero = new TextField(existente != null ? String.valueOf(existente.getNumeroSala()) : "");
        TextField filas = new TextField(existente != null ? String.valueOf(existente.getFilas()) : "8");
        TextField columnas = new TextField(existente != null ? String.valueOf(existente.getColumnas()) : "10");
        TextField pantalla = new TextField(existente != null ? existente.getTipoPantalla() : "2D");
        if (existente != null) {
            filas.setDisable(true);
            columnas.setDisable(true);
        }

        GridPane grid = grid();
        agregar(grid, 0, "Número de sala", numero);
        agregar(grid, 1, "Filas", filas);
        agregar(grid, 2, "Columnas", columnas);
        agregar(grid, 3, "Tipo de pantalla", pantalla);

        if (!mostrar(existente == null ? "Nueva sala" : "Editar sala", grid)) {
            return;
        }
        try {
            int n = entero(numero.getText(), "El número de sala");
            int f = entero(filas.getText(), "Las filas");
            int c = entero(columnas.getText(), "Las columnas");
            if (existente == null) {
                AppContext.get().getFacade().crearSala(admin(), n, f, c, pantalla.getText());
            } else {
                existente.setNumeroSala(n);
                existente.setTipoPantalla(pantalla.getText());
                AppContext.get().getFacade().actualizarSala(admin(), existente);
            }
            recargar();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private void formularioProducto(Producto existente) {
        TextField nombre = new TextField(existente != null ? existente.getNombre() : "");
        TextField precio = new TextField(existente != null ? String.valueOf(existente.getPrecio()) : "");
        ComboBox<TipoProducto> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoProducto.values()));
        tipo.setValue(existente != null && existente.getTipo() != null ? existente.getTipo() : TipoProducto.CRISPETAS);

        GridPane grid = grid();
        agregar(grid, 0, "Nombre", nombre);
        agregar(grid, 1, "Precio", precio);
        agregar(grid, 2, "Tipo", tipo);

        if (!mostrar(existente == null ? "Nuevo producto" : "Editar producto", grid)) {
            return;
        }
        try {
            double valor = decimal(precio.getText(), "El precio");
            if (existente == null) {
                AppContext.get().getFacade().crearProducto(admin(), nombre.getText(), valor, tipo.getValue());
            } else {
                existente.setNombre(nombre.getText());
                existente.setPrecio(valor);
                existente.setTipo(tipo.getValue());
                AppContext.get().getFacade().actualizarProducto(admin(), existente);
            }
            recargar();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private void formularioCombo(Combo existente) {
        TextField nombre = new TextField(existente != null ? existente.getNombre() : "");
        List<CheckBox> checks = new ArrayList<>();
        VBox opciones = new VBox(5);
        for (Producto producto : AppContext.get().getFacade().getProductos()) {
            CheckBox check = new CheckBox(producto.getNombre() + "  ·  " + Formato.moneda(producto.getPrecio()));
            if (existente != null) {
                for (IItemVendible item : existente.getItems()) {
                    if (producto.getIdProducto().equals(item.getId())) {
                        check.setSelected(true);
                    }
                }
            }
            check.setUserData(producto);
            checks.add(check);
            opciones.getChildren().add(check);
        }

        GridPane grid = grid();
        agregar(grid, 0, "Nombre", nombre);
        agregar(grid, 1, "Productos", opciones);

        if (!mostrar(existente == null ? "Nuevo combo" : "Editar combo", grid)) {
            return;
        }
        try {
            List<IItemVendible> seleccionados = new ArrayList<>();
            for (CheckBox check : checks) {
                if (check.isSelected()) {
                    seleccionados.add((Producto) check.getUserData());
                }
            }
            if (existente == null) {
                AppContext.get().getFacade().crearCombo(admin(), nombre.getText(), seleccionados);
            } else {
                existente.setNombre(nombre.getText());
                existente.getItems().clear();
                existente.getItems().addAll(seleccionados);
                AppContext.get().getFacade().actualizarCombo(admin(), existente);
            }
            recargar();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private void formularioPromocion(Promocion existente) {
        TextField nombre = new TextField(existente != null ? existente.getNombre() : "");
        ComboBox<TipoPromocion> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoPromocion.values()));
        tipo.setValue(existente != null && existente.getTipo() != null
                ? existente.getTipo() : TipoPromocion.DESCUENTO_PORCENTAJE);
        TextField valor = new TextField(existente != null ? String.valueOf(existente.getPorcentajeOValor()) : "10");
        DatePicker inicio = new DatePicker(existente != null ? existente.getFechaInicio() : LocalDate.now());
        DatePicker fin = new DatePicker(existente != null ? existente.getFechaFin() : LocalDate.now().plusDays(30));

        GridPane grid = grid();
        agregar(grid, 0, "Nombre", nombre);
        agregar(grid, 1, "Tipo", tipo);
        agregar(grid, 2, "Valor (% o monto)", valor);
        agregar(grid, 3, "Inicio", inicio);
        agregar(grid, 4, "Fin", fin);

        if (!mostrar(existente == null ? "Nueva promoción" : "Editar promoción", grid)) {
            return;
        }
        try {
            double v = decimal(valor.getText(), "El valor");
            if (existente == null) {
                AppContext.get().getFacade().crearPromocion(admin(), nombre.getText(), tipo.getValue(),
                        v, inicio.getValue(), fin.getValue());
            } else {
                existente.setNombre(nombre.getText());
                existente.setTipo(tipo.getValue());
                existente.setPorcentajeOValor(v);
                existente.setFechaInicio(inicio.getValue());
                existente.setFechaFin(fin.getValue());
                AppContext.get().getFacade().actualizarPromocion(admin(), existente);
            }
            recargar();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private boolean mostrar(String titulo, GridPane grid) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        dialogo.initOwner(AppContext.get().getStage());
        Dialogos.aplicarEstilo(dialogo.getDialogPane());
        dialogo.getDialogPane().setContent(grid);
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);
        Optional<ButtonType> resultado = dialogo.showAndWait();
        return resultado.isPresent() && resultado.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    private GridPane grid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(8, 4, 4, 4));
        grid.setPrefWidth(470);
        return grid;
    }

    private void agregar(GridPane grid, int fila, String etiqueta, Node control) {
        Label label = new Label(etiqueta);
        label.getStyleClass().add("form-label");
        grid.add(label, 0, fila);
        grid.add(control, 1, fila);
        if (control instanceof TextField || control instanceof ComboBox || control instanceof DatePicker) {
            control.setStyle("-fx-pref-width: 300;");
        }
    }

    private void ejecutar(Runnable accion) {
        try {
            accion.run();
            recargar();
        } catch (RuntimeException e) {
            Dialogos.error(mensaje(e));
        }
    }

    private int entero(String texto, String campo) {
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor <= 0) {
                throw new NumberFormatException();
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un entero mayor que cero.");
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

    private String valorPromocion(Promocion promocion) {
        if (promocion.getTipo() == TipoPromocion.DESCUENTO_PORCENTAJE) {
            return String.format("%.0f%%", promocion.getPorcentajeOValor());
        }
        return Formato.moneda(promocion.getPorcentajeOValor());
    }

    private String vigencia(Promocion promocion) {
        String inicio = promocion.getFechaInicio() != null ? Formato.fecha(promocion.getFechaInicio()) : "—";
        String fin = promocion.getFechaFin() != null ? Formato.fecha(promocion.getFechaFin()) : "—";
        return inicio + "  –  " + fin;
    }

    private Administrador admin() {
        return AppContext.get().getSesion().getAdministrador();
    }

    private String mensaje(RuntimeException e) {
        if (e instanceof CinemaUQException) {
            return e.getMessage();
        }
        return e.getMessage() != null ? e.getMessage() : "No fue posible guardar los cambios.";
    }
}
