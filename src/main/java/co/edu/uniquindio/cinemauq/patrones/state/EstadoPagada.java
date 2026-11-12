package co.edu.uniquindio.cinemauq.patrones.state;

import co.edu.uniquindio.cinemauq.model.EstadoCompra;

public class EstadoPagada implements IEstadoCompra {

    @Override
    public EstadoCompra getCodigo() {
        return EstadoCompra.PAGADA;
    }

    @Override
    public boolean permitePago() {
        return false;
    }

    @Override
    public boolean permiteReembolso() {
        return true;
    }

    @Override
    public String getNombre() {
        return "Pagada";
    }
}
