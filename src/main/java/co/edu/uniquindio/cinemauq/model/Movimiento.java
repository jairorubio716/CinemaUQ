package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDateTime;

public class Movimiento {

    private int idMovimiento;
    private LocalDateTime fecha;
    private TipoMovimiento tipo;
    private double monto;
    private double saldoResultante;

    public Movimiento(int idMovimiento, LocalDateTime fecha, TipoMovimiento tipo,
                      double monto, double saldoResultante) {
        this.idMovimiento = idMovimiento;
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoResultante = saldoResultante;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(double saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "idMovimiento=" + idMovimiento +
                ", fecha=" + fecha +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", saldoResultante=" + saldoResultante +
                '}';
    }
}