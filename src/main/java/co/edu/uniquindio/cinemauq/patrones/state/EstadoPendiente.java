package co.edu.uniquindio.cinemauq.patrones.state;

import co.edu.uniquindio.cinemauq.model.EstadoCompra;

public class EstadoPendiente implements IEstadoCompra {

    @Override
    public EstadoCompra getCodigo() {
        return EstadoCompra.PENDIENTE;
    }

    @Override
    public boolean permitePago() {
        return true;
    }

    @Override
    public boolean permiteReembolso() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Pendiente";
    }
}
