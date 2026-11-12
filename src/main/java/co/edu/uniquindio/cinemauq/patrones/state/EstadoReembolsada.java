package co.edu.uniquindio.cinemauq.patrones.state;

import co.edu.uniquindio.cinemauq.model.EstadoCompra;

public class EstadoReembolsada implements IEstadoCompra {

    @Override
    public EstadoCompra getCodigo() {
        return EstadoCompra.REEMBOLSADA;
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
        return "Reembolsada";
    }
}
