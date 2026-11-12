package co.edu.uniquindio.cinemauq.patrones.state;

import co.edu.uniquindio.cinemauq.model.EstadoCompra;

public class EstadoCancelada implements IEstadoCompra {

    @Override
    public EstadoCompra getCodigo() {
        return EstadoCompra.CANCELADA;
    }

    @Override
    public boolean permitePago() {
        return false;
    }

    @Override
    public boolean permiteReembolso() {
        return false;
    }

    @Override
    public String getNombre() {
        return "Cancelada";
    }
}
