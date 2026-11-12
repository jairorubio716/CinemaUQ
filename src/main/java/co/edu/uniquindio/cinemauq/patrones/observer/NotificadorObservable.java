package co.edu.uniquindio.cinemauq.patrones.observer;

import java.util.ArrayList;
import java.util.List;

public class NotificadorObservable {

    private final List<INotificable> observadores = new ArrayList<>();

    public void agregarObservador(INotificable observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(INotificable observador) {
        observadores.remove(observador);
    }

    public int getCantidadObservadores() {
        return observadores.size();
    }

    public void notificar(String evento) {
        for (INotificable observador : new ArrayList<>(observadores)) {
            observador.actualizar(evento);
        }
    }
}
