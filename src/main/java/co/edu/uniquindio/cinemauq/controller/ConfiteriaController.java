package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.CompraFlow;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.service.CotizacionCompra;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class ConfiteriaController implements VistaController {

    @FXML private HBox categorias;
    @FXML private FlowPane productos;
    @FXML private Label resumenFuncion;
    @FXML private Label resumenEntradas;
    @FXML private VBox detalleItems;
    @FXML private Label totalConfiteria;
    @FXML private HBox filaDescuento;
    @FXML private Label descuento;
    @FXML private Label total;
    @FXML private Button btnPagar;

    private CompraFlow flow;
    private String categoriaActual = "Todos";

    @FXML
    public void initialize() {
        construirCategorias();
    }

    @Override
    public void alMostrar(Object dato) {
        flow = AppContext.get().getCompraFlow();
        if (flow.getFuncion() == null) {
            AppContext.get().getNavegacion().irA("cartelera");
            return;
        }
        resumenFuncion.setText(flow.getPelicula().getTitulo() + "\n" + Formato.fechaHora(flow.getFuncion().getFechaHora()));
        categoriaActual = "Todos";
        renderizarProductos();
        actualizarResumen();
    }

    private void construirCategorias() {
        String[] nombres = {"Todos", "Crispetas", "Bebidas", "Dulces", "Hotdogs", "Nachos", "Combos"};
        for (String nombre : nombres) {
            Button chip = new Button(nombre);
            chip.getStyleClass().add("chip");
            if (nombre.equals(categoriaActual)) {
                chip.getStyleClass().add("chip-active");
            }
            chip.setOnAction(e -> {
                categoriaActual = nombre;
                for (var nodo : categorias.getChildren()) {
                    nodo.getStyleClass().remove("chip-active");
                }
                chip.getStyleClass().add("chip-active");
                renderizarProductos();
            });
            categorias.getChildren().add(chip);
        }
    }

    private void renderizarProductos() {
        productos.getChildren().clear();
        if (categoriaActual.equals("Todos") || categoriaActual.equals("Combos")) {
            for (Combo combo : AppContext.get().getFacade().getCombos()) {
                productos.getChildren().add(crearTarjeta(combo, true));
            }
        }
        for (Producto producto : AppContext.get().getFacade().getProductos()) {
            if (categoriaActual.equals("Todos") || categoriaActual.equals(nombreCategoria(producto.getTipo()))) {
                productos.getChildren().add(crearTarjeta(producto, false));
            }
        }
    }

    private String nombreCategoria(TipoProducto tipo) {
        return switch (tipo) {
            case CRISPETAS -> "Crispetas";
            case BEBIDA -> "Bebidas";
            case DULCE -> "Dulces";
            case HOTDOG -> "Hotdogs";
            case NACHOS -> "Nachos";
        };
    }

    private VBox crearTarjeta(IItemVendible item, boolean combo) {
        Label icono = new Label(icono(item));
        icono.getStyleClass().add("product-icon");
        Label titulo = new Label(nombre(item));
        titulo.getStyleClass().add("product-title");
        Label descripcion = new Label(descripcion(item));
        descripcion.getStyleClass().add(combo ? "combo-items" : "card-meta");
        descripcion.setWrapText(true);
        Label precio = new Label(Formato.moneda(item.obtenerPrecio()));
        precio.getStyleClass().add("product-price");

        Label cantidad = new Label();
        cantidad.getStyleClass().add("qty-label");
        Button menos = new Button("−");
        menos.getStyleClass().add("qty-button");
        Button mas = new Button("+");
        mas.getStyleClass().add("qty-button");

        Runnable actualizar = () -> {
            int valor = flow.cantidad(item);
            cantidad.setText(String.valueOf(valor));
            actualizarResumen();
        };
        menos.setOnAction(e -> {
            flow.quitarItem(item);
            actualizar.run();
        });
        mas.setOnAction(e -> {
            flow.agregarItem(item);
            actualizar.run();
        });
        int inicial = flow.cantidad(item);
        cantidad.setText(String.valueOf(inicial));

        HBox controles = new HBox(8, menos, cantidad, mas);
        controles.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(6, icono, titulo, descripcion, precio, controles);
        tarjeta.getStyleClass().add("product-card");
        return tarjeta;
    }

    private String nombre(IItemVendible item) {
        if (item instanceof Producto producto) {
            return producto.getNombre();
        }
        if (item instanceof Combo combo) {
            return combo.getNombre();
        }
        return item.descripcion();
    }

    private String descripcion(IItemVendible item) {
        if (item instanceof Combo combo) {
            StringBuilder texto = new StringBuilder();
            for (IItemVendible entrada : combo.getItems()) {
                if (texto.length() > 0) {
                    texto.append(" · ");
                }
                texto.append(nombre(entrada));
            }
            return texto.toString();
        }
        if (item instanceof Producto producto) {
            return Formato.texto(producto.getTipo());
        }
        return item.descripcion();
    }

    private String icono(IItemVendible item) {
        if (item instanceof Combo) {
            return "🎁";
        }
        if (item instanceof Producto producto) {
            return switch (producto.getTipo()) {
                case CRISPETAS -> "🍿";
                case BEBIDA -> "🥤";
                case DULCE -> "🍫";
                case HOTDOG -> "🌭";
                case NACHOS -> "🧀";
            };
        }
        return "🍿";
    }

    private void actualizarResumen() {
        CotizacionCompra cotizacion = AppContext.get().getFacade().cotizarCompra(
                flow.getFuncion(), flow.getAsientos(), flow.itemsConfiteria(), promocionesVigentes());
        resumenEntradas.setText("Entradas: " + flow.getAsientos().size() + "  ·  " + Formato.moneda(flow.totalEntradas()));
        detalleItems.getChildren().clear();
        if (cotizacion.confiteria().isEmpty()) {
            Label vacio = new Label("Sin confitería seleccionada.");
            vacio.getStyleClass().add("hint");
            detalleItems.getChildren().add(vacio);
        } else {
            for (CotizacionCompra.LineaConfiteria linea : cotizacion.confiteria()) {
                HBox fila = new HBox();
                Label nombre = new Label(linea.cantidad() + " × "
                        + Formato.truncar(nombre(linea.item()), 24));
                nombre.getStyleClass().add("summary-row");
                Label valor = new Label(Formato.moneda(linea.subtotalFinal()));
                valor.getStyleClass().add("summary-row");
                Region espacio = new Region();
                HBox.setHgrow(espacio, javafx.scene.layout.Priority.ALWAYS);
                fila.getChildren().addAll(nombre, espacio, valor);
                detalleItems.getChildren().add(fila);
            }
        }
        totalConfiteria.setText(Formato.moneda(cotizacion.subtotal() - flow.totalEntradas()));
        boolean hayDescuento = cotizacion.descuento() > 0.001;
        filaDescuento.setVisible(hayDescuento);
        filaDescuento.setManaged(hayDescuento);
        descuento.setText("- " + Formato.moneda(cotizacion.descuento()));
        total.setText(Formato.moneda(cotizacion.total()));
    }

    private List<Promocion> promocionesVigentes() {
        List<Promocion> vigentes = new java.util.ArrayList<>();
        for (Promocion promocion : AppContext.get().getFacade().getPromociones()) {
            if (promocion.estaVigente()) {
                vigentes.add(promocion);
            }
        }
        return vigentes;
    }

    @FXML
    private void continuar() {
        AppContext.get().getNavegacion().irA("pago");
    }

    @FXML
    private void volver() {
        AppContext.get().getNavegacion().irA("asientos");
    }
}
