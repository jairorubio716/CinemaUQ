package co.edu.uniquindio.cinemauq.patrones.factory;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.Funcion;

public abstract class IFabricaEntrada {

    public abstract Entrada crearEntrada(String idEntrada, Funcion funcion, Asiento asiento, double precio);
}
