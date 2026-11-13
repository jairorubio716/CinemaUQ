package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.CompraFlow;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.service.CotizacionCompra;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PagoController implements VistaController {

    @FXML private Label resumenFuncion;
    @FXML private VBox resumenItems;
    @FXML private Label subtotal;
    @FXML private HBox filaDescuento;
    @FXML private Label descuento;
    @FXML private Label total;
    @FXML private Label puntos;
    @FXML private VBox promociones;
    @FXML private Label tarjetaNumero;
    @FXML private Label tarjetaSaldo;
    @FXML private Label tarjetaEstado;
    @FXML private Label alertaSaldo;
    @FXML private Button btnPagar;

    private CompraFlow flow;
    private CotizacionCompra cotizacion;
    private List<Promocion> promocionesVigentes;

    @Override
    public void alMostrar(Object dato) {
        flow = AppContext.get().getCompraFlow();
        if (flow.getFuncion() == null) {
            AppContext.get().getNavegacion().irA("cartelera");
            return;
        }
        resumenFuncion.setText(flow.getPelicula().getTitulo() + "\n"
                + Formato.fechaHora(flow.getFuncion().getFechaHora()));
        promocionesVigentes = promocionesVigentes();
        cotizacion = AppContext.get().getFacade().cotizarCompra(
                flow.getFuncion(), flow.getAsientos(), flow.itemsConfiteria(), promocionesVigentes);
        construirResumen();
        construirPromociones();
        actualizarTarjeta();
    }

    private void construirResumen() {
        resumenItems.getChildren().clear();
        for (Asiento asiento : flow.getAsientos()) {
            resumenItems.getChildren().add(fila(
                    "Entrada " + asiento.getFila() + asiento.getNumero(),
                    Formato.moneda(flow.getFuncion().getPrecioEntrada())));
        }
        for (CotizacionCompra.LineaConfiteria linea : cotizacion.confiteria()) {
            resumenItems.getChildren().add(fila(
                    linea.cantidad() + " × " + linea.item().descripcion(),
                    Formato.moneda(linea.subtotalFinal())));
            if (linea.descuento() > 0.001) {
                Label ahorro = new Label("Promoción " + linea.promocion().getNombre()
                        + ": -" + Formato.moneda(linea.descuento()));
                ahorro.getStyleClass().add("kpi-note");
                resumenItems.getChildren().add(ahorro);
            }
        }
        if (resumenItems.getChildren().isEmpty()) {
            Label vacio = new Label("No hay ítems en la compra.");
            vacio.getStyleClass().add("hint");
            resumenItems.getChildren().add(vacio);
        }
        subtotal.setText(Formato.moneda(cotizacion.subtotal()));
        boolean hayDescuento = cotizacion.descuento() > 0.001;
        filaDescuento.setVisible(hayDescuento);
        filaDescuento.setManaged(hayDescuento);
        descuento.setText("- " + Formato.moneda(cotizacion.descuento()));
        total.setText(Formato.moneda(cotizacion.total()));
        puntos.setText("Ganarás aproximadamente "
                + AppContext.get().getFacade().calcularPuntos(cotizacion.total())
                + " puntos CinemaUQ.");
    }

    private void construirPromociones() {
        promociones.getChildren().clear();
        Map<Promocion, Double> aplicadas = new LinkedHashMap<>();
        for (CotizacionCompra.LineaConfiteria linea : cotizacion.confiteria()) {
            if (linea.promocion() != null && linea.descuento() > 0.001) {
                aplicadas.merge(linea.promocion(), linea.descuento(), Double::sum);
            }
        }
        if (aplicadas.isEmpty()) {
            Label vacio = new Label("Ninguna promoción aplica a los ítems de esta compra.");
            vacio.getStyleClass().add("hint");
            promociones.getChildren().add(vacio);
        } else {
            for (Map.Entry<Promocion, Double> entrada : aplicadas.entrySet()) {
                Label texto = new Label("Aplicada: " + entrada.getKey().getNombre()
                        + " · -" + Formato.moneda(entrada.getValue()));
                texto.getStyleClass().add("summary-row");
                promociones.getChildren().add(texto);
            }
        }
    }

    private List<Promocion> promocionesVigentes() {
        List<Promocion> vigentes = new ArrayList<>();
        for (Promocion promocion : AppContext.get().getFacade().getPromociones()) {
            if (promocion.estaVigente()) {
                vigentes.add(promocion);
            }
        }
        return vigentes;
    }

    private void actualizarTarjeta() {
        Cliente cliente = AppContext.get().getSesion().getCliente();
        alertaSaldo.setText("");
        if (cliente == null || cliente.getTarjetaVirtual() == null) {
            tarjetaNumero.setText("•••• •••• •••• ----");
            tarjetaSaldo.setText("—");
            tarjetaEstado.setText("Sin tarjeta");
            btnPagar.setDisable(true);
            return;
        }
        TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
        String numero = tarjeta.getNroTarjeta();
        tarjetaNumero.setText("•••• •••• •••• " + (numero.length() > 4 ? numero.substring(numero.length() - 4) : numero));
        tarjetaSaldo.setText(Formato.moneda(tarjeta.getSaldo()));
        tarjetaEstado.setText(Formato.texto(tarjeta.getEstado()));
        double totalCompra = cotizacion != null ? cotizacion.total() : flow.subtotal();
        if (!tarjeta.tieneSaldo(totalCompra)) {
            alertaSaldo.setText("Saldo insuficiente. Recarga tu tarjeta para completar el pago.");
        }
    }

    @FXML
    private void pagar() {
        Cliente cliente = AppContext.get().getSesion().getCliente();
        if (cliente == null) {
            Dialogos.info("Inicia sesión", "Debes iniciar sesión como cliente para pagar.");
            return;
        }
        try {
            List<Promocion> promos = promocionesVigentes != null
                    ? promocionesVigentes : promocionesVigentes();
            Compra compra = AppContext.get().getFacade().comprar(
                    cliente, flow.getFuncion(), flow.getAsientos(), flow.itemsConfiteria(), promos);
            flow.setCompra(compra);
            AppContext.get().getNavegacion().irA("confirmacion");
        } catch (SaldoInsuficienteException e) {
            actualizarTarjeta();
            boolean recargar = Dialogos.confirmar("Saldo insuficiente",
                    "Tu saldo no alcanza para esta compra. ¿Quieres ir a Mi tarjeta para solicitar una recarga?");
            if (recargar) {
                AppContext.get().getNavegacion().irA("tarjeta");
            }
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogos.error("No se pudo completar el pago: " + e.getMessage());
        }
    }

    @FXML
    private void volver() {
        AppContext.get().getNavegacion().irA("confiteria");
    }

    private HBox fila(String texto, String valor) {
        Label nombre = new Label(texto);
        nombre.getStyleClass().add("summary-row");
        nombre.setWrapText(true);
        Label precio = new Label(valor);
        precio.getStyleClass().add("summary-row");
        Region espacio = new Region();
        HBox.setHgrow(espacio, javafx.scene.layout.Priority.ALWAYS);
        HBox fila = new HBox(10, nombre, espacio, precio);
        fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return fila;
    }
}
