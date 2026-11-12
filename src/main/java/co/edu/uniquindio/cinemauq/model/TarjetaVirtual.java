package co.edu.uniquindio.cinemauq.model;

import java.util.ArrayList;
import java.util.List;

public class TarjetaVirtual {

    private String nroTarjeta;
    private double saldo;
    private EstadoTarjeta estado;
    private List<Movimiento> movimientos = new ArrayList<>();

    public TarjetaVirtual(String nroTarjeta, double saldo, EstadoTarjeta estado) {
        this.nroTarjeta = nroTarjeta;
        this.saldo = saldo;
        this.estado = estado;
    }

    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public EstadoTarjeta getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarjeta estado) {
        this.estado = estado;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public boolean estaActiva() {
        return estado == EstadoTarjeta.ACTIVA;
    }

    public boolean tieneSaldo(double monto) {
        return saldo >= monto;
    }

    public void registrarMovimiento(Movimiento movimiento) {
        if (movimiento != null) {
            movimientos.add(movimiento);
        }
    }

    @Override
    public String toString() {
        return "TarjetaVirtual{" +
                "nroTarjeta='" + nroTarjeta + '\'' +
                ", saldo=" + saldo +
                ", estado=" + estado +
                '}';
    }
}
