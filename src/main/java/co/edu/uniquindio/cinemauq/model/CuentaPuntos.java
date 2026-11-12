package co.edu.uniquindio.cinemauq.model;

import java.util.ArrayList;
import java.util.List;

public class CuentaPuntos {

    private String idCuenta;
    private List<PuntoAcumulado> puntos = new ArrayList<>();

    public CuentaPuntos(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public List<PuntoAcumulado> getPuntos() {
        return puntos;
    }

    public void setPuntos(List<PuntoAcumulado> puntos) {
        this.puntos = puntos;
    }

    public void agregarPunto(PuntoAcumulado punto) {
        if (punto != null) {
            puntos.add(punto);
        }
    }

    public int totalPuntosVigentes() {
        int total = 0;
        for (PuntoAcumulado punto : puntos) {
            if (punto.estaVigente()) {
                total += punto.getCantidad();
            }
        }
        return total;
    }

    @Override
    public String toString() {
        return "CuentaPuntos{" +
                "idCuenta='" + idCuenta + '\'' +
                ", puntosVigentes=" + totalPuntosVigentes() +
                '}';
    }
}
