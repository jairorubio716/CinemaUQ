package co.edu.uniquindio.cinemauq.patrones.factory;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.Funcion;

import java.util.concurrent.atomic.AtomicInteger;

public class FabricaEntrada extends IFabricaEntrada {

    private static final AtomicInteger SECUENCIA = new AtomicInteger(1);

    @Override
    public Entrada crearEntrada(String idEntrada, Funcion funcion, Asiento asiento, double precio) {
        String codigo = String.format("E-%05d", SECUENCIA.getAndIncrement());
        return new Entrada(idEntrada, codigo, precio, funcion, asiento);
    }
}
