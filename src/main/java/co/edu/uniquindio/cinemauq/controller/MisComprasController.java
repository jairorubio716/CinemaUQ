package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.patrones.strategy.IReembolso;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MisComprasController implements VistaController {

    @FXML private VBox lista;
    @FXML private VBox vacio;

    @Override
    public void alMostrar(Object dato) {
        renderizar();
    }

    @FXML
    private void actualizar() {
        renderizar();
    }

    private void renderizar() {
        lista.getChildren().clear();
        Cliente cliente = AppContext.get().getSesion().getCliente();
        if (cliente == null) {
            AppContext.get().getNavegacion().irA("login");
            return;
        }
        List<Compra> compras = new ArrayList<>(AppContext.get().getFacade().historialCompras(cliente));
        compras.sort(Comparator.comparing(Compra::getFecha,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        boolean hay = !compras.isEmpty();
        vacio.setVisible(!hay);
        vacio.setManaged(!hay);
        for (Compra compra : compras) {
            lista.getChildren().add(crearTarjeta(compra));
        }
    }

    private VBox crearTarjeta(Compra compra) {
        Label id = new Label(compra.getIdCompra());
        id.getStyleClass().add("h2");
        Label fecha = new Label(Formato.fechaHora(compra.getFecha()));
        fecha.getStyleClass().add("card-meta");
        Label estado = new Label(Formato.texto(compra.getEstado()));
        estado.getStyleClass().addAll("badge", claseEstado(compra.getEstado()));
        Label total = new Label(Formato.moneda(compra.getTotal()));
        total.getStyleClass().add("price");

        VBox izquierda = new VBox(3, id, fecha);
        Region espacio = new Region();
        HBox.setHgrow(espacio, javafx.scene.layout.Priority.ALWAYS);
        HBox cabecera = new HBox(12, izquierda, espacio, estado, total);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        VBox detalles = new VBox(4);
        if (!compra.getEntradas().isEmpty()) {
            Entrada primera = compra.getEntradas().get(0);
            Funcion funcion = primera.getFuncion();
            if (funcion != null && funcion.getPelicula() != null) {
                Label pelicula = new Label(funcion.getPelicula().getTitulo());
                pelicula.getStyleClass().add("card-titulo");
                detalles.getChildren().add(pelicula);
            }
            if (funcion != null) {
                Label sesion = new Label(Formato.fechaHora(funcion.getFechaHora()) + "  ·  Sala "
                        + (funcion.getSala() != null ? funcion.getSala().getNumeroSala() : "—"));
                sesion.getStyleClass().add("card-meta");
                detalles.getChildren().add(sesion);
            }
            List<String> sillas = new ArrayList<>();
            for (Entrada entrada : compra.getEntradas()) {
                Asiento asiento = entrada.getAsiento();
                if (asiento != null) {
                    sillas.add(asiento.getFila() + asiento.getNumero());
                }
            }
            Label asientos = new Label("Asientos: " + String.join(", ", sillas));
            asientos.getStyleClass().add("summary-row");
            detalles.getChildren().add(asientos);
        }
        Label confiteria = new Label(compra.getConfiteria().size() + " ítem(s) de confitería");
        confiteria.getStyleClass().add("card-meta");
        detalles.getChildren().add(confiteria);

        HBox acciones = new HBox(10);
        Button verTicket = new Button("Ver ticket");
        verTicket.getStyleClass().add("secondary-button");
        verTicket.setOnAction(e -> {
            AppContext.get().getCompraFlow().setCompra(compra);
            AppContext.get().getNavegacion().irA("confirmacion");
        });
        acciones.getChildren().add(verTicket);

        if (compra.getEstado() == EstadoCompra.PAGADA && compra.puedeReembolsarse()) {
            Button cancelar = new Button("Cancelar compra");
            cancelar.getStyleClass().add("danger-button");
            cancelar.setOnAction(e -> cancelar(compra));
            acciones.getChildren().add(cancelar);
        }

        VBox tarjeta = new VBox(11, cabecera, detalles, acciones);
        tarjeta.getStyleClass().add("panel");
        return tarjeta;
    }

    private void cancelar(Compra compra) {
        long horas = horasHastaFuncion(compra);
        IReembolso estrategia = AppContext.get().getFacade().getReembolsoService()
                .seleccionarEstrategia(horas, false);
        double valor = estrategia.calcularReembolso(compra.getTotal());
        if (valor <= 0) {
            Dialogos.info("Cancelación no disponible",
                    "Faltan menos de 2 horas para la función, por lo que no aplica reembolso (RN-05).");
            return;
        }
        boolean confirmar = Dialogos.confirmar("Cancelar compra",
                "Se aplicará: " + estrategia.getDescripcion()
                        + "\nReembolso estimado: " + Formato.moneda(valor)
                        + "\n\n¿Deseas continuar?");
        if (!confirmar) {
            return;
        }
        try {
            double reembolsado = AppContext.get().getFacade().cancelarCompra(compra);
            Dialogos.exito("Compra cancelada",
                    "El reembolso de " + Formato.moneda(reembolsado) + " fue abonado a tu tarjeta virtual.");
            renderizar();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        } catch (RuntimeException e) {
            Dialogos.error("No se pudo cancelar la compra: " + e.getMessage());
        }
    }

    private long horasHastaFuncion(Compra compra) {
        LocalDateTime proxima = null;
        for (Entrada entrada : compra.getEntradas()) {
            Funcion funcion = entrada.getFuncion();
            if (funcion != null && funcion.getFechaHora() != null
                    && (proxima == null || funcion.getFechaHora().isBefore(proxima))) {
                proxima = funcion.getFechaHora();
            }
        }
        if (proxima == null) {
            return Long.MAX_VALUE;
        }
        return Duration.between(LocalDateTime.now(), proxima).toHours();
    }

    private String claseEstado(EstadoCompra estado) {
        return switch (estado) {
            case PAGADA -> "badge-success";
            case PENDIENTE -> "badge-warning";
            case CANCELADA -> "badge-danger";
            case REEMBOLSADA -> "badge-info";
        };
    }
}
