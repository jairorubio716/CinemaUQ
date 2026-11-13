package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.controller.ShellController;
import co.edu.uniquindio.cinemauq.patrones.facade.CinemaFacade;
import javafx.stage.Stage;

public final class AppContext {

    public static final String CSS = "/co/edu/uniquindio/cinemauq/css/cinemauq.css";
    public static final String VISTAS = "/co/edu/uniquindio/cinemauq/view/";

    private static final AppContext INSTANCIA = new AppContext();

    private final CinemaFacade facade;
    private final Sesion sesion;
    private final CompraFlow compraFlow;
    private final ToastNotificador toastNotificador;

    private Stage stage;
    private Navegacion navegacion;
    private ShellController shell;

    private AppContext() {
        facade = CinemaFacade.getInstance();
        sesion = new Sesion();
        compraFlow = new CompraFlow();
        toastNotificador = new ToastNotificador();
        facade.getNotificador().agregarObservador(toastNotificador);
    }

    public static AppContext get() {
        return INSTANCIA;
    }

    public CinemaFacade getFacade() {
        return facade;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public CompraFlow getCompraFlow() {
        return compraFlow;
    }

    public ToastNotificador getToastNotificador() {
        return toastNotificador;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Navegacion getNavegacion() {
        return navegacion;
    }

    public void setNavegacion(Navegacion navegacion) {
        this.navegacion = navegacion;
    }

    public ShellController getShell() {
        return shell;
    }

    public void setShell(ShellController shell) {
        this.shell = shell;
    }
}
