package co.edu.uniquindio.cinemauq.controller;

import co.edu.uniquindio.cinemauq.app.AppContext;
import co.edu.uniquindio.cinemauq.app.Dialogos;
import co.edu.uniquindio.cinemauq.app.VistaController;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

public class LoginController implements VistaController {

    @FXML private TabPane tabs;
    @FXML private TextField usuarioLogin;
    @FXML private PasswordField contrasenaLogin;
    @FXML private Label errorLogin;

    @FXML private TextField nombreRegistro;
    @FXML private TextField emailRegistro;
    @FXML private TextField celularRegistro;
    @FXML private TextField usuarioRegistro;
    @FXML private PasswordField contrasenaRegistro;
    @FXML private Label errorRegistro;

    @Override
    public void alMostrar(Object dato) {
        limpiarErrores();
        if (usuarioLogin != null) {
            usuarioLogin.requestFocus();
        }
    }

    @FXML
    private void ingresar() {
        limpiarErrores();
        try {
            Persona persona = AppContext.get().getFacade()
                    .iniciarSesion(usuarioLogin.getText().trim(), contrasenaLogin.getText());
            AppContext.get().getSesion().iniciar(persona);
            AppContext.get().getCompraFlow().limpiar();
            if (persona instanceof Cliente) {
                AppContext.get().getNavegacion().irA("cartelera");
            } else {
                AppContext.get().getNavegacion().irA("admin-dashboard");
            }
        } catch (CinemaUQException e) {
            errorLogin.setText(e.getMessage());
        } catch (RuntimeException e) {
            errorLogin.setText("No se pudo iniciar sesión: " + e.getMessage());
        }
    }

    @FXML
    private void registrar() {
        limpiarErrores();
        try {
            Cliente cliente = AppContext.get().getFacade().registrarCliente(
                    nombreRegistro.getText().trim(),
                    emailRegistro.getText().trim(),
                    celularRegistro.getText().trim(),
                    usuarioRegistro.getText().trim(),
                    contrasenaRegistro.getText());
            AppContext.get().getSesion().iniciar(cliente);
            AppContext.get().getCompraFlow().limpiar();
            Dialogos.info("¡Bienvenido a CinemaUQ!",
                    "Tu cuenta fue creada. Ya puedes recargar tu tarjeta virtual y comprar entradas.");
            AppContext.get().getNavegacion().irA("cartelera");
        } catch (CinemaUQException e) {
            errorRegistro.setText(e.getMessage());
        } catch (RuntimeException e) {
            errorRegistro.setText("No se pudo registrar: " + e.getMessage());
        }
    }

    private void limpiarErrores() {
        if (errorLogin != null) {
            errorLogin.setText("");
        }
        if (errorRegistro != null) {
            errorRegistro.setText("");
        }
    }
}
