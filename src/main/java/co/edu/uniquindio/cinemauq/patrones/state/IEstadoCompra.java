package co.edu.uniquindio.cinemauq.patrones.state;

import co.edu.uniquindio.cinemauq.model.EstadoCompra;

public interface IEstadoCompra {

    EstadoCompra getCodigo();

    boolean permitePago();

    boolean permiteReembolso();

    String getNombre();
}
