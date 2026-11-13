package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Formato;
import co.edu.uniquindio.cinemauq.app.Posters;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Entrada;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ConfirmacionController implements VistaController {

    @FXML private VBox qr;
    @FXML private Label codigo;
    @FXML private Label pelicula;
    @FXML private Label funcion;
    @FXML private Label asientos;
    @FXML private Label total;
    @FXML private Label puntos;
    @FXML private Label mensaje;

    @Override
    public void alMostrar(Object dato) {
        Compra compra = AppContext.get().getCompraFlow().getCompra();
        if (compra == null) {
            AppContext.get().getNavegacion().irA("cartelera");
            return;
        }
        codigo.setText(compra.getIdCompra());
        qr.getChildren().setAll(Posters.qr(compra.getIdCompra(), 150));
        total.setText(Formato.moneda(compra.getTotal()));
        puntos.setText("Ganaste aproximadamente " + (int) (compra.getTotal() / 1000) + " puntos CinemaUQ.");

        if (!compra.getEntradas().isEmpty()) {
            Entrada primera = compra.getEntradas().get(0);
            pelicula.setText(primera.getFuncion() != null ? primera.getFuncion().getPelicula().getTitulo() : "Función");
            funcion.setText(primera.getFuncion() != null
                    ? Formato.fechaHora(primera.getFuncion().getFechaHora()) : "—");
            List<String> sillas = new ArrayList<>();
            for (Entrada entrada : compra.getEntradas()) {
                Asiento asiento = entrada.getAsiento();
                if (asiento != null) {
                    sillas.add(asiento.getFila() + asiento.getNumero());
                }
            }
            asientos.setText("Asientos: " + String.join(", ", sillas));
            mensaje.setText("Tu compra quedó registrada. Presenta este código en la entrada del cine.");
        } else {
            pelicula.setText("Confitería CinemaUQ");
            funcion.setText("Pedido listo para reclamar");
            asientos.setText("Sin entradas asociadas");
            mensaje.setText("Tu pedido de confitería quedó registrado.");
        }
    }

    @FXML
    private void misCompras() {
        AppContext.get().getCompraFlow().limpiar();
        AppContext.get().getNavegacion().irA("mis-compras");
    }

    @FXML
    private void cartelera() {
        AppContext.get().getCompraFlow().limpiar();
        AppContext.get().getNavegacion().irA("cartelera");
    }
}
