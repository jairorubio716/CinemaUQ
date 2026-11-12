package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.MontoInvalidoException;
import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.TipoMovimiento;
import co.edu.uniquindio.cinemauq.patrones.adapter.IPasarelaPago;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecargaService {

    private final CatalogoService catalogo;
    private final IPasarelaPago pasarela;
    private final NotificadorObservable notificador;

    public RecargaService(CatalogoService catalogo, IPasarelaPago pasarela,
                          NotificadorObservable notificador) {
        this.catalogo = catalogo;
        this.pasarela = pasarela;
        this.notificador = notificador;
    }

    public SolicitudRecarga solicitarRecarga(Cliente cliente, double monto) {
        if (cliente == null) {
            throw new CinemaUQException("Se requiere un cliente para solicitar la recarga.");
        }
        if (monto <= 0) {
            throw new MontoInvalidoException(monto);
        }
        TarjetaVirtual tarjeta = cliente.getTarjetaVirtual();
        if (tarjeta == null) {
            throw new CinemaUQException("El cliente no tiene una tarjeta virtual.");
        }
        SolicitudRecarga solicitud = new SolicitudRecarga(
                "SOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                LocalDateTime.now(), monto, EstadoSolicitudRecarga.PENDIENTE, tarjeta);
        cliente.agregarSolicitudRecarga(solicitud);
        catalogo.agregarSolicitud(solicitud);
        notificador.notificar("Solicitud de recarga " + solicitud.getIdSolicitud()
                + " por $" + monto + " creada (pendiente de aprobación).");
        catalogo.guardar();
        return solicitud;
    }

    public void aprobarRecarga(Administrador administrador, SolicitudRecarga solicitud) {
        if (administrador == null) {
            throw new CinemaUQException("Solo un administrador puede aprobar recargas (RN-16).");
        }
        if (solicitud == null) {
            throw new CinemaUQException("Se requiere una solicitud de recarga.");
        }
        if (solicitud.getEstado() != EstadoSolicitudRecarga.PENDIENTE) {
            throw new CinemaUQException("La solicitud ya fue procesada (estado: "
                    + solicitud.getEstado() + ").");
        }
        if (solicitud.getMonto() <= 0) {
            throw new MontoInvalidoException(solicitud.getMonto());
        }
        TarjetaVirtual tarjeta = solicitud.getTarjeta();
        if (tarjeta == null || !tarjeta.estaActiva()) {
            throw new CinemaUQException("La tarjeta no existe o no está activa (RN-12).");
        }
        boolean aprobado = pasarela.pagarExterno(solicitud.getIdSolicitud(), solicitud.getMonto());
        if (!aprobado) {
            solicitud.setEstado(EstadoSolicitudRecarga.RECHAZADA);
            catalogo.guardar();
            throw new CinemaUQException("La pasarela rechazó el pago de la recarga.");
        }
        tarjeta.setSaldo(tarjeta.getSaldo() + solicitud.getMonto());
        tarjeta.registrarMovimiento(new Movimiento(
                catalogo.siguienteIdMovimiento(), LocalDateTime.now(),
                TipoMovimiento.RECARGA, solicitud.getMonto(), tarjeta.getSaldo()));
        solicitud.setEstado(EstadoSolicitudRecarga.APROBADA);
        notificador.notificar("Recarga " + solicitud.getIdSolicitud() + " aprobada por $"
                + solicitud.getMonto() + ".");
        catalogo.guardar();
    }

    public void rechazarRecarga(Administrador administrador, SolicitudRecarga solicitud) {
        if (administrador == null) {
            throw new CinemaUQException("Solo un administrador puede rechazar recargas (RN-16).");
        }
        if (solicitud == null || solicitud.getEstado() != EstadoSolicitudRecarga.PENDIENTE) {
            throw new CinemaUQException("La solicitud no existe o ya fue procesada.");
        }
        solicitud.setEstado(EstadoSolicitudRecarga.RECHAZADA);
        notificador.notificar("Recarga " + solicitud.getIdSolicitud() + " rechazada.");
        catalogo.guardar();
    }
}
