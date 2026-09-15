package co.edu.uniquindio.cinemauq.model;

public class TarjetaVirtual {

    private String nroTarjeta;
    private double saldo;
    private EstadoTarjeta estado;

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

    @Override
    public String toString() {
        return "TarjetaVirtual{" +
                "nroTarjeta='" + nroTarjeta + '\'' +
                ", saldo=" + saldo +
                ", estado=" + estado +
                '}';
    }
}