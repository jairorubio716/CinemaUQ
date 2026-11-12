package co.edu.uniquindio.cinemauq.patrones.state;

import co.edu.uniquindio.cinemauq.model.EstadoCompra;

public final class EstadosCompra {

    private EstadosCompra() {
    }

    public static IEstadoCompra crear(EstadoCompra codigo) {
        if (codigo == null) {
            return new EstadoPendiente();
        }
        return switch (codigo) {
            case PENDIENTE -> new EstadoPendiente();
            case PAGADA -> new EstadoPagada();
            case CANCELADA -> new EstadoCancelada();
            case REEMBOLSADA -> new EstadoReembolsada();
        };
    }
}
