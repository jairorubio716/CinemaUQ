package co.edu.uniquindio.cinemauq.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona {

    private String idCliente;
    private LocalDate fechaRegistro;
    private TarjetaVirtual tarjetaVirtual;
    private CuentaPuntos cuentaPuntos;
    private List<Compra> compras = new ArrayList<>();
    private List<SolicitudRecarga> solicitudesRecarga = new ArrayList<>();

    public Cliente(String nombre, String email, String celular, String usuario, String contrasena,
                   String idCliente, LocalDate fechaRegistro) {
        super(nombre, email, celular, usuario, contrasena);
        this.idCliente = idCliente;
        this.fechaRegistro = fechaRegistro;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public TarjetaVirtual getTarjetaVirtual() {
        return tarjetaVirtual;
    }

    public void setTarjetaVirtual(TarjetaVirtual tarjetaVirtual) {
        this.tarjetaVirtual = tarjetaVirtual;
    }

    public CuentaPuntos getCuentaPuntos() {
        return cuentaPuntos;
    }

    public void setCuentaPuntos(CuentaPuntos cuentaPuntos) {
        this.cuentaPuntos = cuentaPuntos;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }

    public List<SolicitudRecarga> getSolicitudesRecarga() {
        return solicitudesRecarga;
    }

    public void setSolicitudesRecarga(List<SolicitudRecarga> solicitudesRecarga) {
        this.solicitudesRecarga = solicitudesRecarga;
    }

    public void agregarCompra(Compra compra) {
        if (compra != null) {
            compras.add(compra);
        }
    }

    public void agregarSolicitudRecarga(SolicitudRecarga solicitud) {
        if (solicitud != null) {
            solicitudesRecarga.add(solicitud);
        }
    }

    @Override
    public String toString() {
        return "Cliente{" + super.toString() +
                ", idCliente='" + idCliente + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
