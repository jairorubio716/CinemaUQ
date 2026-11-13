package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Promocion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Estado temporal del flujo de compra: película, función, asientos y confitería.
 */
public class CompraFlow {

    private Pelicula pelicula;
    private Funcion funcion;
    private final Set<Asiento> asientos = new LinkedHashSet<>();
    private final Map<IItemVendible, Integer> confiteria = new LinkedHashMap<>();
    private final List<Promocion> promociones = new ArrayList<>();
    private Compra compra;

    public void iniciar(Pelicula pelicula, Funcion funcion) {
        this.pelicula = pelicula;
        this.funcion = funcion;
        this.asientos.clear();
        this.confiteria.clear();
        this.promociones.clear();
        this.compra = null;
    }

    public void limpiar() {
        pelicula = null;
        funcion = null;
        asientos.clear();
        confiteria.clear();
        promociones.clear();
        compra = null;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public List<Asiento> getAsientos() {
        return new ArrayList<>(asientos);
    }

    public boolean alternarAsiento(Asiento asiento) {
        if (asiento == null) {
            return false;
        }
        if (asientos.contains(asiento)) {
            asientos.remove(asiento);
            return false;
        }
        asientos.add(asiento);
        return true;
    }

    public boolean estaSeleccionado(Asiento asiento) {
        return asientos.contains(asiento);
    }

    public void agregarItem(IItemVendible item) {
        if (item != null) {
            confiteria.merge(item, 1, Integer::sum);
        }
    }

    public void quitarItem(IItemVendible item) {
        if (item == null) {
            return;
        }
        Integer cantidad = confiteria.get(item);
        if (cantidad == null) {
            return;
        }
        if (cantidad <= 1) {
            confiteria.remove(item);
        } else {
            confiteria.put(item, cantidad - 1);
        }
    }

    public int cantidad(IItemVendible item) {
        return confiteria.getOrDefault(item, 0);
    }

    public Map<IItemVendible, Integer> getConfiteria() {
        return new LinkedHashMap<>(confiteria);
    }

    public List<IItemVendible> itemsConfiteria() {
        List<IItemVendible> items = new ArrayList<>();
        for (Map.Entry<IItemVendible, Integer> entrada : confiteria.entrySet()) {
            for (int i = 0; i < entrada.getValue(); i++) {
                items.add(entrada.getKey());
            }
        }
        return items;
    }

    public double totalEntradas() {
        return funcion != null ? funcion.getPrecioEntrada() * asientos.size() : 0;
    }

    public double totalConfiteria() {
        double total = 0;
        for (Map.Entry<IItemVendible, Integer> entrada : confiteria.entrySet()) {
            total += entrada.getKey().obtenerPrecio() * entrada.getValue();
        }
        return total;
    }

    public double subtotal() {
        return totalEntradas() + totalConfiteria();
    }

    public int totalItemsConfiteria() {
        int total = 0;
        for (Integer cantidad : confiteria.values()) {
            total += cantidad;
        }
        return total;
    }

    public List<Promocion> getPromociones() {
        return promociones;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }
}
